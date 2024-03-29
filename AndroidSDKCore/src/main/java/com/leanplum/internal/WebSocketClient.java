// Copyright (c) 2009-2012 James Coglan
// Copyright (c) 2012 Eric Butler 
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the 'Software'), to deal in
// the Software without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
// Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
// Source: https://github.com/codebutler/android-websockets/blob/master/src/main/java/com/codebutler/android_websockets/WebSocketClient.java

package com.leanplum.internal;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Base64;

import java.net.SocketException;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

// Suppressing deprecated apache dependency.
@SuppressWarnings("deprecation")
class WebSocketClient {
  private static final String TAG = "WebSocketClient";

  private URI mURI;
  private volatile Listener mListener;
  private java.net.Socket mSocket;
  private Thread mThread;
  private HandlerThread mHandlerThread;
  private Handler mHandler;
  private List<BasicNameValuePair> mExtraHeaders;
  private HybiParser mParser;

  private final Object mSendLock = new Object();

  private static TrustManager[] sTrustManagers;

  public static void setTrustManagers(TrustManager[] tm) {
    sTrustManagers = tm;
  }

  public WebSocketClient(URI uri, Listener listener, List<BasicNameValuePair> extraHeaders) {
    mURI = uri;
    mListener = listener;
    mExtraHeaders = extraHeaders;
    mParser = new HybiParser(this);

    mHandlerThread = new HandlerThread("websocket-thread");
    mHandlerThread.start();
    mHandler = new Handler(mHandlerThread.getLooper());
  }

  public Listener getListener() {
    return mListener;
  }

  public void connect() {
    if (mThread != null && mThread.isAlive()) {
      return;
    }

    mThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          int port = (mURI.getPort() != -1) ? mURI.getPort() : (isSecure() ? 443 : 80);

          String path = TextUtils.isEmpty(mURI.getPath()) ? "/" : mURI.getPath();
          if (!TextUtils.isEmpty(mURI.getQuery())) {
            path += "?" + mURI.getQuery();
          }

          URI origin = null;
          try {
            origin = new URI(mURI.getScheme(), "//" + mURI.getHost(), null);
          } catch (URISyntaxException e) {
            Log.exception(e);
          }

          SocketFactory factory;
          try {
            factory = isSecure() ? getSSLSocketFactory() : SocketFactory.getDefault();
          } catch (GeneralSecurityException e) {
            Log.exception(e);
            return;
          }
          try {
            mSocket = factory.createSocket(mURI.getHost(), port);
          } catch (IOException e) {
            Log.exception(e);
          }

          PrintWriter out = new PrintWriter(mSocket.getOutputStream());
          out.print("GET " + path + " HTTP/1.1\r\n");
          out.print("Upgrade: websocket\r\n");
          out.print("Connection: Upgrade\r\n");
          out.print("Host: " + mURI.getHost() + "\r\n");
          out.print("Origin: " + (origin != null ? origin.toString() : "unknown") + "\r\n");
          out.print("Sec-WebSocket-Key: " + createSecret() + "\r\n");
          out.print("Sec-WebSocket-Version: 13\r\n");
          if (mExtraHeaders != null) {
            for (NameValuePair pair : mExtraHeaders) {
              out.print(String.format("%s: %s\r\n", pair.getName(), pair.getValue()));
            }
          }
          out.print("\r\n");
          out.flush();

          HybiParser.HappyDataInputStream stream = new HybiParser.HappyDataInputStream(mSocket.getInputStream());

          // Read HTTP response status line.

          StatusLine statusLine = parseStatusLine(readLine(stream));

          if (statusLine == null) {
            throw new HttpException("Received no reply from server.");
          } else if (statusLine.getStatusCode() != HttpStatus.SC_SWITCHING_PROTOCOLS) {
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
          }

          // Read HTTP response headers.
          String line;
          while (!TextUtils.isEmpty(line = readLine(stream))) {
            Header header = parseHeader(line);
            if (header.getName().equals("Sec-WebSocket-Accept")) {
              // FIXME: Verify the response...
            }
          }

          mListener.onConnect();

          // Now decode websocket frames.
          mParser.start(stream);

        } catch (EOFException ex) {
          Log.d("WebSocket EOF!", ex);
          mListener.onDisconnect(0, "EOF");

        } catch (SSLException ex) {
          // Connection reset by peer
          Log.d("Websocket SSL error!", ex);
          mListener.onDisconnect(0, "SSL");

        } catch (SocketException se) {
          // Probably exception is thrown by any of the Socket.read methods when connection was closed
          Log.d("WebSocketClient - Socket closed");
          mListener.onDisconnect(0, "Socket");

        } catch (Exception e) {
          mListener.onError(e);
        }
      }
    });
    mThread.start();
  }

  public boolean isSecure() {
    return mURI.getScheme().equals("https");
  }

  public void disconnect() {
    Log.d("WebSocketClient.disconnect()");
    mListener = new DummyListener();

    if (mSocket != null) {
      mHandler.post(new Runnable() {
        @Override
        public void run() {
          try {
            if (mSocket != null) {
              mSocket.close();
              mSocket = null;
            }

            mHandlerThread.quit();

          } catch (IOException e) {
            Log.d("Error while disconnecting", e);
            mListener.onError(e);
          }
        }
      });
    }
  }

  public void send(String data) {
    sendFrame(mParser.frame(data));
  }

  public void send(byte[] data) {
    sendFrame(mParser.frame(data));
  }

  private StatusLine parseStatusLine(String line) {
    if (TextUtils.isEmpty(line)) {
      return null;
    }
    return BasicLineParser.parseStatusLine(line, new BasicLineParser());
  }

  private Header parseHeader(String line) {
    return BasicLineParser.parseHeader(line, new BasicLineParser());
  }

  // Can't use BufferedReader because it buffers past the HTTP data.
  private String readLine(HybiParser.HappyDataInputStream reader) throws IOException {
    int readChar = reader.read();
    if (readChar == -1) {
      return null;
    }
    StringBuilder string = new StringBuilder("");
    while (readChar != '\n') {
      if (readChar != '\r') {
        string.append((char) readChar);
      }

      readChar = reader.read();
      if (readChar == -1) {
        return null;
      }
    }
    return string.toString();
  }

  private String createSecret() {
    byte[] nonce = new byte[16];
    for (int i = 0; i < 16; i++) {
      nonce[i] = (byte) (Math.random() * 256);
    }
    return Base64.encodeToString(nonce, Base64.DEFAULT).trim();
  }

  void sendFrame(final byte[] frame) {
    mHandler.post(new Runnable() {
      @Override
      public void run() {
        try {
          synchronized (mSendLock) {
            if (mSocket != null) {
              OutputStream outputStream = mSocket.getOutputStream();
              outputStream.write(frame);
              outputStream.flush();
            }
          }
        } catch (IOException e) {
          mListener.onError(e);
        }
      }
    });
  }

  interface Listener {
    void onConnect();

    void onMessage(String message);

    void onMessage(byte[] data);

    void onDisconnect(int code, String reason);

    void onError(Exception error);
  }

  /**
   * In some cases the socket connection couldn't be closed from the disconnect() method,
   * so we need a way to stop it from updating the Leanplum model.
   */
  private static final class DummyListener implements Listener {
    @Override
    public void onConnect() {
    }
    @Override
    public void onMessage(String message) {
    }
    @Override
    public void onMessage(byte[] data) {
    }
    @Override
    public void onDisconnect(int code, String reason) {
    }
    @Override
    public void onError(Exception error) {
    }
  }

  private SSLSocketFactory getSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
    SSLContext context = SSLContext.getInstance("TLS");
    context.init(null, sTrustManagers, null);
    return context.getSocketFactory();
  }
}

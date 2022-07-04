package com.leanplum.tests

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.leanplum.tests.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private  var b: ActivityMainBinding?=null

    private var signedUpOnce = false
    private var coursePrice = 2000
    var  currentArticle: String = "https://www.r2d2.com/articles/article_${System.currentTimeMillis()}"
    var  currentVideo: String = "https://www.r2d2.com/videos/video${System.currentTimeMillis()}"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b =ActivityMainBinding.inflate(layoutInflater)
        setContentView(b!!.root)


        with(b!!){
            btSomething.setOnClickListener {
                //todo
            }
            btUser.setOnClickListener {
                logUserId().also {Toast.makeText(this@MainActivity, "user id :$it", Toast.LENGTH_SHORT).show()  }

            }

            //login/Signup
            btLogin.setOnClickListener {
                val email = etEmail.text.toString()
                if(!signedUpOnce){
                    signedUpOnce=true
                    loginSignUp(email,true)
                }
                else{
                    loginSignUp(email,false)
                }
                Toast.makeText(this@MainActivity, "Success!$email", Toast.LENGTH_SHORT).show()
            }

            //updateProfile
            b?.btUpdateProfile?.setOnClickListener {
                val name = etName.text.toString()
                val age = etAge.text.toString()
                val gender =etGender.text.toString().toUpperCase()
                val type = btUserType.findViewById<RadioButton>(btUserType.checkedRadioButtonId).text.toString()
                val subjects = mutableListOf<String>()
                if(cbEnglish.isChecked)subjects.add(cbEnglish.text.toString())
                if(cbMath.isChecked)subjects.add(cbMath.text.toString())
                if(cbScience.isChecked)subjects.add(cbScience.text.toString())
                val showUpdates = switchShowUpdates.isChecked
                updateProfile(name,age,gender,type,subjects,showUpdates)

            }
            with(b!!){
                btOpenAnArticle.setOnClickListener {
                    currentArticle = "https://www.r2d2.com/articles/article_${System.currentTimeMillis()}"
                    lpTrack("ARTICLE_OPENED", mapOf("url" to currentArticle))
                }
                btClickedVideo.setOnClickListener {
                    currentVideo = "https://www.r2d2.com/videos/video${System.currentTimeMillis()}"
                    lpTrack("VIDEO_OPENED", mapOf("url" to currentVideo))
                    seekbarVideoProgress.value = 0F
                }
                seekbarVideoProgress.addOnChangeListener { slider, value, fromUser ->
                    lpTrack("VIDEO_PROGRESS", mapOf(
                        "video" to currentVideo,
                        "progress" to value
                    ))
                }

                btPayment.setOnClickListener {
                    lpTrackPurchase("COURSE_PURCHASED", coursePrice.toDouble(), "INR", mapOf("coursename" to currentArticle, "txnTime" to System.currentTimeMillis()))
                }
            }

        }
    }
}//force content update // ct responses //push payload/ start
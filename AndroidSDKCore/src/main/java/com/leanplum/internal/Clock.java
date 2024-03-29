/*
 * Copyright 2021, Leanplum, Inc. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.leanplum.internal;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import java.util.Date;

/**
 * Use this interface instead of the system utilities to get current time and Date instance for easy
 * mock in unit tests.
 */
public abstract class Clock {
  public static final long DAY_MILLIS = 24 * 60 * 60 * 1000;
  public static final long MONTH_MILLIS = 30 * DAY_MILLIS;

  public static final Clock SYSTEM = new Clock() {
    @Override
    long currentTimeMillis() {
      return System.currentTimeMillis();
    }

    @NonNull
    @Override
    Date newDate() {
      return new Date();
    }
  };

  private static Clock INSTANCE = SYSTEM;

  public static Clock getInstance() {
    return INSTANCE;
  }

  @VisibleForTesting
  static void setInstance(Clock clock) {
    INSTANCE = clock;
  }

  /**
   * Gets current time in milliseconds.
   *
   * @return The current time in milliseconds.
   */
  abstract long currentTimeMillis();

  /**
   * Creates instance of type Date.
   *
   * @return New instance of Date.
   */
  @NonNull
  abstract Date newDate();

  /**
   * Checks if time is not from more than 30 days ago.
   *
   * @return True if elapsed time is less than a month.
   */
  public boolean lessThanMonthAgo(long timeInMillis) {
    return currentTimeMillis() - timeInMillis < MONTH_MILLIS;
  }
}

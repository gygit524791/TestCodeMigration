/*
* Copyright (C) 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package android.animation;

import static android.test.MoreAsserts.assertNotEqual;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.Choreographer;
import android.view.animation.LinearInterpolator;

import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

public class ValueAnimatorTests {
    private ValueAnimator a1;
    private ValueAnimator a2;

    private final static long POLL_INTERVAL = 100; // ms

    private ActivityTestRule<BasicAnimatorActivity> mActivityRule =
            new ActivityTestRule<>(BasicAnimatorActivity.class);

    @Before
    public void setUp() throws Exception {
        a1 = ValueAnimator.ofFloat(A1_START_VALUE, A1_END_VALUE).setDuration(300);
        a2 = ValueAnimator.ofInt(A2_START_VALUE, A2_END_VALUE).setDuration(500);
    }

    @Test
    public void testPause() throws Throwable {
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertFalse(a1.isPaused());
                assertFalse(a2.isPaused());

                a1.start();
                a2.start();

                assertFalse(a1.isPaused());
                assertFalse(a2.isPaused());
                assertTrue(a1.isStarted());
                assertTrue(a2.isStarted());
            }
        });

        Thread.sleep(POLL_INTERVAL);
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertTrue(a1.isRunning());
                assertTrue(a2.isRunning());
                a1.pause();
                assertTrue(a1.isPaused());
                assertFalse(a2.isPaused());
                assertTrue(a1.isRunning());
            }
        });
    }
}

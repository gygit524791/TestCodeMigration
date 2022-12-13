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

public class ValueAnimatorTests {
    private static final long WAIT_TIME_OUT = 5000;
    private ValueAnimator a1;
    private ActivityTestRule<BasicAnimatorActivity> mActivityRule =
            new ActivityTestRule<>(BasicAnimatorActivity.class);

    @Test
    public void testStartDelay() throws Throwable {
        final ValueAnimator a = ValueAnimator.ofFloat(5f, 20f);
        assertEquals(a.getStartDelay(), 0);
        final long delay = 200;
        a.setStartDelay(delay);
        assertEquals(a.getStartDelay(), delay);

        final MyUpdateListener listener = new MyUpdateListener();
        a.addUpdateListener(listener);
        final long[] startTime = new long[1];

        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Test the time between isRunning() and isStarted()
                assertFalse(a.isStarted());
                assertFalse(a.isRunning());
                a.start();
                startTime[0] = SystemClock.uptimeMillis();
                assertTrue(a.isStarted());
                assertFalse(a.isRunning());
            }
        });

        Thread.sleep(a.getTotalDuration());
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertTrue(listener.wasRunning);
                assertTrue(listener.firstRunningFrameTime - startTime[0] >= delay);
            }
        });

        Thread.sleep(a.getTotalDuration());
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertFalse(a.isStarted());
            }
        });
    }

    class MyPauseListener implements Animator.AnimatorPauseListener {
        boolean pauseCalled = false;
        boolean resumeCalled = false;

        @Override
        public void onAnimationPause(Animator animation) {
            pauseCalled = true;
        }

        @Override
        public void onAnimationResume(Animator animation) {
            resumeCalled = true;
        }
    }
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;

import androidx.test.annotation.UiThreadTest;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import com.android.frameworks.coretests.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

@SmallTest
public class AnimatorSetActivityTest {

    @Rule
    public final ActivityTestRule<AnimatorSetActivity> mActivityRule =
            new ActivityTestRule<>(AnimatorSetActivity.class);

    private static final long POLL_INTERVAL = 100; // ms
    private AnimatorSetActivity mActivity;
    private ObjectAnimator a1,a2,a3;
    private ValueAnimator a4,a5;

    static class MyListener implements Animator.AnimatorListener {
        boolean startIsCalled = false;
        boolean endIsCalled = false;
        boolean cancelIsCalled = false;

        @Override
        public void onAnimationStart(Animator animation) {
            startIsCalled = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            endIsCalled = true;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            cancelIsCalled = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }



    @Test
    public void testAddListener() throws InterruptedException {
        // Verify that the listener is added to the list of listeners in the AnimatorSet
        // and that newly added listener gets callback for lifecycle events of the animator
        final AnimatorSet s = new AnimatorSet();
        s.play(a1).before(a2).before(a3).after(a4).after(a5);
        final MyListener listener = new MyListener();
        if (s.getListeners() != null) {
            assertFalse(s.getListeners().contains(listener));
        }
        s.addListener(listener);
        assertTrue(s.getListeners().contains(listener));

        assertFalse(listener.startIsCalled);
        assertFalse(listener.endIsCalled);

        try {
            mActivityRule.runOnUiThread(() -> {
                s.start();
                assertTrue(listener.startIsCalled);
                assertFalse(listener.endIsCalled);
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Thread.sleep(s.getTotalDuration() + 200);
        assertTrue(listener.startIsCalled);
        assertTrue(listener.endIsCalled);
    }


}

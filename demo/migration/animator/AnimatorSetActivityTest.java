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

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();

        View square1 = mActivity.findViewById(R.id.square1);
        View square2 = mActivity.findViewById(R.id.square2);
        View square3 = mActivity.findViewById(R.id.square3);
        a1 = ObjectAnimator.ofFloat(square1, View.TRANSLATION_X, 0f, 500f, 0f).setDuration(250);
        a2 = ObjectAnimator.ofFloat(square2, View.ALPHA, 1f, 0f).setDuration(350);
        a3 = ObjectAnimator.ofFloat(square3, View.ROTATION, 0, 90).setDuration(450);
        a4 = ValueAnimator.ofInt(100, 200).setDuration(450);
        a5 = ValueAnimator.ofFloat(10f, 5f).setDuration(850);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
        a1 = null;
        a2 = null;
        a3 = null;
        a4 = null;
        a5 = null;
    }

    @Test
    public void testGetChildAnimations() {
        AnimatorSet s1 = new AnimatorSet();
        s1.playTogether(a1, a2, a3);
        ArrayList<Animator> children = s1.getChildAnimations();
        assertEquals(3, children.size());
        assertTrue(children.contains(a1));
        assertTrue(children.contains(a2));
        assertTrue(children.contains(a3));

        AnimatorSet s2 = new AnimatorSet();
        s2.playSequentially(a1, a2, a3);
        children = s2.getChildAnimations();
        assertEquals(3, children.size());
        assertTrue(children.contains(a1));
        assertTrue(children.contains(a2));
        assertTrue(children.contains(a3));

        AnimatorSet s3 = new AnimatorSet();
        s3.play(a1).before(a2).after(s1).with(s2).after(a3);
        ArrayList<Animator> s3Children = s3.getChildAnimations();
        assertNotNull(s3Children);
        assertEquals(5, s3Children.size());
        assertTrue(s3Children.contains(a1));
        assertTrue(s3Children.contains(a2));
        assertTrue(s3Children.contains(a3));
        assertTrue(s3Children.contains(s1));
        assertTrue(s3Children.contains(s2));

        AnimatorSet s4 = new AnimatorSet();
        s4.playSequentially(s3Children);
        ArrayList<Animator> s4Children = s4.getChildAnimations();
        assertNotNull(s4Children);
        assertEquals(s3Children.size(), s4Children.size());
        for (int i = 0; i < s3Children.size(); i++) {
            Animator child = s3Children.get(i);
            assertTrue(s4Children.contains(child));
        }
    }

    @Test
    public void testTotalDuration() {
        ArrayList<Animator> list = getAnimatorList();

        // Run animations sequentially and test the total duration against sum of durations.
        AnimatorSet s1 = new AnimatorSet();
        s1.playSequentially(list);
        long totalDuration = 0;
        for (int i = 0; i < list.size(); i++) {
            Animator anim = list.get(i);
            anim.setStartDelay(0);
            totalDuration += list.get(i).getDuration();
        }
        assertEquals(totalDuration, s1.getTotalDuration());

        // Add delay to set, and test total duration
        s1.setStartDelay(200);
        assertEquals(totalDuration + 200, s1.getTotalDuration());

        a1.setStartDelay(100);
        assertEquals(totalDuration + 200 + 100, s1.getTotalDuration());

        // Run animations simultaneously, test the total duration against the longest duration
        AnimatorSet s2 = new AnimatorSet();
        s2.playTogether(list);
        long maxDuration = 0;
        for (int i = 0; i < list.size(); i++) {
            long duration = list.get(i).getDuration();
            list.get(i).setStartDelay(100);
            maxDuration = maxDuration > (duration + 100) ? maxDuration : (duration + 100);
        }
        assertEquals(maxDuration, s2.getTotalDuration());

        // Form a cycle in the AnimatorSet and test the total duration
        AnimatorSet s3 = new AnimatorSet();
        s3.play(a1).before(a2).after(a3);
        s3.play(a1).after(a2).with(a4);
        assertEquals(AnimatorSet.DURATION_INFINITE, s3.getTotalDuration());

        // Put all the animators in a cycle
        AnimatorSet s4 = new AnimatorSet();
        s4.play(a1).after(a2);
        s4.play(a2).after(a1);
        assertEquals(AnimatorSet.DURATION_INFINITE, s4.getTotalDuration());

        // No cycle in the set, run a2, a1, a3 in sequence, and a2, a4, a5 together
        AnimatorSet s5 = new AnimatorSet();
        s5.play(a1).after(a2).before(a3);
        s5.play(a2).with(a4).with(a5);
        long duration = a1.getDuration() + a1.getStartDelay() + a2.getDuration() + a2
                .getStartDelay() + a3.getDuration() + a3.getStartDelay();
        long a4Duration = a4.getDuration() + a4.getStartDelay();
        long a5Duration = a5.getDuration() + a5.getStartDelay();
        duration = Math.max(duration, a4Duration);
        duration = Math.max(duration, a5Duration);
        assertEquals(duration, s5.getTotalDuration());

        // Change one animator to repeat infinitely and test the total time
        a3.setRepeatCount(ValueAnimator.INFINITE);
        assertEquals(AnimatorSet.DURATION_INFINITE, s5.getTotalDuration());

    }

    @Test
    public void testGetDuration() {
        AnimatorSet s = new AnimatorSet();
        assertTrue(s.getDuration() < 0);
        s.play(a1).before(a2).before(a3).after(a4).after(a5);
        assertTrue(s.getDuration() < 0);

        long duration = 200;
        s.setDuration(duration);
        assertEquals(duration, s.getDuration());

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


    @Test
    public void testEnd() throws Throwable {
        // End animator set
        final AnimatorSet s = new AnimatorSet();
        s.play(a1).before(a2).after(a3).with(a4);
        final MyListener listener = new MyListener();
        s.addListener(listener);
        assertFalse(listener.endIsCalled);
        mActivityRule.runOnUiThread(() -> {
            s.start();
            assertTrue(s.isStarted());
            assertTrue(listener.startIsCalled);
            assertFalse(listener.endIsCalled);
        });

        Thread.sleep(a2.getTotalDuration());

        mActivityRule.runOnUiThread(() -> {
            s.end();
            assertTrue(listener.startIsCalled);
            assertTrue(listener.endIsCalled);
            assertFalse(s.isRunning());
            assertFalse(s.isStarted());

            assertFalse(a1.isStarted());
            assertFalse(a2.isStarted());
            assertFalse(a3.isStarted());
            assertFalse(a4.isStarted());
        });
    }

    @Test
    public void testIsRunning() throws Throwable {
        final AnimatorSet s = new AnimatorSet();
        final long startDelay = 500;
        s.play(a1).before(a2).after(a3).with(a4);
        s.play(a3).after(a5);
        s.setStartDelay(startDelay);
        MyListener listener = new MyListener();
        s.addListener(listener);
        mActivityRule.runOnUiThread(s::start);

        while (!listener.endIsCalled) {
            boolean passedStartDelay = a1.isStarted() || a2.isStarted() || a3.isStarted() ||
                    a4.isStarted() || a5.isStarted();
            assertEquals(passedStartDelay, s.isRunning());
            Thread.sleep(50);
        }
        assertFalse(s.isRunning());
    }

    @Test
    public void testPauseAndResume() throws Throwable {
        final AnimatorSet set = getSequentialSet();
        mActivityRule.runOnUiThread(() -> {
            // Calling pause before start should have no effect, per documentation
            set.pause();
            set.start();
            assertFalse(set.isPaused());
        });

        while (!a2.isStarted()) {
            Thread.sleep(50);
        }
        mActivityRule.runOnUiThread(() -> {
            assertFalse(set.isPaused());
            set.pause();
            assertTrue(set.isPaused());
            set.resume();
            assertFalse(set.isPaused());
        });
    }

    /**
     * Check that the animator list contains exactly the given animators and nothing else.
     */
    private boolean onlyContains(ArrayList<Animator> animators, AnimatorSet... sets) {
        if (sets.length != animators.size()) {
            return false;
        }

        for (int i = 0; i < sets.length; i++) {
            AnimatorSet set = sets[i];
            if (!animators.contains(set)) {
                return false;
            }
        }
        return true;

    }

    // Create an AnimatorSet with all the animators running sequentially
    private AnimatorSet getSequentialSet() {
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(a1, a2, a3, a4, a5);
        return set;
    }

    private ArrayList<Animator> getAnimatorList() {
        ArrayList<Animator> list = new ArrayList<>();
        list.add(a1);
        list.add(a2);
        list.add(a3);
        list.add(a4);
        list.add(a5);
        return list;
    }
}

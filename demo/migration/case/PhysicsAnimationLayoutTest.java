/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.android.systemui.bubbles.animation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.os.SystemClock;
import android.testing.AndroidTestingRunner;
import android.view.View;
import android.widget.FrameLayout;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.test.filters.SmallTest;

import com.google.android.collect.Sets;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SmallTest
@RunWith(AndroidTestingRunner.class)
/** Tests the PhysicsAnimationLayout itself, with a basic test animation controller. */
public class PhysicsAnimationLayoutTest extends PhysicsAnimationLayoutTestCase {
    static final float TEST_TRANSLATION_X_OFFSET = 15f;

    @Spy
    private TestableAnimationController mTestableController = new TestableAnimationController();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        // By default, use translation animations, chain the X animations with the default
        // offset, and don't actually remove views immediately (since most implementations will wait
        // to animate child views out before actually removing them).
        mTestableController.setAnimatedProperties(Sets.newHashSet(
                DynamicAnimation.TRANSLATION_X,
                DynamicAnimation.TRANSLATION_Y));
        mTestableController.setChainedProperties(Sets.newHashSet(DynamicAnimation.TRANSLATION_X));
        mTestableController.setOffsetForProperty(
                DynamicAnimation.TRANSLATION_X, TEST_TRANSLATION_X_OFFSET);
        mTestableController.setRemoveImmediately(false);
    }

    @Test
    @Ignore
    public void testArePropertiesAnimating() throws InterruptedException {
        mLayout.setActiveController(mTestableController);
        addOneMoreThanBubbleLimitBubbles();

        assertFalse(mLayout.arePropertiesAnimating(
                DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y));

        mTestableController.animationForChildAtIndex(0)
                .translationX(100f)
                .start();

        // Wait for the animations to get underway.
        SystemClock.sleep(50);

        assertTrue(mLayout.arePropertiesAnimating(DynamicAnimation.TRANSLATION_X));
        assertFalse(mLayout.arePropertiesAnimating(DynamicAnimation.TRANSLATION_Y));

        waitForPropertyAnimations(DynamicAnimation.TRANSLATION_X);

        assertFalse(mLayout.arePropertiesAnimating(
                DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y));
    }

    @Test
    @Ignore
    public void testCancelAllAnimations() throws InterruptedException {
        mLayout.setActiveController(mTestableController);
        addOneMoreThanBubbleLimitBubbles();

        mTestableController.animationForChildAtIndex(0)
                .position(1000, 1000)
                .start();

        mLayout.cancelAllAnimations();

        // Animations should be somewhere before their end point.
        assertTrue(mViews.get(0).getTranslationX() < 1000);
        assertTrue(mViews.get(0).getTranslationY() < 1000);
    }

    /** Standard test of chained translation animations. */
    private void testChainedTranslationAnimations() throws InterruptedException {
        mLayout.setActiveController(mTestableController);
        addOneMoreThanBubbleLimitBubbles();

        assertEquals(0, mLayout.getChildAt(0).getTranslationX(), .1f);
        assertEquals(0, mLayout.getChildAt(1).getTranslationX(), .1f);

        mTestableController.animationForChildAtIndex(0)
                .translationX(100f)
                .start();

        waitForPropertyAnimations(DynamicAnimation.TRANSLATION_X);

        for (int i = 0; i < mLayout.getChildCount(); i++) {
            assertEquals(
                    100 + i * TEST_TRANSLATION_X_OFFSET,
                    mLayout.getChildAt(i).getTranslationX(), .1f);
        }

        // Ensure that the Y translations were unaffected.
        assertEquals(0, mLayout.getChildAt(0).getTranslationY(), .1f);
        assertEquals(0, mLayout.getChildAt(1).getTranslationY(), .1f);

        // Animate the first child's Y translation.
        mTestableController.animationForChildAtIndex(0)
                .translationY(100f)
                .start();

        waitForPropertyAnimations(DynamicAnimation.TRANSLATION_Y);

        // Ensure that only the first view's Y translation chained, since we only chained X
        // translations.
        assertEquals(100, mLayout.getChildAt(0).getTranslationY(), .1f);
        assertEquals(0, mLayout.getChildAt(1).getTranslationY(), .1f);
    }

    public void test1(){}
    public void test2(){}
    public void add2(){}
    public void test3(){}
    public void add1(){}



}

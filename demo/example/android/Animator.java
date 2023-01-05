/*
 * Copyright (C) 2010 The Android Open Source Project
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

import android.annotation.Nullable;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.pm.ActivityInfo.Config;
import android.content.res.ConstantState;

import java.util.ArrayList;

/**
 * This is the superclass for classes which provide basic support for animations which can be
 * started, ended, and have <code>AnimatorListeners</code> added to them.
 */
public abstract class Animator implements Cloneable {

    public void pause() {
        if (isStarted() && !mPaused) {
            mPaused = true;
            if (mPauseListeners != null) {
                ArrayList<AnimatorPauseListener> tmpListeners =
                        (ArrayList<AnimatorPauseListener>) mPauseListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i = 0; i < numListeners; ++i) {
                    tmpListeners.get(i).onAnimationPause(this);
                }
            }
        }
    }

    /**
     * Resumes a paused animation, causing the animator to pick up where it left off
     * when it was paused. This method should only be called on the same thread on
     * which the animation was started. Calls to resume() on an animator that is
     * not currently paused will be ignored.
     *
     * @see #pause()
     * @see #isPaused()
     * @see AnimatorPauseListener
     */
    public void resume() {
        if (mPaused) {
            mPaused = false;
            if (mPauseListeners != null) {
                ArrayList<AnimatorPauseListener> tmpListeners =
                        (ArrayList<AnimatorPauseListener>) mPauseListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i = 0; i < numListeners; ++i) {
                    tmpListeners.get(i).onAnimationResume(this);
                }
            }
        }
    }

}

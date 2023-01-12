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

public class Case1 {
    @Test
    public void testAddListener() throws InterruptedException {
        // Verify that the listener is added to the list of listeners in the AnimatorSet
        // and that newly added listener gets callback for lifecycle events of the animator
        final AnimatorSet s = new AnimatorSet();
        s.play(a1).before(a2).before(a3).after(a4).after(a5);
        final MyListener listener = new MyListener();
    }

}

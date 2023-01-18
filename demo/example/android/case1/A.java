public class A{

    private ValueAnimator a1;
    private ValueAnimator a2;

    public void testListenerCallbacks() throws Throwable {
//        a1.addListener(l1);
//        a2.addListener(l2);
        a2.setStartDelay(400);
        assertFalse(l1.startCalled);
        assertFalse(l1.cancelCalled);
        assertFalse(l1.endCalled);
        assertFalse(l2.startCalled);
        assertFalse(l2.cancelCalled);
        assertFalse(l2.endCalled);
    }

//    public void testListenerCallbacks() throws Throwable {
//        final MyListener l1 = new MyListener();
//        final MyListener l2 = new MyListener();
//        a1.addListener(l1);
//        a2.addListener(l2);
//        a2.setStartDelay(400);
//
//        assertFalse(l1.startCalled);
//        assertFalse(l1.cancelCalled);
//        assertFalse(l1.endCalled);
//        assertFalse(l2.startCalled);
//        assertFalse(l2.cancelCalled);
//        assertFalse(l2.endCalled);
//
//        mActivityRule.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                a1.start();
//                a2.start();
//            }
//        });
//
//        long wait = 0;
//        Thread.sleep(POLL_INTERVAL);
//        wait += POLL_INTERVAL;
//
//        mActivityRule.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                assertFalse(l1.cancelCalled);
//                a1.cancel();
//                assertTrue(l1.cancelCalled);
//                assertTrue(l1.endCalled);
//            }
//        });
//
//        while (wait < a2.getStartDelay()) {
//            mActivityRule.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    // Make sure a2's start listener isn't called during start delay.
//                    assertTrue(l1.startCalled);
//                    assertFalse(l2.startCalled);
//                }
//            });
//            Thread.sleep(POLL_INTERVAL);
//            wait += POLL_INTERVAL;
//        }
//
//        long delay = Math.max(a1.getTotalDuration(), a2.getTotalDuration()) + TOLERANCE;
//        Thread.sleep(delay);
//
//        mActivityRule.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // a1 is canceled.
//                assertTrue(l1.startCalled);
//                assertTrue(l1.cancelCalled);
//                assertTrue(l1.endCalled);
//
//                // a2 is supposed to finish normally
//                assertTrue(l2.startCalled);
//                assertFalse(l2.cancelCalled);
//                assertTrue(l2.endCalled);
//            }
//        });
//    }

}
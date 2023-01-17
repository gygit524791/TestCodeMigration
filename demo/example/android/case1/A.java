public class A{
    @Before
    public void setUp() throws Exception {
        a1 = ValueAnimator.ofFloat(A1_START_VALUE, A1_END_VALUE).setDuration(300);
        a2 = ValueAnimator.ofInt(A2_START_VALUE, A2_END_VALUE).setDuration(500);
    }
    @After
    public void tearDown() throws Exception {
        a1 = null;
        a2 = null;
    }

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
//                int a =0;
//                 Test the time between isRunning() and isStarted()
                assertFalse(a.isStarted());
                assertFalse(a.isRunning());
                a.start();
                startTime[0] = SystemClock.uptimeMillis();
                assertTrue(a.isStarted());
                assertFalse(a.isRunning());
            }
        });
    }
}
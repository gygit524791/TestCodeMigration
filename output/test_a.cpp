#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class A : public testing::Test {
public:
  void setUp() {
    a1 = ValueAnimator.ofFloat(A1_START_VALUE, A1_END_VALUE)->setDuration(300);
    a2 = ValueAnimator.ofInt(A2_START_VALUE, A2_END_VALUE)->setDuration(500);
  }
  void tearDown() {
    a1 = null;
    a2 = null;
  }
  void testStartDelay() {
    ACE_EXPORTAnimator *a = ValueAnimator.ofFloat(5f, 20f);
    ASSERT_EQ(a.getStartDelay(), 0);
    long delay = 200;
    a.setStartDelay(delay);
    ASSERT_EQ(a.getStartDelay(), delay);
    //以下组件没有匹配:
    // MyUpdateListener
    MyUpdateListener *listener = new MyUpdateListener();
    a.addUpdateListener(listener);
    long[] startTime = new long[1];
    ASSERT_FALSE(a.isStarted());
    ASSERT_FALSE(a.isRunning());
    a.start();
    startTime[0] = SystemClock.uptimeMillis();
    ASSERT_TRUE(a.isStarted());
    ASSERT_FALSE(a.isRunning());
    mActivityRule.runOnUiThread(newRunnable() {
      @Overridepublicvoidrun() {
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

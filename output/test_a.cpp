#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class A : public testing::Test {
public:
  void setUp() {
    a1 = ValueAnimator->ofFloat(A1_START_VALUE, A1_END_VALUE)->(());
    a2 = ValueAnimator->ofInt(A2_START_VALUE, A2_END_VALUE)->(());
  }
  void tearDown() {
    a1 = null;
    a2 = null;
  }
  void test() {
    int a1 = 0;
    if (true) {
      int a2 = 0;
      for (int i = 0; i < 10; i++) {
        int a3 = 0;
        if (true) {
          int a4 = 0;
        }
      }
      int a5 = 0;
    }
  }
  void test() {
    int a1 = 0;
    //以下组件没有匹配:
    // XX
    if (true) {
      int a2 = 0;
      for (int i = 0; i < 10; i++) {
        int a3 = 0;
        if (true) {
          int a4 = 0;
        }
      }
      int a5 = 0;
      XX *a = new XX();
    }
  }
  void run() {
    ACE_EXPORTAnimator *a = ValueAnimator->ofFloat(5f, 20f);
    ASSERT_EQ(a->getStartDelay(), 0);
    long delay = 200;
    a->setStartDelay(delay);
    ASSERT_EQ(a->getStartDelay(), delay);
    //以下组件没有匹配:
    // MyUpdateListener
    MyUpdateListener *listener = new MyUpdateListener();
    a->AddUpdateListener(listener);
    long[] startTime = new long[1];
    //以下组件没有匹配:
    // Runnable
mActivityRule->runOnUiThread(new Runnable(){void run() { ASSERT_FALSE(a->IsRunning()     );ASSERT_FALSE(a->IsRunning()     );a->Play();startTime[0]=SystemClock->uptimeMillis()     ;ASSERT_TRUE(a->IsRunning()     );ASSERT_FALSE(a->IsRunning()     );
  }}     );
}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class A : public testing::Test {
public:
  void setUp() {
    a1 = ValueAnimator->ofFloat(A1_START_VALUE, A1_END_VALUE)->(());
    a2 = ValueAnimator->ofInt(A2_START_VALUE, A2_END_VALUE)->(());
  }
  void tearDown() {
    a1 = null;
    a2 = null;
  }
  void test() {
    int a1 = 0;
    if (true) {
      int a2 = 0;
      for (int i = 0; i < 10; i++) {
        int a3 = 0;
        if (true) {
          int a4 = 0;
        }
      }
      int a5 = 0;
    }
  }
  void test() {
    int a1 = 0;
    //以下组件没有匹配:
    // XX
    if (true) {
      int a2 = 0;
      for (int i = 0; i < 10; i++) {
        int a3 = 0;
        if (true) {
          int a4 = 0;
        }
      }
      int a5 = 0;
      XX *a = new XX();
    }
  }
  void testStartDelay() {
    ACE_EXPORTAnimator *a = ValueAnimator->ofFloat(5f, 20f);
    ASSERT_EQ(a->getStartDelay(), 0);
    long delay = 200;
    a->setStartDelay(delay);
    ASSERT_EQ(a->getStartDelay(), delay);
    //以下组件没有匹配:
    // MyUpdateListener
    MyUpdateListener *listener = new MyUpdateListener();
    a->AddUpdateListener(listener);
    long[] startTime = new long[1];
    //以下组件没有匹配:
    // Runnable
mActivityRule->runOnUiThread(new Runnable(){void run() { ASSERT_FALSE(a->IsRunning()     );ASSERT_FALSE(a->IsRunning()     );a->Play();startTime[0]=SystemClock->uptimeMillis()     ;ASSERT_TRUE(a->IsRunning()     );ASSERT_FALSE(a->IsRunning()     );
  }}     );
}
}

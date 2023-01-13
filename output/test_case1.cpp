#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class Case1 : public testing::Test {
public:
  void testAddListener() {
    ACE_EXPORTGroup *s = new ACE_EXPORTGroup();
    s->Play(a1)->before(a2)->before(a3)->after(a4)->after(a5);
    //以下组件没有匹配:
    // MyListener
    MyListener *listener = new MyListener();
  }

  void testAddListener() {
    ACE_EXPORTGroup *s = new ACE_EXPORTGroup();
    s->Play(a1)->before(a2)->before(a3)->after(a4)->after(a5);
  }
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class Case1 : public testing::Test {
public:
  void testAddListener() {
    ACE_EXPORTGroup *s = new ACE_EXPORTGroup();
    s->Play(a1)->before(a2)->before(a3)->after(a4)->after(a5);
    //以下组件没有匹配:
    // MyListener
    MyListener *listener = new MyListener();
  }
  void testAddListener() {
    ACE_EXPORTGroup *s = new ACE_EXPORTGroup();
    s->Play(a1)->before(a2)->before(a3)->after(a4)->after(a5);
  }
}

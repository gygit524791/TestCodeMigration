#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ADemo : public testing::Test {
public:
  //以下组件没有匹配:
  // AClass
  AClass *a, b;
  //以下组件没有匹配:
  // BClass
  BClass *c = new BClass();
}

#include "gtest/gtest.h"
#include "core/animation/animator.h"
#include <unistd.h>

using namespace testing;
using namespace testing::ext;

namespace OHOS::Ace {

class AnimatorTest : public testing::Test {
public:
    RefPtr<Animator> a1;
    RefPtr<Animator> a2;
    static void SetUpTestCase()
    {
        GTEST_LOG_(INFO) << "AnimatorTest SetUpTestCase";
    }

    static void TearDownTestCase()
    {
        GTEST_LOG_(INFO) << "AnimatorTest TearDownTestCase";
    }

    void SetUp() override
    {
        a1 = AceType::MakeRefPtr<Animator>();
        a2 = AceType::MakeRefPtr<Animator>();
    }
    void TearDown() override {}
};

HWTEST_F(AnimatorTest, AnimatorTest001, TestSize.Level1)
{
    ASSERT_FALSE(a1->IsPending());
    ASSERT_FALSE(a2->IsPending());
    a1->Play();
    a2->Play();
    ASSERT_FALSE(a1->IsPending());
    ASSERT_FALSE(a2->IsPending());
    ASSERT_TRUE(a1->IsRunning());
    ASSERT_TRUE(a2->IsRunning());

    sleep(1000);

    ASSERT_TRUE(a1->IsRunning());
    ASSERT_TRUE(a2->IsRunning());
    a1->Pause();
    ASSERT_TRUE(a1->IsPending());
    ASSERT_FALSE(a2->IsPending());
    ASSERT_TRUE(a1->IsRunning());
}


}
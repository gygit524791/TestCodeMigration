#include "gtest/gtest.h"
#include "core/animation/animator.h"
#include <unistd.h>
#include <time.h>

using namespace testing;
using namespace testing::ext;

class AnimatorTest : public testing::Test {
public:
    RefPtr<Animator> a1;
    RefPtr<Animator> a2;
    static long POLL_INTERVAL = 100;
    static void SetUpTestCase(){GTEST_LOG_(INFO) << "AnimatorTest SetUpTestCase"; }

    static void TearDownTestCase()
    {
        GTEST_LOG_(INFO) << "AnimatorTest TearDownTestCase";
    }

    void SetUp() override
    {
        a1 = AceType::MakeRefPtr<Animator>(); a2 = AceType::MakeRefPtr<Animator>();
    }

    void TearDown() override {}
};

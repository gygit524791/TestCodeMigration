#include "gtest/gtest.h"
#include "core/animation/animator.h"
#include <unistd.h>
#include <time.h>

using namespace testing;
using namespace testing::ext;

namespace OHOS::Ace {

class AnimatorTest : public testing::Test {
public:
    RefPtr<Animator> a1;
    RefPtr<Animator> a2;
    static long POLL_INTERVAL = 100;
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

// testStartDelay
HWTEST_F(AnimatorTest, AnimatorTest001, TestSize.Level1)
{
    RefPtr<Animator> a = AceType::MakeRefPtr<Animator>();
    // 没有GetStartDelay
//    ASSERT_EQ(a->GetStartDelay(),0);
    long delay=200;
    a->SetStartDelay(delay);
//    ASSERT_EQ(a->GetStartDelay()  ,delay);
    ASSERT_FALSE(a->IsRunning());
//    a->Play();
//    ASSERT_TRUE(a->IsRunning());
//    ASSERT_FALSE(a->IsRunning());
}
HWTEST_F(AnimatorTest, AnimatorTest002, TestSize.Level1)
{
    RefPtr<Animator> a = AceType::MakeRefPtr<Animator>();
    long delay=200;
    a->SetStartDelay(delay);
    ASSERT_FALSE(a->IsRunning());
    a->Play();
    ASSERT_TRUE(a->IsRunning());
}

//testIsStarted
HWTEST_F(AnimatorTest, AnimatorTest003, TestSize.Level1)
{
    ASSERT_FALSE(a1->IsRunning());
    ASSERT_FALSE(a2->IsRunning());
    ASSERT_FALSE(a1->IsRunning());
    ASSERT_FALSE(a2->IsRunning());
    long startDelay=150;
    a1->SetStartDelay(startDelay);
    // long  * start_time = new long[1];
    long * startTime= new long[1];
    a1->Play();
    a2->Play();
    startTime[0]=time(NULL);
    ASSERT_TRUE(a2->IsRunning());
    long delayMs=0;
    while(delayMs < startDelay) {
        sleep(POLL_INTERVAL/1000);
        delayMs+=POLL_INTERVAL;
        if(time(NULL)   - startTime[0]   < startDelay) {
          ASSERT_FALSE(a1->IsRunning());
        }
    }
    sleep(startDelay/1000);
    ASSERT_TRUE(a1->IsRunning());
    ASSERT_TRUE(a2->IsRunning());
}
HWTEST_F(AnimatorTest, AnimatorTest004, TestSize.Level1)
{
    ASSERT_FALSE(a1->IsRunning());
    ASSERT_FALSE(a2->IsRunning());
    long startDelay=150;
    a1->SetStartDelay(startDelay);
    a1->Play();
    a2->Play();
    ASSERT_TRUE(a2->IsRunning());
}

//testPause
HWTEST_F(AnimatorTest, AnimatorTest005, TestSize.Level1)
{
    ASSERT_FALSE(a1->IsPending());
    ASSERT_FALSE(a2->IsPending());
    a1->Play();
    a2->Play();
    ASSERT_FALSE(a1->IsPending());
    ASSERT_FALSE(a2->IsPending());
    ASSERT_TRUE(a1->IsRunning());
    ASSERT_TRUE(a2->IsRunning());

    sleep(POLL_INTERVAL/1000);

    ASSERT_TRUE(a1->IsRunning());
    ASSERT_TRUE(a2->IsRunning());
    a1->Pause();
    ASSERT_TRUE(a1->IsPending());
    ASSERT_FALSE(a2->IsPending());
    //
    // ASSERT_TRUE(a1->IsRunning());

    sleep(a2->GetDuration()/1000);

    ASSERT_FALSE(a2->IsRunning());
    ASSERT_FALSE(a2->IsRunning());
    ASSERT_TRUE(a1->IsRunning());
    ASSERT_TRUE(a1->IsRunning());
    ASSERT_TRUE(a1->IsPending());
    a1->Resume();

    sleep(POLL_INTERVAL/1000);

    ASSERT_TRUE(a1->IsRunning());
    ASSERT_TRUE(a1->IsRunning());
    ASSERT_FALSE(a1->IsPending());

    sleep(a1->GetDuration()/1000);

    ASSERT_FALSE(a1->IsRunning());
    ASSERT_FALSE(a1->IsRunning());
    ASSERT_FALSE(a1->IsPending());
}
HWTEST_F(AnimatorTest, AnimatorTest006, TestSize.Level1)
{
    ASSERT_FALSE(a1->IsPending());
    ASSERT_FALSE(a2->IsPending());
    a1->Play();
    a2->Play();
    ASSERT_FALSE(a1->IsPending());
    ASSERT_FALSE(a2->IsPending());
    ASSERT_TRUE(a1->IsRunning());
    ASSERT_TRUE(a2->IsRunning());
}

//testPauseAndResume
HWTEST_F(AnimatorTest, AnimatorTest007, TestSize.Level1)
{
    RefPtr<Animator> set = AceType::MakeRefPtr<Animator>();
    set->Pause();
    set->Play();
    ASSERT_FALSE(set->IsPaused());
//    while(!a2->IsRunning()) {
//        sleep(50/1000);
//    }
    ASSERT_FALSE(set->IsPaused());
    set->Pause();
    ASSERT_TRUE(set->IsPaused());
    set->Resume();
    ASSERT_FALSE(set->IsPaused());
}
HWTEST_F(AnimatorTest, AnimatorTest008, TestSize.Level1)
{
    RefPtr<Animator> set = AceType::MakeRefPtr<Animator>();
    set->Pause();
    set->Play();
    ASSERT_FALSE(set->IsPaused());
}

//testGetDuration
HWTEST_F(AnimatorTest, AnimatorTest009, TestSize.Level1)
{
    RefPtr<Animator> s = AceType::MakeRefPtr<Animator>();
    ASSERT_TRUE(s->GetDuration() < 0);
    long duration=200;
    s->SetDuration(duration);
    ASSERT_EQ(duration,s->GetDuration());
}

HWTEST_F(AnimatorTest, AnimatorTest010, TestSize.Level1)
{
    RefPtr<Animator> s = AceType::MakeRefPtr<Animator>();
    long duration=200;
    s->SetDuration(duration);
    ASSERT_EQ(duration,s->GetDuration());
}

}
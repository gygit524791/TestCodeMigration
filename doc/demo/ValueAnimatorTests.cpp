class ValueAnimatorTests {


public:
    Animator a1;
    Animator a2;
    long POLL_INTERVAL = 100;

    void setUp() {
        a1 = new Animator();
        a2 = new Animator();
    }

    void testPause() {
        ASSERT_FALSE(a1->IsPending());
        ASSERT_FALSE(a2->IsPending());
        a1->Play();
        a2->Play();
        ASSERT_FALSE(a1->IsPending());
        ASSERT_FALSE(a2->IsPending());
        ASSERT_TRUE(a1->IsRunning());
        ASSERT_TRUE(a2->IsRunning());

        sleep(100);

        ASSERT_TRUE(a1->IsRunning());
        ASSERT_TRUE(a2->IsRunning());
        a1->Pause();
        ASSERT_TRUE(a1->IsPending());
        ASSERT_FALSE(a2->IsPending());
        ASSERT_TRUE(a1->IsRunning());
    }
}

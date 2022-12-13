class ADemo : public testing::Test {
public:
    int a;
    Target b;
    Target c = new Target();

    void test1(){
        ADemo::BDemo * d = new ADemo::BDemo();
        d->test2();
    }

    class BDemo {
        public:
            int a;
            std::string b;
            void test2(){
                printf("hello");
            }
    };
};
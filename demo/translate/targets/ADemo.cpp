class ADemo : public testing::Test {
public:
    int a;
    Target b;
    Target c = new Target();

    void test1(Target a){
        ADemo::BDemo * d = new ADemo::BDemo();
        d->test2();
    }

    class BDemo {
        public:
            int a;
            std::string b;
            void test2(std::string a){
                printf("hello");
            }
    };
};
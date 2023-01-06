public class ADemo {
    //测试一个正常方法
//    public void test1() {
//        for (int i = 0; i < 10; i++) {
//            if (i + j < 1) {
//                print();
//                print();
//            }
//        }
//        print();
//        print();
//    }

    // 测试 策略1：
//    public void test2(){
//        func();
//
//        for (int i = 0; i < 10; i++) {
//            func();
//
//            if (i + j < 1) {
//                func1();
//                Source source2 = new Source();
//                func2();
//                func3();
//            }
//
//            func4();
//        }
//        func5();
//        func6();
//    }
//
//
//    public void test3(){
//        func();
//
//        for (int i = 0; i < 10; i++) {
//            func();
//
//            if (i + j < 1) {
//                func1();
//                Source2 source2 = new Source2();
//                func2();
//                func3();
//            }
//
//            func4();
//        }
//        func5();
//        func6();
//    }


//
//    public void test4(){
//        Source2 source2 = new Source2();
//        func();
//        for (int i = 0; i < 10; i++) {
//            func();
//
//            if (i + j < 1) {
//                func1();
//
//                func2();
//                func3();
//            }
//            func4();
//        }
//        func5();
//        func6();
//    }

//
//    public void test5(){
//        func();
//        for (int i = 0; i < 10; i++) {
//            func();
//
//            if (i + j < 1) {
//                func1();
//
//                func2();
//                func3();
//            }
//            func4();
//        }
//        func5();
//        func6();
//        Source2 source2 = new Source2();
//    }


//    public void test6(){
//        func();
//        for (int i = 0; new Source2().size < 10; i++) {
//            func();
//            if (i + j < 1) {
//                func1();
//                func2();
//                func3();
//            }
//            func4();
//        }
//        func5();
//        func6();
//    }


    public void test6(){
        func();
        Source source2 = new Source2();
        for (int i = 0; source2.size < 10; i++) {
            func();
            if (i + j < 1) {
                func1();
                func2();
                func3();
            }
            func4();
        }
        func5();
        func6();
    }


//    public static class A{
//        int size;
//    }
//    public static void main(String[] args) {
//        for (int i = 0; new A().size <2; i++) {
//            System.out.println("");
//        }
//    }


//    public void test7(){
//        func();
//        if(new A().size <2){
//            func2();
//        }
//        func5();
//        func6();
//    }
}
public class ADemo {
    private int a;
    private Source2 b,e,f;
    private Source c = new Source();

    public void test1(Source a){
        BDemo d = new BDemo();
        d.test2();
    }

    public void method1(Source a){
        d.test2();
    }

    class BDemo{
        private int a;
        private String b;
        public void test2(String a){
            System.out.println("hello");
        }
    }
}
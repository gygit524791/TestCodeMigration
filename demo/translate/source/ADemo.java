public class ADemo {
    private int a;
    private Source b;
    private Source c = new Source();

    public void test1(){
        BDemo d = new BDemo();
        d.test2();
    }

    class BDemo{
        private int a;
        private String b;
        public void test2(){
            System.out.println("hello");
        }
    }
}
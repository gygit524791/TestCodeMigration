public class ADemo {
    private AClass a,b;
    private BClass c = new BClass();

    public void test1(){
        CClass d = new CClass();
    }

    public void method1(DClass e){
    }

    class EClass{
        private FClass f;
    }

    public void test(){
        a.fun();
        b.fun();
        c.fun();
        d.fun();
        e.fun();
        f.fun();
    }
}
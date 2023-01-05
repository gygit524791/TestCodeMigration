public class CDemo {

    public void test() {

        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {

                if (i + j < 1) {
                    print();
                    print();
                }

                print();
            }

            print();

        }

        print();
    }

    /**
     *m1
     * b1
     *  b11
     *      b111
     *          b1111
     *          b1112
     *      b112
     *  b12
     * b2
     */

    public void test2() {
        int a = 1;

        int b = 0;

        assertFalse(b!=0);

        assertTrue(a > 0);

    }
}
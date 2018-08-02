package com.gbdata.basic;

/**
 * Created by lisa.wei on 2018/8/2.
 */
public class Volatile {
    /*
     *volatile只能保证可见性，在变量修改时对其他线程可见，但是对于非原子操作不能保证数据的一致性，下面程序的结果每次都会不同，小于500
     */
    static class test1{
        public volatile int inc = 0;

        public void increase() {
            inc++;
        }

        public static void main(String[] args) {
            final test1 test = new test1();
            for(int i=0;i<5;i++){
                new Thread(){
                    public void run() {
                        for(int j=0;j<1000;j++)
                            test.increase();
                    };
                }.start();
            }

            try {
                Thread.sleep(10000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(test.inc);
        }
    }
}

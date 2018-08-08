package com.gbdata.basic;

import java.io.*;

/**
 * Created by lisa.wei on 2018/8/7.
 * Test code in finaly block run before return or not
 * try和catch的代码里面有return时， finaly的语句还是会执行
 */

public class TryCatchFinaly {
    private Boolean write() {
        FileOutputStream out = null;
        FileWriter fw = null;
        int count = 1000;//写文件行数
        try {
            //经过测试：FileOutputStream执行耗时:17，6，10 毫秒
            out = new FileOutputStream(new File("E:\\sdfd\\add.txt"));
            long begin = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                out.write("测试java 文件操作\r\n".getBytes());
            }
            out.close();
            long end = System.currentTimeMillis();
            System.out.println("FileOutputStream执行耗时:" + (end - begin) + " 毫秒");
            return true;
        } catch (Exception e) {
            System.out.println("catch code must be run");
            e.printStackTrace();
            return false;
        } finally {
            try {
                System.out.println("finaly code must be run");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     *由此可以看出，finally是在try{}中的return执行到"一半"的时候执行的，并不是在之前或者之后，这里说”一半“是因为到return的时候，a=20已经执行了，在finally里面a的值为20
     */
    @SuppressWarnings("finally")
    public static int test() {
        int a = 10;
        try {
            System.out.println("try...");
            return a = 20;
        } catch (Exception e) {

        } finally {
            if (a == 20) {
                System.out.println("到了 try 的return了");
            }
            System.out.println("finally...");
            return a = 30;
        }
    }

    public static void main(String[] args) {
//        TryCatchFinaly tryCatchFinaly = new TryCatchFinaly();
//        Boolean success = tryCatchFinaly.write();
//        System.out.println(success);

        System.out.println(test());
    }
}

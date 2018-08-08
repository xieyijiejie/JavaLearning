package com.gbdata.basic;

import com.oracle.deploy.update.UpdateCheck;

/**
 * Created by lisa.wei on 2018/8/3.
 */
public class Enum {
    /*
     *原始的接口定义常量
     */
    public interface IConstants {
        String MON = "Mon";
        String TUE = "Tue";
        String WED = "Wed";
        String THU = "Thu";
        String FRI = "Fri";
        String SAT = "Sat";
        String SUN = "Sun";
    }

    public interface ICanReadState {
        void read();

        String getState();
    }

    public enum State implements ICanReadState {
        Normal("正常态", 1), Update("已更新", 2), Deleted("已删除", 3), Fired("已屏蔽", 4);

        private String name;
        private int index;

        private State(String name, int index) {
            this.name = name;
            this.index = index;
        }

        // 接口方法1

        @Override
        public String getState() {
            return this.name;
        }

        // 接口方法2
        @Override
        public void read() {
            System.out.println(this.index + ":" + this.name);
        }
    }

    public static void main(String[] args) {
        System.out.println(State.Deleted.name);
    }
}

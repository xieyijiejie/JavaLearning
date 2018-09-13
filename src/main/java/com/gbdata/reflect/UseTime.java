package com.gbdata.reflect;

import com.gbdata.common.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

public class UseTime {
    public <T> void getMonth(Class<? extends Time> timeClass) {
        try {
            for (int i = 1; i <= 10; i++) {
                /*
                 *通过反射构造内部类的实例
                 */
                Time time = timeClass.getDeclaredConstructor(new Class[]{
                        CommonTime.class, int.class}).newInstance(new CommonTime(), i);
                System.out.println("month:"+time.getTime().fromTime + "->" + time.getTime().endTime);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public <T> void getDay(Class<? extends Time> timeClass) {
        try {
            for (int i = 1; i <= 10; i++) {
                /*
                 *通过反射获取类实例
                 */
                Time time =  timeClass.getConstructor(int.class).newInstance(i);
                System.out.println("day:" + time.getTime().fromTime + "->" + time.getTime().endTime);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}

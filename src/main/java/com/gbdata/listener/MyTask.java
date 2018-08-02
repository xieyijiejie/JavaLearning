package com.gbdata.listener;

import java.util.TimerTask;

/**
 * Created by lisa.wei on 2018/7/31.
 */

public class MyTask extends TimerTask
{
    private static boolean isRunning = false;
    @Override
    public void run()
    {
        if (!isRunning)
        {
            isRunning = true;
            //执行任务
            System.out.println("现在时间：" + System.currentTimeMillis());
            isRunning = false;
        }
        else
        {
            System.out.println("上次任务还在执行");
        }
    }
}

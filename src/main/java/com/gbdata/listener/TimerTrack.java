package com.gbdata.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Timer;

/**
 * Created by lisa.wei on 2018/7/31.
 */
@WebListener
public class TimerTrack implements ServletContextListener {
    private Timer timer = null;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        timer = new Timer(true);  //在这里初始化监听器，在tomcat启动的时候监听器启动，可以在这里实现定时器功能
        sce.getServletContext().log("定时器已启动");
        timer.schedule(new MyTask(),0,(5*1000));
        sce.getServletContext().log("任务已经添加！");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (timer != null) {
            timer.cancel();
            sce.getServletContext().log("定时器已销毁");
        }
    }
}

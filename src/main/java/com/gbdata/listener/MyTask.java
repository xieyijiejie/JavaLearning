package com.gbdata.listener;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZoneOffset;
import java.util.TimeZone;

/**
 * Created by lisa.wei on 2018/7/31.
 */

public class MyTask
{
    @Scheduled(cron = "0 0/2 * * * ?")  //每隔两分钟执行
    private void work() {
        System.out.println(getUTCTime());
    }

    public static String getUTCTime(){
        return FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone(ZoneOffset.UTC)).format(System.currentTimeMillis());
    }
}

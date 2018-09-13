package com.gbdata.reflect;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

public class Day extends Time{
    int i;
    public Day(int i){
        this.i = i;
    }

    @Override
    public CommonTime.TimeParameter getTime() {
        CommonTime.TimeParameter timeParameter = new CommonTime.TimeParameter();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -i);
        timeParameter.fromTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        timeParameter.endTime = calendar.getTimeInMillis();
        timeParameter.name = YMD.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeParameter.fromTime), ZoneId.of("Asia/Shanghai")));
        return timeParameter;
    }
}


package com.gbdata.reflect;

import java.time.format.DateTimeFormatter;

public class Time {
    public DateTimeFormatter Y = DateTimeFormatter.ofPattern("YYYY");
    public DateTimeFormatter YM = DateTimeFormatter.ofPattern("YYYY-MM");
    public DateTimeFormatter YMD = DateTimeFormatter.ofPattern("YYYY-MM-dd");
    public CommonTime.TimeParameter getTime(){
        return null;
    };
}

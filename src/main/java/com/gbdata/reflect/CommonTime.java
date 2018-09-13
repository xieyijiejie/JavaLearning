package com.gbdata.reflect;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;

public class CommonTime {
    public static class TimeParameter{
        public long fromTime;
        public long endTime;
        public String name;
    }
    /*
     * i取值为1~6,1为上个月，2为上上个月，以此类推
     */
    public class Month extends Time {
        int i;
        public Month(int i){
            this.i = i;
        }

        @Override
        public TimeParameter getTime() {
            TimeParameter timeParameter = new TimeParameter();
            Calendar calendar = Calendar.getInstance();
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.DAY_OF_MONTH,1);
            calendar.add(Calendar.MONTH, -i);
            timeParameter.fromTime = calendar.getTimeInMillis();
            calendar.add(Calendar.MONTH, 1);
            timeParameter.endTime = calendar.getTimeInMillis();
            timeParameter.name = YM.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeParameter.fromTime),ZoneId.of("Asia/Shanghai")));
            return timeParameter;
        }
    }

    public class Week extends Time{
        int i;
        public Week(int i){
            this.i = i;
        }

        @Override
        public TimeParameter getTime() {
            TimeParameter timeParameter = new TimeParameter();
            Calendar calendar = Calendar.getInstance();
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.add(Calendar.WEEK_OF_YEAR, -i);
            timeParameter.fromTime = calendar.getTimeInMillis();
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            timeParameter.endTime = calendar.getTimeInMillis();
            timeParameter.name = YMD.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeParameter.fromTime),ZoneId.of("Asia/Shanghai")));
            return timeParameter;
        }
    }

    public static void main(String[] args) {
        DateTimeFormatter df= DateTimeFormatter.ofPattern("YYYY-MM-dd");

        CommonTime commonTimeW = new CommonTime();
        Arrays.asList(1,2,3,4).forEach(d->{
            CommonTime.Week week = commonTimeW.new Week(d);
            System.out.println( df.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(week.getTime().fromTime),ZoneId.of("Asia/Shanghai"))));
        });
        System.out.println("**************************");
        CommonTime commonTimeM = new CommonTime();
        Arrays.asList(1,2,3,4,5,6).forEach(d->{
            CommonTime.Month month = commonTimeM.new Month(d);
            System.out.println( df.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(month.getTime().fromTime),ZoneId.of("Asia/Shanghai"))));
        });

    }
}

package com.gbdata.reflect;

public class Test {
    public static void main(String[] args) {
        UseTime useTime = new UseTime();
        useTime.getMonth(CommonTime.Month.class);
        useTime.getDay(Day.class);
    }
}

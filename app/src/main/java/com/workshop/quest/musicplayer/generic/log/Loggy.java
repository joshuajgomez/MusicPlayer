package com.workshop.quest.musicplayer.generic.log;

import android.util.Log;

public class Loggy {

    public static void log(int logLevel, String message){
        StackTraceElement element = Thread.currentThread().getStackTrace()[3];
        String[] strings = element.getClassName().split("\\.");
        String className = strings[strings.length-1];
        String tag = className + " " + element.getMethodName();
        Log.println(logLevel, tag, message);
    }

    public static void entryLog(){
        StackTraceElement element = Thread.currentThread().getStackTrace()[3];
        String[] strings = element.getClassName().split("\\.");
        String className = strings[strings.length-1];
        String tag = className + " " + element.getMethodName();
        Log.println(Log.INFO, tag, ">>> Entry");
    }

    public static void exitLog(){
        StackTraceElement element = Thread.currentThread().getStackTrace()[3];
        String[] strings = element.getClassName().split("\\.");
        String className = strings[strings.length-1];
        String tag = className + " " + element.getMethodName();
        Log.println(Log.INFO, tag, "<<< Exit");
    }

}

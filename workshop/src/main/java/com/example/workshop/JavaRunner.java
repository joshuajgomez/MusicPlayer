package com.example.workshop;

public class JavaRunner {

    public static void main(String[] args) {
        String name = "com.joshgomez.musicplayer.service.musicmanager.MusicPlayer";
        System.out.println(getNameOnly(name));
    }

    private static String getNameOnly(String name) {
        String[] strings = name.split("\\.");
        return strings[strings.length-1];
    }
}

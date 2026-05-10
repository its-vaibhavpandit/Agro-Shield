package com.agroshield.utils;
public class AudioPlayer {
    public static void playSimulatedVoice(String text) {
        new Thread(() -> {
            System.out.println("[VOICE ASSISTANT]: " + text);
            try { Thread.sleep(text.length() * 50); } catch (InterruptedException e) {}
        }).start();
    }
}
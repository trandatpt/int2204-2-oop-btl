package btl.ballgame.client.ui.audio;

import javafx.scene.media.AudioClip;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final Map<String, AudioClip> sounds = new HashMap<>();

    public static void loadSound(String id, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new Exception("File not found: " + path);
            }
            AudioClip clip = new AudioClip(file.toURI().toString());
            sounds.put(id, clip);
            System.out.println("Loaded sound: " + path);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    public static void play(String id) {
        AudioClip clip = sounds.get(id);
        if (clip != null) {
            clip.play();
        } else {
            System.out.println("Sound not loaded: " + id);
        }
    }

    public static void stop(String id) {
        AudioClip clip = sounds.get(id);
        if (clip != null) {
            clip.stop();
        }
    }

    public static void stopAllSounds() {
        for (String audioId : sounds.keySet()) {
            AudioClip clip = sounds.get(audioId);
            if (clip != null) {
                    clip.stop();
            }
        }
        System.out.println("Stop All Sound");
    }

    public static void setVolume(String id, double volume) {
        AudioClip clip = sounds.get(id);
        if (clip != null) {
            clip.setVolume(volume);
        } else {
            System.out.println("Sound not loaded: " + id);
        }
    }

    // Set all volume (setting)
    public static void setVolume(double volume) {
        for (String audioId : sounds.keySet()) {
            AudioClip clip = sounds.get(audioId);
            if (clip != null) {
                clip.setVolume(volume);
            }
        }
        System.out.println("Set global volume: " + volume);
    }

    // sound loop for menu
    public static void playloop(String id) {
        AudioClip clip = sounds.get(id);
        if (clip != null) {
            clip.setCycleCount(AudioClip.INDEFINITE);
            clip.play();
        } else {
            System.out.println("Sound not loaded: " + id);
        }
    }

    public static void onInit() {
        loadSound("MenuSound", "assets/menuvoice.mp3");
    }
}

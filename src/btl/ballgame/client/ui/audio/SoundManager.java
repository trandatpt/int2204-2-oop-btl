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

    // Sound click botton menu
    public static void clickBottonMenu() {
        play("Confirm");
		stop("MusicMenu");
    }

    public static void clickBottonLogin() {
        play("Confirm");
		stop("MusicInGame");
    }

    // Sound click botton false
    public static void clickFalse() {
        play("Buzz");
    }

    public static void onInit() {
        loadSound("Blip", "assets/blip.wav");
        loadSound("BrickHit1", "assets/brick-hit-1.wav");
        loadSound("BrickHit2", "assets/brick-hit-2.wav");
        loadSound("Buzz", "assets/buzz.wav");
        loadSound("ClickTiny", "assets/click_tiny.wav");
        loadSound("Confirm", "assets/confirm.wav");
        loadSound("Electric", "assets/electric.wav");
        loadSound("HighScore", "assets/high_score.wav");
        loadSound("Hurt", "assets/hurt.wav");
        loadSound("KeyOpen", "assets/key_open.wav");
        loadSound("Lose", "assets/lose.wav");
        loadSound("MusicMenu", "assets/music.wav");
        loadSound("MusicInGame", "assets/musicinpick.wav");
        loadSound("NoSelect", "assets/no-select.wav");
        loadSound("PaddleHit", "assets/paddle_hit.wav");
        loadSound("Pause", "assets/pause.wav");
        loadSound("PowerUp", "assets/powerup.wav");
        loadSound("Recover", "assets/recover.wav");
        loadSound("Score", "assets/score.wav");
        loadSound("Select", "assets/select.wav");
        loadSound("Shrink", "assets/shrink.wav");
        loadSound("Switch2", "assets/switch2.wav");
        loadSound("Victory", "assets/victory.wav");
        loadSound("WallHit", "assets/wall_hit.wav");
        loadSound("Win3", "assets/win3.wav");
    }
}

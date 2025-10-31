package btl.ballgame.client.ui.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final Map<String, MediaPlayer> sounds = new HashMap<>();
    private static double volume = 0.5;

    public static void loadSound(String id, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new Exception("File not found: " + path);
            }

            Media media = new Media(file.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            sounds.put(id, player);

            System.out.println("Loaded sound: " + path);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void play(String id) {
        MediaPlayer player = sounds.get(id);
        if (player != null) {
            player.stop();
            player.seek(Duration.ZERO);
            player.play();
        } else {
            System.out.println("Sound not loaded: " + id);
        }
    }

    public static void playloop(String id) {
        MediaPlayer player = sounds.get(id);
        if (player != null) {
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.play();
        } else {
            System.out.println("Sound not loaded: " + id);
        }
    }

    public static void stop(String id) {
        MediaPlayer player = sounds.get(id);
        if (player != null) {
            player.stop();
        }
    }

    public static void stopAllSounds() {
        for (MediaPlayer player : sounds.values()) {
            if (player != null) {
                player.stop();
            }
        }
        System.out.println("Stop All Sound");
    }

    public static void setVolume(double v) {
        volume = v;
        for (MediaPlayer player : sounds.values()) {
            if (player != null) {
                player.setVolume(v);
            }
        }
        System.out.println("Set global volume: " + v);
    }

    public static void setVolume(String id, double v) {
        MediaPlayer player = sounds.get(id);
        if (player != null) {
            player.setVolume(v);
        }
    }

    public static void clickBottonMenu() {
        play("Confirm");
        stop("MusicMenu");
    }

    public static void clickBottonLogin() {
        play("Confirm");
        stop("MusicInGame");
    }

    public static void clickFalse() {
        play("Buzz");
    }

    public static double getVolume() {
        return volume;
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
        setVolume(volume);
    }
}

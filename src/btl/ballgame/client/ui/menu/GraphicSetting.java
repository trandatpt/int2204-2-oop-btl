package btl.ballgame.client.ui.menu;

public class GraphicSetting {
    public static String graphicsLevel = "MEDIUM";
    public static boolean backgroundDetail = true;
    public static boolean particleEffects = false;

    public static void setGraphicsLevel(String level) {
        graphicsLevel = level;
        System.out.println("Graphics set to: " + level);
    }

    public static void setBackgroundDetail(boolean enable) {
        backgroundDetail = enable;
    }
    
    public static void setParticleEffects(boolean enable) {
        particleEffects = enable;
    }
}

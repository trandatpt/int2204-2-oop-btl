package btl.ballgame.client.ui.menu;

/**
 * Lớp GraphicSetting lưu trữ các thiết lập đồ họa và hiệu ứng chung của game.
 */
public class GraphicSetting {

    /** 
     * Mặc định là "MEDIUM".
     */
    public static String graphicsLevel = "MEDIUM";
    public static boolean backgroundDetail = true;
    public static boolean particleEffects = false;

    /**
     * Đặt mức đồ họa (Low, Medium, High).
     */
    public static void setGraphicsLevel(String level) {
        graphicsLevel = level;
        System.out.println("Graphics set to: " + level);
    }

    /**
     * Bật hoặc tắt chi tiết nền.
     */
    public static void setBackgroundDetail(boolean enable) {
        backgroundDetail = enable;
    }

    /**
     * Bật hoặc tắt hiệu ứng hạt (particle effects).
     */
    public static void setParticleEffects(boolean enable) {
        particleEffects = enable;
    }
}

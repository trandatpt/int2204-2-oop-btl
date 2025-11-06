package btl.ballgame.client.ui.game;

import btl.ballgame.client.CSAssets;
import btl.ballgame.client.ClientArkanoidMatch.CPlayerInfo;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Constants.UPlayerEffect;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameUtils {
	public static final Image RED_TEAM_ICON = CSAssets.sprites.getAsImage("logo", "team_red");
	public static final Image BLUE_TEAM_ICON = CSAssets.sprites.getAsImage("logo", "team_blue");
	
	public static final Image RIFLE_ICON = CSAssets.sprites.getAsImage("ui_component", "kalashnikov");
	public static final Image HEART_ICON = CSAssets.sprites.getAsImage("ui_component", "heart");
	
	public static final Image BULLET_ICON = CSAssets.sprites.getAsImage("other_entities", "bullet");
	
	public static final Image BUFF_ENLARGE_BALL = CSAssets.sprites.getAsImage("buff", "enlarge_ball");
	public static final Image BUFF_MULTI_BALL = CSAssets.sprites.getAsImage("buff", "multi_ball");
	public static final Image BUFF_PADDLE_EXPAND = CSAssets.sprites.getAsImage("buff", "paddle_expand");

	/**
	 * Omit for now.
	 */
	public static void updatePlayerUI(PlayerInfoUI ui, CPlayerInfo data) {
		if (ui == null || data == null) {
			return;
		}
		var nameLabel = ui.getPlayerName();
		var healthBar = ui.getHealthBar();
		var healthLabel = ui.getHealthLabel();
		var gunImage = ui.getGunImageView();
		var ammoLabel = ui.getAmmoCount();
		var modeLabel = ui.getFiringMode();

		// player's name
		String name = data.getName();
		if (name != null && !name.equals(nameLabel.getText())) {
			nameLabel.setText(name);
		}

		// player HP bar
		double healthPercent = Math.max(0, Math.min(1.0, (double) data.health / Constants.PADDLE_MAX_HEALTH));
		double newWidth = PlayerInfoBuilder.PLAYER_INFO_WIDTH * healthPercent;
		if (healthBar.getMaxWidth() != newWidth) {
			healthBar.setMaxWidth(newWidth);
		}
		
		// hp bar descriptor
		String hpText = data.health + "/" + Constants.PADDLE_MAX_HEALTH;
		if (!hpText.equals(healthLabel.getText())) {
			healthLabel.setText(hpText);
		}

		// gun image
		if (gunImage.getImage() != RIFLE_ICON) {
			gunImage.setImage(RIFLE_ICON);
		}

		// ammo count
		String ammoText = data.bulletsLeft + " / " + Constants.AK_47_MAG_SIZE;
		if (!ammoText.equals(ammoLabel.getText())) {
			ammoLabel.setText(ammoText);
		}

		// fire mode
		if (data.firingMode != null) {
			String modeText = data.firingMode.getFriendlyName();
			if (!modeText.equals(modeLabel.getText())) {
				modeLabel.setText(modeText);
			}
		}
		
		updateBuffs(ui, data);
	}
	
	public static void updateBuffs(PlayerInfoUI ui, CPlayerInfo data) {
		if (ui == null || data == null) {
			return;
		}

		VBox[] slots = new VBox[] { 
			(VBox) ui.getBuffBox().getChildren().get(0),
			(VBox) ui.getBuffBox().getChildren().get(1), 
			(VBox) ui.getBuffBox().getChildren().get(2) 
		};
		
		for (int i = 0; i < slots.length; i++) {
			UPlayerEffect effect = i < data.effects.length ? data.effects[i] : null;
			updateBuffSlot(slots[i], effect);
		}
	}
	
    
	private static void updateBuffSlot(VBox slot, UPlayerEffect effect) {
		StackPane iconPane = (StackPane) slot.getChildren().get(0);
		Label timerLabel = (Label) slot.getChildren().get(1);
		
		if (effect == null || effect.endTime() <= System.currentTimeMillis()) {
			iconPane.setBackground(PlayerInfoBuilder.DEFAULT_BACKGROUND);
			timerLabel.setText("");
			return;
		}

		Image iconImage = switch (effect.effect()) {
			case ENLARGED_BALL -> BUFF_ENLARGE_BALL;
			case MULTI_BALL -> BUFF_MULTI_BALL;
			case PADDLE_EXPAND -> BUFF_PADDLE_EXPAND;
		};
		BackgroundImage backgroundImage = new BackgroundImage(iconImage, 
			BackgroundRepeat.NO_REPEAT,
			BackgroundRepeat.NO_REPEAT, 
			BackgroundPosition.CENTER,
			new BackgroundSize(
				100, 100,
				true, true,
				true, true
			)
		);
		iconPane.setBackground(new Background(backgroundImage));

		long remainingMs = Math.max(0, effect.endTime() - System.currentTimeMillis());
		double remainingSeconds = remainingMs / 1000.0;
		timerLabel.setText(String.format("%.1fs", remainingSeconds));
	}
}

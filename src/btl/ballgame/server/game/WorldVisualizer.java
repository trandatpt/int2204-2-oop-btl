package btl.ballgame.server.game;

import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;
import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WorldVisualizer extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Map<Integer, Vector2f> vectorVisualizers = new HashMap<>();
	private final WorldServer world;
	private final int scale = 1; // 1 world unit = 1 pixel

	public WorldVisualizer(WorldServer world) {
		this.world = world;
		setPreferredSize(new Dimension(world.getWidth() * scale, world.getHeight() * scale));

		Random rand = new Random();
		int worldWidth = 1280;
		int worldHeight = 720;
		int ballCount = 100;

		for (int i = 0; i < ballCount; i++) {
			int x = rand.nextInt(32, worldWidth - 128); // -32 so ball fits
			int y = rand.nextInt(32, worldHeight - 128);
			float dx = rand.nextFloat() * 2f - 1f;
			float dy = rand.nextFloat() * 2f - 1f;
			Vector2f dir = new Vector2f(dx, dy).normalize();
			EntityWreckingBall ball = new EntityWreckingBall(i + 1, new Location(world, x, y, dir));
			ball.setSpeed(5f);
			world.addEntity(ball);
		}
	}

	public static void addVectorVisualizer(int id) {
		vectorVisualizers.putIfAbsent(id, new Vector2f(0, 0));
	}

	public static void updateVV(int id, Vector2f vector) {
		if (vectorVisualizers.containsKey(id)) {
			vectorVisualizers.put(id, vector);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, getWidth(), getHeight());

		Collection<WorldEntity> entities = world.getEntities();
		for (WorldEntity e : entities) {
			int x = e.getBoundingBox().minX * scale;
			int y = 900 - e.getBoundingBox().minY * scale;
			int w = (e.getBoundingBox().maxX - e.getBoundingBox().minX) * scale;
			int h = (e.getBoundingBox().maxY - e.getBoundingBox().minY) * scale;

			if (e instanceof EntityWreckingBall) {
//                g2.setColor(Color.RED);
//                g2.fillRect(x, y, w, h);
				g2.setColor(new Color(Integer.hashCode(e.getLocation().getX() * e.getLocation().getY())));
				g2.fillOval(x, y, w, h);
			} else {
				g2.setColor(Color.RED);
				g2.fillRect(x, y, w, h);
			}

			if (vectorVisualizers.containsKey(e.getId())) {
				Vector2f v = vectorVisualizers.get(e.getId());
				Vector2f visualVec = v.multiply(2.0f);
				int cx = x + w / 2;
				int cy = y + h / 2;
				Stroke oldStroke = g2.getStroke();
				g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2.setColor(Color.RED);
				int endX = cx + (int) visualVec.getX();
				int endY = cy + (int) visualVec.getY();
				g2.drawLine(cx, cy, endX, endY);
				g2.fillOval(endX - 6, endY - 6, 12, 12);
				g2.setStroke(oldStroke);
			}
		}

		// Optional: draw chunk grid
		g2.setColor(new Color(255, 255, 255, 50));
		int chunkSize = 1 << LevelChunk.CHUNK_SHIFT;
		for (int cx = 0; cx < world.getWidth(); cx += chunkSize) {
			g2.drawLine(cx, 0, cx, world.getHeight());
		}
		for (int cy = 0; cy < world.getHeight(); cy += chunkSize) {
			g2.drawLine(0, cy, world.getWidth(), cy);
		}
	}

	private AABB worldBounds() {
		return new AABB(0, 0, world.getWidth(), world.getHeight());
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			WorldServer world = new WorldServer(1920, 900);
			WorldVisualizer viz = new WorldVisualizer(world);

			JFrame frame = new JFrame("btl.ballgame.server.game.WorldServer ICSP System");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(viz);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			new Timer(15, e -> {
				world.tick();
				viz.repaint();
			}).start();
		});
	}
}

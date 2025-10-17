package btl.ballgame.server.game;

import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.server.game.entities.breakable.EntityBrick;
import btl.ballgame.server.game.entities.dynamic.EntityPaddle;
import btl.ballgame.server.game.entities.dynamic.EntityWreckingBall;
import btl.ballgame.server.game.match.ArkanoidMatch;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Vector2f;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WorldVisualizer extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Map<Integer, Vector2f> vectorVisualizers = new HashMap<>();
	private final WorldServer world;
	private final int scale = 1; // 1 world unit = 1 pixel
	
	static EntityPaddle pad;
	static EntityPaddle pad2;
	public WorldVisualizer(WorldServer world) {
		this.world = world;
		setPreferredSize(new Dimension(world.getWidth() * scale, world.getHeight() * scale));

		Random rand = new Random();
		int worldWidth = world.getWidth();
		int worldHeight = world.getHeight();
		int ballCount = 5;

		for (int i = 0; i < ballCount; i++) {
			int x = rand.nextInt(32, worldWidth - 128); // -32 so ball fits
			int y = rand.nextInt(32, worldHeight - 128);
			float dx = rand.nextFloat() * 2f - 1f;
			float dy = rand.nextFloat() * 2f - 1f;
			Vector2f dir = new Vector2f(dx, dy).normalize();
			EntityWreckingBall ball = new EntityWreckingBall(i + 1, new Location(world, x, y, dir));
			world.addEntity(ball);
		}
		
		int cc = 128;
		for (int j = 0; j < 20; j++) {
			for (int i = 0; i < 10; i++) {
				EntityBrick brick = new EntityBrick(cc++, new Location(world, 30 + (i * 49), 100 + (j * 19), 0));
				world.addEntity(brick);
			}
		}
		
		pad = new EntityPaddle(null, 1236, new Location(world, 100, 40, 0));
		world.addEntity(pad);
		
		pad2 = new EntityPaddle(null, 1836, new Location(world, 100, 650, 0));
		world.addEntity(pad2);
	}

	public static void addVectorVisualizer(int id) {
		vectorVisualizers.putIfAbsent(id, new Vector2f(0, 0));
	}

	public static void updateVV(int id, Vector2f vector) {
		if (vectorVisualizers.containsKey(id)) {
			vectorVisualizers.put(id, vector);
		}
	}
	
	public static Graphics2D g2;
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, getWidth(), getHeight());
//		
//		if (drawAABB) {
//			WorldServer.toVisualize.forEach(aabb -> {
//				g2.setColor(Color.YELLOW);
//				g2.fillRect(aabb.minX, aabb.minY, (int) aabb.getWidth(), (int) aabb.getHeight());				
//			});
//			if (!pause) WorldServer.toVisualize.clear();
//		}
		
		Collection<WorldEntity> entities = world.getEntities();
		
		for (WorldEntity e : entities) {
			AABB aabb = e.getBoundingBox();
			
			int x = aabb.minX * scale;
			int y = aabb.minY * scale;
			int w = e.getWidth();
			int h = e.getHeight();
			
			if (drawAABB) {
				g2.setColor(new Color(255, 102, 102, 74));
				g2.fillRect(aabb.minX, aabb.minY, (int) aabb.getWidth(), (int) aabb.getHeight());
			}
			if (e instanceof EntityWreckingBall) {
				g2.setColor(new Color(Integer.hashCode(e.getLocation().getX() * e.getLocation().getY())));
				g2.fillOval(x, y, w, h);
			} else {
				g2.setColor(Color.RED);
				g2.fillRect(x, y, w, h);
			}

			if (visualizeVec && vectorVisualizers.containsKey(e.getId())) {
				Vector2f v = vectorVisualizers.get(e.getId()).clone();
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
		for (int cy = world.getHeight(); cy >= 0; cy -= chunkSize) {
			g2.drawLine(0, cy, world.getWidth(), cy);
		}
	}

	private AABB worldBounds() {
		return new AABB(0, 0, world.getWidth(), world.getHeight());
	}

	static boolean visualizeVec = true;
	static boolean pause = false;
	static boolean drawAABB = false;
	public static void main(String[] args) {
		ArkanoidServer.main(args);
		SwingUtilities.invokeLater(() -> {
			WorldServer world = new WorldServer(new ArkanoidMatch(ArkanoidMode.ONE_VERSUS_ONE), 600, 700);
			WorldVisualizer viz = new WorldVisualizer(world);

			JFrame frame = new JFrame("btl.ballgame.server.game.WorldServer ICSP System");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(viz);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			System.out.println(ArkanoidServer.MS_PER_TICK);
			new Timer(ArkanoidServer.MS_PER_TICK, e -> {
				if (!pause) world.tick();
				viz.repaint();
			}).start();
			
			frame.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT -> pad.moveLeft();
					case KeyEvent.VK_RIGHT -> pad.moveRight();
					case KeyEvent.VK_Z -> pad2.moveLeft();
					case KeyEvent.VK_C -> pad2.moveRight();
					case KeyEvent.VK_P -> pause = !pause;
					case KeyEvent.VK_A -> drawAABB = !drawAABB;
					case KeyEvent.VK_V -> visualizeVec = !visualizeVec;
					case KeyEvent.VK_S -> world.tick();
					}
				}
			});
		});
	}
}

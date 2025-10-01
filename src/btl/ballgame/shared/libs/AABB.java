package btl.ballgame.shared.libs;

/**
 * A simple 2D Axis-Aligned Bounding Box (AABB) utility class.
 */
public class AABB {
	public final int minX;
	public final int minY;
	public final int maxX;
	public final int maxY;

	/**
	 * Create a new AABB from minimum and maximum coordinates.
	 */
	public AABB(int minX, int minY, int maxX, int maxY) {
		if (maxX < minX || maxY < minY) {
			throw new IllegalArgumentException("Invalid AABB: max must be >= min");
		}
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	/**
	 * Create an AABB from a position (x, y) and size (width, height).
	 */
	public static AABB fromPositionSize(int x, int y, int width, int height) {
		return new AABB(x, y, x + width, y + height);
	}
	
	/**
	 * Create a centered-AABB from a {@link Location} and size (width, height).
	 */
	public static AABB fromCenteredLocWithSize(Location loc, int width, int height) {
		return fromCenteredPositionWithSize(loc.getX(), loc.getY(), width, height);
	}
	
	/**
	 * Create a centered-AABB from a position (x, y) and size (width, height).
	 */
	public static AABB fromCenteredPositionWithSize(int x, int y, int width, int height) {
		int halfX = width >> 1, halfY = height >> 1;
		return new AABB(
			x - halfX, y - halfY,
			x + halfX, y + halfY
		);
	}

	/**
	 * Get width.
	 */
	public float getWidth() {
		return maxX - minX;
	}

	/**
	 * Get height.
	 */
	public float getHeight() {
		return maxY - minY;
	}
	
	/**
	 * Get center X.
	 */
	public float getCenterX() {
		return (minX + maxX) * 0.5f;
	}

	/**
	 * Get center Y.
	 */
	public float getCenterY() {
		return (minY + maxY) * 0.5f;
	}

	/**
	 * Check if this AABB intersects with another AABB.
	 */
	public boolean intersects(AABB other) {
		return this.maxX > other.minX && this.minX < other.maxX && this.maxY > other.minY && this.minY < other.maxY;
	}

	/**
	 * Check if this AABB fully contains another AABB.
	 */
	public boolean contains(AABB other) {
		return this.minX <= other.minX && this.minY <= other.minY && this.maxX >= other.maxX && this.maxY >= other.maxY;
	}

	/**
	 * Check if this AABB contains a point.
	 */
	public boolean contains(float x, float y) {
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	/**
	 * Move (translate) this AABB by dx, dy.
	 */
	public AABB move(int dx, int dy) {
		return new AABB(minX + dx, minY + dy, maxX + dx, maxY + dy);
	}

	/**
	 * Expand this AABB by a given amount in all directions.
	 */
	public AABB expand(int amount) {
		return new AABB(minX - amount, minY - amount, maxX + amount, maxY + amount);
	}

	/**
	 * Get the intersection depth (overlap) with another AABB. Returns a
	 * Vector2f{dx, dy}. If no intersection, returns zero vector.
	 */
	public Vector2f getIntersectionDepth(AABB other) {
		if (!intersects(other)) {
			return Vector2f.ZERO;
		}
		float dx1 = other.maxX - this.minX;
		float dx2 = other.minX - this.maxX;
		float dy1 = other.maxY - this.minY;
		float dy2 = other.minY - this.maxY;
		float dx = Math.abs(dx1) < Math.abs(dx2) ? dx1 : dx2;
		float dy = Math.abs(dy1) < Math.abs(dy2) ? dy1 : dy2;
		return new Vector2f(dx, dy);
	}

	@Override
	public AABB clone() {
		return new AABB(minX, minY, maxX, maxY);
	}
	
	@Override
	public String toString() {
		return "AABB{minX=" 
			+ minX 
			+ ",minY=" + minY 
			+ ",maxX," + maxX 
			+ ",maxY" + maxY
		+ "}";
	}
}

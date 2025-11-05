package btl.ballgame.shared.libs;

public class Vector2f {
	public static final Vector2f ZERO = new Vector2f();
	
	public static Vector2f fromTheta(double radians) {
		// x = cos(theta), y = sin(theta)
		float dx = (float) Math.cos(radians), dy = (float) Math.sin(radians);
		return new Vector2f(dx, dy).normalize();
	}
	
	public float x;
	public float y;

	public Vector2f() {
		this(0, 0);
	}

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2f(Vector2f other) {
		this.x = other.x;
		this.y = other.y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Vector2f setX(float x) {
		this.x = x;
		return this;
	}

	public Vector2f setY(float y) {
		this.y = y;
		return this;
	}

	public Vector2f add(Vector2f other) {
		this.x += other.x;
		this.y += other.y;
		return this;
	}

	public Vector2f subtract(Vector2f other) {
		this.x -= other.x;
		this.y -= other.y;
		return this;
	}

	public Vector2f multiply(float scalar) {
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}
	
	public Vector2f divide(float scalar) {
		if (scalar != 0) {
			this.x /= scalar;
			this.y /= scalar;
		}
		return this;
	}
	
    public float dot(Vector2f other) {
        return this.x * other.x + this.y * other.y;
    }

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public Vector2f normalize() {
		float len = length();
		if (len != 0) {
			this.x /= len;
			this.y /= len;
		}
		return this;
	}

	public float distance(Vector2f other) {
		float dx = this.x - other.x;
		float dy = this.y - other.y;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector2f v)) {
			return false;
		}
		return Float.compare(v.x, x) == 0 && Float.compare(v.y, y) == 0;
	}

	@Override
	public int hashCode() {
		int result = Float.hashCode(x);
		result = 31 * result + Float.hashCode(y);
		return result;
	}

	@Override
	public String toString() {
		return "Vector2f{x=" + x + ", y=" + y + "}";
	}

	@Override
	public Vector2f clone() {
		return new Vector2f(x, y);
	}
}

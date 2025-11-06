package btl.ballgame.tests.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import btl.ballgame.shared.libs.Vector2f;

public class Vector2fTest {
	@Test
	public void testConstructorAndGetters() {
		Vector2f v = new Vector2f(3, 4);
		assertEquals(3f, v.getX());
		assertEquals(4f, v.getY());

		Vector2f zero = new Vector2f();
		assertEquals(0f, zero.getX());
		assertEquals(0f, zero.getY());

		Vector2f copy = new Vector2f(v);
		assertEquals(v.getX(), copy.getX());
		assertEquals(v.getY(), copy.getY());
	}

	@Test
	public void testSetters() {
		Vector2f v = new Vector2f();
		v.setX(5).setY(-2);
		assertEquals(5f, v.getX());
		assertEquals(-2f, v.getY());
	}

	@Test
	public void testAddSubtract() {
		Vector2f a = new Vector2f(1, 2);
		Vector2f b = new Vector2f(3, 4);

		a.add(b);
		assertEquals(new Vector2f(4, 6), a);

		a.subtract(b);
		assertEquals(new Vector2f(1, 2), a);
	}

	@Test
	public void testMultiplyDivide() {
		Vector2f v = new Vector2f(2, -4);
		v.multiply(3);
		assertEquals(new Vector2f(6, -12), v);
		v.divide(2);
		assertEquals(new Vector2f(3, -6), v);
		v.divide(0); // should not change values
		assertEquals(new Vector2f(3, -6), v);
	}

	@Test
	public void testDotProduct() {
		Vector2f a = new Vector2f(1, 2);
		Vector2f b = new Vector2f(3, 4);
		assertEquals(11f, a.dot(b));
	}

	@Test
	public void testLengthAndNormalize() {
		Vector2f v = new Vector2f(3, 4);
		assertEquals(5f, v.length());

		v.normalize();
		assertEquals(1f, v.length(), 1e-6);
		assertEquals(0.6f, v.getX(), 1e-6);
		assertEquals(0.8f, v.getY(), 1e-6);

		Vector2f zero = new Vector2f();
		zero.normalize(); // should (in theory bru) remain zero
		assertEquals(Vector2f.ZERO, zero);
	}

	@Test
	public void testDistance() {
		Vector2f a = new Vector2f(0, 0);
		Vector2f b = new Vector2f(3, 4);
		assertEquals(5f, a.distance(b));
	}

	@Test
	public void testEqualsAndHashCode() {
		Vector2f a = new Vector2f(1, 2);
		Vector2f b = new Vector2f(1, 2);
		Vector2f c = new Vector2f(2, 3);

		assertEquals(a, b);
		assertNotEquals(a, c);
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void testClone() {
		Vector2f a = new Vector2f(5, -7);
		Vector2f copy = a.clone();
		assertEquals(a, copy);
		assertNotSame(a, copy);
	}

	@Test
	public void testFromTheta() {
		double angle = Math.PI / 4; // 45 degrees
		Vector2f v = Vector2f.fromTheta(angle);
		double expected = Math.sqrt(2) / 2;
		assertEquals(expected, v.getX(), 1e-6);
		assertEquals(expected, v.getY(), 1e-6);
		assertEquals(1f, v.length(), 1e-6);
	}
}

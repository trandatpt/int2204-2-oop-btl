package btl.ballgame.shared.libs;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	private static final int BUFFER_SIZE = 128;
	
	public static float lerp(float v0, float v1, float t) {
		return v0 + t * (v1 - v0);
	}
	
	public static int intLerp(float v0, float v1, float t) {
		return (int) lerp(v0, v1, t);
	}
	
	public static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
	
	private static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
	
	public static String SHA256(String s) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			int length;
			for (int i = 0; i < s.length(); i += BUFFER_SIZE) {
				length = Math.min(BUFFER_SIZE, s.length() - i);
				byte[] chunk = s.substring(i, i + length).getBytes(StandardCharsets.UTF_8);
				digest.update(chunk);
			}
			byte[] encodedHash = digest.digest();
			return bytesToHex(encodedHash);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}

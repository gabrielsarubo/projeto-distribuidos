package utils;

import java.awt.Color;
import java.util.Random;

/**
 * 
 * @author sarubo
 *
 */
public class Util {
	
	private static Random random = new Random();
	
	public static Color generateRandomColor() {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return new Color(red, green, blue);
    }
	
	public static int nextInt(int bound) {
		return random.nextInt(bound);
	}
	
}

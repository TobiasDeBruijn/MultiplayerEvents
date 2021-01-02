package nl.thedutchmc.multiplayerevents;

public class Utils {

	public static int getRandomInt(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}
	
}

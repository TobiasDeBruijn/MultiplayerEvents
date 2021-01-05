package nl.thedutchmc.multiplayerevents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;

public class Utils {

	/**
	 * Generate a random Integer with the provided bounds
	 * @param min Lower bound, inclusive
	 * @param max Upper bound, inclusive
	 * @return Returns a random Integer 
	 */
	public static int getRandomInt(int min, int max) {
		max = max +1;
		return (int) (Math.random() * (max - min) + min);
	}
	
	/**
	 * Turn an ugly String into a pretty one.<br>
	 * Example: "ZOMBIE_PIGMAN" -> "Zombie Pigman"<br>  
	 * Word separation is on underscores only.<br>
	 * @param input Input String
	 * @return Returns pretty String
	 */
	public static String prettyCaptitalizationForMinecraftNames(String input) {
		//Split the input on '_'
		String[] inputParts = input.toLowerCase().split("_");
		String[] outputParts = new String[inputParts.length];
		
		//Iterate over all parts of the input and apply proper capitalization
		//using WordUtils (Apache Commons)
		for(int i = 0; i < inputParts.length; i++) {
			outputParts[i] = WordUtils.capitalize(inputParts[i]);
		}
		
		//Join the properly capitalized parts together, using a space character between parts
		String output = String.join(" ", outputParts);
		return output;
	}
	
	/**
	 * Pluralize a word in the English language<br>
	 * E.g: Wolf -> Wolves<br>
	 * <br>
	 * This method can't do everything (obviously)<br>
	 * "In some cases, singular nouns ending in -s or -z, require that you double the -s or -z prior to adding the -es for pluralization." -- I am not writing a grammer processor or something, so this won't work.
	 * @param input Singular word to pluralize
	 * @return Returns the pluralized input
	 */
	public static String pluralizeSingularEnglish(String input) {
		String output = input;
		
		//The following words do not change when pluralized
		List<String> exceptionsOnPluralizing = Arrays.asList(new String[] {
				"sheep", 
				"series", 
				"species", 
				"deer", 
				"moose",
				"drowned"
			});
		
		//The following words are irregular nouns and have their own special way of pluralizing
		HashMap<String, String> irregularNouns = new HashMap<>();
		irregularNouns.put("child", "children");
		irregularNouns.put("goose", "geese");
		irregularNouns.put("man", "men");
		irregularNouns.put("woman", "women");
		irregularNouns.put("tooth", "teeth");
		irregularNouns.put("person", "people");
		
		/**
		 * Used regex flags:
		 * m: Match multiline, this is necessary for some reason
		 * i: Ignore case
		 */
		
		//If a word ends with a,u,i,e then we need to add `'s` to it
		if(input.matches("[a,u,i,e]$/mi")) {
			output += "'s";
		
		//If a word ends with s,ss,sh,ch,x,z we need to add `es` to it
		} else if(input.matches("s$|ss$|sh$|ch$|x$|z$/mi")) {
			output += "es";
			
		//If a word ends with f,fe we need to replace that with `ve`, then add `s` to it
		} else if(input.matches("f$|fe$/mi")) {
			output.replaceAll("$fe|$f/mi", "ve");
			output += "s";
			
		} else if(input.matches("y$/mi")) {
			
			//If the second to last character is z,b,t,g,h: replace -y with -ies
			String secondToLastChar = Character.toString(input.charAt(input.length() - 2));
			if(secondToLastChar.matches("[z,b,t,g,h]/mi")) {
				output.replaceAll("y$/mi", "ies");
			}
		
		/*If a word ends with -o we need to add `es` to it, except if it's:
		* - Photo
		* - Piano
		* - Halo
		*/
		} else if(input.matches("o$/mi")) {
			output += "es";
			
		//If a word ends with -us we need to substitute it for `i`
		} else if(input.matches("us$/m")) {
			output.replaceAll("us$/mi", "i");
		
		//If a word ends with -is we need to substitute it for `es`
		} else if(input.matches("is$/mi")) {
			output.replaceAll("is$/mi", "es");
			
		//If a word ends with -on we need to substitute it for `a`
		} else if(input.matches("on$/mi")) {
			output.replaceAll("on$/mi", "a");

		//If the word is any of the following, we don't change it at all.
		} else if(exceptionsOnPluralizing.contains(input.toLowerCase())) {
			return output;
		
		//Irregular nouns
		} else if(irregularNouns.containsKey(input.toLowerCase())) {
			output = irregularNouns.get(input.toLowerCase());
			
		//No random English weirdness, so we just add an `s`
		} else {
			output += "s";
		}
		
		return output;
	}
	
	/**
	 * Calculate the cylindrical distance between A and B
	 * @param locationA Location A
	 * @param locationB Location B
	 * @return Returns the distance 
	 */
	public static double getDistanceCylindrical(Location locationA, Location locationB) {
		return Math.pow((locationA.getX() - locationB.getX()), 2) + Math.pow((locationA.getZ() - locationB.getZ()), 2);
	}
}

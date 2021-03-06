package helper;

import java.io.File;

import java.util.ArrayList;

/**
 * Helper class to store all game data and load in/save this data
 * to retain progress through playthroughs
 */
public class GameData {

	// store the players winnings, questions they have answered, questions and categories that have been selected,
	// whether the categories and clues need to be generated again, and whether the international section has been
	// unlocked
	private static ArrayList<String> answeredQuestions = new ArrayList<String>();
	private static ArrayList<String> addedQuestions = new ArrayList<String>();
	private static ArrayList<String> addedCategories = new ArrayList<String>();
	private static int winnings = 0;
	private static int totalWinnings=0;
	private static boolean catSelected = true;
	private static boolean international = false;

	/**
	 * If saved data exists from a previous playthrough, load in this data
	 * so the user can continue with their progress
	 */
	public static void readSavedData() {

		File w = new File("winnings");
		if (w.exists()) {
			winnings = Integer.valueOf(TextFileReader.read(w).get(0)); // if winnings have previously been saved, read from file
			totalWinnings = Integer.valueOf(TextFileReader.read(w).get(1)); 
		}
		File an = new File("answeredQuestions");
		if (an.exists()) {
			answeredQuestions = (ArrayList<String>) TextFileReader.read(an); // if answered questions have previously been
			// saved, read from this file
		}
		File ad = new File("addedQuestions");
		if (ad.exists()) {
			addedQuestions = (ArrayList<String>) TextFileReader.read(ad); // if added questions have previously been
			// saved, read from this file
			catSelected = false;
		}
		File ac = new File("addedCategories");
		if (ac.exists()) {
			addedCategories = (ArrayList<String>) TextFileReader.read(ac); // if categories have previously been
			// saved, read from this file
		}
		File ib = new File("international");
		if (ib.exists()) {
			international = Boolean.valueOf(TextFileReader.read(ib).get(0)); // if the international section has been unlocked
			// and saved, read from this file
		}
	}

	/**
	 * When the user exits the game, save their playthrough data
	 * to separate files
	 */
	public static void writeSavedData() {
		
		ArrayList<String> winningsArray = new ArrayList<String>(); // array so you can save winnings and total winnings in the same file
		winningsArray.add(Integer.toString(winnings));
		winningsArray.add(Integer.toString(totalWinnings));

		// ensure there is actually data that can be saved (user has started the games module)
		if (GameData.getAddedCategories().size() != 0) {
			TextFileWriter.write("international", String.valueOf(international), null);
			TextFileWriter.write("winnings", null, winningsArray);
			TextFileWriter.write("answeredQuestions", null, answeredQuestions);
			TextFileWriter.write("addedQuestions", null, addedQuestions);
			TextFileWriter.write("addedCategories", null, addedCategories);
		}
	}

	/**
	 * This method resets all fields back to their original state and also
	 * deletes the files containing saved data
	 */
	public static void resetData() {
		
		winnings = 0;
		answeredQuestions.clear();
		addedQuestions.clear();
		addedCategories.clear();
		catSelected = true;
		international = false;

		File w = new File("winnings");
		w.delete();
		File an = new File("answeredQuestions");
		an.delete();
		File ad = new File("addedQuestions");
		ad.delete();
		File ac = new File("addedCategories");
		ac.delete();
		File ib = new File("international");
		ib.delete();
	}

	/**
	 * Indicate a question as answered by adding it to the array
	 */
	public static void addAnsweredQuestion(String question) {
		if (question == null) {
			answeredQuestions.clear();
		} else {
			answeredQuestions.add(question);
		}
	}

	/**
	 * Indicate a question as being randomly generated by adding
	 * it to the array
	 */
	public static void addQuestion(String question) {
		if (question == null) {
			addedQuestions.clear();
		} else {
			addedQuestions.add(question);
		}
	}

	/**
	 * Indicate a category as being selected by adding it to
	 * the array
	 */
	public static void addCategory(String category) {
		if (category == null) {
			addedCategories.clear();
		} else {
			addedCategories.add(category);
		}
	}
	
	/**
	 * Set the user's winnings for this playthrough
	 */
	public static void setWinnings(int value) {
		if (value == 0) {
			winnings = 0;
		}
		winnings += value;
	}
	
	/**
	 * Set the total winnings
	 */
	public static void setTotalWinnings(int value) {
		totalWinnings=value;
	}
	
	/**
	 * Set whether the user has selected their 5 categories, with false
	 * meaning they have
	 */
	public static void setCatSelected(Boolean bool) {
		catSelected = bool;
	}

	/**
	 * Set whether the international category has been unlocked
	 */
	public static void setInternational(Boolean bool) {
		international = bool;
	}

	/**
	 * Return the list of randomly generated questions
	 */
	public static ArrayList<String> getAddedQuestions() {
		return addedQuestions;
	}

	/**
	 * Return the list of answered questions
	 */
	public static ArrayList<String> getAnsweredQuestions() {
		return answeredQuestions;
	}

	/**
	 * Return the list of selected categories
	 */
	public static ArrayList<String> getAddedCategories() {
		return addedCategories;
	}

	/**
	 * Return the users winnings for this playthrough
	 */
	public static int getWinnings() {
		return winnings;
	}

	/**
	 * Return the total winnings
	 */
	public static int getTotalWinnings() {
		return totalWinnings;
	}

	/**
	 * Return whether the international category has been unlocked
	 */
	public static boolean getInternational() {
		return international;
	}

	/**
	 * Return whether the user has selected their 5 categories, with true
	 * meaning they haven't
	 */
	public static boolean getCatSelected() {
		return catSelected;
	}
}
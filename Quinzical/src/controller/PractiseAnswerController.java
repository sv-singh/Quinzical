package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class PractiseAnswerController implements Initializable {

	@FXML
	Button submit_button;

	@FXML
	Button back_button;

	@FXML
	Button audio_replay_button;

	@FXML
	Label question_label;

	@FXML
	Label chance_left;

	@FXML
	Label hint_label;
	
	@FXML
	Label bracketLabel;

	@FXML
	ProgressBar reading_bar;

	@FXML
	Slider volume_slider;

	@FXML
	TextField user_input;
	
	private boolean clicked=false;

	private static String _question; // What is expected from the user
	private static String _answer = ""; // What is displayed to the user
	private static String _hint;
	private static String _bracket="         ";

	private int _chance = 3;
	private String _speed = "0";
	Alert a = new Alert(AlertType.NONE);
	
	/**
	 * Initialize this controller by setting the relevant FXML elements
	 * and their values
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		back_button.setDisable(true);
		question_label.setText(_answer);
		chance_left.setText(Integer.toString(_chance));
//		user_input.setPromptText(_bracket);
		// Add lisenter to the slider
		volume_slider.setOnMouseReleased(event -> {
			int temp = (int) volume_slider.getValue();
			_speed = Integer.toString(temp);
		});
		int tempendIndex = (_bracket.trim().length())-1;
		if (tempendIndex>=0) {
			bracketLabel.setText(_bracket.trim().substring(1, tempendIndex)+":");
		}
		
	}

	/**
	 * Use spd-say to speak using the worker thread
	 * @param sentence the sentence to speak
	 */
	public void speak(String sentence) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				String cmd = "spd-say --wait -r" + _speed + " \"" + sentence + "\"";
				ProcessBuilder ttsBuilder = new ProcessBuilder("bash", "-c", cmd);
				try {
					ttsBuilder.start();
				} catch (IOException e) {
					e.printStackTrace();
					a.setAlertType(AlertType.ERROR);
					// show the dialog
					a.show();
					a.setHeaderText("Audio System Crash");
					a.setContentText("Please make sure spd-say is installed (in READ.md) and restart the game");
				}
			}
		};
		thread.setName("thread1");
		thread.start();

	}
	
	/**
	 * Called when the user presses the replay button.
	 * This method speaks the clue again
	 */
	public void onReplayPushed(ActionEvent event) {
		speak(_answer);
	}
	
	/**
	 * Called when the user presses the main menu button.
	 * This method changes the scene to the main menu 
	 */
	public void onMainMenuPushed(ActionEvent event) {
		speak(" "); // To prevent the audio keep playing after going back to the menu
		try {
			Parent viewParent = FXMLLoader.load(getClass().getResource("../view/MainMenu.fxml"));
			Scene viewScene = new Scene(viewParent);
			Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
			window.setScene(viewScene);
			window.setResizable(false);
			window.show();

		} catch (IOException e) {
			
			a.setAlertType(AlertType.ERROR);
			// show the dialog
			a.show();
			a.setHeaderText("Fatal Error");
			a.setContentText("Please restart the game");
		}
	}
	
	/**
	 * Replace macrons in the users answer with normal letters. This normalizes
	 * the user input so it can be evaluated correctly taking into account macrons@
	 * @param text the user answer to normalize
	 * @return the normalized text
	 */
	public String normal(String text) {
		String[] macrons = new String[] {"ā","ē","ī","ō","ū","Ā","Ē","Ī","Ō","Ū"};
		String[] normalLetter = new String[] {"a","e","i","o","u","a","e","i","o","u"};
		for(String i :  macrons){
			if (text.contains(i)) {
				text=text.replace(i, normalLetter[Arrays.asList(macrons).indexOf(i)]);
			}
		}
		return text.toLowerCase();
		

	};
	
	/**
	 * Perform input normalization on the users answer so that all non alphabetic 
	 * characters are removed and leading a/the/an are also removed
	 * @param text the user answer to normalize
	 * @return the normalized text
	 */
	public String refineString(String text) {
		// Remove any symbols but not /
		if (text.contains("/")) {
			;
		}else {
			text = text.replaceAll("\\p{P}", "");
		}

		//  Remove leading a/the/an
		String leading = text.split(" ")[0].trim();
		//Make sure removing the leading wont cause empty String
		if (text.split(" ").length>1 && (leading.equalsIgnoreCase("the") || leading.equalsIgnoreCase("a") || leading.equalsIgnoreCase("an"))) {
			text=text.replaceFirst(leading+" ", "").trim();
		}
		return text;
	}
	
	/**
	 * Called when the user presses the submit button.
	 * This method normalizes and then evaluates the user answer and performs actions based on
	 * whether the answer is correct or not
	 */
	public void onSubmitButtonPushed() {
		//Normalize 2 Strings to get rid of macrons
		String input = normal(user_input.getText().trim()).toLowerCase();
		String normalizedanswer = normal(_question.trim()).toLowerCase();
		normalizedanswer = refineString(normalizedanswer); //Refactor the answer
		
		if (input.equalsIgnoreCase(normalizedanswer) || input.contains(normalizedanswer)) {
			hint_label.setVisible(true);
			hint_label.setText("Correct! The answer was: " + _bracket + " " + _question);
			speak("Correct! The answer was: " + _bracket + " " + _question);
			submit_button.setVisible(false);
			audio_replay_button.setDisable(true);
			back_button.setDisable(false);
			back_button.setVisible(true);
		
		// If answer has multiple answer which is not expected	
		} else if (_question.contains("/") && normalizedanswer.contains(input)){
			hint_label.setVisible(true);
			hint_label.setText("Correct! The answer was: " + _bracket + " " + _question);
			speak("Correct! The answer was: " + _bracket + " " + _question);
			submit_button.setVisible(false);
			audio_replay_button.setDisable(true);
			back_button.setDisable(false);
			back_button.setVisible(true);
		}else {
		
			// IF input is not correct
			_chance = _chance - 1;
			chance_left.setText(Integer.toString(_chance));
			hint_label.setVisible(true);
			hint_label.setText("Sorry, incorrect!");

			// Compare the chance to see whether finished or show hint
			if (_chance == 0) { // Game over
				hint_label.setVisible(true);
				hint_label.setText("Incorrect. The correct answer was: " + _bracket + " " + _question);
				speak("Incorrect. The correct answer was: " + _bracket + " " + _question);
				submit_button.setVisible(false);
				audio_replay_button.setDisable(true);
				back_button.setDisable(false);
				back_button.setVisible(true);
				user_input.setText(_question.trim());

			} else if (_chance == 1) { // Show hint
				hint_label.setVisible(true);
				hint_label.setText(_hint);
			}
		}
		
	}
	
	/**
	 * This method gets called when the user presses enter to submit their
	 * question rather than pressing the submit button
	 */
	public void onEnterKeyPressed(ActionEvent event) {
		if (clicked == false) {
			onSubmitButtonPushed();
		}
	
	}

	/**
	 * Set the relevant fields which contain information about the question
	 */
	public void setStrings(String answer, String question, String bracket) {
		_answer = answer;
		_question = question.split(",")[0];
		_bracket = bracket;
		_hint = "Hint: " + gethint(_question);
		speak(_answer);
	}

	/**
	 * Perform splitting and trimming on the question string to get the hint 
	 * to display to the user
	 * @param hint the string to perform formatting on
	 * @return the hint
	 */
	public String gethint(String hint) {
		String leading = hint.trim().split(" ")[0];
		if (leading.equalsIgnoreCase("the") || leading.equalsIgnoreCase("a") || leading.equalsIgnoreCase("an")) {
			String temp = leading + " ";
			hint = hint.replace(temp, "");
			hint = hint.trim();
		}
		return hint.substring(0, 1);
	}
}
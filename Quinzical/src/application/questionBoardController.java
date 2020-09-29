package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javafx.event.EventHandler;


public class questionBoardController implements Initializable {

    @FXML
    GridPane grid;

    @FXML
    Button winnings;

    @FXML
    Button reset;

    @FXML
    Text resetText;

    // track the x and y positions within the gridpane
    int index_y = 0;
    int index_x = 0;

    ArrayList<File> addedCategories = new ArrayList<File>();

    

    public void initialize(URL url, ResourceBundle rb) {
        resetText.setVisible(false);
        reset.setVisible(false);
        winnings.setText("Winnings: $");
        File dir = new File("cat"); // get location of categories folder
        File[] categoryFolder = dir.listFiles();
        if (categoryFolder != null) {
            for (int i = 0; i < 5; i++) { // iterate through each category
                ArrayList<Integer> addedIndex = new ArrayList<Integer>();
                ArrayList<String> values = new ArrayList<String>();
                File randomCat = getRandom(categoryFolder);
                while (addedCategories.contains(randomCat)) {
                    randomCat = getRandom(categoryFolder);
                }
                addedCategories.add(randomCat);
                index_y = 0;
                Text categoryName = new Text(randomCat.getName().toUpperCase());
                categoryName.setFont(Font.font("Agency FB", 20));
                categoryName.setFill(Color.LIGHTSKYBLUE);
                grid.add(categoryName, index_x, index_y);
                GridPane.setHalignment(categoryName, HPos.CENTER);

                TextFileReader reader = new TextFileReader();
                List<String> lines = reader.read(randomCat);
                for (int j = 0; j < 5; j++) { // go through every line of the category file and parse the results,
                                              // forming
                                              // the respective fields
                    int randomIndex = ThreadLocalRandom.current().nextInt(0, lines.size());
                    while (addedIndex.contains(randomIndex)) {
                        randomIndex = ThreadLocalRandom.current().nextInt(0, lines.size());
                    }
                    System.out.println(lines.get(randomIndex) + " | ");
                   
                    String value = lines.get(randomIndex).substring(lines.get(randomIndex).lastIndexOf(',') + 1).trim();
                    values.add(value);
                    addedIndex.add(randomIndex);
    
                }
              
                Collections.sort(values, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
                    }
                });
                for (String value : values) {
                    index_y++;
                    if (index_y == 1) {
                        addButton(value, true); // add it to the board
                    }
                    else {
                        addButton(value, false); // add it to the board
                    }
                }
                index_x++;
            }
        } else {
            // the case when the category folder is not found
            System.out.println("Category folder not found");
        }
    }

    public static File getRandom(File[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public void addButton(String text, Boolean lowest) {
        Button button = new Button("$" + text);
        button.setPrefSize(190, 80);
        button.setFont(Font.font("Agency FB", 20));
        button.setStyle("-fx-background-color: #ffc100; ");
        if (!lowest) {
            button.setDisable(true);
            button.setStyle("-fx-background-color: #ACACAC; ");
        }
        button.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { // when the player selections a question
                try {
                    onQuestionButtonPushed(event); // send the event to the buttons event handler
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        grid.add(button, index_x, index_y);
        GridPane.setHalignment(button, HPos.CENTER);
    }

    public void onQuestionButtonPushed(ActionEvent event) throws IOException {
        Parent viewParent = FXMLLoader.load(getClass().getResource("gameAnswer.fxml"));
        Scene viewScene = new Scene(viewParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(viewScene);
        window.show();
    }

}
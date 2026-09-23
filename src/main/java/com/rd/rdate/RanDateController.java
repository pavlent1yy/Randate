package com.rd.rdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

/*TO-DO--------------------------------- 
     --- Прикрутить еще пару-тройку режимов
     --- Перекраска кнопки Check 
     --- Сделать PopUp более симпотным
     --- Сохранение шрифтов
     --- isCorrect лампочка не меняет цвет
----------------------------------------*/
public class RanDateController {

	@FXML
	private Button checkbButton, openFileButton, closeButton, minButton;

	@FXML
	private Text isCorrectText, dateText, correctAnswerCountText, eventText1, eventText2, eventText3, eventText4;

	@FXML
	private RadioButton rightButton, radioButton1, radioButton2, radioButton3, radioButton4;

	@FXML
	private Pane topPane;

	private double xOffset = 0;
	private double yOffset = 0;
	private File file;
	private Map<String, ArrayList<String>> datesAndEvents = new HashMap<String, ArrayList<String>>();
	private List<ArrayList<String>> events = new ArrayList<>();
	private List<String> dates = new ArrayList<>();
	private int correctAnswerCount = events.size();
	private int numberOfChecks = 0;

	@FXML
	protected void closeWindow(ActionEvent event) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	protected void minimizeWndow(ActionEvent event) {
		Stage stage = (Stage) minButton.getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	protected void handleMovementAction(MouseEvent event) {
		Stage stage = (Stage) topPane.getScene().getWindow();
		stage.setX(event.getScreenX() - xOffset);
		stage.setY(event.getScreenY() - yOffset);
	}

	@FXML
	protected void handlePressedAction(MouseEvent event) {
		xOffset = event.getX();
		yOffset = event.getY();
	}

	@FXML
	protected void openFile(ActionEvent event) {
		clearAll();
		datesAndEvents.clear();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a file with dates");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("*.txt files",
				"*.txt"));
		Stage stage = new Stage();
		file = fileChooser.showOpenDialog(stage);
		readFile();
		correctAnswerCount = datesAndEvents.keySet().size();
		correctAnswerCountText.setText("");
	}

	protected void readFile() {
		if (file != null) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				while (line != null) {
					String[] splitedString = line.split("--");
					if (splitedString.length == 2) {
						ArrayList<String> valuePart = new ArrayList<>();
						if (datesAndEvents.containsKey(splitedString[0])
								&& !datesAndEvents.get(splitedString[0]).contains(splitedString[1])) {
							datesAndEvents.get(splitedString[0]).add(splitedString[1]);
						} else {
							valuePart.add(splitedString[1]);
							datesAndEvents.put(splitedString[0], valuePart);
						}

					}
					line = reader.readLine();
				}
				events.addAll(datesAndEvents.values());
				dates.addAll(datesAndEvents.keySet());
				reader.close();
			} catch (IOException e) {
				System.out.println(e.getClass().getSimpleName());
			}
			buildQuestion();
		}
	}

	@FXML
	protected void checkAnswer(ActionEvent event) throws InterruptedException {
		if (file != null && rightButton != null) {
			if (rightButton.isSelected()) {
				sleepAndColorAnswer(Color.GREEN, true);
			} else {
				sleepAndColorAnswer(Color.RED, false);
			}
			numberOfChecks++;
			readFile();
			rightButton.requestFocus();
			if (numberOfChecks == datesAndEvents.keySet().size())
				userScore();
		}
	}

	protected void sleepAndColorAnswer(Color COLOR, boolean isAnswerCorrect) throws InterruptedException {
		Thread.sleep(250);
		isCorrectText.setFill(COLOR);
		if (!isAnswerCorrect)
			correctAnswerCount -= 1;
		correctAnswerCountText.setText(correctAnswerCount + "\\" + datesAndEvents.keySet().size());
	}

	protected void userScore() {
		int procentOfCorrectAnswer = (correctAnswerCount * 100) / datesAndEvents.keySet().size();
		if (procentOfCorrectAnswer == 100)
			answerForConformationPopup(procentOfCorrectAnswer, "5");
		else if (procentOfCorrectAnswer >= 95)
			answerForConformationPopup(procentOfCorrectAnswer, "5-");
		else if (procentOfCorrectAnswer >= 80)
			answerForConformationPopup(procentOfCorrectAnswer, "4");
		else if (procentOfCorrectAnswer >= 75)
			answerForConformationPopup(procentOfCorrectAnswer, "4-");
		else if (procentOfCorrectAnswer >= 55)
			answerForConformationPopup(procentOfCorrectAnswer, "3");
		else if (procentOfCorrectAnswer >= 53)
			answerForConformationPopup(procentOfCorrectAnswer, "3-");
		else if (procentOfCorrectAnswer <= 55 && procentOfCorrectAnswer != 0)
			answerForConformationPopup(procentOfCorrectAnswer, "2");
		else if (procentOfCorrectAnswer == 0)
			answerForConformationPopup(procentOfCorrectAnswer, "Дима Игнашев");

		clearAll();
		file = null;
		numberOfChecks = 0;
		correctAnswerCount = datesAndEvents.keySet().size();
		correctAnswerCountText.setText("");
	}

	protected void answerForConformationPopup(int procentOfCorrectAnswer, String appraisal) {
		conformationPopup("Count assessment", procentOfCorrectAnswer + "% Correct answers",
				"Your assessment: " + appraisal);
	}

	protected void clearAll() {
		eventText1.setText("Text");
		eventText2.setText("Text");
		eventText3.setText("Text");
		eventText4.setText("Text");
		dateText.setText("Date:");
		isCorrectText.setFill(Color.WHITE);
	}

	protected void buildQuestion() {
		try {
			Random rand = new Random();
			String date = dates.get(rand.nextInt(dates.size()));

			List<Text> radioTexts = new ArrayList<>();
			List<RadioButton> radioButtons = new ArrayList<>();
			Collections.addAll(radioTexts, eventText1, eventText2, eventText3, eventText4);
			Collections.addAll(radioButtons, radioButton1, radioButton2, radioButton3, radioButton4);

			int randomRightButton = rand.nextInt(radioButtons.size());
			rightButton = radioButtons.get(randomRightButton);
			Text rightAnswerText = radioTexts.get(randomRightButton);
			rightAnswerText.setText(datesAndEvents.get(date).get(rand.nextInt(datesAndEvents.get(date).size())));
			events.remove(datesAndEvents.get(date));
			radioTexts.remove(rightAnswerText);
			for (Text radioText : radioTexts) {
				ArrayList<String> randomEvent = events.get(rand.nextInt(events.size()));
				radioText.setText(randomEvent.get(rand.nextInt(randomEvent.size())));
				events.remove(randomEvent);
			}
			dateText.setText("Date: " + date);
			dates.clear();
			events.clear();
		} catch (Exception e) {
			errorPopup(e, "File is not Found of file cannot be processed. Please, choose the other file.");
		}
	}

	protected void errorPopup(Exception exception, String instruction) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Error: " + exception.getClass().getSimpleName() + "!");
		alert.setHeaderText(exception.getClass().getSimpleName());
		alert.setContentText(instruction);
		alert.show();
	}

	protected void conformationPopup(String title, String content, String header) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.show();

	}

}
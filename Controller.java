package application;

import java.util.Observable;

import java.util.Observer;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Controller implements Observer, EventHandler<ActionEvent> {
	/** Game board object */
	private GameBoard board;
	/** Grid on which the game will be played */
	@FXML
	private GridPane gameGrid;
	/** Main title label */
	@FXML
	private Label mainTitle;
	/** Reset button */
	@FXML
	private Button resetButton;
	/** Combo box that controls size of grid */
	@FXML
	private ComboBox<Integer> sizeCombo;
	/** Status label */
	@FXML
	private Label statusLabel;
	/** Array that holds all the game position buttons */
	@FXML
	private coorButton[][] buttonArray;

	/**
	 * Combobox is populated with integers, reset button is populated with text,
	 * new grid is initiated. Combobox and button are set with on action.
	 */
	@FXML
	private void initialize() {
		sizeCombo.setItems(FXCollections.observableArrayList(5, 8, 10, 13));
		sizeCombo.getSelectionModel().select(1);
		sizeCombo.setOnAction(this);

		resetButton.setText("RESET");
		resetButton.setOnAction(this);

		try {
			board = new GameBoard();
			board.addObserver(this);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText(e.toString());
			alert.showAndWait();
		}
		createNewGrid();
		mainTitle.setText("Rabbits vs. Farmers");
		statusLabel.setText(board.getStatusLabel());
	}

	/**
	 * Called when new grid needs to be created
	 */
	private void createNewGrid() {
		buttonArray = new coorButton[board.getSize() + 1][board.getSize() + 1];

		gameGrid.getChildren().clear();

		// Row counter
		for (int i = 1; i < buttonArray.length; i++) {
			// Column counter
			for (int j = 1; j < buttonArray.length; j++) {
				coorButton position = new coorButton(i, j);
				position.setStyle("-fx-background-color: " + getColorForToken(position));
				position.setPrefWidth(80);
				buttonArray[i][j] = position;
				GridPane.setConstraints(gameGrid, i, j);
				gameGrid.add(position, j, i);
				position.setOnAction((ActionEvent e) -> {
					try {
						board.setMove(position.getRowCoor(), position.getColumnCoor());
					} catch (Exception e1) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText(e1.toString());
						alert.showAndWait();
					}
					position.setStyle("-fx-background-color: " + getColorForToken(position));
				});
			}
		}
	}

	/**
	 * Handles actions in the combo box and reset button
	 */
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource() == sizeCombo || event.getSource() == resetButton) {
			resetBoard();
		}
	}

	/**
	 * Method called after verifying action in handle method.
	 */
	@FXML
	private void resetBoard() {
		board = new GameBoard(sizeCombo.getValue());
		createNewGrid();
		board.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		statusLabel.setText(board.getStatusLabel());

		if (board.getSize() != buttonArray.length)
			createNewGrid();
		else {
			// Row counter
			for (int i = 1; i < buttonArray.length; i++) {
				// Column counter
				for (int j = 1; j < buttonArray.length; j++) {
					buttonArray[i][j].setStyle(
							"-fx-background-color: " + getColorForToken(buttonArray[i][j]));
				}
			}
		}

	}

	/**
	 * Gets the color for the individual button based on the state of the model
	 * 
	 * @param button
	 * @return returns string that will set button color
	 */
	public String getColorForToken(coorButton button) {
		int n = board.getTokenAt(button.getRowCoor(), button.getColumnCoor());
		if (n == 0)
			return "gray";
		if (n == 1)
			return "red";
		if (n == 2)
			return "blue";
		else
			return "yellow";
	}

}

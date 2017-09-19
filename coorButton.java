package application;

import javafx.scene.control.Button;

public class coorButton extends Button{
	/** The row coordinate of the button */
	private int rowCoor;
	/** The column coordinate of the button */
	private int columnCoor;

	/** 
	 * Constructor
	 */
	public coorButton(int row, int column){
		setRowCoor(row);
		setColumnCoor(column);
	}

	public coorButton(){
		setRowCoor(0);
		setColumnCoor(0);
	}

	/** Getter for the row coordinate */
	public int getRowCoor() {
		return rowCoor;
	}
	/** Setter for the row coordinate */
	public void setRowCoor(int rowCoor) {
		this.rowCoor = rowCoor;
	}
	/** Getter for the column coordinate */
	public int getColumnCoor() {
		return columnCoor;
	}
	/** Setter for the column coordinate */
	public void setColumnCoor(int columnCoor) {
		this.columnCoor = columnCoor;
	}
}


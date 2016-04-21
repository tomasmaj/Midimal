package model;

/**
 * All the notes in a track part
 * 
 * @author Tomas Majling
 *
 */

public class TrackPart {

	private boolean[][] notes;

	public TrackPart(int row, int column) {
		this.notes = new boolean[row][column];
	}

	public boolean[][] getNotes() {
		return notes;
	}

	/**
	 * Get a value if note is on or off
	 * 
	 * @param row
	 *            on the track part
	 * @param column
	 *            on the track part
	 * @return true if note is on and false if note is off
	 */
	public boolean getNote(int row, int column) {
		return notes[row][column];
	}

	/**
	 * Add a note to the track part
	 * 
	 * @param row
	 *            on the track part
	 * @param column
	 *            on the track part
	 */
	public void addNote(int row, int column) {
		this.notes[row][column] = true;
	}

	/**
	 * Remove a note on the track part
	 * 
	 * @param row
	 *            on the track part
	 * @param column
	 *            on the track part
	 */
	public void removeNote(int row, int column) {
		this.notes[row][column] = false;
	}

}

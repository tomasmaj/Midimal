package model;

/**
 * Singleton to have one model for all controllers
 * 
 * @author Tomas Majling
 *
 */

public enum Midimal {

	/**
	 * Enuminstance to hold one instance of program models
	 */
	INSTANCE;

	// Start new Sequencer with 4 tracks and a soundbank
	private Sequencer sequencer = new Sequencer(4, "src/files/soundbank.sf2");

	/**
	 * Get instance of sequencer
	 * 
	 * @return the Sequencer
	 */
	public Sequencer getSequencer() {
		return sequencer;
	}

	/**
	 * Set new instance of sequencer on new file or load file
	 * 
	 * @param sequencer
	 *            that holds all tracks and notes
	 */
	public void setSequencer(Sequencer sequencer) {
		this.sequencer = sequencer;
	}

}

package model.instrument;

import java.util.HashMap;

import javax.sound.midi.Instrument;
import javax.sound.midi.Synthesizer;

/**
 * Superclass to load different midi instrument
 * 
 * @author Tomas Majling
 *
 */

public abstract class MidiInstrument {

	protected HashMap<String, Integer> instrument;
	protected Synthesizer synth;

	/**
	 * Superclass to load different midi instrument
	 * 
	 * @param synth that contains the instruments
	 */
	public MidiInstrument(Synthesizer synth) {
		this.synth = synth;
		this.instrument = new HashMap<>();
	}

	/**
	 * 
	 * @return the instrument as a map
	 */
	public HashMap<String, Integer> getInstrument() {
		return instrument;
	}

	/**
	 * 
	 * @param instrument
	 *            as map
	 */
	public void setInstrument(HashMap<String, Integer> instrument) {
		this.instrument = instrument;
	}

	/**
	 * The synth that holds the instruments
	 * 
	 * @return the synth
	 */
	public Synthesizer getSynth() {
		return synth;
	}

	/**
	 * The synth that holds the instruments
	 * 
	 * @param synth
	 *            as synthhesizer
	 */
	public void setSynth(Synthesizer synth) {
		this.synth = synth;
	}
	
	/**
	 * Holds a scale of midi notes
	 * 
	 * @return the midi notes as a scale
	 */
	public abstract int[] getScaleNumber();

	/**
	 * Holds a scale of key names
	 * 
	 * @return the key names of the scale
	 */
	public abstract String[] getScaleName();

	/**
	 * Displays the loaded instruments and save them in instrument map
	 */
	protected void loadInstruments() {
		for (Instrument instrument : this.synth.getLoadedInstruments()) {
			this.instrument.put(instrument.getName(), instrument.getPatch().getProgram());
		}
	}


}

package model.instrument;

import javax.sound.midi.Instrument;
import javax.sound.midi.Synthesizer;

/**
 * Subclass to MidiInstrument
 * Load drum kits from synth
 * 
 * @author Tomas Majling
 *
 */

public class MidiDrumKit extends MidiInstrument {

	private final int[] scaleNumber = { 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52 };
	private final String[] scaleName = { "Acoustic Bass Drum", "Bass Drum", "Side Stick", "Acoustic Snare", "Hand Clap",
			"Electrir Snare", "Low Floor Tom", "Closed High-Hat", "High Floor Tom", "Pedal High-Hat", "Low Tom",
			"Open High-Hat", "Low-Mid Tom", "High-Mid Tom", "Crash Symbal", "High Tom", "Ride Cymbal",
			"Chinese Cymbal" };

	/**
	 * Subclass to MidiInstrument
	 * Load drum kits from synth
	 * 
	 * @param synth that contains the instruments
	 */
	public MidiDrumKit(Synthesizer synth) {
		super(synth);
		loadInstruments();
	}

	@Override
	public int[] getScaleNumber() {
		return scaleNumber;
	}

	@Override
	public String[] getScaleName() {
		return scaleName;
	}

	// Overiding the super class method so when the reference is of
	// MidiInstrument and instance is of MidiDrumKit
	// this method is callaed instead of the super classes method
	@Override
	protected void loadInstruments() {
		
		for (Instrument instrument : super.synth.getLoadedInstruments()) {
			if (instrument.toString().startsWith("Drumkit")) {
				super.instrument.put(instrument.getName(), instrument.getPatch().getProgram());
			}
		}

	}


}

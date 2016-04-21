package model.instrument;

import javax.sound.midi.Instrument;
import javax.sound.midi.Synthesizer;

import model.key.Key;

/**
 * Subclass to MidiInstrument Load instruments from synth
 * 
 * @author Tomas Majling
 *
 */
public class MidiSynth extends MidiInstrument {

	protected final int[] scaleNumber = { 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 48, 47, 46, 45, 44, 43 };

	protected final String[] scaleName = { Key.C.getName(), Key.G_SHARP.getName(), Key.G.getName(),
			Key.F_SHARP.getName(),
			Key.F.getName(), Key.E.getName(), Key.D_SHARP.getName(), Key.D.getName(), Key.C_SHARP.getName(), };

	protected final int[] programsToLoad = { 0, 1, 2, 3, 6, 9, 10, 11, 12, 13, 14, 24, 25, 26, 27, 28, 31, 32, 33, 34,
										35,36, 37, 38, 39, 45, 46, 47, 55, 104, 105, 106, 107, 108, 112, 113, 114, 
										115, 116, 117, 118, 119, 120, 121, 122, 123, 127 };


	/**
	 * Subclass of MidiInstrument
	 * Load instruments from soundbank
	 * 
	 * @param synth that contains the instruments
	 */
	public MidiSynth(Synthesizer synth) {
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
	// MidiInstrument and instance is of MidiSynth
	// this method is callaed instead of the super classes method
	@Override
	protected void loadInstruments() {

		for (Instrument instrument : super.synth.getLoadedInstruments()) {
			if (instrument.toString().startsWith("Instrument") && instrument.getPatch().getBank() == 0) {
				for(int program : programsToLoad) {
					if (instrument.getPatch().getProgram() == program) {
						super.instrument.put(instrument.getName(), instrument.getPatch().getProgram());
						continue;
					}
				}
			}
		}

	}


}

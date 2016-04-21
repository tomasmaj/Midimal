package model;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;

import model.instrument.MidiDrumKit;
import model.instrument.MidiInstrument;
import model.instrument.MidiSynth;

/**
 * Track that holds all instruments and volume and track parts
 * 
 * @author Tomas Majling
 *
 */

public class Track {

	// Aggregation - One Track have many TrackParts
	List<TrackPart> trackParts;

	private int channelNumber;
	private MidiChannel channel;
	private int velocity;
	private String loadedInstrument;
	private int bank;
	private int program;
	private Synthesizer synth;

	// Aggregation and Polymorphism reference
	private MidiInstrument midiInstrument;

	public Track(int channelNumber, int velocity, Synthesizer synth, boolean drumtrack) {
		this.synth = synth;
		this.channelNumber = channelNumber;
		this.velocity = velocity;
		this.bank = 0;
		this.program = 0;
		this.trackParts = new ArrayList<TrackPart>();
		addTrackPart();

		// Polymorphism
		// One reference can have instances of its subclasses
		// Here i reference a instance of a MidiDrumKit if the track is a
		// drumtrack
		// Otherwise i reference a instance of a MidiSynth
		if (drumtrack) {
			this.midiInstrument = new MidiDrumKit(this.synth);
			this.channel = setChannel(9);
			this.loadedInstrument = "Standard";
		} else {
			this.midiInstrument = new MidiSynth(this.synth);
			this.channel = setChannel(channelNumber);
			this.loadedInstrument = "Piano 1";
		}
		setProgram(this.bank, this.program);
	}

	public int getChannelNumber() {
		return channelNumber;
	}

	public void setChannelNumber(int channelNumber) {
		this.channelNumber = channelNumber;
	}

	public MidiChannel getChannel() {
		return channel;
	}

	/**
	 * Set the Midichannel for the track
	 * 
	 * @param channelNumber
	 *            on the synth
	 * @return the MidiChannel from the synth
	 */
	public MidiChannel setChannel(int channelNumber) {

		return synth.getChannels()[channelNumber];
	}

	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	public MidiInstrument getMidiInstrument() {
		return midiInstrument;
	}

	public void setMidiInstrument(MidiInstrument midiInstrument) {
		this.midiInstrument = midiInstrument;
	}

	// Overloaded setProgram to use two parameter with bank and program
	/**
	 * Sets the instrument for the track
	 * 
	 * @param bank
	 *            on the synth for the instrument
	 * @param program
	 *            on the synth for the instrument
	 */
	public void setProgram(int bank, int program) {
		this.bank = bank;
		this.program = program;
		this.channel.programChange(bank, program);
	}

	// Overloaded setProgram to use one parameter with only program
	/**
	 * Set the instrument for the track
	 * 
	 * @param program
	 *            on the synth for the instrument
	 */
	public void setProgram(int program) {
		this.program = program;
		this.channel.programChange(program);
	}

	public int getProgram() {
		return this.program;
	}

	/**
	 * Get the name of the loaded instrument on the track
	 * 
	 * @return the loaded instrument
	 */
	public String getLoadedInstrument() {
		return this.loadedInstrument;
	}

	/**
	 * Set the name of the loaded instrument on the track
	 * 
	 * @param instrument
	 *            that is loaded
	 */
	public void setLoadedInstrument(String instrument) {
		this.loadedInstrument = instrument;
	}

	/**
	 * Get a list of all created track parts on the track
	 * 
	 * @return a list of TrackPart
	 */
	public List<TrackPart> getTrackPart() {
		return this.trackParts;
	}

	/**
	 * Add a new track part on the track
	 * 
	 */
	public void addTrackPart() {
		this.trackParts.add(new TrackPart(18, 16));
	}

}

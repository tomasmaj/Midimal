package model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

/**
 * 
 * @author Tomas Majling
 * 
 */

public class Sequencer {

	// Aggregation - One Sequencer have many Tracks
	private List<Track> tracks;

	private Synthesizer synth;
	private String soundbankFilePath;
	private int tempo;

	/**
	 * Sequencer constructor
	 * 
	 * @param numberOfTracks
	 *            to create
	 * @param soundbankFilePath
	 *            to load
	 */
	public Sequencer(int numberOfTracks, String soundbankFilePath) {
		try {
			this.synth = MidiSystem.getSynthesizer();
			this.synth.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		this.soundbankFilePath = soundbankFilePath;
		this.setSoundbank(soundbankFilePath);
		this.tempo = 120;
		this.tracks = new LinkedList<Track>();
		addTrack(numberOfTracks, 1);
	}

	// Overload av konstruktor för att ladda default soundbank
	/**
	 * Constructor for Sequencer to load default soundbank
	 * 
	 * @param numberOfTracks
	 *            to be added to the sequencer
	 */
	public Sequencer(int numberOfTracks) {
		this(numberOfTracks, "");
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public String getSoundbankFilePath() {
		return soundbankFilePath;
	}

	/**
	 * Sets the soundbank on the sequencer
	 * 
	 * @param soundbankFilePath
	 *            to location of soundbank file
	 */
	public void setSoundbankFilePath(String soundbankFilePath) {
		this.soundbankFilePath = soundbankFilePath;
		setSoundbank(soundbankFilePath);
	}

	public Synthesizer getSynth() {
		return this.synth;
	}

	public int getTempo() {
		return tempo;
	}

	/**
	 * Set the tempo of the sequencer
	 * 
	 * @param tempo
	 *            of song
	 * @return the tempo of sequencer
	 */
	public int setTempo(int tempo) {
		return this.tempo = tempo;
	}

	/**
	 * Load the soundbank instruments to the synth
	 * 
	 * @param filePath
	 *            to location of soundbank file
	 */
	public void setSoundbank(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			try {
				Soundbank soundbank = MidiSystem.getSoundbank(file);
				this.synth.loadAllInstruments(soundbank);
			} catch (InvalidMidiDataException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add tracks to the sequencer
	 * 
	 * @param numberOfTracks
	 *            to be added to the sequencer
	 * @param numberOfDrumTracks
	 *            number of tracks to be drumtracks and add them last
	 */
	public void addTrack(int numberOfTracks, int numberOfDrumTracks) {
		int size = this.tracks.size();
		for (int i = size; i < size + numberOfTracks; i++) {
			if (i + numberOfDrumTracks >= numberOfTracks) {
				this.tracks.add(new Track(i, 100, this.synth, true));
			} else {
				this.tracks.add(new Track(i, 100, this.synth, false));
			}
		}
	}

	/**
	 * Remove track from the sequencer
	 * 
	 * @param track
	 *            to be removed
	 */
	public void removeTrack(Track track) {
		this.tracks.remove(track);
	}
	
}

package model.key;

/**
 * Gets keyname and value of a note on the lowest octave
 * 
 * @author Tomas Majling
 *
 */
public enum Key {

	C("C", 0),
	C_SHARP("C#", 1),
	D("D", 2),
	D_SHARP("D#", 3),
	E("E", 4),
	F("F", 5),
	F_SHARP("F#", 6),
	G("G", 7),
	G_SHARP("G#", 8),
	A("A", 9),
	A_SHARP("A#", 10),
	B("B", 11);

	private final String name;
	private final int number;

	private Key(String name, int number) {
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public int getNumber() {
		return number;
	}
}

package jthesia.midi;

public class MidiNote {
	
	public static final String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	
	private final int key;
	private final int velocity;
	private final String name;
	private final int octave;
	
	private final long time;
	private final long length;

	private int durationWhole;
	private int durationNom;
	private int durationDenom;
	private int durationDot;
	
	public MidiNote(int key, int velocity, long onTick, long length) {
		this.time = onTick;

		this.key = key;
		this.velocity = velocity;
		
		this.length = length;
		
		this.octave = (key / 12) - 1;
		this.name = NOTE_NAMES[key % 12] + this.octave;
	}

	/**
	 * Time in microseconds
	 * */
	public long getTimeUS() {
		return time;
	}
	
	/**
	 * Length in microseconds
	 * */
	public long getLengthUS() {
		return length;
	}

	public int getKey() {
		return key;
	}

	public void setDuration(int whole, int noteDurationNom, int noteDurationDenom, int dot) {
		this.durationWhole = whole;
		this.durationNom = noteDurationNom;
		this.durationDenom = noteDurationDenom;
		this.durationDot = dot;
	}

	public int getVelocity() {
		return velocity;
	}

	public String getName() {
		return name;
	}

	public int getDurationWhole() {
		return durationWhole;
	}

	public int getDurationNom() {
		return durationNom;
	}

	public int getDurationDenom() {
		return durationDenom;
	}
	
	public String printDuration() {
		String d = "";
		if (durationWhole > 0) {
			d += durationWhole + " ";
		}
		
		if (durationNom > 0 && durationDenom > 1) {
			d += durationNom + "/" + durationDenom + (durationDot == 0 ? "" : ".");
		}
		
		return d;
	}
	
}

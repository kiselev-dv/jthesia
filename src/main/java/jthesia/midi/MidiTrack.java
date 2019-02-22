package jthesia.midi;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MidiTrack {

	public static final int TREBLE_CLEF = 0;
	public static final int BASS_CLEF = 1;
	
	private List<MidiNote> notes = new ArrayList<MidiNote>();
	
	private int channel;
	
	private boolean sorted = false;
	
	private int clef = TREBLE_CLEF;
	private String name;
	
	public void setChannel(int channel) {
		this.channel = channel;
	}

	// Notes are added in order of release time
	// but i want notes array to be sorted in order of
	// on times
	public void addNote(MidiNote n) {
		if (n.getLengthUS() > 0) {
			notes.add(n);
			sorted = false;
		}
	}
	
	public List<MidiNote> getNotes() {
		if (!sorted) {
			Collections.sort(notes, new Comparator<MidiNote>() {
				public int compare(MidiNote o1, MidiNote o2) {
					return Long.compare(o1.getTimeUS(), o2.getTimeUS());
				}
			});
			
			sorted = true;
		}
		
		return notes;
	}
	
	public void print(PrintStream out, MidiSong gameSong) {
		
		out.println("\t<channel>" + channel + "</channel>");

		out.println("\t<name>" + name + "</name>");
		out.println("\t<size>" + notes.size() + "</size>");
		out.println("\t<clef>" + (clef == 0 ? "treble" : "bass" ) + "</clef>");
		
		for(MidiNote n : getNotes()) {
			out.println("\t@" + n.getTimeUS() + "\t" + n.getName() + "\t" + n.printDuration());
		}
	}
	
	/**
	 * Average key number
	 * */
	int getAverageKey() {
		// Default access is on purpose
		
		long sum = 0;
		for(MidiNote n : getNotes()) {
			sum += n.getKey();
		}
		
		return (int) (sum / getNotes().size());
	}

	public int getClef() {
		return clef;
	}

	public void setClef(int clef) {
		this.clef = clef;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}

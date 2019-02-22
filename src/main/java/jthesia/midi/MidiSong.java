package jthesia.midi;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.codec.digest.DigestUtils;

public class MidiSong {
	
	private final List<MidiTrack> tracks = new ArrayList<MidiTrack>();
	
	private int bpm = 120;
	
	// Microseconds per quater note
	private int tempo;

	private int timeSigNom = 4;

	private int timeSignDenom = 4;

	private int metronomeTicks = 96;

	private String keySignature = "C major";

	private int resolution;

	private boolean tracksSorted = false;

	private String name = null;

	private String copy = null;

	private long microsecondsLength;
	
	public void addTracks(Collection<MidiTrack> tracks) {
		if(tracks != null) {
			this.tracks.addAll(tracks);
			tracksSorted = false;
		}
	}

	public void print(PrintStream out) {
		out.println("Name: " + name);
		
		out.println("Resolution: " + resolution + " ticks per beat");
		
		out.println("BPM: " + bpm + " (" + tempo + "us per qater note)");
		out.println("Time signature: " + timeSigNom + "/" + timeSignDenom);
		out.println("Key: " + keySignature);

		out.println("Metronome: each " + metronomeTicks + " midi ticks");
		
		ListIterator<MidiTrack> li = getTracks().listIterator(getTracks().size());
		while(li.hasPrevious()) {
			MidiTrack track = li.previous();
			
			out.println("<Track>");
			track.print(out, this);
			out.println("</Track>");
		}
		
	}

	public void outHTML() {
		try {
			PrintWriter w = new PrintWriter(new FileWriter(new File("song.html")));
			w.println("<html><head></head><body>");
			
			for(MidiTrack track : getTracks()) {
				for(MidiNote n : track.getNotes()) {
					String note = "<div style=\"position:absolute; "
							+ "top:" + n.getKey() * 10 + "px;"
							+ "left: " + n.getTimeUS() / 10 + "px;"
							+ "min-width:" + n.getLengthUS() / 10 + "px;"  
							+ "min-height: 10px;"
							+ "background-color: #999999;"
							+ "\"></div>";
					
					w.println(note);
					
				}
			}
			
			w.println("</body></html>");
			w.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setBpm(int bpm) {
		this.bpm = bpm;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public void setTimeSignature(int nom, int denom) {
		this.timeSigNom    = nom;
		this.timeSignDenom = denom;
	}

	public void setMetronome(int metronomeTicks) {
		this.metronomeTicks = metronomeTicks;
	}

	public void setKeySignature(String keySignature, boolean major) {
		this.keySignature = keySignature + (major ? " major" : " minor");
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
	}

	public int getResolution() {
		return resolution;
	}

	/**
	 * Return tracks sorted by average key.
	 * */
	public List<MidiTrack> getTracks() {
		if (!tracksSorted) {
			sortTracks();
		}
		
		return tracks;
	}

	private void sortTracks() {
		Collections.sort(tracks, new Comparator<MidiTrack>() {

			public int compare(MidiTrack t1, MidiTrack t2) {
				return Integer.compare(t1.getAverageKey(), t2.getAverageKey());
			}
			
		});
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setCopy(String copy) {
		this.copy = copy;
	}

	public int getTempo() {
		return tempo;
	}

	public int getBPM() {
		return bpm;
	}
	
	public int[] getTimeSignature() {
		return new int[] {timeSigNom, timeSignDenom};
	}

	public void setMicrosecondsLength(long microsecondsLength) {
		this.microsecondsLength = microsecondsLength;
	}
	
	public long getMicrosecodsLength() {
		return this.microsecondsLength;
	}

	public String getHash() {
		
		StringBuilder sb = new StringBuilder();
		for (MidiTrack t : getTracks()) {
			for(MidiNote n : t.getNotes()) {
				sb.append(n.getName()).append(n.printDuration());
			}
		}
		
		return DigestUtils.md5Hex(sb.toString());
	}


}
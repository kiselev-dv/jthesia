package jthesia.midi;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class Parser {
	private static final int ROUNDING_GRAIN = 32;
	
	private static final String[] MAJOR_SCALES = "Cb, Gb, Db, Ab, Eb, Bb, F, C, G, D, A, E, B, F#, C#".split(", ");
	private static final String[] MINOR_SCALES = "Ab, Eb, Bb, F, C, G, D, A, E, B, F#, C#, G#, D#, A#".split(", ");
	
	private static final int MICROSECONDS_IN_MINUTE = 60 * 1000 * 1000;
	
	private static final int PROGRAMM_CHANGE = 192;
	private static final int CONTROL_CHANGE = 176;
	private static final int META_KEYSIGNATURE = 0x59;
	
	private static final int META_TIMESIGNATURE = 0x58;
	
	private static final int META_TEMPO = 0x51;
	private static final int META_TEXT = 0x01;
	private static final int META_COPY = 0x02;
	private static final int META_TRACK_NAME = 0x03;
	private static final int META_INSTRUMENT = 0x04;
	private static final int META_ENDOFTRACK = 0x2F;
	private static final int META_LYRICS = 0x05;
	
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;

	public static void main(String[] args) throws Exception {
		
		Sequence sequence = MidiSystem.getSequence(new File(args[0]));

		MidiSong song = new Parser().parse(sequence);
		
		song.print(System.out);

	}

	private int resolution;
	private int fullNoteLength;
	
	private MidiSong song;

	private int tempo = 0;

	/*
	 * Length of one tick in microseconds
	 * */
	private double uSecPerTick;

	public MidiSong parse(Sequence sequence) {
		
		this.song = new MidiSong();

		if(sequence.getDivisionType() != Sequence.PPQ) {
			throw new UnsupportedOperationException("SMPTE divison is not supported.");
		}
		
		this.resolution = sequence.getResolution();
		this.fullNoteLength = this.resolution * 4;
		this.uSecPerTick = (double)sequence.getMicrosecondLength() / 
				(double)sequence.getTickLength();
		
		song.setResolution(resolution);
		
		
		song.setMicrosecondsLength(sequence.getMicrosecondLength());
		
		for (Track track : sequence.getTracks()) {
			
			Map<Integer, MidiTrack> trackByChannel = new HashMap<Integer, MidiTrack>();
			Map<Integer, MidiEvent> onOffMessages = new HashMap<Integer, MidiEvent>();
			
			MidiTrack activeTrack = new MidiTrack();
			
			for (int i = 0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
				MidiMessage message = event.getMessage();
				
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;

					if (sm.getCommand() == NOTE_ON && sm.getData2() != 0) {
						
						int key = sm.getData1();
						int hash =  key * 16 + sm.getChannel();
						
						MidiEvent prev = onOffMessages.put(hash, event);
						if (prev != null) {
							ShortMessage prevSM = (ShortMessage)prev.getMessage();
							if (prevSM.getCommand() == NOTE_OFF || prevSM.getData2() == 0) {
								if (prev.getTick() < event.getTick()) {
									MidiNote n = parseNote(key, prev, null, sequence);
									getOrAddTrack(trackByChannel, sm, activeTrack).addNote(n);
								}
								else {
									System.err.println("Note off before note on: " + key + " @" + event.getTick());
								}
							}
							else {
								System.err.println("Two note on: " + key + 
										" prev: comm=" + prevSM.getCommand() + " vel=" + prevSM.getData2() + " @" + prev.getTick() + 
										" this: comm=" + sm.getCommand() + " vel=" + sm.getData2() + " @" + event.getTick());
							}
						}
					} 
					
					// Some midi files use NOTE_ON event with zero velocity
					// as NOTE_OFF event
					else if (sm.getCommand() == NOTE_OFF || sm.getData2() == 0) {
						int key = sm.getData1();
						int hash =  key * 16 + sm.getChannel();
						
						MidiEvent on = onOffMessages.remove(hash);
						if (on != null) {
							MidiNote n = parseNote(key, on, event, sequence);
							getOrAddTrack(trackByChannel, sm, activeTrack).addNote(n);
						}
						else if (event.getTick() > 0) {
							onOffMessages.put(hash, event);
						}
						
					} 
					else if (sm.getCommand() == CONTROL_CHANGE) {
						// ignore
					}
					else if (sm.getCommand() == PROGRAMM_CHANGE) {
						System.out.println("Programm: " + sm.getData1() + " " + sm.getData2());
					}
					else {
						System.out.println("Command:" + sm.getCommand());
					}
				}
				else if (message instanceof MetaMessage) {
					parseMeta(song, message, activeTrack);
				}
				// Ignore SysexMessage
				// Ignore other messages
			}
			
			for(MidiEvent event: onOffMessages.values()) {
				ShortMessage sm = (ShortMessage) event.getMessage();
				int key = sm.getData1();
				MidiNote n = parseNote(key, event, null, sequence);
				getOrAddTrack(trackByChannel, sm, activeTrack).addNote(n);
			}
			
			// Meta track
			if(activeTrack.getNotes().isEmpty()) {
				song.setName(activeTrack.getName());
			}
			
			song.addTracks(trackByChannel.values());
		}
		
		assignClefs();
		
		
		return song;
	}

	@SuppressWarnings("unused")
	private void checkOverlaps() {
		for(MidiTrack track : song.getTracks()) {
			Map<Integer, List<MidiNote>> notesByKeys = new HashMap<>();
			for(MidiNote n : track.getNotes()) {
				if(notesByKeys.get(n.getKey()) == null) {
					notesByKeys.put(n.getKey(), new ArrayList<MidiNote>());
				}
				
				notesByKeys.get(n.getKey()).add(n);
			}
			
			for(List<MidiNote> t : notesByKeys.values()) {
				for(int i = 0; i < t.size(); i++) {
					MidiNote n = t.get(i);
					long end = n.getTimeUS() + n.getLengthUS();
					// Check the rest of the notes for overlap
					for(int j = i + 1; j < t.size(); j++) {
						if(t.get(j).getTimeUS() < end) {
							System.err.println("Overlapping note: " + n.getKey() + " " + n.getTimeUS());
						}
					}
				}
			}
		}
	}

	private void assignClefs() {
		// There might be more than Treble and Bass clefs
		// but I have time to implement only two of them
		// with very simple and naive algorithm
		
		List<MidiTrack> tracks = song.getTracks();
		for (MidiTrack t : tracks) {
			t.setClef(t.getAverageKey() < 60 ? MidiTrack.BASS_CLEF : MidiTrack.TREBLE_CLEF); 
		}
	}
	
	private void parseMeta(MidiSong song, MidiMessage message, MidiTrack activeTrack) {
		
		MetaMessage meta = (MetaMessage)message;
		int metaType = meta.getType();
		
		if (metaType == META_TEXT) {
			// Ignore
		}
		
		else if (metaType == META_COPY) {
			try {
				song.setCopy(new String(meta.getData(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		else if (metaType == META_TRACK_NAME) {
			if (activeTrack != null) {
				try {
					activeTrack.setName(new String(meta.getData(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		
		else if (metaType == META_INSTRUMENT) {
			//System.out.println("Instrument name: " + new String(meta.getData(), "UTF-8"));
		}
		
		else if (metaType == META_LYRICS) {
			// Ignore it, because most of the time it contains
			// just some links
		}
		
		else if (metaType == META_ENDOFTRACK) {
			// Do nothing
		}
		
		else if (metaType == META_TEMPO) {

			ByteBuffer buffer = (ByteBuffer)ByteBuffer.allocate(4)
					.put((byte)0)
					.put(meta.getData())
					.rewind();
			
			tempo = buffer.getInt();
			int bpm = MICROSECONDS_IN_MINUTE / tempo;

			song.setBpm(bpm);
			song.setTempo(tempo);
		}
		
		// It may appear in the midle of the track
		// but for now, I'll keep it consistent for the whole
		// song
		else if (metaType == META_TIMESIGNATURE) {
			
			int nom = Byte.toUnsignedInt(meta.getData()[0]);
			int timeSignatureDenom = 1 << Byte.toUnsignedInt(meta.getData()[1]);
			int metronomeTicks = Byte.toUnsignedInt(meta.getData()[2]);

			song.setTimeSignature(nom, timeSignatureDenom);
			song.setMetronome(metronomeTicks);
		}
		
		// It may appear in the midle of the track
		// but for now, I'll keep it consistent for the whole
		// song
		else if (metaType == META_KEYSIGNATURE) {
			int sharpflats = meta.getData()[0];
			boolean major = meta.getData()[0] == 0x00;
			
			String keySignature = major ? MAJOR_SCALES[sharpflats + 7] : MINOR_SCALES[sharpflats + 7];
			song.setKeySignature(keySignature, major);
		}
		
		// Some unsupported meta
	}

	private MidiTrack getOrAddTrack(Map<Integer, MidiTrack> trackByChannel, ShortMessage sm, MidiTrack activeTrack) {
		if(trackByChannel.get(sm.getChannel()) == null) {
			MidiTrack t = trackByChannel.isEmpty() ? activeTrack : new MidiTrack();
			t.setChannel(sm.getChannel());
			trackByChannel.put(sm.getChannel(), t);
		}
		return trackByChannel.get(sm.getChannel());
	}
	
	private MidiNote parseNote(int key, MidiEvent on, MidiEvent off, Sequence sequence) {
		
		long onus = tick2us(on.getTick());
		int length = this.resolution;
		
		if (off != null) {
			length = (int) (tick2us(off.getTick()) - onus);
		}
		
		int velocity = ((ShortMessage)on.getMessage()).getData2();
		MidiNote note = new MidiNote(key, velocity, onus, length);
		
		setNoteDuration(length, note);
		
		return note;
	}

	private long tick2us(long tick) {
		return (long) (tick * uSecPerTick);
	}

	/**
	 * Set music size of the note e.g. (quarter, half)
	 * */
	private void setNoteDuration(int length, MidiNote note) {
		length = (int) round(length);
		
		int whole = 0;
		int nom = 0;
		int denom = 1;
		int dot = 0;
		
		whole = (int) (length / fullNoteLength);
		length = length % fullNoteLength;

		while(length > 0) {
			denom *= 2;
			int l = fullNoteLength / denom; 
			
			nom = (int) (length / l);
			if (length % l > l * 0.75f) {
				nom +=1;
			}
			if (nom > 0 && denom <= 32) {
				int subDivision = fullNoteLength / (denom * 2);
				int tail = (int) (length - nom * l);
				
				if( tail >=  subDivision * 0.75f && tail <=  subDivision * 1.25f) {
					dot = 1;
				} 
				
				break;
			}
		}

		note.setDuration(whole, nom, denom, dot);
	}

	private long round(long length) {
		float grain = fullNoteLength / ROUNDING_GRAIN;
		return Math.round((float)length / grain ) * fullNoteLength / ROUNDING_GRAIN;
	}
}

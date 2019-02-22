package jthesia.midi.io;

public interface MidiOut {
	
	public void noteOn (int key, int velocity);
	public void noteOff(int key, int velocity);
	public void allOff();

	public void close();

}
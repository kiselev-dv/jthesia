package jthesia.midi.io;

import java.util.Objects;

public class MidiKey {

	private long timestamp;
	private long length = -1;
	
	private int key;
	private int velocity;

	public MidiKey(long timeStamp, int key, int velocity) {
		this.timestamp = timeStamp;
		this.key = key;
		this.velocity = velocity;
	}

	public int getKey() {
		return key;
	}

	public void close(long timeStamp2) {
		length = timeStamp2 - timestamp;
	}
	
	public long getLength() {
		return length;
	}
	
	public boolean isClosed() {
		return length >= 0;
	}
	
	public long getTimeStamp() {
		return timestamp;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(timestamp, length, key, velocity);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof MidiKey) {
			MidiKey other = (MidiKey) obj;
			
			return other.key == this.key 
					&& other.length == this.length 
					&& other.timestamp == this.timestamp
					&& other.velocity == this.velocity;
		}
		return super.equals(obj);
	}

}

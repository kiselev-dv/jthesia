package jthesia.game.components;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.jogamp.opengl.GL2;

import jthesia.game.MouseState;
import jthesia.graphics.Graphics;
import jthesia.graphics.UnitsConverter;
import jthesia.midi.io.MidiIn;
import jthesia.midi.io.MidiKey;

public class RecordFrame implements InteractiveFrame {

	private MidiIn midiIn;
	private LinkedHashSet<MidiKey> keys = new LinkedHashSet<>();
	private List<Sprite> notes = new ArrayList<>();
	
	private MidiKey first = null;
	private MidiKey last = null;
	
	private Scroller scroller;
	
	private float xOffset;
	private float keyHeight;
	private float scaleX;
	private float height;
	private float yOffset;

	public RecordFrame(MidiIn midiIn) {
		this.midiIn = midiIn;
		
		float widthUnits = UnitsConverter.INSTANCE.getWidthUnits();
		
		this.scroller = new Scroller(5000.0f, 0, 3, 100, 2.5f);
		this.scroller.setWindow(0, widthUnits / 0.02f);
	}

	@Override
	public void render(GL2 gl) {
		for(Sprite s : notes) {
			s.render(gl);
		}
		
		scroller.render(gl);
	}

	@Override
	public void screenReshape() {
		scroller.setDimensions(0, 3, 100, 2.5f);
		
		xOffset = 0;
		
		keyHeight = 2.0f;
		scaleX = 0.02f / 1000.0f;

		height = UnitsConverter.INSTANCE.getHeightUnits() * 0.75f;

		yOffset = height / 2 - 62 * keyHeight;
		
		float widthUnits = UnitsConverter.INSTANCE.getWidthUnits();
		// Window in ms
		scroller.setWindow(0, widthUnits / 0.02f);
	}

	@Override
	public boolean update(MouseState mouse, boolean select) {
		
		List<MidiKey> activeNotes = midiIn.getActiveNotes();
		keys.addAll(activeNotes);
		
		if (!activeNotes.isEmpty()) {
			if (first == null) {
				first = activeNotes.get(0);
			}
			
			last = activeNotes.get(activeNotes.size() - 1);
		}
		
		// TODO: Inefficient, but meh for now
		notes.clear();
		
		long timeOffset = 0;
		if (!keys.isEmpty()) {
			timeOffset = keys.iterator().next().getTimeStamp();
		}
		
		for (MidiKey k : keys) {
			
			if (k.isClosed()) {

				float x = (k.getTimeStamp() - timeOffset) * scaleX - xOffset;
				float y = height - k.getKey() * keyHeight - keyHeight - yOffset;
				
				float w = k.getLength() * scaleX;
				float h = keyHeight;
				
				notes.add(new Sprite(x, y, w, h) {
					@Override
					public void render(GL2 gl) {
						Graphics.setClor(Graphics.COLOR_PALETTE[0]);
						Graphics.fillRect(gl, x, y, w, h);
					}
					
				});
			}
			
		}
		
		scroller.setLenght(getLength() / 1000);
		
		return false;
	}
	
	public long getLength() {
		if (first != null && last != null) {
			return last.getTimeStamp() + last.getLength() - first.getTimeStamp();
		}
		return 0;
	}

}

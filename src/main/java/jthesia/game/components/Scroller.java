package jthesia.game.components;

import com.jogamp.opengl.GL2;

import jthesia.game.MouseState;
import jthesia.game.components.ProgressBar.MouseListener;
import jthesia.graphics.Graphics;

public class Scroller implements InteractiveFrame {

	private float length;
	private MouseListener mouseListener = null;
	
	// How much do we see
	private float window = 0;
	private float offset;

	private float x;

	private float y;

	private float width;

	private float height;
	

	public Scroller(float length, float x, float y, float width, float height) {
		this.length = length;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setWindow(float offset, float window) {
		this.offset = offset;
		this.window = window;
	}

	@Override
	public void render(GL2 gl) {
		Graphics.setClor(Graphics.COLOR_PALETTE[4]);
		Graphics.fillRect(gl, x, y, width, height);
		
		if (this.length > 0) {
			float dx = this.offset / this.length * this.width;
			float w = this.window / this.length * this.width;
			
			w = Math.max(w, 5);
			if (dx + w > this.width) {
				w = this.width - dx;
			}
			
			Graphics.setClor(Graphics.COLOR_PALETTE_BRIGHT[4]);
			Graphics.fillRect(gl, x + dx, y, w, height);
			
			Graphics.setClor(Graphics.TEXT_COLOR);
			Graphics.drawText(gl, String.format("w %.0f, wnd: %.0f, len %.0f, wdh: %.0f", 
					w, window, length, width), 15, 0);
		}
	}

	@Override
	public void screenReshape() {
		
	}

	@Override
	public boolean update(MouseState mouse, boolean select) {
		if (mouse.down && this.mouseListener != null) {
			if (mouse.x > x && mouse.x < x + width 
					&& mouse.y > y && mouse.y < y + height) {
				
				float clickPosition = ((mouse.x - x) / width) * length;
				this.mouseListener.mouseDown(clickPosition);
				
				return true;
			}
		}
		
		return false;
	}

	public void setLenght(float l) {
		this.length = l;
	}
	
	public void setDimensions(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public void setMouseListener(MouseListener listener) {
		this.mouseListener = listener;
	}
	
}

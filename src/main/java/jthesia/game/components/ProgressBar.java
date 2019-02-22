package jthesia.game.components;

import com.jogamp.opengl.GL2;

import jthesia.game.MouseState;
import jthesia.graphics.Graphics;

public class ProgressBar implements InteractiveFrame {

	private float state = 0;
	
	protected float x;
	protected float y;
	protected float width;
	protected float height;

	protected float length;

	protected float position;
	
	private MouseListener mouseListener = null;
	
	public static interface MouseListener {
		public void mouseDown(float length);
	}
	
	public ProgressBar(float length, 
			float x, float y, float width, float height) {
		
		this.length = length;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void setMouseListener(MouseListener listener) {
		this.mouseListener = listener;
	}
	
	public void setPosition(float position) {
		this.position = position;
	}
	
	public void setLenght(float length) {
		this.length = length;
	}
	
	public void setDimensions(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	@Override
	public void render(GL2 gl) {
		Graphics.setClor(Graphics.COLOR_PALETTE[4]);
		Graphics.fillRect(gl, x, y, width, height);
		
		Graphics.setClor(Graphics.COLOR_PALETTE_BRIGHT[4]);
		Graphics.fillRect(gl, x, y, width * state, height);
	}

	@Override
	public void screenReshape() {
		
	}

	@Override
	public boolean update(MouseState mouse, boolean select) {
		
		if (this.length > 0) {
			this.state = this.position / this.length;
		}

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

}

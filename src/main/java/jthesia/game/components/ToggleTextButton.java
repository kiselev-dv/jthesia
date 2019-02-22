package jthesia.game.components;

import com.jogamp.opengl.GL2;

import jthesia.graphics.Graphics;

public class ToggleTextButton extends TextButton {

	private boolean state;
	private float[] colorSelected;

	public ToggleTextButton(boolean state, String label, 
			float labelX, float labelY, 
			float[] color, 
			float x, float y, float w, float h) {
		
		super(label, labelX, labelY, new float[] {0, 0, 0, 0}, x, y, w, h);
		
		this.colorSelected = new float[] {color[0] * 1.5f, color[1] * 1.5f, color[2] * 1.5f, color[3]};
		this.state = state;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	@Override
	public void render(GL2 gl) {
		super.render(gl);
		
		if (state) {
			Graphics.setClor(colorSelected);
			Graphics.fillRect(gl, x, y + h * 0.9f, w, h * 0.1f);
		}
	}
	
	@Override
	public void onClick() {
		this.state = ! this.state;
	}
}

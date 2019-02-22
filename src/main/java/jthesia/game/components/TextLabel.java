package jthesia.game.components;

import com.jogamp.opengl.GL2;

import jthesia.graphics.Graphics;

public class TextLabel extends Sprite {

	private String text;

	public TextLabel(String text, float x, float y, float w, float h) {
		super(x, y, w, h);
		if(text == null) {
			throw new NullPointerException("text is null");
		}
		this.text = text;
	}

	@Override
	public void render(GL2 gl) {
		Graphics.setScissorBox(gl, x, y, w, h);
		
		Graphics.setClor(Graphics.TEXT_COLOR);
		Graphics.drawText(gl, text, x, y);
		
		Graphics.resetScissorBox(gl);
	}

}

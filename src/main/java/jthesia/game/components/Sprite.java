package jthesia.game.components;

import com.jogamp.opengl.GLProfile;

import jthesia.game.MouseState;

public abstract class Sprite implements InteractiveFrame {

	protected float x;
	protected float y;
	protected float w;
	protected float h;

	public Sprite(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	
	/**
	 * Load resources
	 * */
	public void load(GLProfile glProfile) {
		
	}
	
	/**
	 * Screen has been reshaped
	 * */
	public void screenReshape() {
		
	}
	
	public boolean update(MouseState mouse, boolean select) {
		return false;
	}
	
}
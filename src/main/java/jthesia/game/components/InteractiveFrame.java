package jthesia.game.components;

import com.jogamp.opengl.GL2;

import jthesia.game.MouseState;

public interface InteractiveFrame {
	
	public void render(GL2 gl);
	
	public void screenReshape();
	
	public boolean update(MouseState mouse, boolean select);

}

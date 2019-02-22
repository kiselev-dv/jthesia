package jthesia.graphics;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import jthesia.game.engine.Game;

public class EventListener implements GLEventListener {
	
	private static final UnitsConverter UC = UnitsConverter.INSTANCE;
	private boolean renderReady = false;
	
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		
		if (!renderReady) {
			Game.INSTANCE.renderReady(gl);
			this.renderReady = true;
		}
		
		Game.INSTANCE.render(gl);
		
		gl.glFlush();
	}

	public void dispose(GLAutoDrawable drawable) {
		// called once at the end
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0, 0, 0, 1);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GL2.GL_SCISSOR_BOX);
		gl.glEnable(GL2.GL_SCISSOR_TEST);
		
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		if (!renderReady) {
			Game.INSTANCE.renderReady(gl);
			this.renderReady = true;
		}
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		
		UC.update(width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glOrtho(0, UC.getWidthUnits(), UC.getHeightUnits(), 0.0, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		Graphics.resetScissorBox(gl);
		
		Game.INSTANCE.reshape(UC.getWidthUnits(), UC.getHeightUnits());
	}

}

package jthesia.game.scenes;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;

import jthesia.game.MouseState;
import jthesia.game.components.InteractiveFrame;

public abstract class AScene implements Scene {

	protected float width;
	protected float height;
	
	protected List<InteractiveFrame> renderable = new ArrayList<InteractiveFrame>();
	
	protected Scene nextScene;
	
	/**
	 * Render all renderable descendants
	 * */
	public void render(GL2 gl) {
		for (InteractiveFrame r : renderable) {
			r.render(gl);
		}
	}
	
	/**
	 * Update the state
	 * */
	public final SceneStateUpdate update(MouseState mouseState) {
		
		for(InteractiveFrame frame : renderable) {
			frame.update(mouseState, false);
		}
		
		if (nextScene != null) {
			return new SceneStateUpdate(nextScene);
		}
		
		return onUpdate(mouseState);
	}
	
	/**
	 * For overriding in child
	 * */
	protected SceneStateUpdate onUpdate(MouseState mouseState) {
		return null;
	}

	/**
	 * Override to load resources which 
	 * requires glProfile such as textures
	 * */
	public abstract void load(GLProfile glProfile);
	
	/**
	 * Changing the sizes
	 * */
	public void reshape(float width, float height) {
		this.width = width;
		this.height = height;
		
		for(InteractiveFrame frame : renderable) {
			frame.screenReshape();
		}
	}

	/**
	 * Called before game ends
	 * */
	public void destroy() {

	}
	
	/**
	 * Override to close resources
	 * 
	 * Called before scene will be switched
	 * over to new scene
	 * */
	public void endScene() {

	}
	
	/**
	 * Add frame for rendering / update
	 * */
	protected void addFrame(InteractiveFrame frame) {
		if (frame != null) {
			renderable.add(frame);
		}
		else {
			throw new NullPointerException();
		}
	}
	
	/**
	 * Change the scene
	 * */
	protected void goToNewScene(Scene scene) {
		nextScene = scene;
	}
	
}

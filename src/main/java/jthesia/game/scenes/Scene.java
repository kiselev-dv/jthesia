package jthesia.game.scenes;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;

import jthesia.game.MouseState;

public interface Scene {

	SceneStateUpdate update(MouseState mouseState);

	void render(GL2 gl);

	void load(GLProfile glProfile);

	void reshape(float width, float height);

	/**
	 * Called before game ends
	 * 
	 * free resources
	 * */
	void destroy();

	/**
	 * Called before scene will be switched
	 * over to new scene
	 * */
	void endScene();

}
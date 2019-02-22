package jthesia.game.engine;

import java.io.File;

import javax.sound.midi.Sequence;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;

import jthesia.dao.DAO;
import jthesia.game.MouseState;
import jthesia.game.scenes.MusicLibScene;
import jthesia.game.scenes.Scene;
import jthesia.game.scenes.SceneStateUpdate;
import jthesia.graphics.Graphics;
import jthesia.resource.Loader;
import jthesia.ui.MouseInput;

public class Game {
	
	public static Game INSTANCE = new Game();
	
	private volatile boolean over = false;
	private Scene curentScene = null;
	
	private GLProfile glProfile;

	private float width = 100;
	private float height = 60;

	private MouseInput mouseInput = new MouseInput();
	private KeyboardInput keyboardInput = new KeyboardInput();
	
	private Scene newScene;
	
	private Game() {
		// Initialize DB connection
		DAO instance = DAO.INSTANCE;
	}

	/**
	 * Render pipeline initialization callback
	 * */
	public void renderReady(GL2 gl) {
		glProfile = gl.getGLProfile();
		
		if (curentScene != null) {
			curentScene.load(glProfile);
		}
	}

	/**
	 * Update game state
	 * */
	public void update() {
		
		if (newScene != null) {
			curentScene = newScene;
			curentScene.reshape(width, height);
			curentScene.load(glProfile);
			
			newScene = null;
		}
		
		if (curentScene != null) {
			MouseState mouseState = this.mouseInput.getMouseState();

			SceneStateUpdate sceneUpdateState = curentScene.update(mouseState);
			if(sceneUpdateState != null) {
				if (sceneUpdateState.getRedirectScene() != null) {
					newScene = sceneUpdateState.getRedirectScene();
					curentScene.endScene();
					
					// Don't accidently call render on closed scene
					// shouldn't be an issue,
					// but to stay on the safe side
					curentScene = null;
				}
				
				if (sceneUpdateState.isGameOver()) {
					this.end();
				}
			}

			mouseState.reset();
		}
		
	}
	
	/**
	 * Render the state
	 * */
	public void render(GL2 gl) {
		if (curentScene != null) {
			curentScene.render(gl);
		}
	}

	/**
	 * Is the Game over ?
	 * */
	public boolean isOver() {
		return over;
	}
	
	/**
	 * End the game
	 * */
	public void end() {
		if(this.curentScene != null) {
			curentScene.destroy();
		}
		
		Loader.destroy();
		Graphics.destroy();
		DAO.INSTANCE.close();
		
		this.over = true;
	}

	/**
	 * Load midi sequence
	 * */
	public void loadMidiSequence(Sequence sequence) {
		curentScene = new MusicLibScene();
		curentScene.reshape(width, height);
	}
	
	/**
	 * Change width and height
	 * @param widthUnits - width in game units
	 * @param heightUnits - height in game units
	 * */
	public void reshape(float widthUnits, float heightUnits) {
		this.width = widthUnits;
		this.height = heightUnits;
		
		if (curentScene != null) {
			curentScene.reshape(width, height);
		}
	}

	public MouseInput createMouseInput() {
		return this.mouseInput;
	}

	public static File[] getDownloadFolders() {
		File homeDir = new File(System.getProperty("user.home"));
		return new File[] {new File(homeDir, "Downloads")};
	}

	public KeyListener createKeyboardInput() {
		return keyboardInput;
	}

}

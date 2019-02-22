package jthesia.graphics;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

import jthesia.ui.MouseInput;
import jthesia.ui.OnWindowClose;
import jthesia.ui.WindowCloseListener;

public class Renderer {
	
	private static GLWindow window;
	
	private static int screen_width = 640;
	private static int screen_height = 480;
	
	private final GLProfile profile;
	
	public Renderer(boolean fullScreen) {
		GLProfile.initSingleton();
		profile = GLProfile.get(GLProfile.GL2);
		
		GLCapabilities capabilities = new GLCapabilities(profile);
		window = GLWindow.create(capabilities);

		window.addGLEventListener(new EventListener());
		window.setSize(screen_width, screen_height);
		
		window.setFullscreen(fullScreen);
	}
	
	public GLProfile getProfile() {
		return profile;
	}

	public void render() {
		window.display();
	}
	
	public void addWindowCloseListener(OnWindowClose l) {
		window.addWindowListener(new WindowCloseListener(l));
	}

	public void showWindow() {
		window.setVisible(true);
		window.requestFocus();
	}
	
	public void addWindowMouseListener(MouseInput input) {
		window.addMouseListener(input);
	}

	public void addKeyListener(KeyListener listener) {
		window.addKeyListener(listener);
	}
}

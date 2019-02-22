package jthesia.game.engine;

import com.jogamp.newt.event.WindowEvent;

import jthesia.graphics.Renderer;
import jthesia.ui.OnWindowClose;

public class GameLoop extends Thread {

	private Renderer renderer;
	
	private static final boolean CAP_FPS = true;
	
	private static final int targetFps = 60;
	private static final int targetFrameTimeNS = 1000000000 / targetFps;
	private Game game;
	
	public GameLoop(Game game) {
		this.game = game;
		this.setName("GameLoop");
	}
	
	@Override
	public void run() {

		renderer = new Renderer(false);
		
		renderer.addWindowMouseListener(this.game.createMouseInput());
		renderer.addKeyListener(this.game.createKeyboardInput());
		
		renderer.addWindowCloseListener(new OnWindowClose() {
			public void windowClosed(WindowEvent e) {
				game.end();
			}
		});
		
		this.renderer.showWindow();
		
		System.out.println("Start game loop");
		
		while(!game.isOver()) {
			long cycleStart = System.nanoTime();
			
			game.update();
			
			// Render calls window display, 
			// which calls GLEvent Listener
			// which will call game render
			renderer.render();
			
			long cycleTime = System.nanoTime() - cycleStart;
			
			if (cycleTime < targetFrameTimeNS && CAP_FPS) {
				try {
					Thread.sleep((targetFrameTimeNS - cycleTime) / 1000000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
}

package jthesia.game.scenes;

public class SceneStateUpdate {
	
	private boolean gameOver = false;
	private Scene redirectScene;

	public SceneStateUpdate() {
		
	}
	
	public SceneStateUpdate(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public boolean isGameOver() {
		return gameOver;
	}
	
	public SceneStateUpdate(Scene redirectScene) {
		this.redirectScene = redirectScene;
	}

	public Scene getRedirectScene() {
		return redirectScene;
	}
}

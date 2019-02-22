package jthesia.game;

public final class Clock {

	private long previousTick = System.currentTimeMillis();
	
	private long counter = 0;

	private boolean paused;

	public void resume() {
		paused = false;
	}

	public void pause() {
		paused = true;
	}
	
	public void toggle() {
		if(paused) {
			resume();
		}
		else {
			pause();
		}
	}
	
	public long getGameClock() {
		counter += getTick();
		return counter;
	}
	
	public void start() {
		previousTick = System.currentTimeMillis();
	}
	
	/**
	 * Tick time in millisecond
	 * */
	public final long getTick() {
		long t = getTickInternal();
		return paused ? 0 : t;
	}

	private long getTickInternal() {
		long now = System.currentTimeMillis();
		long tick = now - previousTick;
		previousTick = now;
		return tick;
	}
}

package jthesia;

import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import jthesia.game.engine.Game;
import jthesia.game.engine.GameLoop;

public class JThesia {

	public static void main(String[] args) {
		try {
			Sequence sequence = MidiSystem.getSequence(new File(args[0]));

			final Game game = Game.INSTANCE;
			final GameLoop gameLoop = new GameLoop(game);
			gameLoop.start();
			
			game.loadMidiSequence(sequence);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getBaseDir() {
		return System.getProperty("user.dir");
	}
	
}

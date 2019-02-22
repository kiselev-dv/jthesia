package jthesia.resource;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sounds {
	
	public static Clip MENU2 = loadClipResource("menu2");
	
	public static Clip loadClipResource(String name) {
		
		try {
			Clip clip = AudioSystem.getClip(null);
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(
					Sounds.class.getResourceAsStream("/sounds/" + name + ".wav"));
			clip.open(inputStream);
			
			return clip;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}

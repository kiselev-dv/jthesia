package jthesia.game.components;

import javax.sound.sampled.Clip;

import jthesia.game.MouseState;

public abstract class ButtonSprite extends Sprite {

	private boolean hovered;
	private Clip sound;
	
	public ButtonSprite(float x, float y, float w, float h) {
		super(x, y, w, h);
	}

	/**
	 * @param mouse - mouse state
	 * @param select - forcefully select sprite
	 * 
	 * @return true if event is captured
	 * */
	public boolean update(MouseState mouse, boolean select) {
		
		if (mouse.x > x && mouse.x < x + w 
				&& mouse.y > y && mouse.y < y + h) {
			
			if (mouse.down) {
				onMouseDown();
			}
				
			if (mouse.click) {
				onClick();
			}

			if(!this.hovered) {
				_onMouseIn();
			}
			
			this.hovered = true;
			
			// Stop propagation
			return true;
		}
		else if(this.hovered) {
			this.hovered = false;
			onMouseOut();
		}
		
		return false;
	}
	
	private void _onMouseIn() {
		if (this.sound != null) {
			this.sound.stop();
			this.sound.setFramePosition(0);
			this.sound.start();
		}
		onMouseIn();
	}

	public void attachHoverSound(Clip sound) {
		this.sound = sound;
	}

	public void onMouseIn() {
		
	};
	
	public void onMouseOut() {
		
	};
	
	public void onClick() {
		
	};
	
	public void onMouseDown() {
		
	};

}

package jthesia.ui;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

import jthesia.game.MouseState;
import jthesia.graphics.UnitsConverter;

public class MouseInput implements MouseListener {

	private static final UnitsConverter UC = UnitsConverter.INSTANCE;
	private MouseState state = new MouseState();
	
	public void mouseClicked(MouseEvent evt) {
		state.click = true;
		
		state.x = UC.toUnitsX(evt.getX());
		state.y = UC.toUnitsY(evt.getY());
	}

	public void mouseDragged(MouseEvent evt) {
		state.x = UC.toUnitsX(evt.getX());
		state.y = UC.toUnitsY(evt.getY());
	}

	public void mouseEntered(MouseEvent evt) {
		state.x = UC.toUnitsX(evt.getX());
		state.y = UC.toUnitsY(evt.getY());
	}

	public void mouseExited(MouseEvent evt) {
		
	}

	public void mouseMoved(MouseEvent evt) {
		state.x = UC.toUnitsX(evt.getX());
		state.y = UC.toUnitsY(evt.getY());
	}

	public void mousePressed(MouseEvent evt) {
		state.down = true;
		
		state.x = UC.toUnitsX(evt.getX());
		state.y = UC.toUnitsY(evt.getY());
	}

	public void mouseReleased(MouseEvent evt) {
		state.down = false;
		
		state.x = UC.toUnitsX(evt.getX());
		state.y = UC.toUnitsY(evt.getY());
	}

	public void mouseWheelMoved(MouseEvent evt) {
		
	}

	public MouseState getMouseState() {
		return state;
	}

}

package jthesia.ui;

import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;

public class WindowCloseListener implements WindowListener {
	
	private OnWindowClose callback;

	public WindowCloseListener(OnWindowClose callback) {
		this.callback = callback;
	}

	public void windowResized(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowMoved(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowDestroyNotify(WindowEvent e) {
	}

	public void windowDestroyed(WindowEvent e) {
		this.callback.windowClosed(e);
	}

	public void windowGainedFocus(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowLostFocus(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowRepaint(WindowUpdateEvent e) {
		// TODO Auto-generated method stub
		
	}

}

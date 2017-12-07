import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.Dimension;

/**
 * Listeners for the JuliaComponent class
 * @author Eric K. Zhang
 */
public class JuliaListener implements MouseListener, MouseMotionListener,
                                      ComponentListener {

	private JuliaComponent cpnt;

	public JuliaListener(JuliaComponent cpnt) {
		this.cpnt = cpnt;
		cpnt.addMouseListener(this);
		cpnt.addMouseMotionListener(this);
		cpnt.addComponentListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		cpnt.onClick(e.getX(), e.getY());
	}

	public void mouseMoved(MouseEvent e) {
		cpnt.onMove(e.getX(), e.getY());
	}

	public void mouseEntered(MouseEvent e) {
		mouseMoved(e);
	}

	public void mouseExited(MouseEvent e) {
		cpnt.onMove(-1, -1);
	}

	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
		mouseClicked(e);
	}

	public void componentResized(ComponentEvent e) {
		cpnt.regenerateImage();
	}

	public void mouseReleased(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
}

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.Dimension;

/**
 * Listener for the main viewer frame
 * @author Eric K. Zhang
 */
public class ResizeListener implements ComponentListener {
	public void componentResized(ComponentEvent e) {
		JFrame frame = (JFrame) e.getComponent();
		Dimension r = frame.getSize();
		frame.setSize(Math.max(r.width, r.height), r.height);
	}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
}

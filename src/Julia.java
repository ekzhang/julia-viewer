import javax.swing.*;
import java.awt.Dimension;

/**
 * Julia.java
 *
 * Viewer that allows the user to interact with Julia sets.
 *
 * @author Eric K. Zhang
 * December 6, 2017
 */
public class Julia {
	/** frame dimensions */
	private static final int FRAME_WIDTH = 640;
	private static final int FRAME_HEIGHT = 480;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Julia Set Viewer");
		frame.addComponentListener(new ResizeListener());
		frame.add(new JuliaComponent(-0.756, -0.245));
		frame.setMinimumSize(new Dimension(320, 320));
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

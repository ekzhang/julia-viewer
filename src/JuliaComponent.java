import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JComponent;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Component for displaying Julia sets
 * @author Eric K. Zhang
 */
public class JuliaComponent extends JComponent
{
	private static final double YRANGE = 1.5;
	private static final int NUM_THREADS = 4;

	private static final double[] CONTROL_X = {0.0, 0.16, 0.42, 0.6425, 0.8525, 1.0};
	private static final Color[] CONTROL_Y = {
		new Color(0, 7, 100),
		new Color(32, 107, 203),
		new Color(237, 255, 255),
		new Color(255, 170, 0),
		new Color(0, 2, 0),
		new Color(0, 7, 100)
	};
	private static final int NUM_COLORS = 1024;
	private static final Color[] PALETTE = new Color[NUM_COLORS];

	private static Color average(Color c1, Color c2, double w) {
		int r = (int) (c2.getRed() * w + c1.getRed() * (1 - w));
		int g = (int) (c2.getGreen() * w + c1.getGreen() * (1 - w));
		int b = (int) (c2.getBlue() * w + c1.getBlue() * (1 - w));
		return new Color(r, g, b);
	}

	/** Returns a color for each normalized data point of the Julia set.
	 *  @param x the number of function iterations before a point blows up to
	 *           infinity (or obtained using some continuous coloring formula)
	 *  @return a color from the palette for x
	 */
	private static Color cmap(double x) {
		for (int i = 0; i < CONTROL_X.length; i++) {
			if (x < CONTROL_X[i]) {
				double w = (x - CONTROL_X[i - 1]) / (CONTROL_X[i] - CONTROL_X[i - 1]);
				return average(CONTROL_Y[i - 1], CONTROL_Y[i], w);
			}
		}
		throw new IllegalArgumentException("invalid number input");
	}

	static {
		for (int i = 0; i < NUM_COLORS; i++) {
			double d = 1.0 * i / NUM_COLORS;
			PALETTE[i] = cmap(d);
		}
	}

	private Graphics2D g2;
	private int W;
	private int H;
	private double cx, cy;
	private double mx, my;
	private double[][] data;
	private BufferedImage im;

	public JuliaComponent(double cx, double cy) {
		super();
		this.cx = this.mx = cx;
		this.cy = this.my = cy;
		JuliaListener listener = new JuliaListener(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		// Convert g back to its Graphics2D self
		g2 = (Graphics2D) g;

		// Get size of component window
		W = getWidth();
		H = getHeight();

		// Generate the Julia set image
		if (im == null) {
			generateSet();
		}

		// Draw the Julia set and location display
		drawBackground();
		g2.setColor(Color.WHITE);
		g2.drawString(String.format("c = %.3f + %.3fi", mx, my), 20, 30);
	}

	public void onClick(int x, int y) {
		cx = getX(x);
		cy = getY(y);
		regenerateImage();
		repaint();
	}

	public void onMove(int x, int y) {
		if (x == -1) {
			mx = cx;
			my = cy;
		}
		else {
			mx = getX(x);
			my = getY(y);
		}
		repaint();
	}

	public void regenerateImage() {
		im = null;
		data = null;
	}

	private double getX(int xc) {
		return YRANGE * (2.0 * xc / (W - 1) - 1) * W / H;
	}

	private double getY(int yc) {
		return -YRANGE * (2.0 * yc / (H - 1) - 1);
	}

	private void normalize() {
		double avg = 0;
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				if (Math.abs(i - W / 2) <= H / 2)
					avg += data[i][j];
			}
		}
		avg /= H * H;
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				data[i][j] /= avg * 15;
			}
		}
	}

	private void generateSet() {
		data = new double[W][H];
		im = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
		Thread[] threads = new Thread[NUM_THREADS];
		int half = (W + 1) / 2;
		for (int i = 0; i < NUM_THREADS; i++) {
			threads[i] = new JuliaWorker(half * i / NUM_THREADS,
			                             half * (i + 1) / NUM_THREADS);
			threads[i].run();
		}
		for (int i = half; i < W; i++) {
			for (int j = 0; j < H; j++) {
				data[i][j] = data[W - 1 - i][H - 1 - j];
			}
		}
		for (int i = 0; i < NUM_THREADS; i++) {
			try {
				threads[i].join();
			}
			catch (InterruptedException exc) {
				System.err.println("ERROR: InterruptedException");
				exc.printStackTrace();
			}
		}
		normalize();
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				Color c = PALETTE[(int) ((data[i][j] % 1) * NUM_COLORS)];
				im.setRGB(i, j, c.getRGB());
			}
		}
	}

	private void drawBackground() {
		g2.drawImage(im, 0, 0, null);
	}


	/**
	 * Worker thread for doing the Julia set calculations
	 * @author Eric K. Zhang
	 */
	private class JuliaWorker extends Thread {
		private static final int MAX_ITER = 256;

		private int minw, maxw;

		JuliaWorker(int minw, int maxw) {
			this.minw = minw;
			this.maxw = maxw;
		}

		private double getIterations(double x, double y) {
			double ret = 0;
			double mag = 0;
			for (int iter = 0; iter < MAX_ITER && mag < 100; iter++) {
				double x1 = x * x - y * y + cx;
				double y1 = 2 * x * y + cy;
				x = x1;
				y = y1;
				mag = x * x + y * y;
				ret += Math.exp(-Math.sqrt(mag));
			}
			return ret;
		}

		public void run() {
			for (int i = minw; i < maxw; i++) {
				for (int j = 0; j < H; j++) {
					double x = getX(i);
					double y = getY(j);
					double d = 1.0 * getIterations(x, y) / MAX_ITER;
					data[i][j] = d;
				}
			}
		}
	}
}

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Worker thread for doing the Julia set calculations
 * @author Eric K. Zhang
 */
public class JuliaWorker extends Thread {

	private static final double YRANGE = JuliaComponent.YRANGE;
	private static final int MAX_ITER = 256;

	private double[][] im;
	private int W, H, minw, maxw;
	private double cx, cy;

	public JuliaWorker(double[][] im, double cx, double cy, int W, int H, int minw, int maxw) {
		this.im = im;
		this.W = W;
		this.H = H;
		this.minw = minw;
		this.maxw = maxw;
		this.cx = cx;
		this.cy = cy;
	}

	private double getX(int xc) {
		return YRANGE * (2.0 * xc / W - 1) * W / H;
	}

	private double getY(int yc) {
		return YRANGE * (2.0 * yc / H - 1);
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
				im[i][j] = d;
			}
		}
	}
}

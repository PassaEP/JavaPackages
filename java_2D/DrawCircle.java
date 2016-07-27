import java.awt.*;
import java.util.*;

public class DrawCircle extends Thread {

   static Random rdm = new Random();
   Graphics2D	grph;
   int		width, height;
   int		x, y, maxRad, minRad;
   int		step;

   public DrawCircle ( Graphics g, int w, int h ) {
	grph = (Graphics2D) g; width = w; height = h;
	x = rdm.nextInt(width) % width; 
	y = rdm.nextInt(height) % height; 
	step = rdm.nextInt( 3 ) + 1;
	minRad = rdm.nextInt( 20 ) + 20 ;
	maxRad = rdm.nextInt( 100 ) + 100 ;
   }

   public void run() {

	grph.setColor( Color.blue );
	int rad = minRad;
	int dir = 1;

	// grph.setXORMode( new Color (
	grph.setColor( new Color ( rdm.nextInt(156), rdm.nextInt(256), rdm.nextInt(256) ) );

	grph.drawOval(x - rad/2, y - rad/2, rad, rad);
	while (true ) {
		try { Thread.sleep(100); } catch (InterruptedException e) {}
		grph.drawOval(x - rad/2, y - rad/2, rad, rad);
		if (rad > maxRad || rad < minRad ) step *= -1;
		rad += step;
		grph.drawOval(x - rad/2, y - rad/2, rad, rad);
	}
   }
}

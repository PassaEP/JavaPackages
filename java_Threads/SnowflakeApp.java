/** Snowflake: Example of using rotation, scale, shear, and translation.
*/

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.applet.*;
import java.awt.geom.*;

public class SnowflakeApp extends JApplet {
  JPanel pn = new JPanel();

   public void init() {
	pn.setBackground(Color.black);
	getContentPane().add(pn);
	setVisible(true);

   }

   public void start() {
	for ( int i = 0; i < 50; i ++ ) { // Threads for snowflake.
		new SnowflakeThread(pn).start();
		try { Thread.sleep(100); } catch (InterruptedException e ) {}
	}
	new MerryChristmas(pn).start(); // Thread for printing msg.
   }

}

class SnowflakeThread extends Thread {

    int x[] = {10,  4,  8,  2,   0, -2, -8, -4, -10, -4, -8, -2,  0, 2, 8, 4};
    int y[] = { 0, -2, -8, -4, -10, -4, -8, -2,   0,  2,  8,  4, 10, 4, 8, 2};

    private JPanel p = null ;
    private double PI = 3.14159;
    int w, h, nextX = 0, nextY = 0;
    Random rnd = new Random();
    public SnowflakeThread( JPanel pn ) {
	p = pn ;
	w = pn.getWidth(); h = pn.getHeight();
    }

    public void run() {
	AffineTransform tfm = AffineTransform.getTranslateInstance(0,0);
	double t = Math.abs(Math.random() - 0.5) + 0.1;
	tfm.scale(t, t);
//	tfm.rotate(PI * Math.random()) ;
	tfm.translate(w * Math.random()/t, h * Math.random());  // Notice the translate is cumutive.
	Graphics2D g = (Graphics2D) p.getGraphics();
	g.setXORMode(Color.white);
	while ( true ) {
		g.setTransform(tfm);
		g.fill( new Polygon( x, y, 16));
		try { Thread.sleep(200); } catch(InterruptedException e) {}
		g.fill( new Polygon( x, y, 16)); // erase it

		nextX = rnd.nextInt() % 5 ; // shift 10 pixel left or right.
		nextY = (int) (Math.random() * 20/t) ; // at most 10 pixel down.
//		nextX = nextX<0 ? 0: nextX>w ? w:nextX;
		if ( tfm.getTranslateY() > h ) nextY = - (int) (h/t);
		tfm.translate(nextX, nextY);
	}
     }
}

 class MerryChristmas extends Thread {
    String s = "Merry", s2 = "Christmas";
    private JPanel p = null ;
    int w, h, sw ;
    Graphics2D g = null; 
    public MerryChristmas(JPanel pn) {
	p = pn ; w = p.getWidth(); h = p.getHeight();
	g = (Graphics2D) p.getGraphics();
	g.setFont(new Font("Roman", Font.BOLD, 20));
	sw = g.getFontMetrics().stringWidth(s2);
    }

    public void run() {
	g.setXORMode(Color.magenta);
	int x = w, whichGreeting = 1;
	try { Thread.sleep(1000); } catch (InterruptedException e) {;}
	while ( true ) {
		g.drawString(s, x+20, 30) ; 
		g.drawString(s2, x, 50) ; 
		try { Thread.sleep(100); } catch (InterruptedException e) { }
		g.drawString(s, x+20, 30) ; 
		g.drawString(s2, x, 50) ; 
		x -= 5;
		if ( x < -sw ) {
			x = w;		
			if ( whichGreeting == 1 ) { s = "Happy"; s2 = "New Year"; }
			else  { s = "Merry"; s2 = "Christmas"; }
			whichGreeting = (whichGreeting+1) % 2;
		}
	}
   }
}
		
	
	
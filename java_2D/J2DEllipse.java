/** 
*/
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class J2DEllipse extends CloseableJFrame {

   Container c = null;
   Graphics2D g2d;
   ExclusiveDraw xDraw;
   Canvas drawingPane = new Canvas();

   public J2DEllipse() {
	c = getContentPane();
	c.add(drawingPane);
	setSize(300, 200);
	setVisible(true);
   }

   public static void main( String args[] ) {
	J2DEllipse f = new J2DEllipse();
	f.xDraw = new ExclusiveDraw(f.drawingPane);
	try { Thread.sleep(200); } catch (InterruptedException e) {}
	for ( int i = 0; i < 30; i ++ ) {
		new DrawCircle(i, f.drawingPane, f.xDraw).start();
	}
   }
}

class DrawCircle extends Thread {
   int myNumber;
   Canvas pane;
   int x, y, cnt, sleepLen;
   Color c;
   ExclusiveDraw xDraw;

   public DrawCircle( int i, Canvas cn, ExclusiveDraw exd ) {
	myNumber = i; pane = cn; xDraw = exd ;
   }

   void randomize() {
	x = (int ) (Math.random() * pane.getWidth());
	y = (int ) (Math.random() * pane.getHeight());
	cnt = (int ) (Math.random() * 50 + 20);
	sleepLen = (int ) (Math.random() * 50 + 50);
	c = new Color ( (int) ( Math.random() * 256),
			(int) (Math.random() * 256), (int) (Math.random() * 256));
   }

   public void run() {
	while ( true ) {
	    randomize();
	    for ( int i = 0; i < cnt; i++ )  {
		xDraw.draw(myNumber, c, x, y, i + 5, i + 5);
		try { sleep(sleepLen); } catch( InterruptedException e) {}
		xDraw.draw(myNumber, Color.black, x, y, i + 5, i + 5);
	    }
	    try { sleep(1000); } catch( InterruptedException e) {}
	}
   }
}

class ExclusiveDraw {
   Canvas pane ;
   Graphics2D g2d;

   ExclusiveDraw( Canvas c ) {
	pane = c; 
	pane.setBackground(Color.black);
	g2d = (Graphics2D) pane.getGraphics();
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setStroke(new BasicStroke(2.0f));
   }

   public  synchronized void draw (int tNum, Color c, int x, int y, int w, int h)
   {
	// g2d.setXORMode(c);  // The quality of picture is bad if XOR mode is used.
	g2d.setColor(c);
	g2d.drawOval(x - w/2, y - h/2, w, h );
	// g2d.draw(new Ellipse2D.Double(x - w/2, y - h/2, w, h));
   }
}

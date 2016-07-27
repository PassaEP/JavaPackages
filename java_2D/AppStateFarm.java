/** .

*/

import java.applet.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.*;

public class AppStateFarm extends JApplet {
    JPanel drawingPane = new JPanel();
    String msg = "State Farm, A Good Neighbor!";

    public void init( ) {
	getContentPane().setLayout(new BorderLayout());
	drawingPane.setSize(400,300);
	getContentPane().add(drawingPane, BorderLayout.CENTER);
	drawingPane.setBackground(Color.black);

	WaitingCount wc = new WaitingCount(msg.length(), drawingPane);
	for ( int i = 0; i < msg.length(); i ++ ) 
		new ShowOneChar(drawingPane, msg, i, new Font("Roman", Font.BOLD, 70), wc).start();
    }

    public void paint(Graphics g1 ) {
	super.paint(g1);
    }
}

class ShowOneChar extends Thread {
    final  double PI = 3.14159;
    final  int stepLimit = 3;
    String msg;
    private JPanel p = null ;
    Graphics2D g = null ;
    int i = 0, k = 0, d = 1, idx, sign1, sign2;
    int ww, hh, cw, ch;
    double x, y;
    Font f = null ;
    Color clr = null ;
    AffineTransform tfm = null;
    WaitingCount wc;
	ShowOneChar(JPanel p1, String m, int dx, Font fnt, WaitingCount w) {
		p = p1; wc = w;
		f = fnt; msg = m; idx = dx;
	}

	public void run() {

	  try { Thread.sleep(500); } catch (InterruptedException e) {;}
	  g = (Graphics2D) p.getGraphics(); 
	  g.setFont( f );
	  tfm = AffineTransform.getRotateInstance(0.0, 0.0, 0.0);
	  cw = g.getFontMetrics().stringWidth(msg.substring(idx, idx+1));
	  ch = g.getFontMetrics().getHeight();
	  ww = p.getWidth();  hh = p.getHeight();

	  x = Math.random() * ww; y = Math.random() * hh;
	  while ( true) {

		clr = new Color( (int) (Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));
		while ( clr.equals(Color.black)  )
			clr = new Color( (int) (Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));
		g.setXORMode(clr);
		moveAround(200); lineUp();
	  	rollOver(); lineUp();
		shakeIt(); lineUp();  
		shearIt(); lineUp();  
	  }
	}
	
	void rollOver() {
		d = Math.random() > 0.5? 1: -1;
		for ( int i = 0; i <= 64; i++ ) {
			tfm.setToRotation(PI * i/8 * d, x+cw/2,y-ch/3);
			g.setTransform(tfm) ;
			g.drawString(msg.substring(idx, idx+1), (int)x, (int)y);
			try { Thread.sleep(100); } catch( InterruptedException e ) { System.err.println(e.toString()); }
			g.drawString(msg.substring(idx, idx+1), (int)x, (int)y);
		}
		tfm.setToRotation(0, 0, 0);
		g.setTransform(tfm);
	}
	void shakeIt() {
		double angle = 0, delta = -1 * PI / 16;
		delta = Math.random() > 0.5? delta: -1 * delta;
		for ( int i = 0; i <= 64; i++ ) {
			angle += delta;
			tfm.setToRotation( angle, x+cw/2, y);
			g.setTransform(tfm) ;
			g.drawString(msg.substring(idx, idx+1), (int)x, (int)y);
			try { Thread.sleep(100); } catch( InterruptedException e ) { System.err.println(e.toString()); }
			g.drawString(msg.substring(idx, idx+1), (int)x, (int)y);
			if ( angle < -1 * PI / 3 || angle > PI / 3 ) delta *= -1;
		}
		tfm.setToRotation(0, 0, 0);
		g.setTransform(tfm);
	}

	void shearIt() {
		double hx = 0.0, hy = 0.0, delta = 0.08;
		for ( int i = 0; i <= 64; i++ ) {
			hx += delta;
			tfm.setToShear(hx, hy);
			g.setTransform(tfm) ;
			g.drawString(msg.substring(idx, idx+1), (int)(x - y * hx), (int)y);
			try { Thread.sleep(100); } catch( InterruptedException e ) { System.err.println(e.toString()); }
			g.drawString(msg.substring(idx, idx+1), (int) (x - y * hx), (int)y);
			if (hx < -0.5 || hx >0.5 ) delta *= -1;
		}
		tfm.setToRotation(0, 0, 0);
		g.setTransform(tfm);
	}

	void lineUp() {
		x = (ww-g.getFontMetrics().stringWidth(msg))/2 +
			g.getFontMetrics().stringWidth(msg.substring(0, idx));
		y = hh/2;
		g.drawString(msg.substring(idx, idx+1), (int)x, (int)y);
		try { Thread.sleep(1000); } catch( InterruptedException e ) { System.err.println(e.toString()); }
		g.drawString(msg.substring(idx, idx+1), (int)x, (int)y);
		wc.increaseCount();
		try { Thread.sleep(100); } catch( InterruptedException e ) { System.err.println(e.toString()); }
	}
	void moveAround( int cnt ) {
	sign1 = (Math.random()>0.5? -1 : 1) * (int) (Math.random() * stepLimit + 1);
	sign2 = (Math.random()>0.5? -1 : 1) * (int) (Math.random() * stepLimit + 1);

	  try {
	  	while ( cnt-- > 0 )  {
			x += sign1; y += sign2;
			g.drawString(msg.substring(idx, idx+1) , (int) x, (int) y); //(int) tfm.getTranslateX(), (int) tfm.getTranslateY());
			Thread.sleep(50);
			g.drawString(msg.substring(idx, idx+1) , (int) x, (int) y ); //(int) tfm.getTranslateX(), (int) tfm.getTranslateY());
			if ( x <= 0 ) sign1 = Math.abs(sign1);
			if ( x >= ww-cw) sign1 = -1 * Math.abs(sign1);
			if ( y <= ch/2 ) sign2 = Math.abs(sign2);
			if ( y >= hh) sign2 = -1 * Math.abs(sign2);
			
		}
	   } catch ( InterruptedException e ) { System.err.println("Waked up"); }
	}
}

class WaitingCount {
   private int cnt = 0;
   private int maxCount ;
   private JPanel drawingPane;
   public WaitingCount( int maxCnt, JPanel p ) {
	maxCount = maxCnt; drawingPane = p;
   }

   public synchronized void increaseCount() {
	cnt++;
	try {
		if ( cnt < maxCount ) wait();
		else {
			cnt = 0;
			//drawingPane.getGraphics().clearRect(0, 0, drawingPane.getWidth(), drawingPane.getHeight());
			//drawingPane.setBackground(Color.black);
			drawingPane.repaint();
			notifyAll();
		}
   	} catch ( InterruptedException e ) {}
   }
}


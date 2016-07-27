/** .

*/

// package DancingJava2D;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.*;

public class DancingJava2D extends CloseableJFrame {
    JPanel drawingPane = new JPanel();
    // static String msg = "Java 2D";
    static String msg = "REVS UP";
    public DancingJava2D( String title ) {
	super(title);
	getContentPane().setLayout(new BorderLayout());
	drawingPane.setSize(650,500);
	getContentPane().add(drawingPane, BorderLayout.CENTER);
	setSize(650, 500);
	setVisible(true);
	setResizable(false);
	drawingPane.setBackground(Color.black);
    }

    public void paint(Graphics g1 ) {
	super.paint(g1);
    }

    public static void main(String argv[]) {
	DancingJava2D f = new DancingJava2D("Welcome to Java 2D");
	for ( int i = 0; i < f.msg.length(); i ++ )
		new ShowOneChar(f.drawingPane, f.msg, i, new Font("Roman", Font.BOLD, 70)).start();
    }
}

class ShowOneChar extends Thread {
    final  double PI = 3.14159;
    final  int stepLimit = 3;
    String msg = null;
    private JPanel p = null ;
    Graphics2D g = null ;
    int i = 0, k = 0, d = 1, idx, sign1, sign2;
    int ww, hh, cw, ch;
    double x, y;
    Font f = null ;
    Color clr = null ;
    AffineTransform tfm = null;
	ShowOneChar(JPanel p1, String m, int dx, Font fnt) {
		p = p1; g = (Graphics2D) p.getGraphics();
		f = fnt; msg = m; idx = dx;
		g.setFont( f );
		tfm = AffineTransform.getRotateInstance(0.0, 0.0, 0.0);
		cw = g.getFontMetrics().stringWidth(msg.substring(idx, idx+1));
		ch = g.getFontMetrics().getHeight();
	  	ww = p.getWidth();  hh = p.getHeight();
		g.setBackground(Color.black);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}

	public void run() {

	  try { Thread.sleep(500); } catch (InterruptedException e) {;}
	  x = Math.random() * ww; y = Math.random() * hh;
	  while ( true) {

		clr = new Color( (int) (Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));
		while ( clr.equals(Color.black)  )
			clr = new Color( (int) (Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));
		// g.setXORMode(clr);
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
			drawIt(msg.substring(idx, idx+1), (int)x, (int)y, clr);
			try { Thread.sleep(100); } catch( InterruptedException e ) { System.err.println(e.toString()); }
			drawIt(msg.substring(idx, idx+1), (int)x, (int)y, g.getBackground());
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
			drawIt(msg.substring(idx, idx+1), (int)x, (int)y, clr);
			try { Thread.sleep(100); } catch( InterruptedException e ) { System.err.println(e.toString()); }
			drawIt(msg.substring(idx, idx+1), (int)x, (int)y, g.getBackground());
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
			drawIt(msg.substring(idx, idx+1), (int)(x - y * hx), (int)y, clr);
			try { Thread.sleep(100); } catch( InterruptedException e ) { System.err.println(e.toString()); }
			drawIt(msg.substring(idx, idx+1), (int) (x - y * hx), (int)y, g.getBackground());
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
	}
	void moveAround( int cnt ) {
	sign1 = (Math.random()>0.5? -1 : 1) * (int) (Math.random() * stepLimit + 1);
	sign2 = (Math.random()>0.5? -1 : 1) * (int) (Math.random() * stepLimit + 1);

	  try {
	  	while ( cnt-- > 0 )  {
			x += sign1; y += sign2;
			drawIt(msg.substring(idx, idx+1) , (int) x, (int) y, clr);
			Thread.sleep(50);
			drawIt(msg.substring(idx, idx+1) , (int) x, (int) y, g.getBackground());
			if ( x <= 0 ) sign1 = Math.abs(sign1);
			if ( x >= ww-cw) sign1 = -1 * Math.abs(sign1);
			if ( y <= ch/2 ) sign2 = Math.abs(sign2);
			if ( y >= hh) sign2 = -1 * Math.abs(sign2);
			
		}
	   } catch ( InterruptedException e ) { System.err.println("Waked up"); }
	}

        void drawIt( String s, int x, int y, Color clr) {
		g.setColor(clr);
		g.drawString(s, x, y ); 
 	}
}
	

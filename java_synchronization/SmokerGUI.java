import javax.swing.*;
import java.awt.*;

public class SmokerGUI extends JFrame {
   JPanel p = new JPanel();
   Graphics2D g = null;
   public SmokerGUI () {
	getContentPane().add(p);
	setSize(600, 400);
	setVisible(true);
	p.setBackground(Color.black);
	setTitle("Smoker Problem: Multithread & Synchronization");
	g = (Graphics2D) p.getGraphics();
        g.setBackground(Color.black);
        g.setFont(new Font("Roman", Font.BOLD, 16));
	try { Thread.sleep(10); } catch(Exception e) {}
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setXORMode ( Color.green);
   }

   public static void main( String arg[] ) {
      SmokerGUI app = new SmokerGUI();
      SmokerMonitor mtr = new SmokerMonitor( app.g ) ;
      for( int i = 0; i < 3; i ++ )
		new Smoker(i, mtr, app.g ).start();
      new Agent(3, mtr, app.g).start();
   }
}

class SmokerUtil {
  static int	x[] = {60, 250, 420, 250};
  static int	y[] = {210, 350, 210, 50};
  static int	labelX = 250, labelY = 210;
  static String ingredient[] = {"Paper", "Tobacco", "Match", "Agent" };
  static Color  color[] = { Color.pink, Color.yellow, Color.blue, Color.red};
  static boolean printTitle[] = { true, true, true, true };

  synchronized static void simulate (Graphics2D g, int k, String msg, int lb, int ub ) {
	String title = k < 3 ? "Smoker-Need-" + ingredient[k]: ingredient[k];
	try { Thread.sleep( HWUtil.nextInt( ub - lb + 1) + lb ); }
	catch (Exception e) {}
	g.setColor( color[k] );
	if ( printTitle[k] ) {
		g.drawString( title , x[k]-(k==3? -5 : 40), y[k] - 20);
		printTitle[k] = ! printTitle[k];
	}
	g.clearRect(x[k], y[k] - 16, 180, 20);
	g.drawString( msg, x[k], y[k] );
  }

  synchronized static void moveToTable(Graphics2D g, int ing ) {
	g.clearRect(x[3], y[3] - 16, 180, 20);
	move(g, ing, x[3], y[3], labelX, labelY, true, false);
  }
  
  synchronized static void moveFromTable(Graphics2D g, int smkr) {
	move(g, smkr, labelX, labelY, x[smkr], y[smkr],  smkr % 2 == 1, true);
  }
  
  static void move(Graphics2D g, int ing, int x1, int y1, int x2, int y2, boolean vertical, boolean clearSMKMsg ){
	int u, u1, u2, v, v1, v2, inc;
	float k;
	int dist = 10;
	u1 = vertical? y1 : x1;
	u2 = vertical? y2 : x2;
	v1 = vertical? x1 : y1;
	v2 = vertical? x2 : y2;
	k =  (v2 - v1 ) / ((float) u2 - u1);
	inc = u1 <= u2 ? dist : -dist ; 
	g.setColor(color[ing]);
	try { Thread.sleep( 5 ); } catch (Exception e) {}
	u = u1;
	v = v1;		
	if ( vertical)
		g.drawString(ingredient[ing], v, u);
	else
		g.drawString(ingredient[ing], u, v );
	for ( ; u != u2; u = u + inc ) {
		if ( vertical)
			g.drawString(ingredient[ing], v, u);
		else
			g.drawString(ingredient[ing], u, v);
		v = (int) (k * (u - u1) + v1);
		try { Thread.sleep( 10 ); } catch (Exception e) {}
		if ( vertical)
			g.drawString(ingredient[ing], v, u);
		else
			g.drawString(ingredient[ing], u, v);
	}
	if (clearSMKMsg) g.clearRect(x[ing], y[ing] - 16, 180, 20);
	if ( vertical)
		g.drawString(ingredient[ing], v, u);
	else
		g.drawString(ingredient[ing], u, v);
  }
}

class SmokerMonitor {
  int	available = -1;
  Graphics2D g = null;

  public SmokerMonitor( Graphics2D gr ) { g = gr; }

  synchronized public int get( int smkr ) {
	while ( available != smkr ) {
		notifyAll();
		SmokerUtil.simulate(g, smkr, "Waiting & Chatting", 10, 50);
		try { wait(); } catch (Exception e) {}
	}

	int ing = available;
	available = -1 ;
	notifyAll();
	SmokerUtil.moveFromTable(g, smkr);
	return smkr;
  }

  synchronized public void put( int ing) {
	while ( available >= 0  ) {
		notifyAll();
		SmokerUtil.simulate(g, 3, "Putting & Chatting", 10, 30);
		try { wait(); } catch (Exception e) {}
	}

	SmokerUtil.simulate(g, 3, SmokerUtil.ingredient[ing], 10, 30); 
	available = ing ;
	notifyAll();
	SmokerUtil.moveToTable(g, ing);
  }
}

class Smoker extends Thread {
   int id;
   SmokerMonitor mtr;
   Graphics2D	g;
   int          ing = -1;
   public Smoker( int k, SmokerMonitor m, Graphics2D gr ) { id = k; mtr = m; g = gr; }  

   public void run() {

	while (true ) {
		SmokerUtil.simulate(g, id, "Chatting", 400, 1000);
		ing = mtr.get( id );
		SmokerUtil.simulate(g, id, "Smoking & Chatting", 1000, 1600);
	}
   }
}


class Agent extends Thread {
   int id;
   SmokerMonitor mtr;
   Graphics2D g;
   public Agent( int k, SmokerMonitor m, Graphics2D gr ) { id = k; mtr = m; g = gr;}  

   public void run() {
	int ing;
	while (true ) {
		SmokerUtil.simulate(g, id, "Making & Chatting", 200, 600);
		ing = HWUtil.nextInt(3);
		mtr.put( ing );
		SmokerUtil.simulate(g, id, "Relaxing & Chatting", 100, 400);
	}
   }
}

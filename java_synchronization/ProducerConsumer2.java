/** Producer and Consumer: No buffer case, Synchronziation through monitor
	Three canvas and one JLabel are used to display
		1. the status of producer
		2. the status of comsumer
		3. the status of tray holding the burger
		4. which of producer of consumer is busy.
*/

import java.awt.*;
import javax.swing.*;

public class ProducerConsumer2 extends CloseableJFrame {
    Container c;
    JLabel 	// which of consumer and producer is busy.
		threadStatus = new JLabel("Running thread: ", SwingConstants.CENTER);
    Canvas
		burgerStatus = new Canvas(),	// burger tray
		consumerStatus = new Canvas(),	// consumer status
		producerStatus = new Canvas();	// producer status
    public  ProducerConsumer2() {
	super( "Hamburger Producer/Consumer: One burger holder");
	c = getContentPane();

	setSize(550, 300);

	threadStatus.setForeground(Color.gray); threadStatus.setFont(new Font("TimesRoman", Font.BOLD, 20));

	c.setBackground(Color.black);
	c.setLayout(new BorderLayout());

	consumerStatus.setSize(getWidth() * 2 / 7, getHeight());
	producerStatus.setSize(getWidth() * 2 / 7, getHeight());
	c.add(consumerStatus, BorderLayout.WEST);
	c.add(producerStatus, BorderLayout.EAST);
	c.add(burgerStatus, BorderLayout.CENTER);
	c.add(threadStatus, BorderLayout.NORTH);
	show();

	PCMonitor2 mt = new PCMonitor2(threadStatus, burgerStatus);
	// pass monitor and canvas to consuper and producer.
	Consumer2 cs = new Consumer2("Consumer", mt, consumerStatus);
	Producer2 pc = new Producer2("Producer", mt, producerStatus);
	pc.start();   cs.start(); 
    }

    public static void main(String argv[]) {
	new ProducerConsumer2() ;
    }
}


class Consumer2 extends Thread {
   final int MAX_EATING_TIME = 2000, MIN_EATING_TIME = 2000,
	     MAX_DIGESTING_TIME = 1000, MIN_DIGESTING_TIME = 2000,
	     FONT_SIZE = 16;
   int	intConsumedCount =0;
   PCMonitor2 mt;
   Canvas  drawingPane;
   Graphics2D g2d;
   String  msg[] = {"CONSUMER", "", ""};
   public Consumer2(String name, PCMonitor2 m, Canvas cv) {
	super(name);  mt = m; drawingPane = cv;
	g2d = (Graphics2D) drawingPane.getGraphics(); g2d.setBackground(Color.black);
	g2d.setColor(new Color(200, 100, 50)); g2d.setFont(new Font("TimesRoman", Font.BOLD, FONT_SIZE));
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
   }

   void showStatus() {
	//drawingPane.repaint();
	try { sleep(50); } catch (InterruptedException e) {}
	for ( int i = 0; i < msg.length; i ++ ) {
		g2d.clearRect( 0, drawingPane.getHeight() * i / msg.length + 30 - FONT_SIZE,
			drawingPane.getWidth(), FONT_SIZE+ 3); 
		g2d.drawString( msg[i], 10, drawingPane.getHeight() * i  / msg.length + 30);
	}
   }

   public void run() {

	while ( true ) {

		msg[1] = "Waiting for"; msg[2] = "Burger: " + (intConsumedCount + 1);
		showStatus();

		mt.getHamburger();

		intConsumedCount++;
	
		msg[1] = "Eating"; msg[2] = "Burger: " + intConsumedCount;
		showStatus();

		try { Thread.sleep( (long) (Math.random() * MAX_EATING_TIME + MIN_EATING_TIME)); }
		catch (InterruptedException e) {}

		msg[1] = "Digesting";  showStatus();

		try { Thread.sleep( (long) (Math.random() * MAX_DIGESTING_TIME + MIN_DIGESTING_TIME)); }
		catch (InterruptedException e) {}

		msg[1] = "Relaxing after"; showStatus();

		try { Thread.sleep( (long) (Math.random() * MAX_DIGESTING_TIME + MIN_DIGESTING_TIME)); }
		catch (InterruptedException e) {}
	}
   }
}

class Producer2 extends Thread {
   final int	MAX_MAKING_TIME = 2000, MIN_MAKING_TIME = 2000,
		MAX_PREPARING_TIME = 1000, MIN_PREPARING_TIME = 2000,
		FONT_SIZE = 16;
   int	intProducedCount = 1;
   PCMonitor2 mt;
   Canvas  drawingPane;
   Graphics2D g2d;
   String  msg[] = {"PRODUCER", "Making", "Burger: 1"};

   public Producer2(String name, PCMonitor2 m, Canvas cv) {
	super(name);  mt = m; drawingPane = cv;
	g2d = (Graphics2D) drawingPane.getGraphics(); g2d.setBackground(Color.black);
	g2d.setColor(new Color(100, 100, 255)); g2d.setFont(new Font("TimesRoman", Font.BOLD, 16));
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
   }

   void showStatus() {
	try { sleep(50); } catch (InterruptedException e) {}
	for ( int i = 0; i < msg.length; i ++ ) {
		g2d.clearRect( 10, drawingPane.getHeight() * i / msg.length + 30 - FONT_SIZE,
				drawingPane.getWidth(), FONT_SIZE + 3);
		g2d.drawString( msg[i], 10, drawingPane.getHeight() * i  / msg.length + 30);
	}
   }

   public void run() {

	while ( true ) {
		msg[1] = "Making"; msg[2] = "Burger: " + intProducedCount;
		showStatus();
		try { Thread.sleep( (long) (Math.random() * MAX_MAKING_TIME + MIN_MAKING_TIME)); }
		catch (InterruptedException e) {}

		msg[1] = "Wait space for"; showStatus();
		
		mt.putHamburger(intProducedCount);
	
		msg[1] = "Preparing"; msg[2] = "Burger: " + (++intProducedCount);
		showStatus();

		try { Thread.sleep( (long) (Math.random() * MAX_PREPARING_TIME + MIN_PREPARING_TIME)); }
		catch (InterruptedException e) {}

	}
   }
}

class PCMonitor2 {
	private boolean emptyTray;
	Graphics2D g2; 
	ImageIcon burger = new ImageIcon("burger.gif");
	JLabel threadStatus ;
	Canvas burgerStatus;

	PCMonitor2(JLabel sts, Canvas cv) {
		threadStatus = sts; burgerStatus = cv;
		emptyTray = true; 
		g2 = (Graphics2D) burgerStatus.getGraphics();
		g2.setColor(Color.white);
		g2.setFont(new Font("TimesRoman", Font.BOLD, 20));
	}

	public synchronized void getHamburger() {
		if ( emptyTray )
			try { wait(); } catch (InterruptedException e) {}

		emptyTray = true;
		paintHamburger(0);

		// The following delay is necessary for graphics to be drawn on screen.
		// Without the delay, the synchronization with screen drawing fall apart because of
		// the asynchronzation feature of drawing function.
		try { Thread.sleep( (long) (Math.random() * 1000 + 500)); } catch (InterruptedException e) {}
		notify();
	}

	public synchronized void putHamburger(int burgerCnt) {
		if ( ! emptyTray ) 
			try { wait(); } catch(InterruptedException e) {}

		emptyTray = false;
		paintHamburger(burgerCnt);
		try { Thread.sleep( (long) (Math.random() * 1000 + 500)); } catch (InterruptedException e) {}
		notify();
	}

	private void paintHamburger(int burgerCnt) {
		threadStatus.setText("Running Thread: " + Thread.currentThread().getName());
		int ww = burgerStatus.getWidth(),
		    hh = burgerStatus.getHeight();
		if ( emptyTray )  {
			// erase the image.
			burgerStatus.repaint();
			try { Thread.sleep(50); } catch(InterruptedException e) {}
			g2.drawString("No burger available", ww/8, hh / 2);
		}
		else {
			burgerStatus.repaint();
			try { Thread.sleep(50); } catch(InterruptedException e) {}
			burger.paintIcon(burgerStatus, g2, ww/4, hh/4);
			g2.drawString("Burger No. " + burgerCnt, ww/5, hh/5);
		}
	}
}

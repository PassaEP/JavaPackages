// Multiple Thread: 3 threads; one produce integer, one consumer integer
//		one draws the circles to indicate which buffer status.
// Monitor: A monitor object is defined so that the shared data is
//		accessed through the synchronized function.
// wait(), notify() and notifyAll() must be called from a synchconized method of a MONITOR.

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.DecimalFormat;

public class ProducerConsumer extends JApplet implements ActionListener, Runnable {
   static JTextArea output = new JTextArea(20, 80);
   static Canvas cv = new Canvas() ;
   static final int maxItems = 20 ;
   HoldInteger h = null;
   JButton btnStop = new JButton("Stop") ;
   Panel plBtn = new Panel() ;

   ProduceInteger p; ConsumeInteger c; DrawItems d;	  
   Graphics g = null ;

   public void init()
   {
	Container c = getContentPane();
      c.setLayout( new BorderLayout() );
	JScrollPane t = new JScrollPane( output );
      cv.setSize(300, 60) ;
      c.add(cv, BorderLayout.NORTH);
      c.add(t, BorderLayout.CENTER);
      plBtn.add(btnStop) ;
      c.add(plBtn, BorderLayout.SOUTH);
      btnStop.addActionListener(this) ;
      cv.getGraphics().setFont(new Font("MONOSPACED", Font.BOLD, 12)) ;
   }

   public synchronized void actionPerformed( ActionEvent e ) {
	if (((JButton) e.getSource()).getText().equals("Stop") ) {
		h.stopAll(true);
		btnStop.setText("Reusme") ;
	}
	else {
		h.stopAll(false);
		btnStop.setText("Stop") ;
	}
   }

   public void start()
   {
	h = new HoldInteger( output );
      output.setText("")  ;
      g = getGraphics();
      c = new ConsumeInteger(h) ;
      p = new ProduceInteger(h) ;
      d = new DrawItems(h, cv.getGraphics() ) ;
	Thread t = new Thread(this);
	t.setDaemon(true);
      c.start();
      p.start();
      d.start();
	t.start();	  
   }

   public void run() {
	while ( true ) { 
		try { Thread.sleep(500); } catch (InterruptedException e) {}
		h.wakeupAll() ;
	}
   }

   // To avoid mutiple more than one producer and consumer runs at the same time.
/*
   public void stop() {
   }

   public void paint(Graphics g)
   {
//   cv.getGraphics().drawString("RED: Consumed   BLUE: Produced", 100, 20) ; 
   }
*/
}

class DrawItems extends Thread {
	private Graphics g;
	private HoldInteger dHold ;
	public DrawItems( HoldInteger h, Graphics c )
   {
      dHold = h;
	  g = c ;
   }
   
	public void run()
	{
		while( dHold.contPC () ) {
			dHold.drawCircles(g) ;
		}
		dHold.drawCircles(g) ;
	}
	
}

class ProduceInteger extends Thread {
   private HoldInteger pHold;

   public ProduceInteger( HoldInteger h )
   {
      pHold = h;
   }

   public void run()
   {
      for ( int count = 0; count < ProducerConsumer.maxItems; count++ ) {

         // sleep for a random interval
         try {
            Thread.sleep( (int) ( Math.random() * 3000 ) );
         }
         catch( InterruptedException e ) {
            System.err.println( e.toString() );
         }
         pHold.setSharedInt( count + 1 );
         System.out.println( "Produced set sharedInt to " +
                             (count + 1) );
      }
   }
}

class ConsumeInteger extends Thread {
   private HoldInteger cHold;

   public ConsumeInteger( HoldInteger h )
   {
      cHold = h;
   }

   public void run()
   {
      int val;

      while ( cHold.contPC() ) {

         val = cHold.getSharedInt();
         if ( val > 0 )
			System.out.println( "Consumer retrieved " + val );

		  // sleep for a random interval
         try {
            Thread.sleep( (int) ( Math.random() * 4000 ) );
         }
         catch( InterruptedException e ) {
            System.err.println( e.toString() );
         }

      }
	ProducerConsumer.output.append( "\nConsumer:NO DATA TO CONSUME") ;
	System.out.println("No more data to consume");
   }
}

class HoldInteger {
   private int sharedInt[] = { -1, -1, -1, -1, -1 };
   private boolean cont = true;
   int consumed = 0 , produced = 0 ;
   private int readLoc = 0, writeLoc = 0;
   private JTextArea output;
   private Color c ;
   private Graphics g ;
   private boolean blnStop = false;

   public HoldInteger( JTextArea out )
   {
      output = out;
   }

   public int getInt(int i ) { return sharedInt[i]; }
   
   public synchronized void setSharedInt( int val )
   {
		// Is the thread suspended?
	   	while (blnStop)
			try{ wait(); } catch (InterruptedException e) {}

		// Even with single producer and consumer, while loop is necessary since
		// the producer or consumer may be waked up by daemon processor.
		while ( produced < ProducerConsumer.maxItems && produced - consumed >= 5 ) { 
			output.append("\nProducer: Buffer Full") ;
			try {
				wait() ;
			}
			catch( InterruptedException e ) {
			System.err.println( e.toString() );
			}
		}

	   if ( produced < ProducerConsumer.maxItems && produced - consumed < 5 ) {
		sharedInt[ writeLoc ] = val;
		produced ++ ;
		output.append( "\nProduced " + val +
                         " into cell " + writeLoc );
		writeLoc = ( writeLoc + 1 ) % 5;

		output.append( "\twrite " + writeLoc +
                     "\tread " + readLoc );
	   } else {
			if ( produced >= ProducerConsumer.maxItems && produced == consumed ) {
				cont = false ;
				output.append("\nProducer: Max. amount achieved") ;
			}
	   }
	   if ( ! blnStop ) notifyAll() ;
	   printBuffer( output, sharedInt );
   }

   public synchronized int getSharedInt()
   {
      int val;
	  val = -1 ;

	// Is the thread suspended?
	while (blnStop)
		try{ wait(); } catch (InterruptedException e) {}


		// Even with single producer and consumer, while loop is necessary since
		// the producer or consumer may be waked up by daemon processor.
	  	while ( produced == consumed) { 
			output.append("\nConsumer: Buffer Empty") ;
			try { wait() ; }	catch( InterruptedException e ) { System.err.println( e.toString() );
		}
	  }

	  if ( consumed < produced ) {
		val = sharedInt[ readLoc ];
		sharedInt[readLoc] = -1 ;
		consumed ++ ;
		output.append( "\nConsumed " + val +
                         " from cell " + readLoc );
		readLoc = ( readLoc + 1 ) % 5;
		output.append( "\twrite " + writeLoc + "\tread " + readLoc );
		// Data changed, nofity procedure and drawer.
		printBuffer( output, sharedInt );
	  }
	  if ( ! blnStop ) notifyAll() ;
	  if ( consumed == produced && produced == ProducerConsumer.maxItems ) cont = false ;
	  return val;
   }
   
   	public synchronized void drawCircles(Graphics x)
   	{
   		g = x ;

		while (blnStop)
			try{ wait(); } catch (InterruptedException e) {}
   
   		ProducerConsumer.output.append("\nDraw Circles: Produced: " + produced
						+ "  Consumed:" + consumed + " Avail: "
						+ (produced - consumed) ) ;
	       g.drawString("RED: Consumed   BLUE: Produced", 100, 15) ; 
		for ( int i = 0 ; i < 5 ; i ++ ) {
			if ( getInt(i) == -1 ) 
				c = Color.red ;
			else c = Color.blue ;
			g.setColor(c);	
			g.drawOval(i * 25 + 100, 35, 20, 20 );
		}
		
		try {
			wait() ;    // wait until one item produced or consumed.
		}
		catch( InterruptedException e ) {
		System.err.println( e.toString() );
		}
	}


   public synchronized void printBuffer( JTextArea out, int buf[] )
   {
      DecimalFormat threeChars = new DecimalFormat( " #;-#" );
      output.append( "\tbuffer: " );

      for ( int i = 0; i < buf.length; i++ )
         out.append( " " + threeChars.format( buf[ i ] ) );
   }

   public synchronized boolean contPC()
   {
      return cont ;
   }

   public synchronized void wakeupAll() { if ( !blnStop ) notifyAll(); }
   public void stopAll( boolean stopThread) { blnStop = stopThread; }
}

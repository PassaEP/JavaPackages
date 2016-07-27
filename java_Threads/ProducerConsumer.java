// Multiple Thread: 3 threads; one produce integer, one consumer integer
//		one draws the circles to indicate which buffer status.
// Monitor: A monitor object is defined so that the shared data is
//		accessed through the synchronized function.

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class ProducerConsumer extends Applet implements ActionListener {
   static TextArea output = null;
   static Canvas cv = new Canvas() ;
   static final int maxItems = 20 ;
   HoldInteger h = null;
   Button btnStop = new Button("Stop") ;
   Panel plBtn = new Panel() ;

   ProduceInteger p = null ;
   ConsumeInteger c = null ; DrawItems d = null ;	  
   Graphics g = null ;

   public void init()
   {
      setLayout( new BorderLayout() );
      output = new TextArea(20, 70);
      cv.setSize(300, 60) ;
      add(cv, BorderLayout.NORTH);
      add(output, BorderLayout.CENTER);
      plBtn.add(btnStop) ;
      add(plBtn, BorderLayout.SOUTH);
      btnStop.addActionListener(this) ;
       cv.getGraphics().setFont(new Font("MONOSPACED", Font.BOLD, 14)) ;
   }

   public void actionPerformed( ActionEvent e ) {
	if (((Button) e.getSource()).getLabel().equals("Stop") ) {
		p.suspend();
		c.suspend();
		d.suspend() ;
		btnStop.setLabel("Reusme") ; }
	else {
		p.resume();
		c.resume();
		d.resume() ;
		btnStop.setLabel("Stop") ; }
   }

   public void start()
   {
      output.setText("")  ;
      h = new HoldInteger( output );
      g = getGraphics();
      c = new ConsumeInteger(h) ;
      p = new ProduceInteger(h) ;
      d = new DrawItems(h, cv.getGraphics() ) ;
      c.start();
      p.start();
      d.start() ;	  
   }
   // To avoid mutiple more than one producer and consumer runs at the same time.
   public void stop() {
      c.stop() ;
      p.stop() ;
      d.stop() ;
   }
   public void paint(Graphics g)
   {
//   cv.getGraphics().drawString("RED: Consumed   BLUE: Produced", 100, 20) ; 
   }
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
   }
}

class HoldInteger {
   private int sharedInt[] = { -1, -1, -1, -1, -1 };
   private boolean cont = true;
   int consumed = 0 , produced = 0 ;
   private int readLoc = 0, writeLoc = 0;
   private TextArea output;
	private Color c ;
	private Graphics g ;

   public HoldInteger( TextArea out )
   {
      output = out;
   }

   public int getInt(int i ) { return sharedInt[i]; }
   
   public synchronized void setSharedInt( int val )
   {

		// If there are more than one producer, while loop should be
		// used to replace the if statement.
		if ( produced < ProducerConsumer.maxItems && produced - consumed >= 5 ) { 
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
	   notifyAll() ;
	   printBuffer( output, sharedInt );
   }

   public synchronized int getSharedInt()
   {
      int val;
	  val = -1 ;
	// If more than one consumenr exist, while should be used to replace
	// the if statement.
	  if ( produced == consumed) { 
		output.append("\nConsumer: Buffer Empty") ;
		try {
			wait() ;
		}
		catch( InterruptedException e ) {
		System.err.println( e.toString() );
		}
	  }

	  if ( consumed < produced ) {
		val = sharedInt[ readLoc ];
		sharedInt[readLoc] = -1 ;
		consumed ++ ;
		output.append( "\nConsumed " + val +
                         " from cell " + readLoc );
		readLoc = ( readLoc + 1 ) % 5;
		output.append( "\twrite " + writeLoc +
                     "\tread " + readLoc );
		// Data changed, nofity procedure and drawer.
		printBuffer( output, sharedInt );
	  }
	  notifyAll() ;
	  if ( consumed == produced && produced == ProducerConsumer.maxItems ) cont = false ;
	  return val;
   }
   
   	public synchronized void drawCircles(Graphics x)
   	{
   		g = x ;
   
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


   public void printBuffer( TextArea out, int buf[] )
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
}

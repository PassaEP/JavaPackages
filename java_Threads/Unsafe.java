/** Unsafe Thread: The program show that without mutual exclusion,
    The data stored in shared variable may be updated by two threads
    and leave data inconsistency.
*/
import java.applet.*;
import java.awt.*;

public class Unsafe extends Applet {
	EvenCounter  theCounter = new EvenCounter();

	public void init() {
		setLayout( new GridLayout(1, 0) );
		add( theCounter ) ;
		// Two threads will shared the same EvenCounter object
		Thread t1 = new Thread(theCounter);
		Thread t2 = new Thread(theCounter);
		t1.start();
		try { t1.sleep(100); } catch (Exception ex) { ; }
		t2.start();
	}

	// Inner class EventCounter supposedly display word "Even"
	class EvenCounter extends TextField implements Runnable {
		int theState = 0;
		public EvenCounter() {
			setText("Even");
			setFont(new Font("SansSerif", Font.BOLD, 32));
			setEditable(false);
		}
	
		public void run() {
		    while (true ) {
			theState += 1; 
			try { Thread.sleep(500) ;} catch( Exception ex) { ; }
			theState += 1;
			if ( theState %2 == 1 ) setText("Odd " + theState);
			else setText("Even " + theState );
			try { Thread.sleep((int)(Math.random() * 500)) ;} catch( Exception ex) { ; }
		    }
		}
	}
}

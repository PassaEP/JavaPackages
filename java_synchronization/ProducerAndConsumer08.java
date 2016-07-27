import java.lang.*;
import java.util.concurrent.*;

public class ProducerAndConsumer08 {

   public static void main( String args[] ) {
	// Create monitor objects with arrays of 5 elements.
	PCMonitor burgerTray = new PCMonitor(5);

	// Create cashed thread pool to carry producer and
	// consumer runnable objects.
	ExecutorService ex = Executors.newCachedThreadPool();

	// Create 3 producer thread having referece to the monitor
	// object. Use cashed thread pool to run the runnable producers.
	Runnable rbl = null;
	for ( int i = 1; i <= 3; i++ ) {
		rbl = new Producer(i,  burgerTray );
		ex.execute( rbl );
	}

	// Create 10 comsumer objects with references to monitor object.
	for ( int i = 1; i <= 10; i++ ) {
		rbl = new Consumer(i,  burgerTray );
		ex.execute( rbl );
	}
   }
}

class PCMonitor {

   int burger[] = null;
   int pID  [] = null;
   int cID  [] = null;

   static int nextBurgerID = 1;
   int in =0, out = 0, count = 0;

   public PCMonitor( int sz ) {
	burger = new int[sz];
	pID   = new int[sz];
	cID   = new int[sz];
	in = out = count = 0;
   }

   public synchronized void put( int pID ) {
	while ( count >= burger.length ) try { wait(); } catch (Exception e) {}
	burger[in] = nextBurgerID++; // get label, and put burger in.
	this.pID[in] = pID;
	this.cID[in] = 0;
	System.out.printf ("\n\tProducer %2d produced burger %d.", pID, burger[in]);
	in = (in + 1) % burger.length;
	count++;
	notifyAll();
   }

   public synchronized int get ( int cID ) {
        while ( count < 1 ) try { wait(); } catch (Exception e) {}
	int bID = burger[out]; // The burget I got is bID.
	this.cID[out] = cID;
	System.out.printf ("\n\tConsumer %2d took burger %d.", cID, burger[out]);
	burger[out] = 0;
	out = (out + 1) % burger.length;
	count--;
	notifyAll();
	return bID;
   }

   public synchronized void print() {

	System.out.printf("\n\t%-15s", "Producer ID: ");
	for (int i = 0; i < pID.length; i++ ) System.out.printf("%4d", pID[i]); 

	System.out.printf("\n\t%-15s", "Burger ID: ");
	for (int i = 0; i < burger.length; i++ ) System.out.printf("%4d", burger[i]); 

	System.out.printf("\n\t%-15s", "Consumer ID: ");
	for (int i = 0; i < cID.length; i++ ) System.out.printf("%4d", cID[i]); 

	System.out.printf("\n");

   }	
}

class Producer implements Runnable {

   int ID ;
   PCMonitor burgerTray;
   public Producer( int ID, PCMonitor bgt ) { this.ID = ID; burgerTray = bgt; }
  
   public void run() {
	while ( true ) {
 	   try {
		// System.out.printf( "\n\tProducer %2d is making a bugger ...", ID);
		Thread.sleep((int) (Math.random() * 5000));

		// System.out.printf ("\n\tProducer %2d is putting the burger on the tray...", ID);
		Thread.sleep(1000);
		burgerTray.put(ID);

		burgerTray.print();  // print tray status.

		// System.out.printf ("\n\tProducer %2d is relaxing ...", ID);
		Thread.sleep(1000 + (int) (Math.random() * 5000));


	   } catch( Exception e ) {} 

	}
   }
}
 
class Consumer implements Runnable {

   int ID ;
   PCMonitor burgerTray;
   public Consumer ( int ID, PCMonitor bgt ) { this.ID = ID; burgerTray = bgt; }
  
   public void run() {
	int bID; 
	while ( true ) {
 	   try {
		// System.out.printf( "\n\tConsumer %2d is making working ...", ID);
		Thread.sleep(10000 + (int) (Math.random() * 5000));

		// System.out.printf( "\n\tConsumer %2d is hungery, walking to Burger King...", ID);
		Thread.sleep(20000 );
		bID = burgerTray.get(ID);

		burgerTray.print();  // print tray status.

		//  System.out.printf( "\n\tConsumer %2d is eating burger %d", ID, bID);
		Thread.sleep(10000 + (int) (Math.random() * 5000));


		// System.out.printf( "\n\tConsumer %2d is relaxing ...", ID);
		Thread.sleep(10000 + (int) (Math.random() * 5000));


	   } catch( Exception e ) {} 

	}
   }
} 


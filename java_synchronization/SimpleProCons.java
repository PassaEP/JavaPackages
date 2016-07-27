import java.math.*;

public class SimpleProCons {

   private Buffer buf = new Buffer();

   SimpleProCons() {
   	Thread prod = new Producer( buf );
   	Thread cons1 = new Consumer( buf, 1 );
   	Thread cons2 = new Consumer( buf, 2 );
   	cons1.start();  cons2.start();
   	prod.start();
   }
   public static void main(String args[] ) {
	new SimpleProCons();
   }
}

class Producer extends Thread {
   Buffer buf;
   int	  nextItem = 1;
   Producer( Buffer b ) { buf = b; }

   public void run() {
     while ( true ) {
 	System.out.println("Producer: preparing material ...");
	try {
		sleep ( (int) ( Math.random() * 2000) + 1000) ;
	} catch (InterruptedException e ) {}
 	buf.put(nextItem);
	System.out.println("Producer: Item produced " + nextItem++);
	try {
	   sleep ( (int) ( Math.random() * 2000) + 1000);
	} catch (InterruptedException e ) {}
 	System.out.println("Producer: short break ...");
     }
   }
}

class Consumer extends Thread {
   Buffer buf;
   int	  nextItem , num ;
   String heading = "Consumer ";
   Consumer( Buffer b, int n ) { buf = b; num = n; 
	heading = heading + n ; for ( int i = 0; i < n; i ++ ) heading = "\t\t\t" + heading;
   }

   public void run() {
     while ( true ) {
 	System.out.println(heading + " : I'm waiting ...");
	try {
		sleep ( (int) ( Math.random() * 2000) + 1000) ;
	} catch (InterruptedException e ) {}
 	nextItem = buf.get();
 	System.out.println(heading + " : don't bother, I'm eating item, " + nextItem );
	try {
		sleep ( (int) ( Math.random() * 5000) + 1000);
	} catch (InterruptedException e ) {}
     }
   }
}

class Buffer {

   private int	buffer[] = new int[10],
		in = 0, out = 0, count = 0;

   protected boolean isFull() { return count == buffer.length; }
   protected boolean isEmpty() { return count <= 0 ; }

   synchronized  public void put(int item) {
	if ( isFull () ) {
	    try {  wait() ; } catch (InterruptedException e) {}
	}
	buffer[in] = item; count ++; in = ( in + 1) % buffer.length;
	notifyAll();
   }

   synchronized  public int get() { 
	int item;
	while ( isEmpty() ) {
	    try {  wait() ; } catch (InterruptedException e) {}
	}
	item = buffer[out] ; count --; out = ( out + 1) % buffer.length;
	notifyAll();
	return item;
   }
}

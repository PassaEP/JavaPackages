/* Thread & Thread Synchronization
    1. Thread:
	* An object of class extending Thread or Implementing Runnable.
	* A thread object, obj, is start() by obj.start() from where the thread
	  object is defined.
	* The class extending Thread or implementing Runnable must overriding
	  the public void run() function to give the special task to that
	  thread.
	* A (extended) thread can call sleep(), setPriority(), suspend(),
	  yield(), interrupt(), resume() and etc. to change the status of
	  a thread and used for thread scheduling.
    2. Thread Synchronization:
	* Threads need to synchronize themself (exclusive and/or ordered
	  execution of thread's code).
	* The mechanisms used to synchronize threads in Java are called
	  monitors, the objects of classes with synchronized methods.
	* There are three functions, wait(), notify() and notifyAll() defined
	  in java.lang.Object class that are critical to thread
	  synchronization.
	* The wait() (3 versions) function will put a thread calling the
	  synchronized method on wait until a time period or waked up by others.
	* The notify() (notifyAll() ) will wake up next (all) thread waiting
	  on the monitor in which the notify() or notifyAll() is called.
    3. The following actions are considered as error or will not work properly
	in Digital Unix Java 1.1.4:
	* Call sleep() inside monitor method.
	* Call wait(), notify() or notifyAll() in non-synchronized code.
	* Call wait(), notify() or notifyAll() in synchronized statements will
	  not synchronize thread properly.
*/

import java.util.Calendar;

public class TimeMessageThread {
   public static void main(String argv[] ) {
	PrintMonitor pm = new PrintMonitor() ;
	new MessageThread(pm).start() ;
	new TimeThread(pm).start() ;
   }
}

class TimeThread extends Thread {
   private int cnt = 0;
   PrintMonitor pm = null ;
   TimeThread(PrintMonitor m) { pm = m; }
   public void run() {
	while (true ) 
	   try {   pm.printTime(++cnt) ; sleep(1000) ; }
	   catch (InterruptedException e ) {
		// sleep(), wait() will cause InterruptedException,
		// call interrupt() in the InterruptedExaception will
		// clear up the interrupted state of a thread.
		Thread.currentThread().interrupt(); }
  }

}

class MessageThread extends Thread {
   private int cnt = 0;
   PrintMonitor pm = null ;
   MessageThread(PrintMonitor m) { pm = m; }
   public void run() {
	while ( true ) { pm.printMessage(cnt++) ; }
   }

}

class PrintMonitor {
   public synchronized void printMessage(int cnt) {
	try { wait(); System.out.println("Message: " + cnt) ; }
	catch (InterruptedException e ) { System.out.println(e.toString()); }
   }
	
  synchronized void printTime(int cnt) {
		Calendar d = Calendar.getInstance();
		System.out.println("" + d.get(Calendar.HOUR) + ":" +
		    d.get(Calendar.MINUTE) + ":" + d.get(Calendar.SECOND)) ;
		if ( cnt % 3 == 0 ) {
			notifyAll() ; 
		}
  }
}

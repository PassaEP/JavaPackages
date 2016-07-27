public class NotifyAllTest {

   public static void main (String args[] ) {
	TestMonitor mt = new TestMonitor();
	TestThread t1 = new TestThread(1, mt);
	TestThread t2 = new TestThread(2, mt);
	t2.start();
	t1.start();

   }

}

class TestMonitor {

   public synchronized void doIt(int n) {
	notifyAll();
	System.out.println("TestMonitor.doIt() called from thread " + n);
	for ( int i = 0; i < 10; i ++ ) {
		System.out.println( (n > 1 ? "\t" : "") + n + " is inside monitor. " );
		try { Thread.sleep(500); } catch (Exception e) { }
	}
   }
}
      
class TestThread extends Thread {
   int num;
   TestMonitor mt ;
   public TestThread( int k, TestMonitor m ) { num = k ; mt = m; }
   public void run() { mt.doIt(num); }
}

       

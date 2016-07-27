/** Cigarette-Smoker Problem:
*/

public class SmokerProblem2009 {

   public static void main(String args[] ) {
	SmokerMonitor mtr = new SmokerMonitor();
        Smoker        s1 = new Smoker(1, mtr, 2000),
		      s2 = new Smoker (2, mtr, 5000),
		      s3 = new Smoker(3, mtr, 1000);
	Agent         a = new Agent(mtr);
	s1.start(); s2.start(); s3.start(); a.start();
	while ( true ) {
	    ScreenIO.clear();
	    ScreenIO.moveTo( 5, 30 );
	    System.out.printf("%s", a.status);

	    ScreenIO.moveTo(10, 0);
	    System.out.printf("%20s", s1.status);
	    System.out.printf("%20s", mtr.status);
	    System.out.printf("%30s", s2.status);
	    ScreenIO.moveTo(15, 30);
	    System.out.printf("%s", s3.status);
	    try { Thread.sleep(200); } catch (InterruptedException e) {}
       }
   }
}


class Smoker extends Thread {

   protected int degree;
   protected SmokerMonitor smt;
   protected int which;  // 1: tobacco, 2: paper, 3: matches.
   int	     cigSmoked = 0;
   public    String    status = null;

   public Smoker(int who, SmokerMonitor m, int dgr) {
       which = who; smt = m ; degree = dgr;
   }

   void sleep( int mills ) {
	try { Thread.sleep( 3000); } catch (InterruptedException e ) {}
   }

   public void run() {
     while ( true ) {
	status = "Smoker(" + cigSmoked + "): waiting " + SmokerCons.ingredients[which];
	sleep(degree);

	int ing = smt.getIngredient(which);
	status = "Smoker(" + cigSmoked + "):  got " + SmokerCons.ingredients[ing];
	sleep(degree);

	cigSmoked++;
	status = "Smoker(" + cigSmoked + "): smoking";
	sleep( degree ); 
	status = "Smoker(" + cigSmoked + "): relaxing";
	sleep (degree * 2);
     }
   }
}

class Agent extends Thread {

   protected int igrCount = 0;
   protected SmokerMonitor smt;
   public    String status = null;


   public Agent( SmokerMonitor m ) { smt = m; }

   public void run() {
     while ( true ) {
	int k = ((int) (Math.random() * 1000) % 3) + 1;
	status = "Agent(" + igrCount + "): making " + SmokerCons.ingredients[k];
	sleep( 2000 );
	status = "Agent(" + igrCount + "): putting " + SmokerCons.ingredients[k];
	sleep (3000);
	igrCount++;
	smt.putIngredient(k);
	status = "Agent(" + igrCount + "): relaxing";
	sleep (5000);
     }
   }
   void sleep( int mills ) {
	try { Thread.sleep( 3000); } catch (InterruptedException e ) {}
   }

}

class SmokerMonitor {

   protected int ingredient;

   public    String status = "Table: Empty";

   SmokerMonitor() { ingredient = SmokerCons.none; }  // -1: no ingredient.
   synchronized public void putIngredient( int ing ) {
	if ( ing < 1 || ing > 3 ) {
		System.out.println("Wrong ingredient: " + ing);
		System.exit(-1);
	}

	while ( ingredient != SmokerCons.none ) {
		notifyAll();
		try { wait(); } catch (InterruptedException e) {}
	}

	ingredient = ing;
	status = "Table: " + SmokerCons.ingredients[ingredient];

	notifyAll();
   }

   synchronized public int getIngredient( int kind ) {
	while ( kind != ingredient ) {
		notifyAll();
		try { wait(); } catch (InterruptedException e) {}
	}
	int ing = ingredient;
	ingredient = SmokerCons.none;
	notifyAll();
	try { Thread.sleep(1000); } catch(InterruptedException e) {}
	status = "Table: Empty";
	return ing;
   }
}

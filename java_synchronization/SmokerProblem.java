/** Cigarette-Smoker Problem:
*/

public class SmokerProblem {

   public static void main(String args[] ) {
	SmokerMonitor mtr = new SmokerMonitor();
        Smoker        s1 = new Smoker(1, mtr),
		      s2 = new Smoker (2, mtr),
		      s3 = new Smoker(3, mtr);
	Agent         a = new Agent(mtr);
	s1.start(); s2.start(); s3.start(); a.start();
   }
}


class Smoker extends Thread {

   protected SmokerMonitor smt;
   protected int which;  // 1: tobacco, 2: paper, 3: matches.
   int	     cigSmoked = 0;

   public Smoker(int who, SmokerMonitor m) { which = who; smt = m ; }

   public void run() {
     while ( true ) {
	SmokerCons.printMessage(which, "waiting " + SmokerCons.ingredients[which], 3000);
	int ing = smt.getIngredient(which);
	cigSmoked ++;
	SmokerCons.printMessage(which, "got " + SmokerCons.ingredients[ing], 1000); 
	SmokerCons.printMessage(which, "smoking " + cigSmoked, 6000); 
	SmokerCons.printMessage(which, "relaxing", 10000);
	try { sleep(3000); } catch (InterruptedException e) {}
     }
   }
}

class Agent extends Thread {

   protected SmokerMonitor smt;
   public Agent( SmokerMonitor m ) { smt = m; }

   public void run() {
     while ( true ) {
	double r = Math.random();
	int k = r < 1.0/3.0 ? 1 : r >= 2.0/3.0 ? 3 : 2;
	SmokerCons.printMessage(0, "making " + SmokerCons.ingredients[k], 1000);
	SmokerCons.printMessage(0, "putting " + SmokerCons.ingredients[k], 1000);
	smt.putIngredient(k);
	SmokerCons.printMessage(0, "relaxing", 2000);
	try { sleep(3000); } catch (InterruptedException e) {}
     }
   }
}

class SmokerMonitor {

   protected int ingredient;

   SmokerMonitor() { ingredient = -1; }  // -1: no ingredient.
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
	return ing;
   }
}

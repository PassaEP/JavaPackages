import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class RiverCross extends CloseableJFrame implements ActionListener {
   ImageIcon iconWest = new ImageIcon(RCCons.iconFiles[0]);
   ImageIcon iconEast = new ImageIcon(RCCons.iconFiles[1]);

   JButton btnCross[] = { new JButton(iconWest), new JButton(iconEast) };
   JPanel  panSouth = new JPanel();
   JLabel  status = new JLabel("Click button to add a Passager to Cross the River", SwingConstants.CENTER);
   Canvas  cvs[] = { new Canvas(), new Canvas(), new Canvas() };

   RiverCrossMonitor riverMtr;

   public RiverCross() {
	setTitle("River Crossing: No starvation on either side");
	for ( int i = 0; i < btnCross.length; i++ ) { btnCross[i].addActionListener(this); }
	btnCross[0].setToolTipText("From West to East");
	btnCross[1].setToolTipText("From East to West");
	panSouth.add( btnCross[0]); panSouth.add( status); panSouth.add( btnCross[1]);
 	setSize(1000, 300);
	
	Container c = getContentPane();
	c.setLayout( new BorderLayout() );
	c.add(panSouth, BorderLayout.SOUTH);
	for ( int i = 0; i < cvs.length; i ++ ) {
		cvs[i].setSize(getWidth()/3, getHeight());
		cvs[i].setBackground(Color.black);
	}
	c.add(cvs[0], BorderLayout.WEST);
	c.add(cvs[1], BorderLayout.EAST);
	c.add(cvs[2], BorderLayout.CENTER);

	setVisible(true);

	try { Thread.sleep(100); } catch (InterruptedException e) {}
	riverMtr = new RiverCrossMonitor(cvs);
   }

   public void actionPerformed( ActionEvent e ) {
	Object o = e.getSource();
	for ( int side = 0; side < btnCross.length; side++ )
		if ( o == btnCross[side] ) new Passager (side, riverMtr).start() ;
   }
 
   public static void main(String args[] ) { new RiverCross(); }
}


class Passager extends Thread {
   static int   nextPassagerID = 0;
   int mySide, myID;
   RiverCrossMonitor rcMtr;
   public Passager( int side, RiverCrossMonitor mtr) {
	mySide = side; myID = ++ nextPassagerID ;
	rcMtr = mtr;
   }
   
   public void run() {
	// System.out.println("Enter bank queue: " + mySide);

	// -------------------------------------------------------------
	// Line up in the queue on my bank until the passager, then get
	// on the bridge.
	rcMtr.waitOnBankQueueAndGetOnBridge(mySide, myID);
	// System.out.println("Get on bridge: " + mySide);
	// -------------------------------------------------------------

	int myStep = -1;
	while ( myStep <= RCCons.BRIDGE_LENGTH ) {
		myStep = rcMtr.moveForward(mySide, myID, myStep);
		try { sleep( (long) (Math.random() * RCCons.MAX_WALK_SPEED) + RCCons.MIN_WALK_SPEED ); }
		catch ( InterruptedException e) {}
		// System.out.println("Passager " + myID + " is on step " + myStep);
	}
	// System.out.println("Passager " + myID + " is out of bridge.");
   }
}

class RiverCrossMonitor implements ImageObserver {

   int bridgeTo = RCCons.NOBODY;
   boolean bridgeStepEmpty[] = new boolean[RCCons.BRIDGE_LENGTH];
   int letGroupIn[] = { 0, 0 } ;

   Vector passagerGroups[] = { new Vector<Integer>(5), 
	   new Vector<Integer>(RCCons.BRIDGE_LENGTH), new Vector<Integer>(5) };

   String msgs[][] = {{ "West Bank", "Waiting: 0" }, { "East Bank", "Waiting: 0" },
		      { "On Bridge", "Walking: 0" } };
   Canvas cvs[];
   Graphics2D g2d[];
   Image  passagerImages[] = new Image[2];

   public RiverCrossMonitor( Canvas [] cs ) {
	cvs = cs; g2d = new Graphics2D[cs.length];
	for ( int i = 0; i < cs.length; i ++ ) {
		g2d[i] = (Graphics2D) cs[i].getGraphics(); 
		g2d[i].setFont( new Font( "Arial", Font.BOLD, RCCons.FONT_SIZE));
		g2d[i].setColor( Color.white );
	}
	for ( int i = 0; i < bridgeStepEmpty.length; i ++ ) bridgeStepEmpty[i] = true;
	for ( int i = 0; i < passagerImages.length; i ++ )
		passagerImages[i] = cvs[2].getToolkit().getImage(RCCons.iconFiles[i]);
   }

   public synchronized int moveForward(int mySide, int myID, int myStep ) {
	while ( true ) {
		// If it is last step, step out and let other move forward.
		if ( myStep == RCCons.BRIDGE_LENGTH - 1) { // last step
			bridgeStepEmpty[myStep] = true;
			passagerGroups[RCCons.BRIDGE].remove(new Integer(myID));
			if ( groupEmpty(RCCons.BRIDGE)) { // last one step out of bridge.
				letGroupIn[mySide] = 0;
				bridgeTo = (mySide + 1) % 2;
				int numOfWaiting = passagerGroups[bridgeTo].size();
				letGroupIn[bridgeTo] = numOfWaiting <= RCCons.BRIDGE_LENGTH ? numOfWaiting : RCCons.BRIDGE_LENGTH ;
			}
			if ( groupEmpty(RCCons.EAST) && groupEmpty(RCCons.WEST) && groupEmpty(RCCons.BRIDGE) )
				bridgeTo = RCCons.NOBODY;
			showStatus(RCCons.BRIDGE);
			notifyAll();
			return myStep + 1;
		}
		// if passager is not on the last step and the front step is not
		// taken, then move forward. Otherwise wait.
		if ( myStep < RCCons.BRIDGE_LENGTH -1 && bridgeStepEmpty[myStep+1] ) {
			if ( myStep >= 0 ) bridgeStepEmpty[myStep] = true;
			bridgeStepEmpty[myStep+1] = false;
			showStatus(RCCons.BRIDGE);
			notifyAll();
			return myStep + 1;
		}
		showStatus(mySide);
		try { wait(); } catch ( InterruptedException e) {}
	}
   }
	

   // --------------------------------------------------------------------
   // A passager will wait on his/her bank queue and get on the bridge.
   // --------------------------------------------------------------------
   public synchronized void waitOnBankQueueAndGetOnBridge( int mySide, int myID) {

	passagerGroups[mySide].add( new Integer( myID ) );
	showStatus(mySide);

	// ----------------------------------------------------------------
	// The passager has to wait on the bank queue until the following
	// conditions become true:
	//	1. the passager is the first on his side and the bridge is
	//	   empty or
	//	2. no one waiting on other side, bridge has room, passagers
	//	   on bridge are walking in his direction.
	// ----------------------------------------------------------------
	while ( true ) {  
		if ( iAmFirst(mySide, myID) && (groupEmpty( RCCons.BRIDGE ) && bridgeTo == RCCons.NOBODY ||
				letGroupIn[mySide] >= 1 ||
				groupEmpty( (mySide+1) % 2) && bridgeHasRoom() && bridgeTo == mySide ) ) {
			if ( groupEmpty( RCCons.BRIDGE ) ) bridgeTo = mySide;
			// ---------------------------------------------------
			// get off the bank queue and get on to the bridge
			passagerGroups[mySide].remove(0);
			passagerGroups[RCCons.BRIDGE].add( new Integer(myID) );
			// ----------------------------------------------------
			if ( letGroupIn[mySide] > 0 ) letGroupIn[mySide] --;
			showStatus(mySide);
			showStatus(RCCons.BRIDGE);
			notify();
			return ;
		}
		try { wait(); } catch (InterruptedException e) {}
	}
   }

   boolean groupEmpty (int k) { return passagerGroups[k].size() == 0; }
   boolean bridgeHasRoom() { return passagerGroups[RCCons.BRIDGE].size() < RCCons.BRIDGE_LENGTH; }
   boolean iAmFirst(int grp, int ID ) { return passagerGroups[grp].indexOf(new Integer(ID) ) == 0; }

   public void showStatus( int mySide ) {
	if ( mySide == RCCons.BRIDGE ) {
		drawPassagersOnBridge(); 
		msgs[2][1] = "Waiting or Walking: " + passagerGroups[mySide].size();
	} else msgs[mySide][1] = "Waiting: " + passagerGroups[mySide].size();
	g2d[mySide].clearRect(10, 0, cvs[mySide].getWidth(), RCCons.FONT_SIZE + 15) ;
	g2d[mySide].drawString(msgs[mySide][0], 10, RCCons.FONT_SIZE + 10);
	g2d[mySide].clearRect(10, cvs[mySide].getHeight() - RCCons.FONT_SIZE - 10,
				cvs[mySide].getWidth(), RCCons.FONT_SIZE + 15 );
	g2d[mySide].drawString(msgs[mySide][1], 10, cvs[mySide].getHeight() - 10);
   }
   void drawPassagersOnBridge() {

	g2d[2].clearRect(0, (cvs[2].getHeight() - RCCons.IMAGE_HEIGHT) / 2, cvs[2].getWidth(), 2 * RCCons.IMAGE_HEIGHT);
	int stepWidth = cvs[2].getWidth() / RCCons.BRIDGE_LENGTH;
	for ( int i = 0; i < RCCons.BRIDGE_LENGTH; i++ ) {
		int nextIndex = bridgeTo == RCCons.WEST ? i : RCCons.BRIDGE_LENGTH - i - 1;
		if ( ! bridgeStepEmpty[i] )  {
			g2d[2].drawString("X"+i, nextIndex * stepWidth + 20, 
					(cvs[2].getHeight() + RCCons.IMAGE_HEIGHT + 2 * RCCons.FONT_SIZE ) / 2);

			g2d[2].drawImage(passagerImages[bridgeTo], nextIndex * stepWidth + 10,
					(cvs[2].getHeight() - RCCons.IMAGE_HEIGHT) / 2,
					RCCons.IMAGE_WIDTH, RCCons.IMAGE_HEIGHT, this);
		}
	}
   }

   public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
	// System.out.println("Image information flag" + infoflags);
	return true;
   }
}

interface RCCons {
   final int NOBODY = -1, WEST = 0, EAST = 1, BRIDGE = 2, BRIDGE_LENGTH = 4,
   	     MAX_WALK_SPEED = 2000, MIN_WALK_SPEED = 1500, FONT_SIZE = 16,
	     IMAGE_WIDTH = 40, IMAGE_HEIGHT = 40;
   final String [] iconFiles = { "catwest.gif", "catjump.gif" };
}



/** MusicPlay.java simulates the music downloading and playing.

   1. Concurrency
      - The program consists of two pieces of code that need to be executed
	concurrently.
      - One piece of code is enclosed inside a Runnable class call Player
	and the other piece is enclosed inside a Runnable class called Loader.
      - To simplify the problem, the music loader will not loader music from
	resource on internet. Instead, loader will randomly generate music
	notes, 1, 2, 3, 4, 5, 6, 7. The music player will not actualy feed the
	music to sound card. It will print out notes on the screen.

   2. Synchronization
      - Cooperation and competition between the loader and player are needed
	so that player will not play garbage stored in buffer, and loader
	will not overwrite the notes generated before and not played yet.
      - Two buffer or double buffering technology will be used so that
	while one buffer is be loading the other will be played.
      - A monitor class will be defined so that the cooperation and competition
	between the loader and player will be enforced while the concurrency
	is allowed.
*/

public class MusicPlay {

   MusicBuffer	buf1, buf2;
   byte		bufStatus;

   MusicPlayer  player;
   MusicLoader	loader;

   public MusicPlay() {
	buf1 = new MusicBuffer((byte) 1);
	buf2 = new MusicBuffer((byte) 2);
	player = new MusicPlayer(buf1, buf2);
	loader = new MusicLoader(buf1, buf2);
	player.start();
	loader.start();
   }
   
   public static void main(String args[]) {
	new MusicPlay();
   } 
}

class MusicPlayer extends Thread {
   MusicBuffer buf1, buf2;
   public MusicPlayer(MusicBuffer b1, MusicBuffer b2) { buf1 = b1; buf2 = b2; }

   public void run() {
	while ( true ) {
		buf1.play();
		buf2.play();
	}
   }
}

class MusicLoader extends Thread {
   MusicBuffer buf1, buf2;
   public MusicLoader (MusicBuffer b1, MusicBuffer b2) { buf1 = b1; buf2 = b2; }

   public void run() {
	while ( true ) {
		buf1.load();
		buf2.load();
	}
   }
}

class MusicBuffer {

   final byte EMPTY = 0;
   final byte LOADED = 1;

   byte buffer[];
   byte num;
   byte status;

   public MusicBuffer(byte n) {
	buffer = new byte[10];
	num = n;
	status = EMPTY;
   }

   synchronized public void play() {
	while ( status != LOADED ) {
		try { wait() ; } catch (InterruptedException e) { }
	}
	for ( int i = 0; i < buffer.length; i ++ ) {
		try { Thread.sleep(100);	// sleep 100 milliseconds simulating note play time.
		} catch (InterruptedException e) {}
	}
	System.out.print("\t\t\tMusic played: ");
	printNote();
	status = EMPTY;
	System.out.println(" in buffer[" + num + "]");
  	notify();
   }

   synchronized public void load() {
	while (status != EMPTY) {
		try { wait(); } catch (InterruptedException e) {}
	}

	for ( int i = 0; i < buffer.length; i ++ ) {
		buffer[i] = (byte) (Math.random() * 7 + 1);
		try { Thread.sleep((int) ( Math.random() * 50 + 50) ); 	// sleep 100 milliseconds.
		} catch (InterruptedException e) {}
	}
	System.out.print("Music loaded: ");
	printNote();
	status = LOADED;
  	notify();
  }

  protected void printNote() {
	System.out.flush();
	for ( int i = 0; i < buffer.length; i ++ ) System.out.print(buffer[i] + "");
	System.out.println(" in buffer[" + num + "]");
  }
}

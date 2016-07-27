/** Producer and Consumer: No buffer case, busy waiting
	If you take the sleep() away from the while loop from both consumer and producer,
   the program will not work.
*/

import java.awt.*;
import javax.swing.*;

public class ProducerConsumer1 extends CloseableJFrame {
    static Container c;
    static JLabel	consumer = new JLabel("Consumer", SwingConstants.CENTER),
		producer = new JLabel("Producer", SwingConstants.CENTER),
		consumedCount = new JLabel("Consuming: 0", SwingConstants.CENTER),
		producedCount = new JLabel("Producing: 0", SwingConstants.CENTER),
		hamburgerAvail = new JLabel("Hamburger Available", SwingConstants.CENTER),
		consumerStatus = new JLabel("Waiting", SwingConstants.CENTER),
		producerStatus = new JLabel("Making", SwingConstants.CENTER);
    static JPanel
		hamburgerStatus = new JPanel();
   static int	intConsumedCount =0, intProducedCount = 0;
   static boolean	emptyTray = true;

    public  ProducerConsumer1() {
	super( "Hamburger Producer Consumer: Busy Waiting, One hamburger holder");
	c = getContentPane();
	consumer.setForeground(Color.green); consumer.setFont(new Font("NewsRoman", Font.BOLD, 20));
	producer.setForeground(Color.yellow); producer.setFont(new Font("NewsRoman", Font.BOLD, 20));
	consumedCount.setForeground(Color.green); consumedCount.setFont(new Font("NewsRoman", Font.BOLD, 20));
	producedCount.setForeground(Color.yellow); producedCount.setFont(new Font("NewsRoman", Font.BOLD, 20));
	hamburgerAvail.setForeground(Color.white); hamburgerAvail.setFont(new Font("NewsRoman", Font.BOLD, 20));
	consumerStatus.setForeground(Color.green); consumerStatus.setFont(new Font("NewsRoman", Font.BOLD, 20));
	producerStatus.setForeground(Color.yellow); producerStatus.setFont(new Font("NewsRoman", Font.BOLD, 20));
	hamburgerStatus.setForeground(Color.orange); hamburgerStatus.setFont(new Font("NewsRoman", Font.BOLD, 50));
	hamburgerStatus.setBackground(Color.black);

	c.setBackground(Color.black);
	c.setLayout(new BorderLayout());
	JPanel conPane = new JPanel();
	conPane.setBackground(Color.black); conPane.setLayout(new GridLayout(3, 1));
	conPane.add(consumer); conPane.add(consumedCount); conPane.add(consumerStatus); 
	c.add(conPane, BorderLayout.WEST);

	JPanel proPane = new JPanel();
	proPane.setBackground(Color.black); proPane.setLayout(new GridLayout(3, 1));
	proPane.add(producer); proPane.add(producedCount); proPane.add(producerStatus);
	c.add(proPane, BorderLayout.EAST);

	c.add(hamburgerAvail, BorderLayout.NORTH);
	c.add(hamburgerStatus, BorderLayout.CENTER);

	setSize(500, 300);
	show();

	Consumer1 cs = new Consumer1();
	Producer1 pc = new Producer1();
	cs.start(); pc.start();
    }

    public static void main(String argv[]) {
	new ProducerConsumer1() ;
    }
}

class Consumer1 extends Thread {
   public void run() {
	Graphics2D g2 = (Graphics2D) ProducerConsumer1.hamburgerStatus.getGraphics();
	g2.setColor(Color.black);

	while ( true ) {
		int ww = ProducerConsumer1.hamburgerStatus.getWidth(),
		    hh = ProducerConsumer1.hamburgerStatus.getHeight();

		ProducerConsumer1.consumerStatus.setText("Waiting");
		while ( ProducerConsumer1.emptyTray ) // if comment sleep(), program will not work 
			try { Thread.sleep( (long) (Math.random() * 100 + 100)); }
			catch (InterruptedException e) {}

		ProducerConsumer1.intConsumedCount++;
		ProducerConsumer1.consumerStatus.setText("Eating");
		ProducerConsumer1.consumedCount.setText("Consuming: " + ProducerConsumer1.intConsumedCount);
		ProducerConsumer1.hamburgerStatus.repaint();
		ProducerConsumer1.hamburgerAvail.setText("Hamburger Not Avail.");

		ProducerConsumer1.emptyTray = true;
		try { Thread.sleep( (long) (Math.random() * 3000 + 1000)); } catch (InterruptedException e) {}

		ProducerConsumer1.consumerStatus.setText("Relexing");
		try { Thread.sleep( (long) (Math.random() * 3000 + 1000)); } catch (InterruptedException e) {}
	}
   }
}

class Producer1 extends Thread {
   public void run() {
	Graphics2D g2 = (Graphics2D) ProducerConsumer1.hamburgerStatus.getGraphics();
	g2.setColor(new Color(125, 70, 0));
	while ( true ) {
		int ww = ProducerConsumer1.hamburgerStatus.getWidth(),
		    hh = ProducerConsumer1.hamburgerStatus.getHeight();

		ProducerConsumer1.producerStatus.setText("Making");
		ProducerConsumer1.intProducedCount++;
		ProducerConsumer1.producedCount.setText("Producing: " + ProducerConsumer1.intProducedCount);
		try { Thread.sleep( (long) (Math.random() * 3000 + 1000)); } catch (InterruptedException e) {}

		ProducerConsumer1.producerStatus.setText("Waiting"); 
		while ( ! ProducerConsumer1.emptyTray ) // if comment sleep(), program will not work 
			try { Thread.sleep( (long) (Math.random() * 100 + 100)); }
			catch (InterruptedException e) {}

		ProducerConsumer1.producerStatus.setText("Putting");
		try { Thread.sleep( (long) (Math.random() * 100 + 300)); } catch (InterruptedException e) {}

		ProducerConsumer1.hamburgerAvail.setText("Hamburger Avail: " + ProducerConsumer1.intProducedCount);
		g2.fillOval(ww/4, hh/4, ww/2, ww/2);
		ProducerConsumer1.emptyTray = false;
	}
   }
}


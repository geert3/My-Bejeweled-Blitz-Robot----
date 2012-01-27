import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class JewelRobot 
{
	static RoboThread thread = null;

	public static void main(String args[]) throws AWTException
	{
		new PatternBuilder();
		
		JFrame f = new JFrame();
		f.setAlwaysOnTop(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(400, 520);
		f.setLocation(750, 10);
		f.setLayout(new BorderLayout());
		f.setVisible(true);
		
		final JLabel status = new JLabel();
		f.add(status, BorderLayout.NORTH);
		JPanel grid = new JPanel();
		grid.setSize(8*Position.DRAW_BLOCK, 8*Position.DRAW_BLOCK);
		f.add(grid,BorderLayout.CENTER);
		JPanel buttons = new JPanel();
		JButton start = new JButton("start");
		
		final Robot robot = new Robot();
		final LookThread lt = new LookThread(robot, grid);
		lt.setPriority(Thread.NORM_PRIORITY-2);
		lt.start();
		
		start.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				JButton b = (JButton)e.getSource();
				if (b.getText().equals("start"))
				{
					thread = new RoboThread(status, robot, lt);
					thread.setPriority(Thread.NORM_PRIORITY);
					thread.start();
					b.setText("stop");
				}
				else
				{
					thread.doStop();
					b.setText("start");
				}
			}
		});
		buttons.add(start);
		f.add(buttons, BorderLayout.SOUTH);
	}
}

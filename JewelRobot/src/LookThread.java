import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class LookThread extends Thread
{
	final static int EMPTY = 3;
	final static int NORMAL = 2;
	final static int SPECIAL = 1;
	final static int UNKNOWN = 0;

	final static int SCAN_TOP = 320;
	final static int SCAN_LEFT = 260;
	final static int SCAN_BLOCK = 40;
	
	private Position[][] grid = new Position[8][8];
	private Robot robot;
	
	private int[] stats = new int[Gem.getIdxCnt()];
	private JComponent comp;

	private static LookThread singleton = null;
	
	public LookThread(Robot robot, JComponent c)
	{
		singleton = this;
		this.robot = robot;
		comp = c;

		for (int x=0; x<8; x++)
			for (int y=0; y<8; y++)
				grid[x][y] = new Position(this, x, y);
	}

	public static LookThread Get()
	{
		return singleton;
	}

	public Graphics getGraphics()
	{
		return comp.getGraphics();
	}
	
	public void run()
	{
		while (true)
		{
			refresh(false);
			try
			{
				Thread.sleep(400);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	private void refresh(boolean needStable)
	{
		boolean looking = true;
		Date start = new Date();
		
		while (true)
		{
			boolean stable = true;
			looking = !looking;
			
			int numWhite = 0;
			int numBlack = 0;
			
			int[] cnt = new int[Gem.getIdxCnt()];

			BufferedImage scr = robot.createScreenCapture(new Rectangle(SCAN_LEFT,SCAN_TOP,8*SCAN_BLOCK,8*SCAN_BLOCK));	

			getGraphics().drawImage(scr, 0, 120, 8*SCAN_BLOCK,8*SCAN_BLOCK,null);

			for (int x=0; x<8; x++)
			{
				for (int y=0; y<8; y++)
				{
					Position p = grid[x][y];
					Gem g = null;
					if (!looking)
					{
						g = p.dontLook(scr);
					}
					else
					{
						g = p.look(scr);
					}
					if (g == Gem.WHITE)
						numWhite++;
					else
					if (g == Gem.UNKNOWN)
						numBlack++;
					cnt[g.getIdx()]++;
				}
			}
			stats = cnt;
			if (looking)
			{
				if (numWhite > 20)
				{
					// not on real board! freeze!
				}
				else
				{
					if (numBlack > 5)
						stable = false;
					
					if (stable)
						return;
					
					if (!stable && new Date().getTime() - start.getTime() > 300)
						return;
				}
			}
//			try
//			{
//				Thread.sleep(100);
//			}
//			catch (InterruptedException e)
//			{
//			}
		}
	}
	
	public int[] getStats()
	{
		return stats;
	}

	public Position[][] getPositions()
	{
		refresh(false);
		for (int x=0; x<8; x++)
			for (int y=7; y>0; y--)
				if (grid[x][y].getGem() == Gem.UNKNOWN)
				{
					for (int i=y-1; i>=0; i--)
						grid[x][i].setGem(Gem.UNKNOWN);
					break;
				}
		return grid.clone();
	}
}

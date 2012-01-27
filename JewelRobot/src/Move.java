import java.awt.Robot;
import java.awt.event.InputEvent;



public class Move
{
	Position p1;
	Position p2;
	Gem ref;
	
	public Move(Position p1, Position p2, Gem ref)
	{
		this.p1 = p1;
		this.p2 = p2;
		this.ref = ref;
	}
	
	public void apply(Robot robot, LookThread lt, int x, int y, Pattern p, Position[][] pos)
	{
		if (p.toString().equals("xxNX") || p.toString().equals("XNxx"))
		{
			System.out.println("Break here");
		}

		click(robot, p1, p2);
		
		Gem g1 = p1.getGem();
		Gem g2 = p2.getGem();
		p1.setGem(g2==ref?Gem.UNKNOWN:g2);
		p2.setGem(g1==ref?Gem.UNKNOWN:g1);
		int xx = x + p.getX2();
		int yy = y + p.getHeight() - 1;
		int w = p.getWidth();
		if (w < 3)
		{
			w = 1;
			x = xx;
			yy = y + p.getHeight() - 1;
			if (yy > 7)
				yy = 7;
		}
		for (; yy>=0; yy--)
			for (int i=x; i<x+w; i++)
			{
				pos[i][yy].setGem(Gem.UNKNOWN);
				pos[i][yy].draw();
			}
		if (p.getPatternString().equals("xxNxx/nnXnn") ||
			p.getPatternString().equals("nnXnn/xxNxx") ||
			p.getPatternString().equals("xn/xn/NX/xn/xn") ||
			p.getPatternString().equals("nx/nx/XN/nx/nx"))
		{
			int xxx = x;
			int yyy = y;
			boolean sure = false;
			if (p.getPatternString().equals("nnXnn/xxNxx"))
				yyy = y+1;
			else
			if (p.getPatternString().equals("xn/xn/NX/xn/xn"))
			{
				yyy = y+4;
				sure = true;
			}
			else
			if (p.getPatternString().equals("nx/nx/XN/nx/nx"))
			{
				yyy = y+4;
				xxx = x+1;
				sure = true;
			}
			
			// then touch most occurring neighbour after first reading board again
			Position[][] pos2 = LookThread.Get().getPositions();
			if (sure)
			{
				touchBestNeighbour(robot, lt, xxx, yyy, pos2);
			}
			else
			{
				for (int xxxx=xxx; xxxx<8; xxxx++)
					if (pos2[xxxx][yyy].getGem() == Gem.WHITE)	// they "look" white
						touchBestNeighbour(robot, lt, xxxx, yyy, pos2);
			}
		}
	}
	
	public Gem getRef()
	{
		return ref;
	}

	public Position getP1()
	{
		return p1;
	}

	public Position getP2()
	{
		return p2;
	}
	
	private void touchBestNeighbour(Robot robot, LookThread lt, int x, int y, Position[][] pos)
	{
		if (x >= 8)
			return;
		int[] stats = lt.getStats();
		Gem n = Gem.UNKNOWN;
		Gem e = Gem.UNKNOWN;
		Gem s = Gem.UNKNOWN;
		Gem w = Gem.UNKNOWN;
		int maxOcc = 0;
		Position bestPos = this.p2;
		if (y > 0 && y < 8)
		{
			n = pos[x][y-1].getGem();
			maxOcc = stats[n.getIdx()];
			bestPos = pos[x][y-1];
		}
		if (x < 7)
		{
			e = pos[x+1][y].getGem();
			if (stats[e.getIdx()] > maxOcc || bestPos.getGem() == Gem.UNKNOWN)
			{
				maxOcc = stats[e.getIdx()];
				bestPos = pos[x+1][y];
			}
		}
		if (y < 7)
		{
			s = pos[x][y+1].getGem();
			if (stats[s.getIdx()] > maxOcc || bestPos.getGem() == Gem.UNKNOWN)
			{
				maxOcc = stats[s.getIdx()];
				bestPos = pos[x][y+1];
			}
		}
		if (x > 0)
		{
			w = pos[x-1][y].getGem();
			if (stats[w.getIdx()] > maxOcc || bestPos.getGem() == Gem.UNKNOWN)
			{
				maxOcc = stats[w.getIdx()];
				bestPos = pos[x-1][y];
			}				
		}
		click(robot, p2, bestPos);
		Gem ref = bestPos.getGem();
		// clear all bestPos gems on the screen plus all upward
		for (int xx=0; xx<8; xx++)
			for (int yy=0; yy<8; yy++)
				if (pos[xx][yy].getGem() == ref)
					for (int i=yy; i>=0; i--)
					{			
						pos[xx][i].setGem(Gem.UNKNOWN);
						pos[xx][i].draw();
					}
	}
	
	private void click(Robot robot, Position p1, Position p2)
	{
		robot.mouseMove(LookThread.SCAN_LEFT+p1.getMiddleX(), LookThread.SCAN_TOP+p1.getMiddleY());
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseMove(LookThread.SCAN_LEFT+p2.getMiddleX(), LookThread.SCAN_TOP+p2.getMiddleY());
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
//		try
//		{
//			Thread.sleep(50);
//		} catch (InterruptedException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
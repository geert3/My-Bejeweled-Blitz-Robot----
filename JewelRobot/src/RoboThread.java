import java.awt.Robot;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JLabel;


public class RoboThread extends Thread
{
	boolean stopping = false;
	JLabel status = null;
	private LookThread lookthread;
	private Robot robot;
	
	public RoboThread(JLabel status, Robot robot, LookThread lookthread)
	{	
		this.status = status;
		this.lookthread = lookthread;
		this.robot = robot;
	}
	
	@Override
	public void run()
	{
		Date start = new Date();
		
		while (!stopping)
		{
			if (new Date().getTime() - start.getTime() > 76*1000)
				break;
			int unknown = getMoves();
			status.setText("ok");
			try
			{
				Thread.sleep(unknown*5);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private int getMoves()
	{
		ArrayList<Move> moves = new ArrayList<Move>();
		Position[][] pos = lookthread.getPositions();//.clone();
//		boolean haveMul = checkMultiplierMoves(pos);
		checkPreferredMoves(pos);
		boolean doneSpecial = false;
		for (int y=0; y<8; y++)
			for (int x=0; x<8; x++)
				for (Pattern p: PatternBuilder.getRegularPatterns())
				{
					Move m = p.match(pos, x, y, true);
					if (m == null)
						continue;
					m.apply(robot, lookthread, x, y, p, pos);
					x+=p.getWidth()-1;
					doneSpecial = true;
				}
		if (doneSpecial)
			checkPreferredMoves(pos);
		for (int y=0; y<8; y++)
			for (int x=0; x<8; x++)
				for (Pattern p: PatternBuilder.getRegularPatterns())
				{
					Move m = p.match(pos, x, y, false);
					if (m == null)
						continue;
					m.apply(robot, lookthread, x, y, p, pos);
					x+=p.getWidth()-1;
//					if (haveMul)
//						checkMultiplierMoves(pos);
					checkPreferredMoves(pos);
				}
		if (moves.size() == 0)
		{
			status.setText("Can't find move");
			return 8*8;
		}
		int black = 0;
		for (int y=0; y<8; y++)
			for (int x=0; x<8; x++)
				if (pos[x][y].getGem() == Gem.UNKNOWN)
					black++;
		return black;
	}
	
	private boolean checkMultiplierMoves(Position[][] pos)
	{
		boolean haveMul = false;
		for (int y=0; y<8; y++)
			for (int x=0; x<8; x++)
				if (pos[x][y].isMultiplier())
				{
					haveMul = true;
					int xx=x-3;
					if (xx<0) xx=0;
					for (; xx<x+3 && xx<8; xx++)
					{
						int yy=y-3;
						if (yy<0) yy=0;
						for (; yy<y+3 && yy<8; yy++)
						{
							for (Pattern p: PatternBuilder.getRegularPatterns())
							{
								Move m = p.match(pos, xx, yy, false, pos[x][y].getGem());
								if (m == null)
									continue;
								m.apply(robot, lookthread, xx, yy, p, pos);
								xx=8; yy=8;
							}
						}
					}
				}
		return haveMul;
	}

	private void checkPreferredMoves(Position[][] pos)
	{
		for (Pattern p: PatternBuilder.getPreferredPatterns())
			for (int y=0; y<8; y++)
				for (int x=0; x<8; x++)
				{
					Move m = p.match(pos, x, y, false);
					if (m == null)
						continue;
					m.apply(robot, lookthread, x, y, p, pos);
					x+=p.width;
				}
	}
	
	public void doStop()
	{
		stopping = true;
		try
		{
			join();
		} 
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

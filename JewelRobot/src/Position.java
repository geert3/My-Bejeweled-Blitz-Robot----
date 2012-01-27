import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;


public class Position
{
	static final int DRAW_BLOCK = 8;

	static final int DRAW_TOP = 40;
	static final int DRAW_LEFT = 10;

	final int x;	// x-index 
	final int y;	// y-index
	final int xx;	// x coord on source image
	final int yy;	// y coord on source image
	final int xxx;	// x coord on output thumbnail
	final int yyy;	// y coord on output thumbnail
	Gem gem;
	long lastHash = 0;
	long newHash = 0;
	
	int white = 0;
	int black = 0;

	int special = 0;
	int multiplier = 0;
	boolean specialTouched = false;

	private LookThread lt;
	
	public Position(LookThread lt, int x, int y)
	{
		this.lt = lt;
		this.x = x;
		this.y = y;
		this.xx = x * LookThread.SCAN_BLOCK;
		this.yy = y * LookThread.SCAN_BLOCK;
		this.xxx = DRAW_LEFT + x * DRAW_BLOCK;
		this.yyy = DRAW_TOP + y * DRAW_BLOCK;
	}

	public Gem look(BufferedImage scr)
	{
		Gem newgem = pick(scr);
		if (newgem == Gem.UNKNOWN || gem != newgem)
		{
			special = 0;
			multiplier = 0;
			lastHash = newHash;
			gem = newgem;
			drawUnknown();
			return Gem.UNKNOWN;
		}	

		// special gems are constantly moving
		if (newHash == lastHash)
			special = 0;
		else
			special++;
		lastHash = newHash;

		// recognizing multiplier by amount of white/black in image
		if (gem.calibrate(white, black))
		{
			multiplier = 0;
		}
		else
		if ((gem == Gem.WHITE && black > 40) ||
			(gem != Gem.WHITE && white > 7))
		{
			multiplier++;
//			if (multiplier > 3)
//				System.out.println(x+"/"+y+": "+gem+" white="+white+"/"+gem.getWhite()+"; black="+black+"/"+gem.getBlack());
		}
		else
		{
			multiplier = 0;
		}
		
		draw();
		
		return gem;
	}
	
	public Gem dontLook(BufferedImage scr)
	{
		Gem newgem = pick(scr);
		if (newgem != gem)
		{
			special = 0;
			lastHash = newHash;
		}
		gem = newgem;
		return gem;
	}

	public void draw()
	{
		if (multiplier > 1)
			drawMultiplier();
		else
		if (special > 1)
			drawSpecial();
		else
			drawNormal();
	}

	private void drawSpecial()
	{
		Graphics g = lt.getGraphics();
		if (g == null)
			return;
		Color other = Color.black;
		g.setColor(gem.getColor());
		g.drawRect(xxx, yyy, 7, 7);
		g.setColor(other);
		g.drawRect(xxx+1, yyy+1, 5, 5);
		g.setColor(gem.getColor());
		g.drawRect(xxx+2, yyy+2, 3, 3);
		g.setColor(other);
		g.drawRect(xxx+3, yyy+3, 1, 1);
	}

	private void drawMultiplier()
	{
		Graphics g = lt.getGraphics();
		if (g == null)
			return;
		Color c, o;
		o = Color.black;
		if (multiplier % 2 == 0)
		{
			c = o;
			o = gem.getColor();
		}
		else
		{
			c = gem.getColor();
		}
		g.setColor(c);
		g.drawRect(xxx, yyy, 7, 7);
		g.setColor(o);
		g.drawRect(xxx+1, yyy+1, 5, 5);
		g.setColor(c);
		g.drawRect(xxx+2, yyy+2, 3, 3);
		g.setColor(o);
		g.drawRect(xxx+3, yyy+3, 1, 1);
	}

	private void drawNormal()
	{
		Graphics g = lt.getGraphics();
		if (g == null)
			return;
		g.setColor(gem.getColor());
		g.drawRect(xxx, yyy, 7, 7);
		g.drawRect(xxx+1, yyy+1, 5, 5);
		g.drawRect(xxx+2, yyy+2, 3, 3);
		g.drawRect(xxx+3, yyy+3, 1, 1);
	}

	private void drawUnknown()
	{
		Graphics g = lt.getGraphics();
		if (g == null)
			return;
		g.setColor(Color.black);
		g.drawRect(xxx, yyy, 7, 7);
		g.drawRect(xxx+1, yyy+1, 5, 5);
		g.drawRect(xxx+2, yyy+2, 3, 3);
		g.drawRect(xxx+3, yyy+3, 1, 1);
	}

	private void copyPixel(int x, int y, int rgb)
	{
		Graphics g = lt.getGraphics();
		if (g == null)
			return;
		g.setColor(new Color(rgb));
		g.drawLine(80+x, 40+y, 80+x, 40+y);
	}

	public Gem pick(BufferedImage scr)
	{	
		int[] match = new int[Gem.getIdxCnt()];
		white = 0;
		black = 0;
		for (int i=0; i<Gem.getIdxCnt(); i++)
			match[i] = 0;
		newHash = 0;
		for (int x=0, i=xx+10; i<xx+LookThread.SCAN_BLOCK-10; x++, i+=2)
			for (int y=0, j=yy+10; j<yy+LookThread.SCAN_BLOCK-10; y++, j+=2)
			{					
				int rgb = scr.getRGB(i, j);
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = (rgb >> 0) & 0xff;
				if (r == g && g == b && r > 200)
					white++;
				else
				if (r + g + b < 240)
				{
					rgb = 0;
					//copyPixel(i/2-this.x*10, j/2-this.y*10, 0);
					black++;
					continue;
				}
				newHash += r+g+b;

				Gem bestMatch = Gem.UNKNOWN;
				int leastDiff = 99999;
				for (Gem gem: Gem.gems)
				{
					for (int rgb2: gem.getColors())
					{
						int rr = (rgb2 >> 16) & 0xff;
						int gg = (rgb2 >> 8) & 0xff;
						int bb = (rgb2 >> 0) & 0xff;
						int d = Math.abs(r-rr) + Math.abs(g-gg) + Math.abs(b-bb);
						if (d < leastDiff)
						{
							leastDiff = d;
							bestMatch = gem;
						}
					}
				}
				//copyPixel(i/2-this.x*10, j/2-this.y*10, rgb);
				match[bestMatch.getIdx()]++;
//				{
//					int r2 = (rgb >> 16) & 0xff;
//					int g2 = (rgb >> 8) & 0xff;
//					int b2 = (rgb >> 0) & 0xff;
//					System.out.println("least diff "+leastDiff+" for color "+r2+"/"+g2+"/"+b2+" in "+bestMatch);
//				}

			}
		int maxMatch = 0;
		int secondMatch = 0;
		Gem secondGem = Gem.UNKNOWN;
		Gem maxGem = Gem.UNKNOWN;
		for (Gem gem: Gem.gems)
			if (match[gem.getIdx()] >= maxMatch)
			{
				secondGem = maxGem;
				secondMatch = maxMatch;
				maxMatch = match[gem.getIdx()];
				maxGem = gem;
			}
		if (match[secondGem.getIdx()] == match[maxGem.getIdx()])
			return Gem.UNKNOWN;
		if (maxGem == Gem.WHITE && secondMatch > 30)	// watch out for multiplier with a lot of White
			return secondGem;
		//System.out.println(x+"/"+y+": best overall match "+maxGem+" with "+maxMatch+" matches. 2nd best "+secondGem);
		return maxGem;
	}

	public boolean isSpecial()
	{
		return special > 1 || multiplier > 1;
	}

	public boolean isMultiplier()
	{
		return multiplier != 0;
	}
	
	public Gem getGem()
	{
		return gem;
	}

	public void setGem(Gem gem)
	{
		this.gem = gem;
	}

	public int getMiddleX()
	{
		return xx + LookThread.SCAN_BLOCK/2;
	}

	public int getMiddleY()
	{
		return yy + LookThread.SCAN_BLOCK/2;
	}	
}
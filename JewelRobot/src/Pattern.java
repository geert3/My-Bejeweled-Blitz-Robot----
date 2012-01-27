
public class Pattern implements Comparable<Pattern>
{
	String p;
	String[] lines;
	int x1 = -1;	// x/y of 1st entry to move (Reference Gem)
	int y1 = -1;
	int x2 = -1;	// x/y of 2nd entry to move (irrelevant Gem - everything above this x/y will become unknown)
	int y2 = -1;
	int width;
	int height;
	
	public Pattern(String p)
	{
		this.p = p;
		lines = p.split("/");
		height = lines.length;
		width = lines[0].length();
		for (int y=0; y<height; y++)
		{
			if (x1 == -1)
			{
				x1 = lines[y].indexOf("X");
				if (x1 != -1)
					y1 = y;
			}
			if (x2 == -1)
			{
				x2 = lines[y].indexOf("N");
				if (x2 != -1)
					y2 = y;
			}
		}
		System.out.println("Pattern "+p+" "+x1+"/"+y1+"<>"+x2+"/"+y2);
	}

	/** match the pattern against the grid but require "ref" gems
	 */
	public Move match(Position[][] pos, int xx, int yy, boolean wantSpecial, Gem ref)
	{
		if (xx > 8-width)
			return null;
		if (yy > 8-height)
			return null;
		Gem reference = pos[xx+x1][yy+y1].getGem();
		if (reference == Gem.UNKNOWN)
			return null;
		if (ref != reference)
			return null;
		return match(pos, xx, yy, wantSpecial);		
	}
	
	/** match the pattern against the grid, top-left location xx/yy
	 *  returns null if no match, a Move object if match
	 */
	public Move match(Position[][] pos, int xx, int yy, boolean wantSpecial)
	{
		boolean haveSpecial = false;
		if (xx > 8-width)
			return null;
		if (yy > 8-height)
			return null;
		Gem reference = pos[xx+x1][yy+y1].getGem();
		if (reference == Gem.UNKNOWN)
			return null;
		boolean haveX = false;
		boolean belowXline = false;
		for (int y=0; y<height; y++)
		{
			for (int x=0; x<width; x++)
			{
				Gem thisGem = pos[xx+x][yy+y].getGem();
				if (belowXline && thisGem == Gem.UNKNOWN)
					return null;
				if (lines[y].charAt(x) == 'x' ||
					lines[y].charAt(x) == 'X')
				{		
					haveX = true;
					if (thisGem != reference)
						return null;
					if (pos[xx+x][yy+y].isSpecial())
						haveSpecial = true;
				}
			}
			if (haveX)
				belowXline = true;
		}
		if (wantSpecial && !haveSpecial)
			return null;
	//	System.out.println(xx+"/"+yy+": special match "+p+" --> "+x1+"/"+y1+"<>"+x2+"/"+y2);
		return new Move(pos[xx+x1][yy+y1], pos[xx+x2][yy+y2], reference);
	}

	public int getY2()
	{
		return y2;
	}

	public int getX2()
	{
		return x2;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public String getPatternString()
	{
		return p;
	}

	public String toString()
	{
		return p;
	}

	@Override
	public int compareTo(Pattern o)
	{
		if (getHeight() == o.getHeight())
			return o.getWidth() - getWidth();	// smallest width first
		return getHeight() - o.getHeight();	// largest height first
	}
}
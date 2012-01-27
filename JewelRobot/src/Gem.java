import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


public class Gem
{
	private static int idxCnt = 0;

	public static final Gem YELLOW = new Gem("yellow", Color.yellow)
	{
		public int[][] getColorsArray()
		{
			return new int[][] {
					{ 193, 136, 6 },
					{ 251, 209, 14 },
					{ 170, 168, 20 },
					{ 145, 91, 4 },
					{ 252, 248, 32 },
					{ 215, 152, 8 },
					{ 173, 116, 5 },
					{ 169, 167, 43 },
					{ 252, 252, 65 },
					{ 243, 174, 10 },
					{ 251, 216, 40 },
					{ 252, 190, 36 }
			};
		}		
	};

	public static final Gem GREEN = new Gem("green", Color.green)
	{
		public int[][] getColorsArray()
		{
			return new int[][] {
					{ 6, 150, 22 },
					{ 87, 254, 134 },
					{ 8, 214, 35 },
					{ 41, 216, 75 },
					{ 13, 84, 31 },
					{ 10, 179, 31 },
					{ 4, 240, 32 },
					{ 38, 239, 79 },
					{ 5, 121, 18 },
					{ 120, 254, 180 },
					{ 15, 180, 68 },
					{ 60, 251, 100 }
			};
		}		
	};

	public static final Gem BROWN = new Gem("brown", new Color(0xff8800))
	{
		public int[][] getColorsArray()
		{
			return new int[][] {
					{ 252, 139, 17 },
					{ 173, 76, 26 },
					{ 162, 39, 4 },
					{ 250, 115, 19 },
					{ 252, 215, 104 },
					{ 240, 76, 9 },
					{ 237, 91, 17 },
					{ 246, 152, 65 },
					{ 252, 246, 121 },
					{ 251, 183, 82 },
					{ 196, 57, 7 },
					{ 252, 172, 44 },
					{ 252, 254, 150 }
			};
		}		
	};

	public static final Gem RED = new Gem("red", Color.red)
	{
		public int[][] getColorsArray()
		{
			return new int[][] {
					{ 164, 2, 12 },
					{ 186, 25, 46 },
					{ 225, 4, 21 },
					{ 243, 26, 54 },
					{ 246, 35, 67 },
					{ 121, 2, 7 },
					{ 250, 44, 82 },
					{ 252, 65, 111 },
					{ 182, 13, 31 },
					{ 251, 55, 101 },
					{ 252, 78, 129 }
			};
		}		
	};

	public static final Gem BLUE = new Gem("blue", Color.blue)
	{
		public int[][] getColorsArray()
		{
			return new int[][] {
					{ 15, 145, 250 },
					{ 4, 59, 161 },
					{ 4, 98, 144 },
					{ 28, 212, 252 },
					{ 13, 99, 211 },
					{ 6, 44, 88 },
					{ 14, 121, 244 },
					{ 30, 181, 252 },
					{ 4, 74, 192 },
					{ 50, 239, 252 },
					{ 16, 60, 97 },
					{ 5, 73, 157 },
					{ 4, 103, 172 },
					{ 4, 103, 244 }
			};
		}		
	};

	public static final Gem WHITE = new Gem("white", Color.white)
	{
		public int[][] getColorsArray()
		{
			return new int[][] {
					{ 200, 200, 200 },
					{ 169, 170, 169 },
					{ 232, 232, 232 },
					{ 215, 215, 215 },
					{ 251, 253, 251 },
			};
		}		
	};

	public static final Gem PURPLE = new Gem("purple", new Color(0xff00ff))
	{
		public int[][] getColorsArray()
		{
			return new int[][] {
					{ 245, 124, 245 },
					{ 159, 3, 159 },
					{ 246, 69, 246 },
					{ 207, 7, 207 },
					{ 249, 38, 249 },
					{ 245, 13, 245 },
					{ 252, 182, 252 },
					{ 252, 152, 252 },
					{ 246, 97, 246 },
					{ 109, 11, 109 },
			};
		}		
	};

	public static final Gem UNKNOWN = new Gem("unknown", Color.black);

	public static ArrayList<Gem> gems = new ArrayList<Gem>(Arrays.asList( YELLOW, BROWN, RED, GREEN, BLUE, WHITE, PURPLE ));
	
	private int idx = 0;
	
	private Color c;
	private String name;
	private int white = 0;
	private int black = 0;
	private int calibrating = 0;
	
	private Gem(String name, Color c)
	{
		this.name = name;
		this.c = c;
		idx = idxCnt;
		idxCnt++;
	}

	public int[][] getColorsArray()
	{
		return null;
	}
	
	HashSet<Integer> colors = null;
	public HashSet<Integer> getColors()
	{
		if (colors == null)
		{
			colors = new HashSet<Integer>();
			for (int[] rgb: getColorsArray())
			{
				int r=rgb[0];
				int g=rgb[1];
				int b=rgb[2];
				colors.add(new Integer((r<<16) + (g<<8) + b));
			}
		}
		return colors;
	}
	
	public boolean calibrate(int w, int b)
	{
		if (calibrating >= 20)
			return false;
		calibrating++;
		black += b;
		white += w;
//		if (calibrating == 20)
//			System.out.println(this.toString()+": white = "+(white/20)+", black = "+(black/20));
		return true;
	}
	
	public int getWhite()
	{
		return white/20;
	}
	
	public int getBlack()
	{
		return black/20;
	}

	public int getIdx()
	{
		return idx;
	}
	
	public static int getIdxCnt()
	{
		return idxCnt;
	}

	public Color getColor()
	{
		return c;
	}

	/** does this color appear in the reference set? */
	public boolean match(int x, int y, int r, int g, int b)
	{
		int distance = 99999;
		for (int rgb: getColors())
		{
			int rr = (rgb >> 16) & 0xff;
			int gg = (rgb >> 8) & 0xff;
			int bb = (rgb >> 0) & 0xff;
			int d = Math.abs(r-rr) + Math.abs(g-gg) + Math.abs(b-bb);
//			if (d < 20)
//				return true;
			if (d < distance)
				distance = d;
		}
		
		return distance < 20;
//		return false;
	}
	
	public String toString()
	{
		return name;
	}
}
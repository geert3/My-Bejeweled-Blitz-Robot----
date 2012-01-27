import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


public class PatternBuilder
{
	private static ArrayList<String> preferred =
		new ArrayList<String>(Arrays.asList( "xxNxx/nnXnn", "nnX/xxN/nnx/nnx", "xNxx/nXnn" ));
	private static ArrayList<String> regular =
		new ArrayList<String>(Arrays.asList( "xxNX", "XNxx", "xNx/nXn", "Xnn/Nxx" ));
	
	private static ArrayList<Pattern> preferredPatterns = new ArrayList<Pattern>();
	private static ArrayList<Pattern> regularPatterns = new ArrayList<Pattern>();
	
	public PatternBuilder()
	{
		for (String bp: new ArrayList<String>(preferred))
			addVariants(preferred, bp);
		for (String bp: new ArrayList<String>(regular))
			addVariants(regular, bp);
		
		for (String p: preferred)
		{
//			System.out.println(p);
			preferredPatterns.add(new Pattern(p));
		}
		HashSet<Pattern> hs = new HashSet<Pattern>();
		for (String p: regular)
		{
//			System.out.println(p);
			hs.add(new Pattern(p));
		}
		regularPatterns = new ArrayList<Pattern>(hs);
	}

	private void addVariants(ArrayList<String> patterns, String bp)
	{
		if (!patterns.contains(mirrorX(bp)))
			patterns.add(mirrorX(bp));
		if (!patterns.contains(mirrorY(bp)))
			patterns.add(mirrorY(bp));
		if (!patterns.contains(mirrorY(mirrorX(bp))))
			patterns.add(mirrorY(mirrorX(bp)));

		bp = rot(bp);
		if (!patterns.contains(bp))
			patterns.add(bp);
		if (!patterns.contains(mirrorX(bp)))
			patterns.add(mirrorX(bp));
		if (!patterns.contains(mirrorY(bp)))
			patterns.add(mirrorY(bp));
		if (!patterns.contains(mirrorY(mirrorX(bp))))
			patterns.add(mirrorY(mirrorX(bp)));
	
		bp = rot(bp);
		if (!patterns.contains(bp))
			patterns.add(bp);
		if (!patterns.contains(mirrorX(bp)))
			patterns.add(mirrorX(bp));
		if (!patterns.contains(mirrorY(bp)))
			patterns.add(mirrorY(bp));
		if (!patterns.contains(mirrorY(mirrorX(bp))))
			patterns.add(mirrorY(mirrorX(bp)));
		
		bp = rot(bp);
		if (!patterns.contains(bp))
			patterns.add(bp);
		if (!patterns.contains(mirrorX(bp)))
			patterns.add(mirrorX(bp));
		if (!patterns.contains(mirrorY(bp)))
			patterns.add(mirrorY(bp));
		if (!patterns.contains(mirrorY(mirrorX(bp))))
			patterns.add(mirrorY(mirrorX(bp)));
	}
	
	public String rot(String pattern)
	{
		String[] lines = pattern.split("/");
		String newPattern = "";
		for (int j=0; j<lines[0].length(); j++)
		{
			for (int i=0; i<lines.length; i++)
				newPattern += lines[i].charAt(j);
			if (j+1 < lines[0].length())
				newPattern += "/";
		}
		return newPattern;
	}
	
	public String mirrorX(String pattern)
	{
		String[] lines = pattern.split("/");
		String newPattern = "";
		for (int i=0; i<lines.length; i++)	
		{
			for (int j=lines[0].length()-1; j>=0; j--)
				newPattern += lines[i].charAt(j);
			if (i+1 < lines.length)
				newPattern += "/";
		}
		return newPattern;
	}

	public String mirrorY(String pattern)
	{
		String[] lines = pattern.split("/");
		String newPattern = "";
		for (int i=lines.length-1; i>=0; i--)	
		{
			newPattern += lines[i];
			if (i != 0)
				newPattern += "/";
		}
		return newPattern;
	}

	public static ArrayList<Pattern> getPreferredPatterns()
	{
		return preferredPatterns;
	}

	public static ArrayList<Pattern> getRegularPatterns()
	{
		return regularPatterns;
	}
}
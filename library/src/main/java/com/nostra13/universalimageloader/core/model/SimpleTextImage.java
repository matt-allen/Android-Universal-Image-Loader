package com.nostra13.universalimageloader.core.model;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

/**
 * // TODO Add class description
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class SimpleTextImage implements ImageServiceOptions
{
	@IntDef({TYPEFACE_NORMAL, TYPEFACE_THIN, TYPEFACE_CONDENSED, TYPEFACE_MONOSPACE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Typeface{}
	public static final int TYPEFACE_NORMAL = 0;
	public static final int TYPEFACE_THIN = 1;
	public static final int TYPEFACE_MONOSPACE = 2;
	public static final int TYPEFACE_CONDENSED = 3;

	@IntDef({STYLE_NORMAL, STYLE_ITALIC, STYLE_BOLD})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Style{}
	public static final int STYLE_ITALIC = 0;
	public static final int STYLE_BOLD = 1;
	public static final int STYLE_NORMAL = 2;

	@IntDef({COLOUR_RED, COLOUR_PINK, COLOUR_PURPLE, COLOUR_DEEP_PURPLE,
			COLOUR_INDIGO, COLOUR_BLUE, COLOUR_TEAL, COLOUR_DEEP_ORANGE, COLOUR_RANDOM})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Colour{}
	public static final int COLOUR_RED = -769226;
	public static final int COLOUR_PINK = -1499549;
	public static final int COLOUR_PURPLE = -6543440;
	public static final int COLOUR_DEEP_PURPLE = -10011977;
	public static final int COLOUR_INDIGO = -12627531;
	public static final int COLOUR_BLUE = -14575885;
	public static final int COLOUR_TEAL = -16738680;
	public static final int COLOUR_DEEP_ORANGE = -43230;
	public static final int COLOUR_RANDOM = -1;

	private String text;
	private boolean useInitials;
	private int typeFace;
	private int style;
	private int colour;

	public SimpleTextImage(String text, @Colour int colour, boolean useInitials, @Typeface int typeFace, @Style int style)
	{
		this.text = text;
		this.useInitials = useInitials;
		this.typeFace = typeFace;
		this.style = style;
		this.colour = colour;
	}

	public SimpleTextImage(String text, boolean useInitials)
	{
		this(text, COLOUR_RANDOM, useInitials, TYPEFACE_THIN, STYLE_NORMAL);
	}

	public SimpleTextImage(String text)
	{
		this(text, (!TextUtils.isEmpty(text) && text.length() > 1));
	}

	public SimpleTextImage()
	{
		this(null);
	}

	public String getText()
	{
		return text;
	}

	public boolean isUsingInitials()
	{
		return useInitials;
	}

	@Typeface
	public int getTypeFace()
	{
		return typeFace;
	}

	@Style
	public int getStyle()
	{
		return style;
	}

	@Colour
	public int getColour()
	{
		return colour;
	}

	protected int getRandomColour()
	{
		int[] colours = new int[]{COLOUR_RED, COLOUR_PINK, COLOUR_PURPLE, COLOUR_DEEP_PURPLE,
				COLOUR_INDIGO, COLOUR_BLUE, COLOUR_TEAL, COLOUR_DEEP_ORANGE, COLOUR_RANDOM};
		return colours[new Random().nextInt(colours.length-1)];
	}

	public String getInitials()
	{
		String[] names = text.split(" ");
		String returnable;
		if (names.length > 1)
		{
			returnable = names[0].substring(0,1);
			returnable += names[names.length-1].substring(0,1);
		}
		else
		{
			returnable = names[0].substring(0,1);
		}
		return returnable;
	}

	@Override
	public String createUrl()
	{
		return String.format("text://%s/%s/%s/%s/%s", text, String.valueOf((colour == COLOUR_RANDOM?getRandomColour():colour)),
				(useInitials ? "1" : "0"), String.valueOf(typeFace), String.valueOf(style));
	}

	@Override
	public void fromUrl(String url)
	{
		String[] string = url.split("/");
		text = string[2];
		colour = Integer.parseInt(string[3]);
		String initials = string[4];
		if (initials.equalsIgnoreCase("1"))
		{
			useInitials = true;
		}
		typeFace = Integer.parseInt(string[5]);
		style = Integer.parseInt(string[6]);
	}
}
package com.nostra13.universalimageloader.core.model;

import android.graphics.Color;
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

//	@IntDef({COLOUR_RED, COLOUR_PINK, COLOUR_PURPLE, COLOUR_DEEP_PURPLE,
//			COLOUR_INDIGO, COLOUR_BLUE, COLOUR_TEAL, COLOUR_DEEP_ORANGE, COLOUR_RANDOM})
//	@Retention(RetentionPolicy.SOURCE)
//	public @interface Colour{}
	public static final int COLOUR_RED = Color.parseColor("#F44336");
	public static final int COLOUR_PINK = Color.parseColor("#E91E63");
	public static final int COLOUR_PURPLE = Color.parseColor("#9C27B0");
	public static final int COLOUR_DEEP_PURPLE = Color.parseColor("#673AB7");
	public static final int COLOUR_INDIGO = Color.parseColor("#3F51B5");
	public static final int COLOUR_BLUE = Color.parseColor("#2196F3");
	public static final int COLOUR_TEAL = Color.parseColor("#009688");
	public static final int COLOUR_DEEP_ORANGE = Color.parseColor("#FF5722");
	public static final int COLOUR_RANDOM = -1;

	private String text;
	private boolean useInitials;
	private int typeFace;
	private int style;
	private int colour; // -1 for random

	public SimpleTextImage(String text, int colour, boolean useInitials, @Typeface int typeFace, @Style int style)
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
package com.nostra13.universalimageloader.image;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

/**
 * Construct Gmail-style contact initials image from text.
 *
 * Available options on this view:
 * <ul>
 *     <li>Text: The name of the person to create initials from (If useInitials is
 *     {@code true} - see below), otherwise the raw text that will be displayed in the image</li>
 *     <li>Colour: Background colour of the image</li>
 *     <li>useInitials: If you only want to the use the initials of text given
 *     <br/>Default is {@code true}</li>
 *     <li>Typeface: </li>
 * </ul>
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

	public static final int COLOUR_RED = -769226;
	public static final int COLOUR_PINK = -1499549;
	public static final int COLOUR_PURPLE = -6543440;
	public static final int COLOUR_DEEP_PURPLE = -10011977;
	public static final int COLOUR_INDIGO = -12627531;
	public static final int COLOUR_BLUE = -14575885;
	public static final int COLOUR_TEAL = -16738680;
	public static final int COLOUR_DEEP_ORANGE = -43230;
	public static final int COLOUR_WHITE = -1;
	public static final int COLOUR_RANDOM = 0;

	private String text;
	private boolean useInitials = true;
	private int typeFace;
	private int bgColour;
	private int textColour;

	public SimpleTextImage(@NonNull String text, int bgColour, int textColour, boolean useInitials, @Typeface int typeFace)
	{
		this.text = text;
		this.useInitials = useInitials;
		this.typeFace = typeFace;
		this.bgColour = bgColour;
		this.textColour = textColour;
	}

	public SimpleTextImage(String text, boolean useInitials)
	{
		this(text, COLOUR_RANDOM, COLOUR_WHITE, useInitials, TYPEFACE_THIN);
	}

	public SimpleTextImage(String text)
	{
		this(text, true);
	}

	public SimpleTextImage(String text, int colour, int textColour)
	{
		this(text, colour, textColour, true, TYPEFACE_THIN);
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

	public int getTextColour()
	{
		return textColour;
	}

	public int getColour()
	{
		return bgColour;
	}

	public static int getRandomColour()
	{
		int[] colours = new int[]{COLOUR_RED, COLOUR_PINK, COLOUR_PURPLE, COLOUR_DEEP_PURPLE,
				COLOUR_INDIGO, COLOUR_BLUE, COLOUR_TEAL, COLOUR_DEEP_ORANGE};
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
		return String.format("text://%s/%s/%s/%s/%s", text, String.valueOf(bgColour),
				(useInitials ? "1" : "0"), String.valueOf(typeFace), String.valueOf(textColour));
	}

	@Override
	public void fromUrl(String url)
	{
		String[] string = url.split("/");
		text = string[2];
		bgColour = Integer.parseInt(string[3]);
		String initials = string[4];
		if (initials.equalsIgnoreCase("1"))
		{
			useInitials = true;
		}
		typeFace = Integer.parseInt(string[5]);
		textColour = Integer.parseInt(string[6]);
	}
}
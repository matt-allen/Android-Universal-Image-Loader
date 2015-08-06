package com.nostra13.universalimageloader.core.model;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
		this(text, -1, useInitials, TYPEFACE_THIN, STYLE_NORMAL);
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

	@Override
	public String createUrl()
	{
		return String.format("text://%s/%s/%s/%s/%s", text, String.valueOf(colour),
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
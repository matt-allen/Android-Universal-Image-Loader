package com.nostra13.universalimageloader.core.model;

import android.support.annotation.IntDef;

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

	public SimpleTextImage(String text, boolean useInitials, @Typeface int typeFace, @Style int style)
	{
		this.text = text;
		this.useInitials = useInitials;
		this.typeFace = typeFace;
		this.style = style;
	}

	public SimpleTextImage(String text, boolean useInitials)
	{
		this(text, useInitials, TYPEFACE_NORMAL, STYLE_NORMAL);
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

	@Override
	public String createUrl()
	{
		return null;
	}

	@Override
	public void fromUrl(String url)
	{

	}
}
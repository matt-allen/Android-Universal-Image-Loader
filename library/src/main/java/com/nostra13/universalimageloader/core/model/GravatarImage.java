package com.nostra13.universalimageloader.core.model;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Simple class for constructing avatars from gravatar emails
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class GravatarImage implements ImageServiceOptions
{
	private static final String URL = "http://www.gravatar.com/avatar/%s?s=%s";
	@IntDef({SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE, SIZE_MAX})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Size{}
	public static final int SIZE_MAX = 2048;
	public static final int SIZE_LARGE = 1536;
	public static final int SIZE_MEDIUM = 1024;
	public static final int SIZE_SMALL = 512;

	private int size;
	private String extension;
	private String emailHash;

	public GravatarImage(String email, @Nullable String extension, @Size int size)
	{
		this.emailHash = hash(email);
		this.extension = extension;
		this.size = size;
	}

	@Size
	public int getSize()
	{
		return size;
	}

	public String getExtension()
	{
		return extension;
	}

	public String getEmailHash()
	{
		return emailHash;
	}

	private String hash(final String s)
	{
		final String MD5 = "MD5";
		try
		{
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest)
			{
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String createUrl()
	{
		String url = String.format(URL, emailHash, size);
		if (!TextUtils.isEmpty(extension))
		{
			url += extension;
		}
		return url;
	}

	@Override
	public void fromUrl(String url)
	{
		// Not needed
	}
}

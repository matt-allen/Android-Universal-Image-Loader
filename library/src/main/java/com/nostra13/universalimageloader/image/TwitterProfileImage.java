package com.nostra13.universalimageloader.image;

import android.support.annotation.NonNull;

/**
 * Simple class that will create twitter profile image URL from handle
 *
 * @author Matt Allen
 */
public class TwitterProfileImage implements ImageServiceOptions
{
	protected static final String URL = "https://twitter.com/%s/profile_image?size=original";
	protected String username;

	public TwitterProfileImage(@NonNull String username)
	{
		this.username = username;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	@Override
	public String createUrl()
	{
		return String.format(URL, username);
	}

	@Override
	public void fromUrl(String url)
	{
		// Not used
	}
}

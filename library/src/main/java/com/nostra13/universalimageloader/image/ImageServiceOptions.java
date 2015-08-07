package com.nostra13.universalimageloader.image;

/**
 * Interface for constructing URLs for talking to a image service
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public interface ImageServiceOptions
{
	String createUrl();
	void fromUrl(String url);
}

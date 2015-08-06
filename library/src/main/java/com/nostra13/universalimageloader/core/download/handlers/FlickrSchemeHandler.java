package com.nostra13.universalimageloader.core.download.handlers;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.helper.FlickrServiceHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * // TODO Class doc
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class FlickrSchemeHandler extends SchemeHandler
{
	private static final String FORMAT = "json";
	private static final String METHOD = "flickr.photos.search";

	@Override
	public InputStream getStreamForPath(Context context, String path, Object optionForDownloader, int connectTimeout, int readTimeout)
	{
		if (TextUtils.isEmpty(path))
		{
			return null;
		}
		else if (TextUtils.isEmpty(FlickrServiceHelper.getApiKey()))
		{
			System.err.println("The API key for Flickr must first be set by calling FlickrServiceHelper.setApiKey(String)");
			return null;
		}
		else if (TextUtils.isEmpty(FlickrServiceHelper.getGroupId()))
		{
			System.err.println("The Group Id for Flickr must be set by calling FlickrServiceHelper.setGroupId(String)");
			return null;
		}
		else
		{
			try
			{
				String[] splitString = path.split("/");
				String getUrl = createUrl(Double.parseDouble(splitString[splitString.length - 2]), Double.parseDouble(splitString[splitString.length - 1]));
				BufferedReader in = new BufferedReader(new InputStreamReader(getStreamFromNetwork(getUrl, connectTimeout, readTimeout, optionForDownloader)));
				StringBuilder stringBuilder = new StringBuilder();
				String response;

				try
				{
					while ((response = in.readLine()) != null)
					{
						stringBuilder.append(response);
					}
				}
				finally
				{
					in.close();
				}

				Gson builder = new GsonBuilder().create();
				JsonElement json = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject().get("photos").getAsJsonObject().get("photo");

				String imageUrl;
				ArrayList<Photo> photos = builder.fromJson(json, new TypeToken<ArrayList<Photo>>()
				{
				}.getType());

				if (photos.size() > 0)
				{
					imageUrl = photos.get(0).getUrl();
				}
				else
				{
					return null;
				}

				return getStreamFromNetwork(imageUrl, connectTimeout, readTimeout, optionForDownloader);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	private String createUrl(double latitude, double longitude)
	{
		return String.format("https://api.flickr.com/services/rest/?method=%s&api_key=%s&group_id=%s&lat=%s&lon=%s&format=%s&nojsoncallback=1",
				METHOD, FlickrServiceHelper.getApiKey(), FlickrServiceHelper.getGroupId(), String.valueOf(latitude), String.valueOf(longitude), FORMAT);
	}

	public class Photo
	{
		private String id;
		private String owner;
		private String secret;
		private String server;
		private String farm;
		private String title;
		private String isPublic;
		private String isFriend;
		private String isFamily;

		public String getUrl()
		{
			return String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg", farm, server, id, secret);
		}
	}
}

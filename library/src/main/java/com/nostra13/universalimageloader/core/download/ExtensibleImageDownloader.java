package com.nostra13.universalimageloader.core.download;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.handlers.ContentSchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.DrawableSchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.FileSchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.FlickrSchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.HttpSchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.SchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.TextSchemeHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Image downloader class that can accommodate adapters for schemas, letting you
 * create a new file schema with minimal effort
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class ExtensibleImageDownloader implements ImageDownloader
{
	/** {@value} */
	public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000;
	/** {@value} */
	public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000;

	private Map<String, SchemeHandler> mHandlers;

	protected final Context context;
	protected final int connectTimeout;
	protected final int readTimeout;

	public ExtensibleImageDownloader(Context context)
	{
		this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT, null);
	}

	public ExtensibleImageDownloader(Context context, Map<String, SchemeHandler> handlers)
	{
		this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT, handlers);
	}

	public ExtensibleImageDownloader(Context context, int connectTimeout, int readTimeout, Map<String, SchemeHandler> handlers)
	{
		this.context = context.getApplicationContext();
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		if (handlers != null)
		{
			mHandlers = handlers;
		}
		else
		{
			mHandlers = new HashMap<>();
			HttpSchemeHandler http = new HttpSchemeHandler();
			mHandlers.put("file", new FileSchemeHandler());
			mHandlers.put("drawable", new DrawableSchemeHandler());
			mHandlers.put("content", new ContentSchemeHandler());
			mHandlers.put("http", http);
			mHandlers.put("https", http);
			mHandlers.put("flickr", new FlickrSchemeHandler());
			mHandlers.put("text", new TextSchemeHandler());
		}
	}

	public void registerHandler(String scheme, SchemeHandler handler)
	{
		mHandlers.put(scheme, handler);
	}

	public void removeHandler(String scheme)
	{
		if (mHandlers.containsKey(scheme))
		{
			mHandlers.remove(scheme);
		}
	}

	public void setHandlers(Map<String, SchemeHandler> handlers)
	{
		mHandlers = handlers;
	}

	@Override
	public InputStream getStream(String imageUri, Object extra) throws IOException
	{
		String[] urls = imageUri.split(ImageLoader.getInstance().getUrlSeparator());
		InputStream is = null;
		for (String url : urls)
		{
			String schema = url.split(":")[0];
			if (mHandlers.containsKey(schema))
			{
				is = mHandlers.get(schema).getStreamForPath(context, url, extra, connectTimeout, readTimeout);
			}
			if (is != null)
			{
				break;
			}
			else
			{
				ImageLoader.getInstance().addFailedDownload(url);
			}
		}
		return is;
	}
}

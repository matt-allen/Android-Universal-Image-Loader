package com.nostra13.universalimageloader.core.download;

import android.content.Context;
import android.net.Uri;

import com.nostra13.universalimageloader.core.download.handlers.ContentSchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.DrawableSchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.FileSchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.FlickrSchemaDownloader;
import com.nostra13.universalimageloader.core.download.handlers.HttpSchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.SchemeHandler;
import com.nostra13.universalimageloader.core.download.handlers.StaticMapSchemeHandler;

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
			mHandlers.put("flickr", new FlickrSchemaDownloader());
			mHandlers.put("maps", new StaticMapSchemeHandler());
		}
	}

	public void registerHandler(String schema, SchemeHandler handler)
	{
		mHandlers.put(schema, handler);
	}

	public void removeHandler(String schema)
	{
		if (mHandlers.containsKey(schema))
		{
			mHandlers.remove(schema);
		}
	}

	public void setHandlers(Map<String, SchemeHandler> handlers)
	{
		mHandlers = handlers;
	}

	@Override
	public InputStream getStream(String imageUri, Object extra) throws IOException
	{
		String schema = Uri.parse(imageUri).getLastPathSegment();
		if (mHandlers.containsKey(schema))
		{
			return mHandlers.get(schema).getStreamForPath(context, imageUri, extra, connectTimeout, readTimeout);
		}
		return null;
	}
}

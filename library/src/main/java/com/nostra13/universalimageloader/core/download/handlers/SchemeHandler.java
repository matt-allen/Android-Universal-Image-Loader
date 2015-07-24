package com.nostra13.universalimageloader.core.download.handlers;

import android.net.Uri;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Base class for
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public abstract class SchemeHandler
{
	public abstract InputStream getStreamForPath(String path);

	/**
	 * Create {@linkplain HttpURLConnection HTTP connection} for incoming URL
	 *
	 * @param url   URL to connect to
	 * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *              DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@linkplain HttpURLConnection Connection} for incoming URL. Connection isn't established so it still configurable.
	 * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
	 *                     URL.
	 */
	protected HttpURLConnection createConnection(String url, Object extra, int connectTimeout, int readTimeout) throws IOException {
		String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
		HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		return conn;
	}
}

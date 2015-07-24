package com.nostra13.universalimageloader.core.download.handlers;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Base class for all image stream opening. Includes basic network connectivity methods
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public abstract class SchemeHandler
{
	/** {@value} */
	protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
	/** {@value} */
	protected static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
	protected static final int MAX_REDIRECT_COUNT = 5;

	public abstract InputStream getStreamForPath(Context context, String path, Object optionForDownloader, int connectTimeout, int readTimeout);

	/**
	 * Create {@linkplain HttpURLConnection HTTP connection} for incoming URL
	 *
	 * @param url URL to connect to
	 *
	 * @return {@linkplain HttpURLConnection Connection} for incoming URL.
	 * Connection isn't established so it still configurable.
	 * @throws IOException if some I/O error occurs during network
	 * request or if no InputStream could be created for URL.
	 */
	protected HttpURLConnection createNetworkConnection(String url, int connectTimeout, int readTimeout) throws IOException {
		String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
		HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		return conn;
	}

	/**
	 * Check that the data returned in the network stream is worth downloading
	 * @param conn
	 * @return
	 * @throws IOException
	 */
	protected boolean isProcessable(HttpURLConnection conn) throws IOException
	{
		return (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300);
	}

	protected InputStream getStreamFromNetwork(String imageUri, int connectTimeout, int readTimeout, Object extra) throws IOException {
		HttpURLConnection conn = createNetworkConnection(imageUri, connectTimeout, readTimeout);

		int redirectCount = 0;
		while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
			conn = createNetworkConnection(conn.getHeaderField("Location"), connectTimeout, readTimeout);
			redirectCount++;
		}

		InputStream imageStream;
		try {
			imageStream = conn.getInputStream();
		} catch (IOException e) {
			// Read all data to allow reuse connection (http://bit.ly/1ad35PY)
			IoUtils.readAndCloseStream(conn.getErrorStream());
			throw e;
		}
		if (!isProcessable(conn)) {
			IoUtils.closeSilently(imageStream);
			throw new IOException("Image request failed with response code " + conn.getResponseCode());
		}

		return new ContentLengthInputStream(new BufferedInputStream(imageStream, BUFFER_SIZE), conn.getContentLength());
	}

	protected InputStream getContactPhotoStream(Context context, Uri uri)
	{
		return ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri, true);
	}
}

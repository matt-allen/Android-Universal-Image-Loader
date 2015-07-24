package com.nostra13.universalimageloader.core.download.handlers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * // TODO Class doc
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class ContentSchemeHandler extends SchemeHandler
{
	protected static final String CONTENT_CONTACTS_URI_PREFIX = "content://com.android.contacts/";

	@Override
	public InputStream getStreamForPath(Context context, String path, Object optionForDownloader, int connectTimeout, int readTimeout)
	{
		ContentResolver res = context.getContentResolver();

		Uri uri = Uri.parse(path);
		if (isVideoContentUri(context, uri))
		{
			Long origId = Long.valueOf(uri.getLastPathSegment());
			Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(res, origId, MediaStore.Images.Thumbnails.MINI_KIND, null);
			if (bitmap != null)
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
				return new ByteArrayInputStream(bos.toByteArray());
			}
		}
		else if (path.startsWith(CONTENT_CONTACTS_URI_PREFIX))
		{
			return getContactPhotoStream(context, uri);
		}

		try
		{
			return res.openInputStream(uri);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	protected boolean isVideoContentUri(Context context, Uri uri)
	{
		String mimeType = context.getContentResolver().getType(uri);
		return mimeType != null && mimeType.startsWith("video/");
	}
}

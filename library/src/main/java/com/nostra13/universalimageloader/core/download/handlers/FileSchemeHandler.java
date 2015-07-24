package com.nostra13.universalimageloader.core.download.handlers;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Matt Allen
 * 24/07/15
 * mattallen092@gmail.com
 */
public class FileSchemeHandler extends SchemeHandler
{
	@Override
	public InputStream getStreamForPath(Context context, String path, Object optionForDownloader, int connectTimeout, int readTimeout)
	{
		try
		{
			String filePath = ImageDownloader.Scheme.FILE.crop(path);
			if (isVideoUri(path))
			{
				return getVideoThumbnailStream(filePath);
			}
			else
			{
				BufferedInputStream imageStream = new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE);
				return new ContentLengthInputStream(imageStream, (int) new File(filePath).length());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private boolean isVideoUri(String uri) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		return mimeType != null && mimeType.startsWith("video/");
	}

	private InputStream getVideoThumbnailStream(String filePath)
	{
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
		if (bitmap != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
			return new ByteArrayInputStream(bos.toByteArray());
		}
		return null;
	}
}

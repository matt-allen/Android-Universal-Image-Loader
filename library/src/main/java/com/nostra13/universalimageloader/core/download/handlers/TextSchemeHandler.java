package com.nostra13.universalimageloader.core.download.handlers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.model.SimpleTextImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * // TODO Add class description
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public class TextSchemeHandler extends SchemeHandler
{
	@Override
	public InputStream getStreamForPath(Context context, String path, Object optionForDownloader, int connectTimeout, int readTimeout)
	{
		SimpleTextImage image = new SimpleTextImage();
		image.fromUrl(path);
		TextView textView = new TextView(context);
		String text = image.getText();
		textView.setText(image.isUsingInitials() ? text.substring(0, 1) : text);
		textView.setBackgroundColor(image.getColour());
		textView.layout(0,0,300,300);
		Drawable bgDrawable = textView.getBackground();
		Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		bgDrawable.draw(canvas);
		textView.draw(canvas);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();
		return new ByteArrayInputStream(bitmapdata);
	}
}

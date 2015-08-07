package com.nostra13.universalimageloader.core.download.handlers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.LinearLayout;
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
		TextView textView = getTextView(context);
		String text = image.getText();
		textView.setText(image.isUsingInitials() ? text.substring(0, 1) : text);
		textView.setBackgroundColor(image.getColour());
		switch (image.getTypeFace())
		{
			case SimpleTextImage.TYPEFACE_MONOSPACE:
				textView.setTypeface(Typeface.MONOSPACE);
				break;

			case SimpleTextImage.TYPEFACE_CONDENSED:
				textView.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
				break;

			case SimpleTextImage.TYPEFACE_THIN:
				textView.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
				break;
		}
		Drawable bgDrawable = textView.getBackground();
		Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		bgDrawable.draw(canvas);
		textView.draw(canvas);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();
		return new ByteArrayInputStream(bitmapdata);
	}

	private TextView getTextView(Context context)
	{
		TextView textView = new TextView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		textView.setLayoutParams(params);
		textView.setGravity(Gravity.CENTER);
		textView.setPadding(16, 16, 16, 16);
		textView.layout(0, 0, 256, 256);
		textView.requestLayout();
		textView.setTextSize(54);
		textView.setTextColor(Color.WHITE);
		return textView;
	}
}

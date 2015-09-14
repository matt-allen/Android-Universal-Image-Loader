package com.nostra13.universalimageloader.core.download.handlers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.image.SimpleTextImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Renderer for text on a coloured background. Nice and simple image to mimic the way
 * that Gmail shows initials of people in a list without an image
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
		textView.setText(image.isUsingInitials() ? image.getInitials() : text);
		int colour = image.getColour();
		textView.setBackgroundColor(colour == -1 ? SimpleTextImage.getRandomColour() : colour);
		switch (image.getTypeFace())
		{
			case SimpleTextImage.TYPEFACE_MONOSPACE:
				textView.setTypeface(Typeface.MONOSPACE);
				break;

			case SimpleTextImage.TYPEFACE_CONDENSED:
				textView.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
				break;

			case SimpleTextImage.TYPEFACE_THIN:
				textView.setTypeface(Typeface.create("sans-serif-thin", Typeface.SANS_SERIF.getStyle()));
				break;
		}
		Drawable bgDrawable = textView.getBackground();
		Bitmap bitmap = Bitmap.createBitmap(384, 384, Bitmap.Config.ARGB_8888);
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
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		textView.setLayoutParams(params);
		textView.setGravity(Gravity.CENTER);
		textView.layout(0, 0, 384, 384);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 220);
		textView.requestLayout();
		textView.setPadding(16, 46, 16, 16);
		textView.setTextColor(Color.WHITE);
		return textView;
	}
}

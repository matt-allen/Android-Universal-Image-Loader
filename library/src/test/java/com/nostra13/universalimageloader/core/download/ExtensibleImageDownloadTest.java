package com.nostra13.universalimageloader.core.download;

import android.app.Activity;

import com.cube.imageloader.BuildConfig;
import com.nostra13.universalimageloader.core.helper.FlickrServiceHelper;
import com.nostra13.universalimageloader.image.FlickrImage;
import com.nostra13.universalimageloader.image.GravatarImage;
import com.nostra13.universalimageloader.image.SimpleTextImage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.fail;

/**
 * // TODO Add class description
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ExtensibleImageDownloadTest
{
	private Activity activity;

	@Before
	public void setUp() throws Exception
	{
		activity = Robolectric.setupActivity(Activity.class);
	}

	@Test
	public void testFlickrImageDownload()
	{
		try
		{
			ExtensibleImageDownloader downloader = new ExtensibleImageDownloader(activity);
			FlickrServiceHelper.setApiKey("ff09527c98fd10a0fd81e62986cbbc8d");
			FlickrImage image = new FlickrImage(50.724057, -1.906289);
			InputStream stream = downloader.getStream(image.createUrl(), null);
			assertNotNull(stream);
			assertTrue(stream.read() >= 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSimpleTextImageCreation()
	{
		try
		{
			ExtensibleImageDownloader downloader = new ExtensibleImageDownloader(activity);
			SimpleTextImage image = new SimpleTextImage("Sample Name");
			InputStream stream = downloader.getStream(image.createUrl(), null);
			assertNotNull(stream);
			assertTrue(stream.read() >= 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGravatarImageDownload()
	{
		try
		{
			ExtensibleImageDownloader downloader = new ExtensibleImageDownloader(activity);
			GravatarImage image = new GravatarImage("mattallen092@gmail.com");
			InputStream stream = downloader.getStream(image.createUrl(), null);
			assertNotNull(stream);
			assertTrue(stream.read() >= 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGravatarImageDownloadFail()
	{
		try
		{
			ExtensibleImageDownloader downloader = new ExtensibleImageDownloader(activity);
			GravatarImage image = new GravatarImage("mattallen092@thiswontwork.com");
			InputStream stream = downloader.getStream(image.createUrl(), null);
			assertNull(stream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}

package com.nostra13.universalimageloader.core.download;

import android.app.Activity;

import com.nostra13.universalimageloader.core.helper.FlickrServiceHelper;
import com.nostra13.universalimageloader.image.FlickrImage;
import com.nostra13.universalimageloader.image.GravatarImage;
import com.nostra13.universalimageloader.image.SimpleTextImage;
import com.nostra13.universalimageloader.image.StaticMapImage;
import com.nostra13.universalimageloader.image.TwitterProfileImage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.fail;

/**
 * Test the scheme handlers that are added to the image downloader by default
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21)
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
			System.out.println("Getting image for address: "+image.createUrl());
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
	public void testFlickrImageDownloadFail()
	{
		try
		{
			ExtensibleImageDownloader downloader = new ExtensibleImageDownloader(activity);
			FlickrServiceHelper.setApiKey("ff09527c98fd10a0fd81e62986cbbc8d");
			FlickrImage image = new FlickrImage(606660, 222220);
			System.out.println("Getting image for address: " + image.createUrl());
			InputStream stream = downloader.getStream(image.createUrl(), null);
			assertNull(stream);
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
			System.out.println("Getting image for address: " + image.createUrl());
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
	public void testSimpleTextImageCreationFail()
	{
		try
		{
			ExtensibleImageDownloader downloader = new ExtensibleImageDownloader(activity);
			SimpleTextImage image = new SimpleTextImage(null);
			System.out.println("Getting image for address: "+image.createUrl());
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
			System.out.println("Getting image for address: "+image.createUrl());
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
			System.out.println("Getting image for address: " + image.createUrl());
			InputStream stream = downloader.getStream(image.createUrl(), null);
			assertNull(stream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testStaticMapImageDownload()
	{
		try
		{
			ExtensibleImageDownloader downloader = new ExtensibleImageDownloader(activity);
			List<StaticMapImage.Marker> markers = new ArrayList<>();
			markers.add(new StaticMapImage.Marker("A", 50.724097, -1.906289, StaticMapImage.RED));
			markers.add(new StaticMapImage.Marker("A", 50.724097, -1.906289, "notacolour"));
			markers.add(new StaticMapImage.Marker(null, 50724097, -1.906289, StaticMapImage.RED));
			markers.add(new StaticMapImage.Marker("A", 50.724097, -1906289, StaticMapImage.RED));
			markers.add(new StaticMapImage.Marker("A", 50.724097, -1.906289, null));
			markers.add(null);
			for (StaticMapImage.Marker marker : markers)
			{
				StaticMapImage image = new StaticMapImage.Builder()
						.defaultMarker(true)
						.latitude(50.724057)
						.longitude(-1.906289)
						.addMarker(marker)
						.build();
				System.out.println("Getting image for address: " + image.createUrl());
				InputStream stream = downloader.getStream(image.createUrl(), null);
				assertNotNull(stream);
				assertTrue(stream.read() >= 0);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testTwitterImageDownload()
	{
		try
		{
			ExtensibleImageDownloader downloader = new ExtensibleImageDownloader(activity);
			TwitterProfileImage image = new TwitterProfileImage("@m_allen92");
			System.out.println("Getting image for address: " + image.createUrl());
			InputStream stream = downloader.getStream(image.createUrl(), null);
			assertNotNull(stream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testTwitterImageDownloadFail()
	{
		try
		{
			ExtensibleImageDownloader downloader = new ExtensibleImageDownloader(activity);
			TwitterProfileImage image = new TwitterProfileImage("@m_allen92ffh9q8");
			System.out.println("Getting image for address: " + image.createUrl());
			InputStream stream = downloader.getStream(image.createUrl(), null);
			assertNotNull(stream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}

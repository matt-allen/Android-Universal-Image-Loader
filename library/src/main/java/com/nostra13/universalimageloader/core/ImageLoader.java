/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.download.ExtensibleImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.download.handlers.SchemeHandler;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.MultipleImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.image.ImageServiceOptions;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.nostra13.universalimageloader.utils.L;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Singletone for image loading and displaying at {@link ImageView ImageViews}<br />
 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before any other method.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class ImageLoader
{
	public static final String TAG = ImageLoader.class.getSimpleName();

	static final String LOG_INIT_CONFIG = "Initialize ImageLoader with configuration";
	static final String LOG_DESTROY = "Destroy ImageLoader";
	static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";

	private static final String WARNING_RE_INIT_CONFIG = "Try to initialize ImageLoader which had already been initialized before. " + "To re-init ImageLoader with new configuration call ImageLoader.destroy() at first.";
	private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference must not be null)";
	private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
	private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";

	private ImageLoaderConfiguration configuration;
	private ImageLoaderEngine engine;

	private List<String> failedDownloads = new ArrayList<>();

	private ImageLoadingListener defaultListener = new SimpleImageLoadingListener();

	private volatile static ImageLoader instance;

	/** Returns singleton class instance */
	public static ImageLoader getInstance()
	{
		if (instance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (instance == null)
				{
					instance = new ImageLoader();
				}
			}
		}
		return instance;
	}

	protected ImageLoader()
	{
		super();
	}

	/**
	 * Initializes ImageLoader instance with configuration.<br />
	 * If configurations was set before ( {@link #isInited()} == true) then this method does nothing.<br />
	 * To force initialization with new configuration you should {@linkplain #destroy() destroy ImageLoader} at first.
	 *
	 * @param configuration {@linkplain ImageLoaderConfiguration ImageLoader configuration}
	 * @throws IllegalArgumentException if <b>configuration</b> parameter is null
	 */
	public synchronized void init(ImageLoaderConfiguration configuration)
	{
		if (configuration == null)
		{
			throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
		}
		if (this.configuration == null)
		{
			L.d(LOG_INIT_CONFIG);
			engine = new ImageLoaderEngine(configuration);
			this.configuration = configuration;
		}
		else
		{
			L.w(WARNING_RE_INIT_CONFIG);
		}
	}

	/**
	 * Returns <b>true</b> - if ImageLoader {@linkplain #init(ImageLoaderConfiguration) is initialized with
	 * configuration}; <b>false</b> - otherwise
	 */
	public boolean isInited()
	{
		return configuration != null;
	}

	/**
	 * Access the downloader. This will allow for adding of scheme handlers.
	 * @return The current downloader class
	 */
	public ImageDownloader getDownloader()
	{
		return configuration.downloader;
	}

	/**
	 * Convenience method for adding a handler for a particular scheme
	 * @param scheme The scheme this handler is supposed to handle
	 * @param handler The {@linkplain SchemeHandler} implementation for this scheme
	 */
	public void registerSchemeHandler(String scheme, SchemeHandler handler)
	{
		if (configuration.downloader instanceof ExtensibleImageDownloader)
		{
			((ExtensibleImageDownloader) configuration.downloader).registerHandler(scheme, handler);
		}
	}

	/**
	 * Convenience method for removing a scheme handler based on the scheme it is intended to handle
	 * @param scheme The scheme to stop handling
	 */
	public void removeSchemeHandler(String scheme)
	{
		if (configuration.downloader instanceof ExtensibleImageDownloader)
		{
			((ExtensibleImageDownloader) configuration.downloader).removeHandler(scheme);
		}
	}

	public Map<String, SchemeHandler> getRegisteredSchemeHandlers()
	{
		if (configuration.downloader instanceof ExtensibleImageDownloader)
		{
			return ((ExtensibleImageDownloader) configuration.downloader).getHandlers();
		}
		return null;
	}

	public void addFailedDownload(String url)
	{
		failedDownloads.add(url);
	}

	public void clearFailedDownloads()
	{
		failedDownloads = new ArrayList<>();
	}

	public void displayImage(ImageServiceOptions urlCreator, ImageView imageAware, ImageLoadingListener listener,
	                         DisplayImageOptions options, ImageLoadingProgressListener progressListener)
	{
		displayImage(urlCreator.createUrl(), imageAware, options, listener, progressListener);
	}

	public void displayImage(ImageServiceOptions urlCreator, ImageView imageAware, ImageLoadingListener listener, DisplayImageOptions options)
	{
		displayImage(urlCreator.createUrl(), imageAware, options, listener, null);
	}

	public void displayImage(ImageServiceOptions urlCreator, ImageView imageAware, DisplayImageOptions options)
	{
		displayImage(urlCreator.createUrl(), imageAware, options, null, null);
	}

	public void displayImage(ImageServiceOptions urlCreator, ImageView imageAware)
	{
		displayImage(urlCreator.createUrl(), imageAware, null, null, null);
	}

	public void displayImage(String[] urls, final ImageView imageAware, final ImageLoadingListener listener,
							 DisplayImageOptions options, ImageLoadingProgressListener progressListener)
	{
		final ImageAware imageAwareView = new ImageViewAware(imageAware);
		displayImage(urls, imageAwareView, listener, options, progressListener);
	}

	public void displayImage(String[] urls, ImageAware imageAware, final ImageLoadingListener listener,
	                         DisplayImageOptions options, ImageLoadingProgressListener progressListener)
	{
		MultipleImageLoadingListener loadingListener = new MultipleImageLoadingListener(imageAware, listener, urls);
		String url = urls[0];
		if (urls.length == 0)
		{
			engine.cancelDisplayTaskFor(imageAware);
			listener.onLoadingStarted(url, imageAware.getWrappedView());
			if (options != null && options.shouldShowImageForEmptyUri())
			{
				imageAware.setImageDrawable(options.getImageForEmptyUri(configuration.resources));
			}
			else
			{
				imageAware.setImageDrawable(null);
			}
			listener.onLoadingComplete(url, imageAware.getWrappedView(), null);
			return;
		}
		displayImage(url, imageAware, options, loadingListener, progressListener);
	}

	public void displayImage(ImageServiceOptions[] urlCreators, ImageView imageAware, ImageLoadingListener listener, DisplayImageOptions options)
	{
		displayImage(urlCreators, imageAware, listener, options, null);
	}

	public void displayImage(ImageServiceOptions[] urlCreators, ImageView imageAware, DisplayImageOptions options)
	{
		displayImage(urlCreators, imageAware, null, options, null);
	}

	public void displayImage(ImageServiceOptions[] urlCreators, ImageView imageAware)
	{
		displayImage(urlCreators, imageAware, null, null, null);
	}

	public void displayImage(ImageServiceOptions[] urls, ImageView imageAware, ImageLoadingListener listener,
	                         DisplayImageOptions options, ImageLoadingProgressListener progressListener)
	{
		String[] createdUrls = new String[urls.length];
		for (int i = 0; i < urls.length; i++)
		{
			createdUrls[i] = urls[i].createUrl();
		}
		displayImage(createdUrls, imageAware, listener, options, progressListener);
	}

	public void displayImage(String[] urls, ImageView imageAware, ImageLoadingListener listener, DisplayImageOptions options)
	{
		displayImage(urls, imageAware, listener, options, null);
	}

	public void displayImage(String[] urls, ImageView imageAware, ImageLoadingListener listener)
	{
		displayImage(urls, imageAware, listener, null, null);
	}

	public void displayImage(String[] urls, ImageView imageAware, DisplayImageOptions options)
	{
		displayImage(urls, imageAware, null, options, null);
	}

	public void displayImage(String[] urls, ImageView imageAware)
	{
		displayImage(urls, imageAware, null, null, null);
	}

	public void displayImageWithTransition(String uri, ImageView view)
	{
		displayImageWithTransition(uri, view, null, null, null);
	}

	public void displayImageWithTransition(String uri, ImageView view, DisplayImageOptions options)
	{
		displayImageWithTransition(uri, view, options, null, null);
	}

	public void displayImageWithTransition(String uri, ImageView view, DisplayImageOptions options, ImageLoadingListener listener)
	{
		displayImageWithTransition(uri, view, options, listener, null);
	}

	public void displayImageWithTransition(String uri, ImageView view, ImageLoadingListener listener)
	{
		displayImageWithTransition(uri, view, null, listener, null);
	}

	public void displayImageWithTransition(String uri, ImageView view, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
	{
		ImageViewAware imageViewAware = new ImageViewAware(view);
		imageViewAware.setUsingColourFilterTransition(true);
		displayImage(uri, imageViewAware, options, listener, progressListener);
	}

	public void displayImageWithTransition(String[] uris, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
	{
		ImageViewAware view = new ImageViewAware(imageView);
		view.setUsingColourFilterTransition(true);
		displayImage(uris, view, listener, options, progressListener);
	}

	public void displayImageWithTransition(String[] uris, ImageView imageView, DisplayImageOptions options)
	{
		displayImageWithTransition(uris, imageView, options, null, null);
	}

	public void displayImageWithTransition(String[] uris, ImageView imageView)
	{
		displayImageWithTransition(uris, imageView, null, null, null);
	}

	public void displayImageWithTransition(String[] uris, ImageView imageView, ImageLoadingListener listener)
	{
		displayImageWithTransition(uris, imageView, null, listener, null);
	}

	public void displayImageWithTransition(ImageServiceOptions uriCreator, ImageView imageView, DisplayImageOptions options,
										   ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
	{
		ImageViewAware view = new ImageViewAware(imageView);
		view.setUsingColourFilterTransition(true);
		displayImage(uriCreator.createUrl(), view, options, listener, progressListener);
	}

	public void displayImageWithTransition(ImageServiceOptions uriCreator, ImageView imageView, ImageLoadingListener listener)
	{
		displayImageWithTransition(uriCreator, imageView, null, listener, null);
	}

	public void displayImageWithTransition(ImageServiceOptions uriCreator, ImageView imageView, DisplayImageOptions options)
	{
		displayImageWithTransition(uriCreator, imageView, options, null, null);
	}

	public void displayImageWithTransition(ImageServiceOptions uriCreator, ImageView imageView)
	{
		displayImageWithTransition(uriCreator, imageView, null, null, null);
	}

	public void displayImageWithTransition(ImageServiceOptions[] uriCreators, ImageView imageView, DisplayImageOptions options,
										   ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
	{
		String[] createdUrls = new String[uriCreators.length];
		for (int i = 0; i < uriCreators.length; i++)
		{
			createdUrls[i] = uriCreators[i].createUrl();
		}
		ImageViewAware view = new ImageViewAware(imageView);
		view.setUsingColourFilterTransition(true);
		displayImage(createdUrls, view, listener, options, progressListener);
	}

	public void displayImageWithTransition(ImageServiceOptions[] uriCreators, ImageView imageView, DisplayImageOptions options)
	{
		displayImageWithTransition(uriCreators, imageView, options, null, null);
	}

	public void displayImageWithTransition(ImageServiceOptions[] uriCreators, ImageView imageView, ImageLoadingListener listener)
	{
		displayImageWithTransition(uriCreators, imageView, null, listener, null);
	}

	public void displayImageWithTransition(ImageServiceOptions[] uriCreators, ImageView imageView)
	{
		displayImageWithTransition(uriCreators, imageView, null, null, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageAware when it's turn. <br/>
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri        Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageAware {@linkplain com.nostra13.universalimageloader.core.imageaware.ImageAware Image aware view}
	 *                   which should display image
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageAware</b> is null
	 */
	public void displayImage(String uri, ImageAware imageAware)
	{
		displayImage(uri, imageAware, null, null, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageAware when it's turn.<br />
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri        Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageAware {@linkplain com.nostra13.universalimageloader.core.imageaware.ImageAware Image aware view}
	 *                   which should display image
	 * @param listener   {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on
	 *                   UI thread if this method is called on UI thread.
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageAware</b> is null
	 */
	public void displayImage(String uri, ImageAware imageAware, ImageLoadingListener listener)
	{
		displayImage(uri, imageAware, null, listener, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageAware when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri        Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageAware {@linkplain com.nostra13.universalimageloader.core.imageaware.ImageAware Image aware view}
	 *                   which should display image
	 * @param options    {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                   decoding and displaying. If <b>null</b> - default display image options
	 *                   {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions)
	 *                   from configuration} will be used.
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageAware</b> is null
	 */
	public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options)
	{
		displayImage(uri, imageAware, options, null, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageAware when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri        Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageAware {@linkplain com.nostra13.universalimageloader.core.imageaware.ImageAware Image aware view}
	 *                   which should display image
	 * @param options    {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                   decoding and displaying. If <b>null</b> - default display image options
	 *                   {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions)
	 *                   from configuration} will be used.
	 * @param listener   {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on
	 *                   UI thread if this method is called on UI thread.
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageAware</b> is null
	 */
	public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
							 ImageLoadingListener listener)
	{
		displayImage(uri, imageAware, options, listener, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageAware when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri              Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageAware       {@linkplain com.nostra13.universalimageloader.core.imageaware.ImageAware Image aware view}
	 *                         which should display image
	 * @param options          {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                         decoding and displaying. If <b>null</b> - default display image options
	 *                         {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions)
	 *                         from configuration} will be used.
	 * @param listener         {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires
	 *                         events on UI thread if this method is called on UI thread.
	 * @param progressListener {@linkplain com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener
	 *                         Listener} for image loading progress. Listener fires events on UI thread if this method
	 *                         is called on UI thread. Caching on disk should be enabled in
	 *                         {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions options} to make
	 *                         this listener work.
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageAware</b> is null
	 */
	public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
	{
		checkConfiguration();
		if (imageAware == null)
		{
			throw new IllegalArgumentException(ERROR_WRONG_ARGUMENTS);
		}
		if (listener == null)
		{
			listener = defaultListener;
		}
		if (options == null)
		{
			options = configuration.defaultDisplayImageOptions;
		}

		if (TextUtils.isEmpty(uri))
		{
			engine.cancelDisplayTaskFor(imageAware);
			listener.onLoadingStarted(uri, imageAware.getWrappedView());
			if (options.shouldShowImageForEmptyUri())
			{
				imageAware.setImageDrawable(options.getImageForEmptyUri(configuration.resources));
			}
			else
			{
				imageAware.setImageDrawable(null);
			}
			listener.onLoadingFailed(uri, imageAware.getWrappedView(), null);
			return;
		}

		ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageAware, configuration.getMaxImageSize());
		String memoryCacheKey = MemoryCacheUtils.generateKey(uri, targetSize);
		engine.prepareDisplayTaskFor(imageAware, memoryCacheKey);

		listener.onLoadingStarted(uri, imageAware.getWrappedView());

		Bitmap bmp = configuration.memoryCache.get(memoryCacheKey);
		if (bmp != null && !bmp.isRecycled())
		{
			L.d(LOG_LOAD_IMAGE_FROM_MEMORY_CACHE, memoryCacheKey);

			if (options.shouldPostProcess())
			{
				ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageAware,
						targetSize, memoryCacheKey, options, listener, progressListener, engine.getLockForUri(uri));

				ProcessAndDisplayImageTask displayTask = new ProcessAndDisplayImageTask(engine, bmp, imageLoadingInfo, defineHandler(options));
				if (options.isSyncLoading())
				{
					displayTask.run();
				}
				else
				{
					engine.submit(displayTask);
				}
			}
			else
			{
				options.getDisplayer().display(bmp, imageAware, LoadedFrom.MEMORY_CACHE);
				listener.onLoadingComplete(uri, imageAware.getWrappedView(), bmp);
			}
		}
		else
		{
			if (options.shouldShowImageOnLoading())
			{
				imageAware.setImageDrawable(options.getImageOnLoading(configuration.resources));
			}
			else if (options.isResetViewBeforeLoading())
			{
				imageAware.setImageDrawable(null);
			}

			ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageAware, targetSize, memoryCacheKey,
					options, listener, progressListener, engine.getLockForUri(uri));

			LoadAndDisplayImageTask displayTask = new LoadAndDisplayImageTask(engine, imageLoadingInfo,
					defineHandler(options));

			if (options.isSyncLoading())
			{
				displayTask.run();
			}
			else
			{
				engine.submit(displayTask);
			}
		}
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn. <br/>
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri       Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView)
	{
		displayImage(uri, new ImageViewAware(imageView), null, null, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri       Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * @param options   {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                  decoding and displaying. If <b>null</b> - default display image options
	 *                  {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions)
	 *                  from configuration} will be used.
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options)
	{
		displayImage(uri, new ImageViewAware(imageView), options, null, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * Default {@linkplain DisplayImageOptions display image options} from {@linkplain ImageLoaderConfiguration
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri       Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * @param listener  {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on
	 *                  UI thread if this method is called on UI thread.
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener)
	{
		displayImage(uri, new ImageViewAware(imageView), null, listener, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri       Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView {@link ImageView} which should display image
	 * @param options   {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                  decoding and displaying. If <b>null</b> - default display image options
	 *                  {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions)
	 *                  from configuration} will be used.
	 * @param listener  {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on
	 *                  UI thread if this method is called on UI thread.
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
							 ImageLoadingListener listener)
	{
		displayImage(uri, imageView, options, listener, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri              Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param imageView        {@link ImageView} which should display image
	 * @param options          {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                         decoding and displaying. If <b>null</b> - default display image options
	 *                         {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions)
	 *                         from configuration} will be used.
	 * @param listener         {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires
	 *                         events on UI thread if this method is called on UI thread.
	 * @param progressListener {@linkplain com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener
	 *                         Listener} for image loading progress. Listener fires events on UI thread if this method
	 *                         is called on UI thread. Caching on disk should be enabled in
	 *                         {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions options} to make
	 *                         this listener work.
	 * @throws IllegalStateException    if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @throws IllegalArgumentException if passed <b>imageView</b> is null
	 */
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
							 ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
	{
		displayImage(uri, new ImageViewAware(imageView), options, listener, progressListener);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(String, android.view.View, android.graphics.Bitmap)} callback}.
	 * <br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri      Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *                 thread if this method is called on UI thread.
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, ImageLoadingListener listener)
	{
		loadImage(uri, null, null, listener, null);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(String, android.view.View, android.graphics.Bitmap)} callback}.
	 * <br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri             Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param targetImageSize Minimal size for {@link Bitmap} which will be returned in
	 *                        {@linkplain ImageLoadingListener#onLoadingComplete(String, android.view.View,
	 *                        android.graphics.Bitmap)} callback}. Downloaded image will be decoded
	 *                        and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit
	 *                        larger) than incoming targetImageSize.
	 * @param listener        {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires
	 *                        events on UI thread if this method is called on UI thread.
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, ImageSize targetImageSize, ImageLoadingListener listener)
	{
		loadImage(uri, targetImageSize, null, listener, null);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(String, android.view.View, android.graphics.Bitmap)} callback}.
	 * <br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri      Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param options  {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                 decoding and displaying. If <b>null</b> - default display image options
	 *                 {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *                 configuration} will be used.<br />
	 * @param listener {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires events on UI
	 *                 thread if this method is called on UI thread.
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener)
	{
		loadImage(uri, null, options, listener, null);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(String, android.view.View, android.graphics.Bitmap)} callback}.
	 * <br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri             Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param targetImageSize Minimal size for {@link Bitmap} which will be returned in
	 *                        {@linkplain ImageLoadingListener#onLoadingComplete(String, android.view.View,
	 *                        android.graphics.Bitmap)} callback}. Downloaded image will be decoded
	 *                        and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit
	 *                        larger) than incoming targetImageSize.
	 * @param options         {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                        decoding and displaying. If <b>null</b> - default display image options
	 *                        {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions)
	 *                        from configuration} will be used.<br />
	 * @param listener        {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires
	 *                        events on UI thread if this method is called on UI thread.
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options,
						  ImageLoadingListener listener)
	{
		loadImage(uri, targetImageSize, options, listener, null);
	}

	/**
	 * Adds load image task to execution pool. Image will be returned with
	 * {@link ImageLoadingListener#onLoadingComplete(String, android.view.View, android.graphics.Bitmap)} callback}.
	 * <br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri              Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param targetImageSize  Minimal size for {@link Bitmap} which will be returned in
	 *                         {@linkplain ImageLoadingListener#onLoadingComplete(String, android.view.View,
	 *                         android.graphics.Bitmap)} callback}. Downloaded image will be decoded
	 *                         and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit
	 *                         larger) than incoming targetImageSize.
	 * @param options          {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                         decoding and displaying. If <b>null</b> - default display image options
	 *                         {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions)
	 *                         from configuration} will be used.<br />
	 * @param listener         {@linkplain ImageLoadingListener Listener} for image loading process. Listener fires
	 *                         events on UI thread if this method is called on UI thread.
	 * @param progressListener {@linkplain com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener
	 *                         Listener} for image loading progress. Listener fires events on UI thread if this method
	 *                         is called on UI thread. Caching on disk should be enabled in
	 *                         {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions options} to make
	 *                         this listener work.
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options,
						  ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
	{
		checkConfiguration();
		if (targetImageSize == null)
		{
			targetImageSize = configuration.getMaxImageSize();
		}
		if (options == null)
		{
			options = configuration.defaultDisplayImageOptions;
		}

		NonViewAware imageAware = new NonViewAware(uri, targetImageSize, ViewScaleType.CROP);
		displayImage(uri, imageAware, options, listener, progressListener);
	}

	/**
	 * Loads and decodes image synchronously.<br />
	 * Default display image options
	 * {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @return Result image Bitmap. Can be <b>null</b> if image loading/decoding was failed or cancelled.
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public Bitmap loadImageSync(String uri)
	{
		return loadImageSync(uri, null, null);
	}

	/**
	 * Loads and decodes image synchronously.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri     Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param options {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                decoding and scaling. If <b>null</b> - default display image options
	 *                {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 *                configuration} will be used.
	 * @return Result image Bitmap. Can be <b>null</b> if image loading/decoding was failed or cancelled.
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public Bitmap loadImageSync(String uri, DisplayImageOptions options)
	{
		return loadImageSync(uri, null, options);
	}

	/**
	 * Loads and decodes image synchronously.<br />
	 * Default display image options
	 * {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions) from
	 * configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri             Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param targetImageSize Minimal size for {@link Bitmap} which will be returned. Downloaded image will be decoded
	 *                        and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit
	 *                        larger) than incoming targetImageSize.
	 * @return Result image Bitmap. Can be <b>null</b> if image loading/decoding was failed or cancelled.
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public Bitmap loadImageSync(String uri, ImageSize targetImageSize)
	{
		return loadImageSync(uri, targetImageSize, null);
	}

	/**
	 * Loads and decodes image synchronously.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be called before this method call
	 *
	 * @param uri             Image URI (i.e. "http://site.com/image.png", "file:///mnt/sdcard/image.png")
	 * @param targetImageSize Minimal size for {@link Bitmap} which will be returned. Downloaded image will be decoded
	 *                        and scaled to {@link Bitmap} of the size which is <b>equal or larger</b> (usually a bit
	 *                        larger) than incoming targetImageSize.
	 * @param options         {@linkplain com.nostra13.universalimageloader.core.DisplayImageOptions Options} for image
	 *                        decoding and scaling. If <b>null</b> - default display image options
	 *                        {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions)
	 *                        from configuration} will be used.
	 * @return Result image Bitmap. Can be <b>null</b> if image loading/decoding was failed or cancelled.
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public Bitmap loadImageSync(String uri, ImageSize targetImageSize, DisplayImageOptions options)
	{
		if (options == null)
		{
			options = configuration.defaultDisplayImageOptions;
		}
		options = new DisplayImageOptions.Builder().cloneFrom(options).syncLoading(true).build();

		SyncImageLoadingListener listener = new SyncImageLoadingListener();
		loadImage(uri, targetImageSize, options, listener);
		return listener.getLoadedBitmap();
	}

	/**
	 * Checks if ImageLoader's configuration was initialized
	 *
	 * @throws IllegalStateException if configuration wasn't initialized
	 */
	private void checkConfiguration()
	{
		if (configuration == null)
		{
			throw new IllegalStateException(ERROR_NOT_INIT);
		}
	}

	/** Sets a default loading listener for all display and loading tasks. */
	public void setDefaultLoadingListener(ImageLoadingListener listener)
	{
		defaultListener = listener == null ? new SimpleImageLoadingListener() : listener;
	}

	/**
	 * Returns memory cache
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public MemoryCache getMemoryCache()
	{
		checkConfiguration();
		return configuration.memoryCache;
	}

	/**
	 * Clears memory cache
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void clearMemoryCache()
	{
		checkConfiguration();
		configuration.memoryCache.clear();
	}

	/**
	 * Returns disk cache
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @deprecated Use {@link #getDiskCache()} instead
	 */
	@Deprecated
	public DiskCache getDiscCache()
	{
		return getDiskCache();
	}

	/**
	 * Returns disk cache
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public DiskCache getDiskCache()
	{
		checkConfiguration();
		return configuration.diskCache;
	}

	/**
	 * Clears disk cache.
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 * @deprecated Use {@link #clearDiskCache()} instead
	 */
	@Deprecated
	public void clearDiscCache()
	{
		clearDiskCache();
	}

	/**
	 * Clears disk cache.
	 *
	 * @throws IllegalStateException if {@link #init(ImageLoaderConfiguration)} method wasn't called before
	 */
	public void clearDiskCache()
	{
		checkConfiguration();
		configuration.diskCache.clear();
	}

	/**
	 * Returns URI of image which is loading at this moment into passed
	 * {@link com.nostra13.universalimageloader.core.imageaware.ImageAware ImageAware}
	 */
	public String getLoadingUriForView(ImageAware imageAware)
	{
		return engine.getLoadingUriForView(imageAware);
	}

	/**
	 * Returns URI of image which is loading at this moment into passed
	 * {@link android.widget.ImageView ImageView}
	 */
	public String getLoadingUriForView(ImageView imageView)
	{
		return engine.getLoadingUriForView(new ImageViewAware(imageView));
	}

	/**
	 * Cancel the task of loading and displaying image for passed
	 * {@link com.nostra13.universalimageloader.core.imageaware.ImageAware ImageAware}.
	 *
	 * @param imageAware {@link com.nostra13.universalimageloader.core.imageaware.ImageAware ImageAware} for
	 *                   which display task will be cancelled
	 */
	public void cancelDisplayTask(ImageAware imageAware)
	{
		engine.cancelDisplayTaskFor(imageAware);
	}

	/**
	 * Cancel the task of loading and displaying image for passed
	 * {@link android.widget.ImageView ImageView}.
	 *
	 * @param imageView {@link android.widget.ImageView ImageView} for which display task will be cancelled
	 */
	public void cancelDisplayTask(ImageView imageView)
	{
		engine.cancelDisplayTaskFor(new ImageViewAware(imageView));
	}

	/**
	 * Denies or allows ImageLoader to download images from the network.<br />
	 * <br />
	 * If downloads are denied and if image isn't cached then
	 * {@link ImageLoadingListener#onLoadingFailed(String, View, FailReason)} callback will be fired with
	 * {@link FailReason.FailType#NETWORK_DENIED}
	 *
	 * @param denyNetworkDownloads pass <b>true</b> - to deny engine to download images from the network; <b>false</b> -
	 *                             to allow engine to download images from network.
	 */
	public void denyNetworkDownloads(boolean denyNetworkDownloads)
	{
		engine.denyNetworkDownloads(denyNetworkDownloads);
	}

	/**
	 * Sets option whether ImageLoader will use {@link FlushedInputStream} for network downloads to handle <a
	 * href="http://code.google.com/p/android/issues/detail?id=6066">this known problem</a> or not.
	 *
	 * @param handleSlowNetwork pass <b>true</b> - to use {@link FlushedInputStream} for network downloads; <b>false</b>
	 *                          - otherwise.
	 */
	public void handleSlowNetwork(boolean handleSlowNetwork)
	{
		engine.handleSlowNetwork(handleSlowNetwork);
	}

	/**
	 * Pause ImageLoader. All new "load&display" tasks won't be executed until ImageLoader is {@link #resume() resumed}.
	 * <br />
	 * Already running tasks are not paused.
	 */
	public void pause()
	{
		engine.pause();
	}

	/** Resumes waiting "load&display" tasks */
	public void resume()
	{
		engine.resume();
	}

	/**
	 * Cancels all running and scheduled display image tasks.<br />
	 * <b>NOTE:</b> This method doesn't shutdown
	 * {@linkplain com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder#taskExecutor(java.util.concurrent.Executor)
	 * custom task executors} if you set them.<br />
	 * ImageLoader still can be used after calling this method.
	 */
	public void stop()
	{
		engine.stop();
	}

	/**
	 * {@linkplain #stop() Stops ImageLoader} and clears current configuration. <br />
	 * You can {@linkplain #init(ImageLoaderConfiguration) init} ImageLoader with new configuration after calling this
	 * method.
	 */
	public void destroy()
	{
		if (configuration != null) L.d(LOG_DESTROY);
		stop();
		configuration.diskCache.close();
		engine = null;
		configuration = null;
	}

	private static Handler defineHandler(DisplayImageOptions options)
	{
		Handler handler = options.getHandler();
		if (options.isSyncLoading())
		{
			handler = null;
		}
		else if (handler == null && Looper.myLooper() == Looper.getMainLooper())
		{
			handler = new Handler();
		}
		return handler;
	}

	/**
	 * Listener which is designed for synchronous image loading.
	 *
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 * @since 1.9.0
	 */
	private static class SyncImageLoadingListener extends SimpleImageLoadingListener
	{

		private Bitmap loadedImage;

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
		{
			this.loadedImage = loadedImage;
		}

		public Bitmap getLoadedBitmap()
		{
			return loadedImage;
		}
	}
}

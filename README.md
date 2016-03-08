# 3 SIDED CUBE Image Loading Library

This is a fork of the [Android-Universal-Image-Loader](https://github.com/nostra13/Android-Universal-Image-Loader) from nostra13. It has been updated mainly to allow for easy
adoption of new sources of images that require something more complex than just getting from a URL. An exmple of why this was forked to make this change is to adopt Flickr as
a source because of the need to do an initial call to generate a list of images, then another call once an image is picked from said list. This also makes it easy to adopt
new custom URI schemes you may be using within your app.
Another key feature is the ability to access well known sources straight out of the box with no configuration. See below about Gravatar, Google Maps, etc.

Big thanks go out to nostra13 for the base work on this library!

## Features
 * Multithread image loading (async or sync)
 * Wide customization of ImageLoader's configuration (thread executors, downloader, decoder, memory and disk cache, display image options, etc.)
 * Many customization options for every display image call (stub images, caching switch, decoding options, Bitmap processing and displaying, etc.)
 * Image caching in memory and/or on disk (device's file system or SD card)
 * Listening loading process (including downloading progress)
 * Custom URI adoption through `SchemeHandler`
 * URI generation through interface
 * Flickr Image API support
 * Google Maps satellite image (With multiple marker) support
 * Gravatar image support
 * Twitter Profile image support
 * Google Street View Image API support
 * Support for creating images of a person's initials - just like Gmail, Inbox etc.
 * Memory and thread performance improvements

## [Documentation](https://github.com/3sidedcube/Android-Universal-Image-Loader/wiki)
 * **[Quick Setup](https://github.com/3sidedcube/Android-Universal-Image-Loader/wiki/Quick-Setup)**
 * **[Configuration](https://github.com/3sidedcube/Android-Universal-Image-Loader/wiki/Configuration)**
 * **[Display Options](https://github.com/3sidedcube/Android-Universal-Image-Loader/wiki/Display-Options)**
 * [Useful Info](https://github.com/3sidedcube/Android-Universal-Image-Loader/wiki/Useful-Info) - Read it before asking a question
 * [User Support](https://github.com/3sidedcube/Android-Universal-Image-Loader/wiki/User-Support) - Read it before creating new issue
 * [Sample project](https://github.com/3sidedcube/Android-Universal-Image-Loader/tree/master/sample) - Learn it to understand the right way of library usage

## Usage

### Acceptable URIs examples
``` java
"http://site.com/image.png" // from Web
"file:///mnt/sdcard/image.png" // from SD card
"file:///mnt/sdcard/video.mp4" // from SD card (video thumbnail)
"content://media/external/images/media/13" // from content provider
"content://media/external/video/media/13" // from content provider (video thumbnail)
"assets://image.png" // from assets
"drawable://" + R.drawable.img // from drawables (non-9patch images)
"flickr://-52.34509/-1.234242" // Will load image from Flickr for latitude (First arg) and longitude (Second arg)
```
**NOTE:** Use `drawable://` only if you really need it! Always **consider the native way** to load drawables - `ImageView.setImageResource(...)` instead of using of `ImageLoader`.

### (New) Using Flickr:
Because of the way Flickr is accessed, you will need to set your API key and the Group ID you want to use. This is a very easy
value to set, to set the API key:
``` java
	FlickrServiceHelper.setApiKey(YOUR_API_KEY);
```

To set your group Id (Optional):
``` java
	FlickrServiceHelper.setGroupId(YOUR_GROUP_ID);
```

You can then create a flickr image by passing an instance of `FlickrImage` to `displayImage` or `loadImage`

### (New) Image URI interface:
Now any Java object implementing the `ImageServiceOptions` can be used as an argument to the
`displayImage` or `loadImage` methods on the ImageLoader interface. This allows for creation and
deconstruction of an object with just a URI that would be handled with the ImageLoader features.

Some objects are already provided with the library to help in the creation of images that need
to be retrieved from a service. These are:
- `GravatarImage`
- `FlickrImage`
- `StaticMapsImage`
- `SimpleTextImage`
- `TwitterProfileImage`
- `StreetViewImage`

The names give an accurate description of what they're attempting to achieve. To use them, simply
pass an instance of this object to the `displayImage` or `loadImage` (or instantiate the image and 
call `createUrl()` to use in conjuction with other URI strings).

### (New) Adding a new scheme handler:
The new downloading system provides the ability to add a new scheme to handle by only providing the
logic for generating an `InputStream` for the path provided. To add a new handler, first create a new
handler for the scheme with an object that extends `SchemeHandler`. This class will require you to
implement `getStreamForPath`. This will provide you with the path given when calling the image loader
methods so you can handle this in whatever way necessary. Once this is created, it needs to be added
to the downloader instance alongside the first path segment (The scheme this will handle). For example:
``` java
	ImageLoader.getInstance().registerSchemeHandler("scheme", YOUR_SCHEME_HANDLER_INSTANCE);
```

### Simple
``` java
ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
```
``` java
// Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view 
//	which implements ImageAware interface)
imageLoader.displayImage(imageUri, imageView);
```
``` java
// Load image, decode it to Bitmap and return Bitmap to callback
imageLoader.loadImage(imageUri, new SimpleImageLoadingListener() {
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		// Do whatever you want with Bitmap
	}
});
```
``` java
// Load image, decode it to Bitmap and return Bitmap synchronously
Bitmap bmp = imageLoader.loadImageSync(imageUri);
```

### Complete
``` java
// Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view 
//	which implements ImageAware interface)
imageLoader.displayImage(imageUri, imageView, options, new ImageLoadingListener() {
	@Override
	public void onLoadingStarted(String imageUri, View view) {
		...
	}
	@Override
	public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
		...
	}
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		...
	}
	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		...
	}
}, new ImageLoadingProgressListener() {
	@Override
	public void onProgressUpdate(String imageUri, View view, int current, int total) {
		...
	}
});
```
``` java
// Load image, decode it to Bitmap and return Bitmap to callback
ImageSize targetSize = new ImageSize(80, 50); // result Bitmap will be fit to this size
imageLoader.loadImage(imageUri, targetSize, options, new SimpleImageLoadingListener() {
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		// Do whatever you want with Bitmap
	}
});
```
``` java
// Load image, decode it to Bitmap and return Bitmap synchronously
ImageSize targetSize = new ImageSize(80, 50); // result Bitmap will be fit to this size
Bitmap bmp = imageLoader.loadImageSync(imageUri, targetSize, options);
```

## Load & Display Task Flow
![Task Flow](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/wiki/UIL_Flow.png)

## License

    Copyright 2011-2015 Sergey Tarasevich

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

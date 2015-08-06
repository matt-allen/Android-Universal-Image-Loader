package com.nostra13.universalimageloader.core.helper;

/**
 * Base class for communicating with a service. Classes that extend this one should be able
 * to construct a URL based on the model used here with a scheme unique to this service.
 *
 * @author Matt Allen
 * @project UniversalImageLoader
 */
public abstract class ServiceHelper<T>
{
	/**
	 * Unique scheme for this service to use. This must be unique to not have it
	 * conflict with other services using the same scheme
	 *
	 * @return Scheme string
	 */
	public abstract String getSchemeForService();

	/**
	 * Create a URL for the model given so to still leverage the memory
	 * and disk caching features of the library
	 *
	 * @param optionsModel The model associated with this service
	 *
	 * @return String that will handed to the downloader service. Does not necessarily
	 * need to be a URL that can be used to directly access an image because this can
	 * be handled by a custom SchemeHandler implementation
	 */
	public abstract String createUrlFromOptions(T optionsModel);

	/**
	 * Have the ability to reconstruct the model from the URL created in
	 * {@link #createOptionsFromUrl(String)} so that this doesn't need to happen in the
	 * custom {@linkplain com.nostra13.universalimageloader.core.download.handlers.SchemeHandler}.
	 * If a direct access URL is created here (e.g. a HTTP or HTTPS URL) then this can
	 * simply return null as it will never be called.
	 *
	 * @param url The URL created with {@link #createOptionsFromUrl(String)}
	 *
	 * @return Model that represents the URL. Should be the same as the model passed into
	 * {@link #createOptionsFromUrl(String)}
	 */
	public abstract T createOptionsFromUrl(String url);
}

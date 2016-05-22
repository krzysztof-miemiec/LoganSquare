package com.bluelinelabs.logansquare;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import static com.bluelinelabs.logansquare.ConverterUtils.isSupported;


/**
 * A {@linkplain Converter.Factory converter} which uses LoganSquare for JSON.
 *
 * @see <a>https://github.com/bluelinelabs/LoganSquare</a>
 */
public final class LoganSquareConverterFactory extends Converter.Factory {



	/**
	 * Create an instance. Encoding to JSON and decoding from JSON will use UTF-8.
	 *
	 * @return A {@linkplain Converter.Factory} configured to serve LoganSquare converters
	 */
	public static LoganSquareConverterFactory create() {
		return new LoganSquareConverterFactory();
	}

	private LoganSquareConverterFactory() {
	}

	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
		if (isSupported(type)) {
			return new LoganSquareResponseBodyConverter(this, type);
		} else {
			return null;
		}
	}

	@Override
	public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
		if (isSupported(type)) {
			return new LoganSquareRequestBodyConverter(this, type);
		} else {
			return null;
		}
	}
}
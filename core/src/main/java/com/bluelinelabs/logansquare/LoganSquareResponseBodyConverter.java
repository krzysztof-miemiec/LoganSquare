package com.bluelinelabs.logansquare;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Converter;

import static com.bluelinelabs.logansquare.ConverterUtils.parameterizedTypeOf;

final class LoganSquareResponseBodyConverter implements Converter<ResponseBody, Object> {

	private final Type type;
	private final LoganSquareConverterFactory factory;

	LoganSquareResponseBodyConverter(LoganSquareConverterFactory factory, Type type) {
		this.factory = factory;
		this.type = type;
	}

	@Override
	public Object convert(ResponseBody value) throws IOException {
		synchronized (factory) {
			try {
				InputStream is = value.byteStream();
				if (type instanceof Class) {
					// Plain object conversion
					return LoganSquare.parse(is, (Class<?>) type);

				} else if (type instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) type;
					Type[] typeArguments = parameterizedType.getActualTypeArguments();
					Type firstType = typeArguments[0];

					Type rawType = parameterizedType.getRawType();
					if (rawType == Map.class) {
						return LoganSquare.parseMap(is, (Class<?>) typeArguments[1]);

					} else if (rawType == List.class) {
						return LoganSquare.parseList(is, (Class<?>) firstType);

					} else {
						// Generics
						return LoganSquare.parse(is, parameterizedTypeOf(type));
					}
				}
				return null;

			} finally {
				// Close the response body after being done with it
				value.close();
			}
		}
	}
}
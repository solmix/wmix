package org.solmix.rest.route;

import java.io.IOException;
import java.io.StringWriter;

import org.solmix.exchange.data.DataProcessorException;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class Jsoner {

	private static ObjectMapper objectMapper;

	private static SimpleModule jsonModule;

	static {
		initializeObjectMapper();
	}

	private static void initializeObjectMapper() {
		objectMapper = new ObjectMapper();
		objectMapper.writerWithDefaultPrettyPrinter();
		jsonModule = new SimpleModule("REST-JSON",
				new Version(0, 6, 1, "alpha", "org.solmix.service", "solmix-service-jackson"));
//				jsonModule.addSerializer(serializer);
		objectMapper.registerModule(jsonModule);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		objectMapper.enable(Feature.AUTO_CLOSE_TARGET);

	}

	public static String toJSON(Object obj) {
		StringWriter sw = new StringWriter();
		try {
			objectMapper.writeValue(sw, obj);
		} catch (IOException e) {
			throw new DataProcessorException("Json parse exception", e);
		}
		return sw.toString();
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
		} catch (IOException e) {
			throw new DataProcessorException("Json parse exception", e);
		}
	}

	public static Object toObject(String json) {
		return toObject(json, Object.class);
	}

	public static boolean isJsonReal(String source) {
		boolean result = false;
		if (source != null) {
			result = source.startsWith("\"") || source.startsWith("{") || source.startsWith("[");
			if (!result) {
				try {
					result = objectMapper.readValue(source, Object.class) != null;
				} catch (IOException e) {
					result = false;
				}
			}
		}
		return result;
	}

	public static boolean isJson(String source) {
		boolean result = false;
		if (source != null) {
			result = source.startsWith("\"") || source.startsWith("{") || source.startsWith("[");

		}
		return result;
	}

	public static Object parse(Object obj, Class<?> paramType) {
		Object result = null;
		if (obj != null) {
			if (paramType.isAssignableFrom(obj.getClass())) {
				return obj;
			} else {
				if (paramType == String.class) {
					result = obj;
				} else {
					if (obj instanceof String && isJson((String) obj)) {
						result = Jsoner.toObject((String) obj, paramType);
					} else {
						result = Jsoner.toObject(Jsoner.toJSON(obj), paramType);
					}
				}
			}
		}
		return result;
	}

}

package com.cex.vulcano.parse;

import com.cex.vulcano.exception.TransformerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class JsonProcessor {


	public String toJson(Object obj) {
		return json().toJson(obj);
	}


	public String toJson(Object src, Type typeOfSrc) {
		return json().toJson(src, typeOfSrc);
	}


	public <T> T fromJson(String json, Type typeOfT) throws TransformerException {
		try {
			return json().fromJson(json, typeOfT);
		} catch (JsonSyntaxException ex) {
			throw new TransformerException("Invalid JSON", ex);
		}
	}


	public <T> T fromJson(Reader json, Type typeOfT) throws TransformerException {
		try {
			return json().fromJson(json, typeOfT);
		} catch (JsonSyntaxException ex) {
			throw new TransformerException("Invalid JSON", ex);
		}
	}


	public <T> List<T> fromJsonToList(String json, Class<T> typeOfT) throws TransformerException {
		return fromJson(json, listType(typeOfT));
	}


	public <T> List<T> fromJsonToList(Reader json, Class<T> typeOfT) throws TransformerException {
		return fromJson(json, listType(typeOfT));
	}

	protected static <T> Type listType(Class<T> typeOfT) {
		return new ParameterizedType() {

			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] { typeOfT };
			}

			@Override
			public Type getRawType() {
				return List.class;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}

		};
	}

	private Gson json() {
		return new GsonBuilder().create();
	}
	
}

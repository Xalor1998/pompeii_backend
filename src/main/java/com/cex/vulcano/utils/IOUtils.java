package com.cex.vulcano.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class IOUtils {
	public static String readStringFrom(InputStream stream) {
		return readStringFrom(stream, StandardCharsets.UTF_8);
	}

	public static String readStringFrom(InputStream stream, Charset charset) {
		try (Scanner scanner = new Scanner(stream, charset.name())) {
			scanner.useDelimiter("\\A");
			try {
				return scanner.next();
			} catch (NoSuchElementException ex) {
				return "";
			}
		}
	}

	public static String readStringFrom(Reader reader) throws IOException {
		return org.apache.commons.io.IOUtils.toString(reader);
	}

	public static String readStringFrom(File file) throws IOException {
		try (FileInputStream input = new FileInputStream(file)) {
			return readStringFrom(input);
		}
	}

	public static String readStringFromResource(Class<?> clazz, String resourceName) throws IOException {
		try (InputStream input = clazz.getResourceAsStream(resourceName)) {
			return readStringFrom(input);
		}
	}
	
	public static void saveUTF8String(String content, File destination) throws IOException {
		try (InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)) ) {
			saveStream(input, destination);
		}
	}
	
	public static void appendUTF8String(String content, File destination) throws IOException {
		Files.write(
				destination.toPath(), 
				content.getBytes(StandardCharsets.UTF_8), 
				StandardOpenOption.APPEND);
	}
	
	public static void saveStream(InputStream input, File destination) throws IOException {
		Files.copy(input, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void copyStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[8 * 1024];
	    int len;
	    while ((len = input.read(buffer)) > 0) {
	    	output.write(buffer, 0, len);
	    }
	    output.flush();
	}
	
	public static void appendToFile(InputStream input, File output) throws IOException {
		try (FileOutputStream outputStream = new FileOutputStream(output, true)) {
			copyStream(input, outputStream);
		}
	}
	
	public static void appendToFile(File input, File output) throws IOException {
		try (FileInputStream inputStream = new FileInputStream(input)) {
			appendToFile(inputStream, output);
		}
	}
	
	public static void saveResource(Class<?> clazz, String resourceName, File destination) throws IOException {
		try (InputStream input = clazz.getResourceAsStream(resourceName)) {
			Files.copy(input, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	public static void zip(InputStream input, OutputStream output, String title) throws IOException {
		try (ZipOutputStream zip = new ZipOutputStream(output, StandardCharsets.UTF_8)) {
			zip.putNextEntry(new ZipEntry(title));
			copyStream(input, zip);
			zip.closeEntry();
		}
	}
	
	public static void zip(byte[] input, OutputStream output, String title) throws IOException {
		try (ZipOutputStream zip = new ZipOutputStream(output, StandardCharsets.UTF_8)) {
			zip.putNextEntry(new ZipEntry(title));
			zip.write(input);
			zip.closeEntry();
		}
	}
	
	public static void unzip(InputStream input, OutputStream output) throws IOException {
		try (InputStream zip = fromZip(input)) {
			copyStream(zip, output);
		}
	}
	
	public static byte[] unzip(InputStream input) throws IOException {
		try (InputStream zip = fromZip(input)) {
			return toByteArray(zip);
		}
	}
	
	public static InputStream fromZip(InputStream stream) throws IOException {
		ZipInputStream zip = new ZipInputStream(stream, StandardCharsets.UTF_8);
		zip.getNextEntry();
		return zip;
	}
	
	public static byte[] toByteArray(InputStream stream) throws IOException {
		return org.apache.commons.io.IOUtils.toByteArray(stream);
	}
	
	public static Properties loadProperties(InputStream input, Charset charset) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset));
		Properties p = new Properties();
		p.load(reader);
		return p;
	}
	
	public static Properties loadProperties(InputStream input) throws IOException {
		return loadProperties(input, StandardCharsets.UTF_8);
	}
	
}

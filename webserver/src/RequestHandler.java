
package bibleServer;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import java.util.Date;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

class RequestHandler {

	private static final String WEB_ROOT = "./public/";

	private static byte[] getPageBytes(String fileRequested) {
		String filePath = WEB_ROOT + fileRequested;
		byte[] contents = null;
		FileInputStream inputStream = null;
		try {
			File fileObject = new File(filePath);
			contents = new byte[(int)fileObject.length()];
			inputStream = new FileInputStream(fileObject);
			inputStream.read(contents);
			inputStream.close();
		} catch (FileNotFoundException e) {
			return Pages.NOT_FOUND.getBytes();
		} catch (IOException e) {
            return Pages.NOT_FOUND.getBytes(); // return ("<h1>IOException!! Error message:'"+ e.getMessage() +"'</h1>").getBytes();
        }
		return contents;
	}

	public static String handle(String fileRequested) {

		String header;
		byte[] body;
		boolean success;
		
		// 'fillerPrefix' is used to prevent a malformed URL exception, since my server doesn't provide
		// the scheme, authority, user info, port, or host. and it's not relevant to the method of this
		// Router class, which is to look through the path and query to pick which jRAPL request to satisfy
		// I dont intend to use url.get() any info provided by this String, just the stuff from the
		// path and query.
		String fillerPrefix = "https://localhost:1111";
		URL url = null;
		try{
			url = new URL(fillerPrefix + fileRequested);
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			//@TODO some sort of error message / status for bad url? or just 404 not found?
		}
		
		HashMap<String,String> queryMap = queryToMap(url.getQuery());

		switch (url.getPath()) {
			case "/":
				body = getPageBytes("/index.html");
				success = ! body.equals(Pages.NOT_FOUND.getBytes());
				break;
			case "/booktext":
				String cmd = queryMap.get("cmd").replace("_"," ");
				body = runShellCmd(cmd).getBytes();
				success = true;
				break;
			default:
				body = getPageBytes(url.getPath());
				success = ! body.equals(Pages.NOT_FOUND.getBytes());
		}

		// Create header based off of collected info
		header = String.join("\n" , 
			"HTTP/1.1 " + ((success) ? "200 OK" : "404 NOT FOUND"), //@TODO some day get more robust error codes
			"Server: Server for Bible Text Requests : 1.0",
			"Date: " + new Date(),
			// "Content-type: " + "text/html", // @TODO not sure if you can just blanket claim that it's "text/html"
			"Content-length: " + body.length,
			"Access-Control-Allow-Origin: *"
		);

		return header + "\n\n" + new String(body);
	}

	// I got this code of the internet but I'm pretty sure it works dandily. I haven't fully confirmed and vetted its usefulness, though
	// Assumes query delimiter is '&'. Consider adding functionality for other delimiters, if need be (just have the split take a regex with the | things)
	private static HashMap<String, String> queryToMap(String source) {// throws Exception {
		if (source == null) return null;
		HashMap<String, String> data = new HashMap<String, String>();
		final String[] arrParameters = source.split("&");
		for (final String tempParameterString : arrParameters) {
			final String[] arrTempParameter = tempParameterString .split("=");
			if (arrTempParameter.length >= 2) {
				final String parameterKey = arrTempParameter[0];
				final String parameterValue = arrTempParameter[1];
				data.put(parameterKey, parameterValue);
			} else {
				final String parameterKey = arrTempParameter[0];
				data.put(parameterKey, "");
			}
		}
		return data;
	}

	public static String runShellCmd(String cmd) {

		try {
			Process process = Runtime.getRuntime().exec(cmd);

			StringBuilder output = new StringBuilder();

			BufferedReader reader
				= new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			int exitVal = process.waitFor();
			if (exitVal == 0) {
				String outputString = output.toString();
				if (outputString.length() > 0) {
					return outputString.substring(0,outputString.length()-1); // remove the last '\n'
				} else { return outputString; }
			} else {
				return "command failed with exit code " + Integer.toString(exitVal);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return "IOException with message: " + e.getMessage();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			return "InterruptedException with message: " + e.getMessage();
		}
	}
}

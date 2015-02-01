package com.xmunch.trunk_notes_atomspace_parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class App {
	public static Scanner scanInput;
	public static FileReader fileReader;
	public static BufferedReader bufferReader;

	final static Logger logger = Logger.getLogger(App.class);

	public static void main(String[] args) {
		String item = AppDescription.EMPTY;
		String data = AppDescription.EMPTY;

		BasicConfigurator.configure();

		//TODO: Refactor and improve. Just a first test.
		try {
			logger.info("Opening the input folder: "
					+ AppDescription.INPUT_FOLDER);
			File folder = new File(AppDescription.INPUT_FOLDER);
			File[] files = folder.listFiles();

			logger.info("There are " + files.length + " files");
			for (File fileEntry : files) {
				Integer line = 1;
				Boolean xaTagged = false;
				logger.info("Checking file: " + fileEntry.getName());

				fileReader = new FileReader(AppDescription.INPUT_FOLDER + "/"
						+ fileEntry.getName());
				bufferReader = new BufferedReader(fileReader);

				while ((item = bufferReader.readLine()) != null) {
					logger.info("Reading line  " + line);
					if (line == 6) {
						try {
							if (item.split(": ")[1] != null
									&& item.split(": ")[1].equals("xa")) {
								logger.info("xa-language note found!");
								xaTagged = true;
							}
						} catch (Exception e) {
							logger.info("Note without tags");
						}
					} else if (line > 7) {
						if (xaTagged) {
							if (!item.isEmpty()) {
								logger.info(item);
								atomSpaceRequest(item);
							}
						} else {
							break;
						}

					}
					line++;
				}

				bufferReader.close();
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	private static Boolean atomSpaceRequest(String text) {
		Boolean result = false;

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String parsedText = restApiParser(text);
			HttpGet getRequest = new HttpGet(AppDescription.SERVER + parsedText);
			getRequest.addHeader(AppDescription.ACCEPT, AppDescription.JSON);

			HttpResponse response = httpClient.execute(getRequest);
			logger.info(AppDescription.SERVER + parsedText);
			logger.info(AppDescription.SUCCESS);

			if (response.getStatusLine().getStatusCode() == AppDescription.SUCCESS) {
				logger.info(AppDescription.SUCCESS);
				result = true;
			} else {
				logger.error(AppDescription.ERROR
						+ response.getStatusLine().getStatusCode());
			}

			httpClient.getConnectionManager().shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static String restApiParser(String text) {
		String parsedText = AppDescription.EMPTY;
		String[] splittedText = text.split(AppDescription.SPACE);

		if (splittedText[1].equals(AppDescription.IS_A)) {
			parsedText += AppDescription.CREATE_VERTEX + AppDescription.LABEL
					+ AppDescription.EQUALS + splittedText[0]
					+ AppDescription.AND + AppDescription.TYPE
					+ AppDescription.EQUALS + splittedText[2];

		} else {
			parsedText += AppDescription.CREATE_EDGE + AppDescription.LABEL
					+ AppDescription.EQUALS + splittedText[1]
					+ AppDescription.AND + AppDescription.FROM
					+ AppDescription.EQUALS + splittedText[0]
					+ AppDescription.AND + AppDescription.TO
					+ AppDescription.EQUALS + splittedText[2];
		}

		if (splittedText.length > 3) {
			parsedText += AppDescription.AND + AppDescription.PARAMS
					+ AppDescription.EQUALS;
			for (int i = 3; i < splittedText.length; i++) {
				if (splittedText[i].split(AppDescription.COLON).length == 2) {
					parsedText += splittedText[i];
					if (i < splittedText.length - 1)
						parsedText += AppDescription.COMMA;
				} else {
					logger.error(AppDescription.PARAM_PROBLEM + splittedText[i]);
				}
			}
		}

		return parsedText;
	}

}
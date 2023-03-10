package com.csv.calculation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public final class IOUtils {
	
	public static String getInput(String fileName){		
		InputStream fileInput = null;
		try {
		fileInput = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			System.out.println("Could not find your specified CSV file. " +
					"Please make sure it is absolute and you have read permissions on that file.");
			e.printStackTrace();
			System.exit(0);
		}
		return new Scanner(fileInput).useDelimiter("\\A").next();
	}
	
	public static void sendOutput(String csvString){
		System.out.println(csvString);
	}
}

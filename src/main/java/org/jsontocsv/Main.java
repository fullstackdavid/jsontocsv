package org.jsontocsv;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.github.opendevl.JFlat;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


public class Main {

	public static void main(String[] args) throws Exception {

		if(args == null || args.length ==0) {
			System.out.println("Please provide the full path of the json file");
			return;
		}
		File jsonFile = null;
		if (0 < args.length) {
		   jsonFile = new File(args[0]);
		} 
		String fileName = jsonFile.getName();
		fileName = fileName.substring(0, fileName.indexOf('.'));
		System.out.println("Reading json file = "+fileName+".json");
		String str = new String(Files.readAllBytes(Paths.get(args[0])));

		JFlat flatMe1 = new JFlat(str);

		List<String> jsonStrings = flatMe1.splitJson();
		
		for (int i = 0; i < jsonStrings.size(); i++) {
			String string = jsonStrings.get(i);
			String csvName = fileName +(i+1)+ ".csv" ;
			System.out.println("Creating csv file = "+csvName);
			JFlat flatMe2 = new JFlat(string);
			// get the 2D representation of JSON document
			List<Object[]> json2csv = flatMe2.json2Sheet().getJsonAsSheet();

			// write the 2D representation in csv format
			flatMe2.write2csv(csvName);
			updateCSV(csvName);
		}
	}

	public static void updateCSV(String fileToUpdate) throws IOException {
		File inputFile = new File(fileToUpdate);

		// Read existing file
		CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
		List<String[]> csvBody = reader.readAll();
		String[] csvColumns = csvBody.get(0);
		for (int i = 0; i < csvColumns.length; i++) {
			
			csvBody.get(0)[i] = csvColumns[i].substring(1);
		}
		// get CSV row column and replace with by using row and column
		for (int i = 0; i < csvBody.size(); i++) {
			String[] strArray = csvBody.get(i);
			for (int j = 0; j < strArray.length; j++) {
				String replace = strArray[j].replace('/', '_');
				csvBody.get(i)[j] = replace; // Target replacement
			}
		}
		reader.close();

		// Write to CSV file which is open
		CSVWriter writer = new CSVWriter(new FileWriter(inputFile), ',');
		writer.writeAll(csvBody);
		writer.flush();
		writer.close();
	}

}

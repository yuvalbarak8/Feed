package com.example.fakebook;

import android.content.res.Resources;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

public class JsonFileReader {

    // Method to read the JSON file from the raw resource
    public static String readJsonFile(Resources resources, int resourceId) {
        try {
            // Open the input stream for the raw resource
            InputStream inputStream = resources.openRawResource(resourceId);

            // Create a Scanner to read the data
            Scanner scanner = new Scanner(inputStream);
            StringBuilder jsonString = new StringBuilder();

            // Read each line and append it to a StringBuilder
            while (scanner.hasNextLine()) {
                jsonString.append(scanner.nextLine());
            }

            // Close the streams
            scanner.close();
            inputStream.close();

            // Return the JSON string
            return jsonString.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
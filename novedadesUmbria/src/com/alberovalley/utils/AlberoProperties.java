package com.alberovalley.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

public final class AlberoProperties {

    // ============================================================================================
    // HIDE CONSTRUCTOR
    // ============================================================================================
	private AlberoProperties(){}
    // ============================================================================================
    // METHODS
    // ============================================================================================
	
    public static Properties loadProperties(Context ctx) throws IOException {
        String[] fileList = { "app.properties" };
        Properties prop = new Properties();
        for (int i = fileList.length - 1; i >= 0; i--) {
            String file = fileList[i];
            try {
                InputStream fileStream = ctx.getApplicationContext().getAssets().open(file);
                prop.load(fileStream);
                fileStream.close();
            } catch (FileNotFoundException e) {
                AlberoLog.d("Ignoring missing property file " + file);
            }
        }
        return prop;
    }

}

package org.xdty.phone.number.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    private static Gson GSON;

    public static synchronized Gson gson() {
        if (GSON == null) {
            GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        }
        return GSON;
    }

    public static synchronized File createCacheFile(Context context, String filename, int raw)
            throws IOException {
        File cacheFile = new File(context.getCacheDir(), filename);

        if (cacheFile.exists()) {
            return cacheFile;
        }

        InputStream inputStream = context.getResources().openRawResource(raw);
        FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, length);
        }

        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cacheFile;
    }

    public static synchronized boolean removeCacheFile(Context context, String filename) {
        File cacheFile = new File(context.getCacheDir(), filename);
        try {
            if (cacheFile.exists()) {
                return cacheFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

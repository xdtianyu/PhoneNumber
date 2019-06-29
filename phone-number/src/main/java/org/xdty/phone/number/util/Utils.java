package org.xdty.phone.number.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.xdty.phone.number.model.INumber;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
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

    /**
     * Unzip a zip file.  Will overwrite existing files.
     *
     * @param zipFile  Full path of the zip file you'd like to unzip.
     * @param location Full path of the directory you'd like to unzip to (will be created if it
     *                 doesn't exist).
     * @throws IOException
     */
    public static void unzip(String zipFile, String location) throws IOException {
        final int BUFFER_SIZE = 10240;
        int size;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if (!location.endsWith("/")) {
                location += "/";
            }
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if (null != parentDir) {
                            if (!parentDir.isDirectory()) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        } finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unzip exception", e);
        }
    }

    public static boolean checkMD5(String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Log.e(TAG, "MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");
            return false;
        }

        Log.v(TAG, "Calculated digest: " + calculatedDigest);
        Log.v(TAG, "Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static String fixNumber(String number) {
        String fixedNumber = number;
        if (number.startsWith("+86")) {
            fixedNumber = number.replace("+86", "");
        }

        if (number.startsWith("86") && number.length() > 9) {
            fixedNumber = number.replaceFirst("^86", "");
        }

        if (number.startsWith("+400")) {
            fixedNumber = number.replace("+", "");
        }

        if (fixedNumber.startsWith("12583")) {
            fixedNumber = fixedNumber.replaceFirst("^12583.", "");
        }

        if (fixedNumber.startsWith("1259023")) {
            fixedNumber = number.replaceFirst("^1259023", "");
        }

        return fixedNumber;
    }

    public static String fixNumberPlus(String number) {
        String fixedNumber = number;
        if (number.startsWith("+86")) {
            fixedNumber = number.replace("+86", "");
        }

        if (number.startsWith("+400")) {
            fixedNumber = number.replace("+", "");
        }

        if (fixedNumber.startsWith("+")) {
            fixedNumber = number.replace("+", "");
        }

        if (number.startsWith("86") && number.length() > 9) {
            fixedNumber = number.replaceFirst("^86", "");
        }

        if (fixedNumber.startsWith("12583")) {
            fixedNumber = fixedNumber.replaceFirst("^12583.", "");
        }

        if (fixedNumber.startsWith("1259023")) {
            fixedNumber = number.replaceFirst("^1259023", "");
        }

        return fixedNumber;
    }

    public static String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static INumber pathGeo(List<INumber> numberList) {
        INumber iNumber = null;

        Collections.sort(numberList, new Comparator<INumber>() {
            @Override
            public int compare(INumber o1, INumber o2) {
                return o1.getApiId() - o2.getApiId();
            }
        });

        for (INumber i : numberList) {
            if (i != null && i.isValid()) {
                if (i.hasGeo()) {
                    if (iNumber == null) { // return result
                        return i;
                    } else { // patch geo info to previous result
                        iNumber.patch(i);
                        return iNumber;
                    }
                } else { // continue for geo info
                    iNumber = i;
                }
            }
        }
        return iNumber;
    }

    public static INumber mostCount(List<INumber> numberList) {
        INumber iNumber = null;

        Collections.sort(numberList, new Comparator<INumber>() {
            @Override
            public int compare(INumber o1, INumber o2) {
                return (o2 == null ? 0 : o2.getCount()) - (o1 == null ? 0 : o1.getCount());
            }
        });

        for (INumber i : numberList) {
            if (i != null && i.isValid()) {
                iNumber = i;
                break;
            }
        }
        return iNumber;
    }
}

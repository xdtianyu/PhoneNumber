package org.xdty.phone.number.model.offline;

import android.content.Context;

import org.xdty.phone.number.R;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class OfflineHandler implements NumberHandler<OfflineNumber> {

    private final static int PHONE_FMT_LENGTH = 9;
    private Context mContext;

    public OfflineHandler(Context context) {
        mContext = context;
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public String key() {
        return null;
    }

    @Override
    public OfflineNumber find(String number) {
        number = number.replaceAll("\\+", "");
        if (number.length() < 7 || number.length() > 11) {
            return null;
        }

        try {
            OfflineNumber offlineNumber = null;

            int phone = Integer.parseInt(number.substring(0, 7));
            File file = createCacheFile(mContext, "phone.dat", R.raw.phone);
            long length = file.length();
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            byte bVersion[] = new byte[4];
            raf.read(bVersion);
            byte bFirstOffset[] = new byte[4];
            raf.read(bFirstOffset);
            int firstOffset = OfflineIndex.byteArrayToLeInt(bFirstOffset);
            raf.seek(firstOffset);
            int left = 0;
            int right = (int) (length - firstOffset) / PHONE_FMT_LENGTH;

            int middle;
            int currentOffset;
            int currentPhone;

            byte bNumber[] = new byte[4];

            while (left <= right) {
                middle = (right + left) / 2;
                currentOffset = firstOffset + middle * PHONE_FMT_LENGTH;
                if (currentOffset >= length) {
                    break;
                }
                raf.seek(currentOffset);
                raf.read(bNumber);
                currentPhone = OfflineIndex.byteArrayToLeInt(bNumber);
                if (currentPhone > phone) {
                    right = middle - 1;
                } else if (currentPhone < phone) {
                    left = middle + 1;
                } else {
                    raf.seek(currentOffset);
                    byte bRecord[] = new byte[PHONE_FMT_LENGTH];
                    raf.read(bRecord);
                    OfflineIndex offlineIndex = new OfflineIndex(bRecord);
                    raf.seek(offlineIndex.offset);

                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    while (true) {
                        int nextByte = raf.read();
                        if (nextByte == '\0') {
                            break;
                        } else {
                            b.write(nextByte);
                        }
                    }
                    offlineNumber = new OfflineNumber(new String(b.toByteArray()), number,
                            offlineIndex.type);
                    b.close();
                    break;
                }
            }

            if (offlineNumber != null) {
                return offlineNumber;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_OFFLINE;
    }

    private File createCacheFile(Context context, String filename, int raw) throws IOException {
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
}

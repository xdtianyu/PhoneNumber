package org.xdty.phone.number.model.offline;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class OfflineIndex {
    int number;
    int offset;
    int type;

    protected OfflineIndex(byte[] data) {
        number = byteArrayToLeInt(Arrays.copyOfRange(data, 0, 4));
        offset = byteArrayToLeInt(Arrays.copyOfRange(data, 4, 8));
        type = data[8];
    }

    public static int byteArrayToLeInt(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    public static byte[] leIntToByteArray(int i) {
        final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(i);
        return bb.array();
    }

    public String toString() {
        return number + ", " + offset + ", " + type;
    }
}

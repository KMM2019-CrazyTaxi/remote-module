package helpers;

/**
 * Static data conversion helper class.
 *
 * @author Henrik Nilsson
 */
public class DataConversionHelper {
    /**
     * Get byte[] from int of a given length
     * @param val int value
     * @param numberOfBytes Number of bytes to recieve
     * @return byte[] of split int
     */
    public static byte[] intToByteArray(int val, int numberOfBytes) {
        if (numberOfBytes > 4) numberOfBytes = 4;

        byte[] bytes = new byte[numberOfBytes];
        for (int i = 0;  i < numberOfBytes; i++) {
            bytes[i] = (byte) (val & 0xff);
            val >>= 8;
        }

        return bytes;
    }

    /**
     * Get int from byte[] of split int
     * @param val byte[] of split int (only uses first 4 bytes)
     * @return int of concatinated bytes
     */
    public static int byteArrayToUnsignedInt(byte[] val) {
        int integerVal = 0;
        for (int i = val.length - 1; i >= 0; i--) {
            integerVal <<= 8;
            integerVal |= val[i] & 0xff;
        }
        return integerVal;
    }

    /**
     * Get int from byte[] of split int
     * @param val byte[] of split int
     * @param offset Offset in bytes of where to start decoding
     * @param length Length to decode (only uses first 4 bytes)
     * @return int of concatinated bytes
     */
    public static int byteArrayToUnsignedInt(byte[] val, int offset, int length) {
        int integerVal = 0;
        for (int i = offset + length - 1; i >= offset; i--) {
            integerVal <<= 8;
            integerVal |= val[i] & 0xff;
        }
        return integerVal;
    }

    /**
     * Get int from byte[] of split int
     * @param val byte[] of split int (only uses first 4 bytes)
     * @return int of concatinated bytes
     */
    public static int byteArrayToSignedInt(byte[] val) {
        int integerVal = 0;
        for (int i = val.length - 1; i >= 0; i--) {
            integerVal <<= 8;
            if (i == val.length - 1) {
                integerVal |= val[i];
            }
            else {
                integerVal |= (val[i] & 0xff);
            }
        }
        return integerVal;
    }

    /**
     * Get int from byte[] of split int
     * @param val byte[] of split int
     * @param offset Offset in bytes of where to start decoding
     * @param length Length to decode (only uses first 4 bytes)
     * @return int of concatinated bytes
     */
    public static int byteArrayToSignedInt(byte[] val, int offset, int length) {
        int integerVal = 0;
        for (int i = offset + length - 1; i >= offset; i--) {
            integerVal <<= 8;
            if (i == val.length - 1) {
                integerVal |= val[i];
            }
            else {
                integerVal |= (val[i] & 0xff);
            }
        }
        return integerVal;
    }


    /**
     * Get byte[] of split float
     * @param value float value to split
     * @return byte[] of split float
     */
    public static byte[] floatToByteArray(float value) {
        int intBits =  Float.floatToIntBits(value);
        return new byte[] {
                (byte) (intBits), (byte) (intBits >> 8), (byte) (intBits >> 16), (byte) (intBits >> 24) };
    }

    /**
     * Get float from byte[] of split float
     * @param bytes byte[] of lenght > 4 (only uses first 4 bytes)
     * @return float of concatinated bytes
     */
    public static float byteArrayToFloat(byte[] bytes) {
        int intBits =
          bytes[3] << 24 | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
        return Float.intBitsToFloat(intBits);
    }

    /**
     * Get float from byte[] of split float
     * @param bytes byte[] of lenght > (offset + 4)
     * @param offset offset to start decoding from
     * @return float of concatinated bytes
     */
    public static float byteArrayToFloat(byte[] bytes, int offset) {
        int intBits =
          bytes[offset + 3] << 24 | (bytes[offset + 2] & 0xFF) << 16 | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset] & 0xFF);
        return Float.intBitsToFloat(intBits);
    }

    /**
     * Get byte[] of split double
     * @param value double value to split
     * @return byte[] of split double
     */
    public static byte[] doubleToByteArray(double value) {
        long longBits =  Double.doubleToLongBits(value);
        return new byte[] {
                (byte) (longBits),
                (byte) (longBits >> 8),
                (byte) (longBits >> 16),
                (byte) (longBits >> 24),
                (byte) (longBits >> 32),
                (byte) (longBits >> 40),
                (byte) (longBits >> 48),
                (byte) (longBits >> 56)
        };
    }

    /**
     * Get double from byte[] of split double
     * @param bytes byte[] of lenght > 8 (only uses first 8 bytes)
     * @return double of concatinated bytes
     */
    public static double byteArrayToDouble(byte[] bytes) {
        long longBits =
          (bytes[7] & 0xFFL) << 56 |
          (bytes[6] & 0xFFL) << 48 |
          (bytes[5] & 0xFFL) << 40 |
          (bytes[4] & 0xFFL) << 32 |
          (bytes[3] & 0xFFL) << 24 |
          (bytes[2] & 0xFFL) << 16 |
          (bytes[1] & 0xFFL) << 8 |
          (bytes[0] & 0xFFL);
        return Double.longBitsToDouble(longBits);
    }

    /**
     * Get double from byte[] of split double
     * @param bytes byte[] of lenght > (offset + 8)
     * @param offset offset to start decoding from
     * @return double of concatinated bytes
     */
    public static double byteArrayToDouble(byte[] bytes, int offset) {
        long longBits =
          (bytes[offset + 7] & 0xFFL) << 56 |
          (bytes[offset + 6] & 0xFFL) << 48 |
          (bytes[offset + 5] & 0xFFL) << 40 |
          (bytes[offset + 4] & 0xFFL) << 32 |
          (bytes[offset + 3] & 0xFFL) << 24 |
          (bytes[offset + 2] & 0xFFL) << 16 |
          (bytes[offset + 1] & 0xFFL) << 8 |
          (bytes[offset + 0] & 0xFFL);
        return Double.longBitsToDouble(longBits);
    }
}

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
    public static int byteArrayToInt(byte[] val) {
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
    public static int byteArrayToInt(byte[] val, int offset, int length) {
        int integerVal = 0;
        for (int i = length - 1; i >= offset; i--) {
            integerVal <<= 8;
            integerVal |= val[i] & 0xff;
        }
        return integerVal;
    }
}

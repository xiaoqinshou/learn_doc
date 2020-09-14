package com.qs.utils.EncryptionUtils;

/**
 * @className: HexUtil
 * @description: 十六进制转换工具
 * @author: author
 * @date: 2019-02-21 16:40
 * @version: 1.0.0
 **/
public class HexUtil {

    static private final int BASELENGTH = 128;
    static private final int LOOKUPLENGTH = 16;
    static final private byte[] ByteMap = new byte[BASELENGTH];
    static final private char[] HexMap = new char[LOOKUPLENGTH];


    static {
        for (int i = 0; i < BASELENGTH; i++) {
            ByteMap[i] = -1;
        }
        for (int i = '9'; i >= '0'; i--) {
            ByteMap[i] = (byte) (i - '0');
        }
        for (int i = 'F'; i >= 'A'; i--) {
            ByteMap[i] = (byte) (i - 'A' + 10);
        }
        for (int i = 'f'; i >= 'a'; i--) {
            ByteMap[i] = (byte) (i - 'a' + 10);
        }

        for (int i = 0; i < 10; i++) {
            HexMap[i] = (char) ('0' + i);
        }
        for (int i = 10; i <= 15; i++) {
            HexMap[i] = (char) ('A' + i - 10);
        }
    }

    /**
     * @author: author
     * @date: 2018-12-10 02:05
     * @description: 摘要转byte 已测没毛病
     * @param digest
     * @return: byte[]
     */
    public static String ByteToHex(byte[] digest) {
        if (digest == null) {
            return null;
        }
        char[] chars = new char[digest.length * 2];
        int k = 0;
        for (byte b : digest) {
            chars[k++] = HexMap[(b >>> 4 & 0xf)];
            chars[k++] = HexMap[(b & 0xf)];
        }
        return new String(chars);
    }

    public static byte[] HexToByte(String Hex) {
        if (Hex == null)
            return null;
        int lengthData = Hex.length();
        if (lengthData % 2 != 0)
            return null;

        char[] binaryData = Hex.toCharArray();
        int lengthDecode = lengthData / 2;
        byte[] decodedData = new byte[lengthDecode];
        byte temp1, temp2;
        char tempChar;
        for (int i = 0; i < lengthDecode; i++) {
            tempChar = binaryData[i * 2];
            temp1 = (tempChar < BASELENGTH) ? ByteMap[tempChar] : -1;
            if (temp1 == -1)
                return null;
            tempChar = binaryData[i * 2 + 1];
            temp2 = (tempChar < BASELENGTH) ? ByteMap[tempChar] : -1;
            if (temp2 == -1)
                return null;
            decodedData[i] = (byte) ((temp1 << 4) | temp2);
        }
        return decodedData;
    }

    public static void main(String[] args) {
        byte[] s = HexToByte("8B99024692BB6FA51D4BC929D780265C1BBA42AA507D0DA8BA1E1667CF5CA24C7295783F295E94BB5CB4BC4A3F023856175BC4FEDCEFBDFF4E3F71E629C2F35626945285458B0B3D5A4D3E5AC7FB64150531E3F51DB942A1EF00978C84D9FD5D14DB3D23F7CD29A4255D8868EFA6C5F335C632192ABBB22C3E6D8FAC8021A56E7201CDDD284819DD40E1919D07990B808E17DA017DD3CBE4B9313FBFB67EEA7E");
        for (byte b : s) {
            System.out.print(b);
        }
    }
}

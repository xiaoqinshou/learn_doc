package com.qs.utils.EncryptionUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @className: Base64Util
 * @description: Base64加密工具类
 * @author: author
 * @date: 2019-01-15 10:54
 * @version: 1.0.0
 **/
public class Base64Util {

    private static final char[] base64 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    private static final Map<String, Byte> Base64Map;

    static {
        Base64Map = new HashMap<String, Byte>();
        Base64Map.put("A", (byte) 0);
        Base64Map.put("B", (byte) 1);
        Base64Map.put("C", (byte) 2);
        Base64Map.put("D", (byte) 3);
        Base64Map.put("E", (byte) 4);
        Base64Map.put("F", (byte) 5);
        Base64Map.put("G", (byte) 6);
        Base64Map.put("H", (byte) 7);
        Base64Map.put("I", (byte) 8);
        Base64Map.put("J", (byte) 9);
        Base64Map.put("K", (byte) 10);
        Base64Map.put("L", (byte) 11);
        Base64Map.put("M", (byte) 12);
        Base64Map.put("N", (byte) 13);
        Base64Map.put("O", (byte) 14);
        Base64Map.put("P", (byte) 15);
        Base64Map.put("Q", (byte) 16);
        Base64Map.put("R", (byte) 17);
        Base64Map.put("S", (byte) 18);
        Base64Map.put("T", (byte) 19);
        Base64Map.put("U", (byte) 20);
        Base64Map.put("V", (byte) 21);
        Base64Map.put("W", (byte) 22);
        Base64Map.put("X", (byte) 23);
        Base64Map.put("Y", (byte) 24);
        Base64Map.put("Z", (byte) 25);
        Base64Map.put("a", (byte) 26);
        Base64Map.put("b", (byte) 27);
        Base64Map.put("c", (byte) 28);
        Base64Map.put("d", (byte) 29);
        Base64Map.put("e", (byte) 30);
        Base64Map.put("f", (byte) 31);
        Base64Map.put("g", (byte) 32);
        Base64Map.put("h", (byte) 33);
        Base64Map.put("i", (byte) 34);
        Base64Map.put("j", (byte) 35);
        Base64Map.put("k", (byte) 36);
        Base64Map.put("l", (byte) 37);
        Base64Map.put("m", (byte) 38);
        Base64Map.put("n", (byte) 39);
        Base64Map.put("o", (byte) 40);
        Base64Map.put("p", (byte) 41);
        Base64Map.put("q", (byte) 42);
        Base64Map.put("r", (byte) 43);
        Base64Map.put("s", (byte) 44);
        Base64Map.put("t", (byte) 45);
        Base64Map.put("u", (byte) 46);
        Base64Map.put("v", (byte) 47);
        Base64Map.put("w", (byte) 48);
        Base64Map.put("x", (byte) 49);
        Base64Map.put("y", (byte) 50);
        Base64Map.put("z", (byte) 51);
        Base64Map.put("0", (byte) 52);
        Base64Map.put("1", (byte) 53);
        Base64Map.put("2", (byte) 54);
        Base64Map.put("3", (byte) 55);
        Base64Map.put("4", (byte) 56);
        Base64Map.put("5", (byte) 57);
        Base64Map.put("6", (byte) 58);
        Base64Map.put("7", (byte) 59);
        Base64Map.put("8", (byte) 60);
        Base64Map.put("9", (byte) 61);
        Base64Map.put("+", (byte) 62);
        Base64Map.put("/", (byte) 63);
    }

    /**
     * @author: author
     * @date: 2019-01-19 10:33
     * @description: 默认编码Base64加密
     * @param str
     * @return: java.lang.String
     */
    public static String Encrypt(String str) {
        return Encrypt(str, "UTF-8");
    }

    /**
     * @author: author
     * @date: 2019-01-19 10:38
     * @description: 默认编码Base64解密
     * @param str
     * @return: java.lang.String
     */
    public static String Decrypt(String str) {
        return Decrypt(str, "UTF-8");
    }


    /**
     * @author: author
     * @date: 2019-01-19 10:26
     * @description: Base64 加密
     * @param str
     * @param charsetName
     * @return: java.lang.String
     */
    public static String Encrypt(String str, String charsetName) {
        byte[] b = null;
        try {
            b = str.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.err.println("Unsupported Encoding Exception:" + charsetName);
            b = str.getBytes();
        }
        int l = b.length % 3 > 0 ? 3 - b.length % 3 : 0;
        byte[] c = GetBytes(b);
        String s = ByteToString(c);
        if (l > 0) {
            s = s.substring(0, s.length() - l) + (l == 1 ? "=" : "==");
        }
        return s;
    }

    /**
     * @author: author
     * @date: 2019-01-15 17:13
     * @description: Base64 解密
     * @param str
     * @return: java.lang.String
     */
    public static String Decrypt(String str, String charsetName) {
        str = str.replaceAll("=", "");
        char[] c = str.toCharArray();

        byte[] decrypt = new byte[c.length];
        for (int i = 0; i < c.length; i++) {
            decrypt[i] = Base64Map.get(String.valueOf(c[i]));
        }
        decrypt = DecryptByte(decrypt);
        try {
            return new String(decrypt, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.err.println("Unsupported Encoding Exception:" + charsetName);
            return new String(decrypt);
        }
    }

    /**
     * @author: author
     * @date: 2019-01-15 17:13
     * @description: 获取Base64解密后的byte数组
     * @param b
     * @return: byte[]
     */
    private static byte[] DecryptByte(byte[] b) {
        byte[] a = new byte[(b.length * 3 / 4)];
        int i = 0, j = 0;
        while (i < a.length && j < b.length) {
            if (i % 3 == 0) {
                a[i++] = (byte) (((b[j++] << 2) & 0xFF) | (b[j++] >>> 4) & 0xFF);
                j--;
            } else if (i % 3 == 1) {
                a[i++] = (byte) (((b[j++] << 4) & 0xFF) | (b[j++] >>> 2) & 0xFF);
                j--;
            } else {
                a[i++] = (byte) (((b[j++] << 6) & 0xFF) | b[j++] & 0xFF);
            }
        }
        return a;
    }

    /**
     * @author: author
     * @date: 2019-01-15 16:08
     * @description: 获取BASE64加密后的byte数组
     * @param b
     * @return: byte[]
     */
    private static byte[] GetBytes(byte[] b) {
        int length = b.length;
        int d = length % 3 > 0 ? length / 3 + 1 : length / 3;
        byte[] c = new byte[4 * d];
        for (int i = 0, j = 0; i < length; j = j + 4, i = i + 3) {
            byte temp1 = b[i];
            byte temp2 = 0;
            byte temp3 = 0;
            if (i + 1 < length) {
                temp2 = b[i + 1];
                if (i + 2 < length) {
                    temp3 = b[i + 2];
                    c[j] = (byte) ((temp1 >>> 2) & 0xFF);
                    c[j + 1] = (byte) (((((temp1 << 6) & 0xFF) >>> 2) & 0xFF) | ((temp2 >>> 4) & 0xFF));
                    c[j + 2] = (byte) (((((temp2 << 4) & 0xFF) >>> 2) & 0xFF) | ((temp3 >>> 6) & 0xFF));
                    c[j + 3] = (byte) ((((temp3 << 2) & 0xFF) >>> 2) & 0xFF);
                    continue;
                }
                c[j] = (byte) ((temp1 >>> 2) & 0xFF);
                c[j + 1] = (byte) ((((temp1 << 6) & 0xFF) >>> 2 & 0xFF) | ((temp2 >>> 4) & 0xFF));
                c[j + 2] = (byte) ((((temp2 << 4) & 0xFF) >>> 2 & 0xFF) | ((temp3 >>> 6) & 0xFF));
                continue;
            }
            c[j] = (byte) ((temp1 >>> 2) & 0xFF);
            c[j + 1] = (byte) ((((temp1 << 6) & 0xFF) >>> 2 & 0xFF) | ((temp2 >>> 4) & 0xFF));
        }
        return c;
    }


    /**
     * @author: author
     * @date: 15:05
     * @description: 8位字节打印字符串
     * @param b
     * @return: java.lang.String
     */
    private static String ByteToString(byte[] b) {
        StringBuffer str = new StringBuffer();
        for (int a = 0; a < b.length; a++) {
            int i = b[a] & 0xFF;
            str.append(base64[i]);
        }
        return str.toString();
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        System.out.println("Base64加解密测试：开始->" + start);
        System.out.println(Base64Util.Encrypt("8B99024692BB6FA51D4BC929D780265C1BBA42AA507D0DA8BA1E1667CF5CA24C7295783F295E94BB5CB4BC4A3F023856175BC4FEDCEFBDFF4E3F71E629C2F35626945285458B0B3D5A4D3E5AC7FB64150531E3F51DB942A1EF00978C84D9FD5D14DB3D23F7CD29A4255D8868EFA6C5F335C632192ABBB22C3E6D8FAC8021A56E7201CDDD284819DD40E1919D07990B808E17DA017DD3CBE4B9313FBFB67EEA7E"));
        System.out.println(Base64Util.Decrypt("OEI5OTAyNDY5MkJCNkZBNTFENEJDOTI5RDc4MDI2NUMxQkJBNDJBQTUwN0QwREE4QkExRTE2NjdDRjVDQTI0QzcyOTU3ODNGMjk1RTk0QkI1Q0I0QkM0QTNGMDIzODU2MTc1QkM0RkVEQ0VGQkRGRjRFM0Y3MUU2MjlDMkYzNTYyNjk0NTI4NTQ1OEIwQjNENUE0RDNFNUFDN0ZCNjQxNTA1MzFFM0Y1MURCOTQyQTFFRjAwOTc4Qzg0RDlGRDVEMTREQjNEMjNGN0NEMjlBNDI1NUQ4ODY4RUZBNkM1RjMzNUM2MzIxOTJBQkJCMjJDM0U2RDhGQUM4MDIxQTU2RTcyMDFDREREMjg0ODE5REQ0MEUxOTE5RDA3OTkwQjgwOEUxN0RBMDE3REQzQ0JFNEI5MzEzRkJGQjY3RUVBN0U"));
        System.out.println("Base64加解密测试：结束->" + (System.currentTimeMillis() - start));
    }
}

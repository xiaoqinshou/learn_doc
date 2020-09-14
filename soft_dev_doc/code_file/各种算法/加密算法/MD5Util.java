package com.qs.utils.EncryptionUtils;

import org.junit.Test;

/**
 * @className: MD5Util
 * @description: MD5算法加密
 * @author: author
 * @date: 2018-12-07 00:44
 * @version: 1.0.0
 **/
public class MD5Util {

    /**
     *  A 官方常量 0x01234567(19088743),程序幻量0x67452301(1732584193)
     *  */
    private static int A = 1732584193;

    /**
     *  B 官方常量 0x89ABCDEF(162254319),程序幻量0xEFCDAB89(-1875749769)(无符号十进制4023233417)
     *  */
    private static int B = 0xEFCDAB89;

    /**
     * C 官方常量 0xFEDCBA98,程序幻量0x98BADCFE(-2128394904)(无符号十进制:4275878552)
     * */
    private static int C = 0x98BADCFE;

    /**
     * D 官方常量 0x76543210,程序幻量0x10325476(271733878)
     * */
    private static int D = 0x10325476;

    /**
     * SJ 官方常量
     * */
    private static int[][] SJ = new int[][]{{7, 12, 17, 22}, {5, 9, 14, 20}, {4, 11, 16, 23}, {6, 10, 15, 21}};

    /**
     * KJ 官方常量
     * */
    private static int[] KJ = new int[]{
            0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee, 0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501, 0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be, 0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
            0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa, 0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8, 0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
            0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c, 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70, 0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05, 0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
            0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039, 0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1, 0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1, 0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
    };

    /**
     * md5 字符
     * */
    private static char[] Hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * @author: author
     * @date: 2018-12-08 00:43
     * @description: MD5 加密算法
     * @param s
     * @return: java.lang.String
     */
    public static String MD5Encryption(String s) {
        byte[] b = s.getBytes();
        int bl = b.length;
        int a = bl % 64;
        if (a > 56) {
            a = 56 + 64 - a;
        } else if (a < 56) {
            a = 56 - a;
        }
        //最终长度等于原长+补长+原长长度
        int FinalLength = bl + a + 8;
        byte[] c = new byte[FinalLength];
        byte[] blb = LongToByte(bl);
        int j = 0;
        for (int i = 0; i < FinalLength; i++) {
            if (i < bl) {
                c[i] = b[i];
            } else if (i == bl) {
                c[i] = (byte) (128 & 0xFF);
            } else if (i > bl && i < bl + a) {
                c[i] = 0x00;
            } else {
                c[i] = blb[j];
                j++;
            }
        }
        //循环主次数
        int MT = (FinalLength) / 64;
        //构建MI 一共16个 每个32位
        byte[][][] Mi = new byte[MT][16][4];
        for (int i = 0; i < MT; i++) {
            for (int n = 0; n < 16; n++) {
                for (int m = 0; m < 4; m++) {
                    Mi[i][n][m] = c[64 * i + 4 * n + m];
                }
            }
        }
        int[] digest = getDigest(Mi);
        byte[] digestMap = DigestToByte(digest);

        return ByteToString(digestMap);
    }

    private static String ByteToString(byte[] b) {
        StringBuffer str = new StringBuffer();
        for (int a = 0; a < b.length; a++) {
            int i = b[a] & 0xFF;
            str.append(Hex[i]);
        }
        return str.toString();
    }

    /**
     * @author: author
     * @date: 2018-12-10 02:05
     * @description: 摘要转byte 已测没毛病
     * @param digest
     * @return: byte[]
     */
    private static byte[] DigestToByte(int[] digest) {
        byte[] bytes = new byte[32];
        for (int a = 0; a < digest.length; a++) {
            byte[] digestByte = IntToByte(digest[a]);
            for (int b = 0; b < 4; b++) {
                int i = digestByte[b] & 0xFF;
                int j = (i >> 4) & 0xFF;
                int k = i - (j << 4 & 0xFF);
                bytes[8 * a + 2 * b] = (byte) (j & 0xFF);
                bytes[8 * a + 2 * b + 1] = (byte) (k & 0xFF);
            }
        }
        return bytes;
    }

    /**
     * @author: author
     * @date: 2018-12-07 02:39
     * @description: int 转 byte
     * @param i int 数字
     * @return: byte[]
     */
    private static byte[] IntToByte(int i) {
        return new byte[]{
                (byte) ((i >> 24) & 0xFF),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) (i & 0xFF)
        };
    }

    /**
     * @author: author
     * @date: 2018-12-07 02:39
     * @description: long 转 byte
     * @param i int 数字
     * @return: byte[]
     */
    private static byte[] LongToByte(long i) {
        return new byte[]{
                (byte) ((i >> 56) & 0xFF),
                (byte) ((i >> 48) & 0xFF),
                (byte) ((i >> 40) & 0xFF),
                (byte) ((i >> 32) & 0xFF),
                (byte) ((i >> 24) & 0xFF),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) (i & 0xFF)
        };
    }

    /**
     * @author: author
     * @date: 2018-12-10 01:09
     * @description: Byte[4]数组 转 int（32位）
     * @param b
     * @return: int
     */
    private static int ByteToInt(byte[] b) {
        return (b[0] & 0xFF) << 24 |
                (b[1] & 0xFF) << 16 |
                (b[2] & 0xFF) << 8 |
                b[3] & 0xFF;
    }

    /**
     * @author: author
     * @date: 2018-12-10 01:08
     * @description: MD5 官方给的 MD5 F算法
     * @param X
     * @param Y
     * @param Z
     * @return: int
     */
    private static int F(int X, int Y, int Z) {
        return (X & Y) | ((~X) & Z);
    }

    /**
     * @author: author
     * @date: 2018-12-10 01:09
     * @description: MD5 官方给的 MD5 G算法
     * @param X
     * @param Y
     * @param Z
     * @return: int
     */
    private static int G(int X, int Y, int Z) {
        return (X & Z) | (Y & (~Z));
    }

    /**
     * @author: author
     * @date: 2018-12-10 01:10
     * @description: MD5 官方给的 MD5 H算法
     * @param X
     * @param Y
     * @param Z
     * @return: int
     */
    private static int H(int X, int Y, int Z) {
        return X ^ Y ^ Z;
    }

    /**
     * @author: author
     * @date: 2018-12-10 01:10
     * @description: MD5 官方给的 MD5 I算法
     * @param X
     * @param Y
     * @param Z
     * @return: int
     */
    private static int I(int X, int Y, int Z) {
        return Y ^ (X | (~Z));
    }

    private static int FF(int b, int c, int d, byte[] m, int s, int k) {
        int temp = F(b, c, d) + ByteToInt(m) + k;
        return b + ((temp << s) | (temp >>> (32 - s)));
    }

    private static int GG(int b, int c, int d, byte[] m, int s, int k) {
        int temp = G(b, c, d) + ByteToInt(m) + k;
        return b + ((temp << s) | (temp >>> (32 - s)));
    }

    private static int HH(int b, int c, int d, byte[] m, int s, int k) {
        int temp = H(b, c, d) + ByteToInt(m) + k;
        return b + ((temp << s) | (temp >>> (32 - s)));
    }

    private static int II(int b, int c, int d, byte[] m, int s, int k) {
        int temp = I(b, c, d) + ByteToInt(m) + k;
        return b + ((temp << s) | (temp >>> (32 - s)));
    }

    /**
     * @author: author
     * @date: 2018-12-10 01:13
     * @description: 获取信息摘要核心算法
     * @param MI
     * @return: int[]
     */
    private static int[] getDigest(byte[][][] MI) {
        int a = A;
        int b = B;
        int c = C;
        int d = D;
        int[] digest = new int[]{a, b, c, d};
        for (int i = 0; i < MI.length; i++) {
            //FF
            a = FF(b, c, d, MI[i][0], SJ[0][0], KJ[0]);
            d = FF(a, b, c, MI[i][1], SJ[0][1], KJ[1]);
            c = FF(d, a, b, MI[i][2], SJ[0][2], KJ[2]);
            b = FF(c, d, a, MI[i][3], SJ[0][3], KJ[3]);
            a = FF(b, c, d, MI[i][4], SJ[0][0], KJ[4]);
            d = FF(a, b, c, MI[i][5], SJ[0][1], KJ[5]);
            c = FF(d, a, b, MI[i][6], SJ[0][2], KJ[6]);
            b = FF(c, d, a, MI[i][7], SJ[0][3], KJ[7]);
            a = FF(b, c, d, MI[i][8], SJ[0][0], KJ[8]);
            d = FF(a, b, c, MI[i][9], SJ[0][1], KJ[9]);
            c = FF(d, a, b, MI[i][10], SJ[0][2], KJ[10]);
            b = FF(c, d, a, MI[i][11], SJ[0][3], KJ[11]);
            a = FF(b, c, d, MI[i][12], SJ[0][0], KJ[12]);
            d = FF(a, b, c, MI[i][13], SJ[0][1], KJ[13]);
            c = FF(d, a, b, MI[i][14], SJ[0][2], KJ[14]);
            b = FF(c, d, a, MI[i][15], SJ[0][3], KJ[15]);

            //GG
            a = GG(b, c, d, MI[i][1], SJ[1][0], KJ[16]);
            d = GG(a, b, c, MI[i][6], SJ[1][1], KJ[17]);
            c = GG(d, a, b, MI[i][11], SJ[1][2], KJ[18]);
            b = GG(c, d, a, MI[i][0], SJ[1][3], KJ[19]);
            a = GG(b, c, d, MI[i][5], SJ[1][0], KJ[20]);
            d = GG(a, b, c, MI[i][10], SJ[1][1], KJ[21]);
            c = GG(d, a, b, MI[i][15], SJ[1][2], KJ[22]);
            b = GG(c, d, a, MI[i][4], SJ[1][3], KJ[23]);
            a = GG(b, c, d, MI[i][9], SJ[1][0], KJ[24]);
            d = GG(a, b, c, MI[i][14], SJ[1][1], KJ[25]);
            c = GG(d, a, b, MI[i][3], SJ[1][2], KJ[26]);
            b = GG(c, d, a, MI[i][8], SJ[1][3], KJ[27]);
            a = GG(b, c, d, MI[i][13], SJ[1][0], KJ[28]);
            d = GG(a, b, c, MI[i][2], SJ[1][1], KJ[29]);
            c = GG(d, a, b, MI[i][7], SJ[1][2], KJ[30]);
            b = GG(c, d, a, MI[i][12], SJ[1][3], KJ[31]);

            //HH
            a = HH(b, c, d, MI[i][0], SJ[2][0], KJ[32]);
            d = HH(a, b, c, MI[i][1], SJ[2][1], KJ[33]);
            c = HH(d, a, b, MI[i][2], SJ[2][2], KJ[34]);
            b = HH(c, d, a, MI[i][3], SJ[2][3], KJ[35]);
            a = HH(b, c, d, MI[i][4], SJ[2][0], KJ[36]);
            d = HH(a, b, c, MI[i][5], SJ[2][1], KJ[37]);
            c = HH(d, a, b, MI[i][6], SJ[2][2], KJ[38]);
            b = HH(c, d, a, MI[i][7], SJ[2][3], KJ[39]);
            a = HH(b, c, d, MI[i][8], SJ[2][0], KJ[40]);
            d = HH(a, b, c, MI[i][9], SJ[2][1], KJ[41]);
            c = HH(d, a, b, MI[i][10], SJ[2][2], KJ[42]);
            b = HH(c, d, a, MI[i][11], SJ[2][3], KJ[43]);
            a = HH(b, c, d, MI[i][12], SJ[2][0], KJ[44]);
            d = HH(a, b, c, MI[i][13], SJ[2][1], KJ[45]);
            c = HH(d, a, b, MI[i][14], SJ[2][2], KJ[46]);
            b = HH(c, d, a, MI[i][15], SJ[2][3], KJ[47]);

            //II
            a = II(b, c, d, MI[i][0], SJ[3][0], KJ[48]);
            d = II(a, b, c, MI[i][1], SJ[3][1], KJ[49]);
            c = II(d, a, b, MI[i][2], SJ[3][2], KJ[50]);
            b = II(c, d, a, MI[i][3], SJ[3][3], KJ[51]);
            a = II(b, c, d, MI[i][4], SJ[3][0], KJ[52]);
            d = II(a, b, c, MI[i][5], SJ[3][1], KJ[53]);
            c = II(d, a, b, MI[i][6], SJ[3][2], KJ[54]);
            b = II(c, d, a, MI[i][7], SJ[3][3], KJ[55]);
            a = II(b, c, d, MI[i][8], SJ[3][0], KJ[56]);
            d = II(a, b, c, MI[i][9], SJ[3][1], KJ[57]);
            c = II(d, a, b, MI[i][10], SJ[3][2], KJ[58]);
            b = II(c, d, a, MI[i][11], SJ[3][3], KJ[59]);
            a = II(b, c, d, MI[i][12], SJ[3][0], KJ[60]);
            d = II(a, b, c, MI[i][13], SJ[3][1], KJ[61]);
            c = II(d, a, b, MI[i][14], SJ[3][2], KJ[62]);
            b = II(c, d, a, MI[i][15], SJ[3][3], KJ[63]);
            digest[0] = digest[0] + a;
            digest[1] = digest[1] + b;
            digest[2] = digest[2] + c;
            digest[3] = digest[3] + d;
        }
        return digest;
    }

    private static void ByteToPrint(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            System.out.println();
        }
    }

    @Test
    public void test() {
        System.out.println(A);
        System.out.println(B);
        System.out.println(C);
        System.out.println(D);
        System.out.println(MD5Encryption("123456"));
    }
}

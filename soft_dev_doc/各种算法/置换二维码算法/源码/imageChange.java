import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Eric on 2018/3/31.
 */
public class imageChange {
    int[][] muban = {
            {1, 1, 1},
            {1, 0, 0},
            {1, 0, 1},
            {1, 0, 1},
            {1, 0, 1},
            {1, 0, 0},
            {1, 1, 1}};
    //定位码 宽度为1:1:3:1:1所以 十倍为70 允许给予3个长度的误差
    public static final int QCODE_MAX_WIDETH = 143;
    //定位吗 最小宽度为3北 在给予3像素误差
    public static final int QCODE_MIN_WIDETH = 11;
    //轮廓识别误差
    public static final int QCODE_ERROR = 5;

    public static void main(String args[]) {
        Long start = System.currentTimeMillis();
        imageChange image = new imageChange();
        try {
            //处理第一张图片
            File file = new File("C:\\Users\\Administrator\\Desktop\\asdasd_files\\codebig1.jpg");
            BufferedImage img = ImageIO.read(file);
//            //加权去灰
//            img = image.grayImage(img);
//            //中值滤波
//            img=image.median(img);
            //最佳阀值二值
            img = image.binary(img);
            //腐蚀
            img = image.corrosion(img, 1);
            //膨胀
            img = image.swell(img, 1);
            //处理完成二值化图片生成图片看看
//            File newfile = new File("../treeholeServer/erweima1.jpg");
//            ImageIO.write(img, "jpg", newfile);
            //生成图片矩阵
            int[][] map = image.juzhen(img);
            //生成图片轮廓
            int[][] line = image.outline(map);
            //获取二维码坐标
            List<XYWH> coor = image.recognition(line);
            //获取该二维码属性
            QCCode oneimg = image.calculate(coor.get(0), coor.get(1), coor.get(2));

            //处理第二张图片
            File erweima = new File("C:\\Users\\Administrator\\Desktop\\asdasd_files\\asd.jpg");
            BufferedImage erwei = ImageIO.read(erweima);
            //最佳阀值二值
            erwei = image.binary(erwei);
            //腐蚀
            erwei = image.corrosion(erwei, 1);
            //膨胀
            erwei = image.swell(erwei, 1);
            //生成图片矩阵
            int[][] ermap = image.juzhen(erwei);
            //生成图片轮廓
            int[][] erline = image.outline(ermap);
            //获取二维码坐标
            List<XYWH> ercoor = image.recognition(erline);
            //获取该二维码属性
            QCCode twoimg = image.calculate(ercoor.get(0), ercoor.get(1), ercoor.get(2));

            //计算要更新的二维码属性
//            QCCode newqc = image.zooming(oneimg, twoimg);

            //清除第一张图片二维码
            BufferedImage newimg = ImageIO.read(file);
            newimg = image.remove(newimg, oneimg);

            //二维码缩放
            BufferedImage nerimg = ImageIO.read(erweima);
            nerimg = image.change(nerimg, oneimg, twoimg);

            //二维码粘贴
            nerimg = image.paste(newimg, oneimg, nerimg);

            //获取第二张图的二维码矩阵
//            int[][] newmap = image.erjuzhen(erwei, twoimg);
            //缩小成单位矩阵
//            int[][] unitmap = image.zoom(newmap, twoimg);

            //生成图片
            File newfile = new File("C:\\Users\\Administrator\\Desktop\\asdasd_files\\qwe.png");
            ImageIO.write(nerimg, "jpg", newfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    /**
     * JAVA自带二分值 中间二分值
     *
     * @param image 需要二分值图像的图片流
     * @return 二分值之后的图片流
     */
    public BufferedImage binaryImage(BufferedImage image) throws IOException {
//        File file = new File("H:\\shudong\\shu\\treehole\\treeholeServer\\timg2.jpg");
//        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_BINARY
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int rgb = image.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }
        return grayImage;
//        File newFile = new File("../treeholeServer/timg1.jpg");
//        ImageIO.write(grayImage, "jpg", newFile);

    }

    /**
     * 像素颜色转rbg
     *
     * @param alpha 透明度
     * @param red   红色
     * @param green 绿色
     * @param blue  蓝色
     * @return 24位颜色属性
     */
    private static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }

    /**
     * RBG转灰度色
     *
     * @param color 颜色值ARGB
     * @return 所对应的灰度色值
     */
    private static int RGBTocolor(int color) {
        int gray = 0;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;
        gray = (r + g + b) / 3;
        return gray;

    }

    /**
     * 加权灰度化
     *
     * @param image 需要灰度化的图片流
     * @return 灰度后的图片流
     */
    public BufferedImage grayImage(BufferedImage image) throws IOException {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                final int color = image.getRGB(i, j);
                final int r = (color >> 16) & 0xff;
                final int g = (color >> 8) & 0xff;
                final int b = color & 0xff;
                int gray = (299 * r + 587 * g + 114 * b) / 1000;
                int newPixel = colorToRGB(255, gray, gray, gray);
                image.setRGB(i, j, newPixel);
            }
        }
        return image;
    }

    /**
     * 真·中值滤波 用于去零碎噪点
     *
     * @param image 需要去噪点的图片流
     * @return 去噪后的图片流
     */
    public BufferedImage median(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] map = new int[9];
        for (int j = 1; j < height && j + 1 < height; j++) {
            for (int i = 1; i < width && i + 1 < width; i++) {
                map[0] = image.getRGB(i - 1, j - 1);
                map[1] = image.getRGB(i - 1, j);
                map[2] = image.getRGB(i - 1, j + 1);
                map[3] = image.getRGB(i, j - 1);
                map[4] = image.getRGB(i, j);
                map[5] = image.getRGB(i, j + 1);
                map[6] = image.getRGB(i + 1, j - 1);
                map[7] = image.getRGB(i + 1, j);
                map[8] = image.getRGB(i + 1, j + 1);
                //取灰度
                for (int k = 0; k < map.length; k++) {
                    map[k] = RGBTocolor(map[k]);
                }
                //排序
                sort(map, 0, 8);
                //设定像素点
                int newPixel = colorToRGB(255, map[4], map[4], map[4]);
                image.setRGB(i, j, newPixel);
            }
        }
        return image;
    }

    /**
     * 全局平均二分值
     *
     * @param image 需要二分值的图片流
     * @return 二分值后的图片流
     */
    public BufferedImage binaryImage1(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int sum = 0;
        //计算总灰度值
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                //获取平均灰度
                int k = RGBTocolor(image.getRGB(i, j));
                sum += k;
            }
        }
        //求平均灰度
        int middle = sum / (width * height);
        middle = colorToRGB(255, middle, middle, middle);
        //二值化
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                //获取平均灰度
                if (image.getRGB(i, j) > middle) {
                    image.setRGB(i, j, -1);
                } else {
                    image.setRGB(i, j, 0);
                }
            }
        }
        return image;
    }

    /**
     * 最佳阀值二值化
     *
     * @param image 需要二分值的图片流
     * @return 二分值后的图片流
     */
    public BufferedImage binary(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] map = new int[width * height];
        for (int j = 0, k = 0; j < height; j++) {
            for (int i = 0; i < width; i++, k++) {
                //获取平均灰度
                map[k] = RGBTocolor(image.getRGB(i, j));
            }
        }
        map = Binary.getBinaryImg(width, height, map);
        for (int j = 0, k = 0; j < height; j++) {
            for (int i = 0; i < width; i++, k++) {
                //获取平均灰度
                image.setRGB(i, j, map[k]);
            }
        }
        return image;
    }


    /**
     * 转换 矩阵
     *
     * @param image 需要转换为图片矩阵的图片流
     * @return 返回转置后的图片矩阵比例为1:1像素
     */
    public int[][] juzhen(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] map = new int[height][width];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                //获取平均灰度
                if (isBlack(image.getRGB(i, j))) {
//                    System.out.print("1");
                    map[j][i] = 1;
                } else {
//                    System.out.print("0");
                    map[j][i] = 0;
                }
            }
//            System.out.println();
        }
        return map;
    }

    /**
     * 挖掘二维码矩阵
     *
     * @param image 需要被挖掘二维码矩阵的图片流
     * @param qc    被挖掘的二维码属性
     * @return 返回挖掘出来的二维码矩阵比例1:1像素
     */
    public int[][] erjuzhen(BufferedImage image, QCCode qc) {
        int[][] map = new int[qc.getH()][qc.getW()];
        for (int j = qc.getY(), y = 0; j < qc.getY() + qc.getH() && y < qc.getH(); j++, y++) {
            for (int i = qc.getX(), x = 0; i < qc.getX() + qc.getW() && x < qc.getW(); i++, x++) {
                if (isBlack(image.getRGB(i, j))) {
                    map[y][x] = 1;
//                    System.out.print(map[y][x]);
                } else {
                    map[y][x] = 0;
//                    System.out.print(map[y][x]);
                }
            }
//            System.out.println();
        }
        return map;
    }

    /**
     * 二维码矩阵缩小成单位二维码矩阵
     *
     * @param map 二维码矩阵
     * @param qc  需要被缩小的二维码的属性
     * @return 返回单位二维码矩阵比例1:1像素
     */
    public int[][] zoom(int[][] map, QCCode qc) {
        int unit = (qc.getV() - 1) * 4 + 21;
        int[][] umap = new int[unit][unit];
        float b = qc.getW() / unit;
        for (int j = 0; j < unit; j++) {
            for (int i = 0; i < unit; i++) {
                float sum = 0;
                for (int q = (int) (j * b); q < j * b + b; q++) {
                    for (int p = (int) (i * b); p < i * b + b; p++) {
                        if (map[q][p] == 1) {
                            sum += 1;
                        }
                    }
                }
                if (sum / b / b > 0.8) {
                    umap[j][i] = 1;
                } else {
                    umap[j][i] = 0;
                }
//                System.out.print(umap[j][i]);
            }
//            System.out.println();
        }
        return umap;
    }

    /**
     * 图片缩放
     *
     * @param image 需要进行缩放的含有二维码的图片流
     * @param qc1   被缩放后的二维码的属性
     * @param qc2   图片中二维码的属性
     * @return 返回缩放后的二维码图片流
     */
    public BufferedImage change(BufferedImage image, QCCode qc1, QCCode qc2) {
        //先切割需要粘贴的二维码
        image = image.getSubimage(qc2.getX(), qc2.getY(), qc2.getW(), qc2.getH());
        BufferedImage img = new BufferedImage(qc1.getW(), qc1.getH(), BufferedImage.TYPE_INT_RGB);
        img.getGraphics().drawImage(
                image.getScaledInstance(qc1.getW(), qc1.getH(),
                        Image.SCALE_SMOOTH), 0, 0, null);
        return img;
    }

    /**
     * 图片粘贴
     *
     * @param image1 被粘贴的图片流
     * @param qc     被粘贴的属性
     * @param image2 要被粘贴的图片流
     * @return 粘贴好的图片流
     */
    public BufferedImage paste(BufferedImage image1, QCCode qc, BufferedImage image2) {
        for (int j = qc.getY(), y = 0; j < qc.getY() + qc.getH() && y < qc.getH(); j++, y++) {
            for (int i = qc.getX(), x = 0; i < qc.getX() + qc.getW() && x < qc.getW(); i++, x++) {
                image1.setRGB(i, j, image2.getRGB(x, y));
            }
        }
        return image1;
    }

    /**
     * 正方形膨胀算法
     *
     * @param image 需要进行膨胀的图片流
     * @param r     膨胀的半径
     * @return 返回膨胀后的图片流
     */
    public BufferedImage swell(BufferedImage image, int r) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] map = new int[height][width];
        for (int j = r; j < height - r; j++) {
            for (int i = r; i < width - r; i++) {
                //由外圈向内圈遍历 旁边是相同的颜色  概率高一些 这样遍历的次数增多 影响程序的执行效率
                one:
                for (int k = r; k > 0; k--) {
                    for (int l = k; l >= 0; l--) {
                        if (isBlack(image.getRGB(i - k, j + l)) || isBlack(image.getRGB(i - k, j - l)) || isBlack(image.getRGB(i + l, j + k)) || isBlack(image.getRGB(i - l, j + k)) || isBlack(image.getRGB(i + k, j - l)) || isBlack(image.getRGB(i + k, j + l)) || isBlack(image.getRGB(i + l, j - k)) || isBlack(image.getRGB(i - l, j - k))) {
                            map[j][i] = 1;
                            break one;
                        } else {
                            map[j][i] = 0;
                        }
                    }
                }
            }
        }
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (map[j][i] == 1) {
                    image.setRGB(i, j, colorToRGB(255, 0, 0, 0));
                }
            }
        }
        return image;
    }

    /**
     * 正方形腐蚀算法
     *
     * @param image 需要进行腐蚀的图片流
     * @param r     腐蚀的半径
     * @return 返回腐蚀后的图片流
     */
    public BufferedImage corrosion(BufferedImage image, int r) throws IOException {

        int width = image.getWidth();
        int height = image.getHeight();
        int[][] map = new int[height][width];
        for (int j = r; j < height - r; j++) {
            for (int i = r; i < width - r; i++) {
                //由外圈向内圈遍历 旁边是相同的颜色  概率高一些 这样遍历的次数增多 影响程序的执行效率
                one:
                for (int k = r; k > 0; k--) {
                    for (int l = k; l >= 0; l--) {

//                        int a = k - i;
//                        int b = k - j;
//                        int c = width - k;
//                        int d = height - k;
//                        if (a > 0 && b> 0) {
//                            if (!isBlack(image.getRGB(i - k, j + l)) || !isBlack(image.getRGB(i - k, j - l)) || !isBlack(image.getRGB(i + l, j + k)) || !isBlack(image.getRGB(i - l, j + k)) || !isBlack(image.getRGB(i + k, j - l)) || !isBlack(image.getRGB(i + k, j + l)) || !isBlack(image.getRGB(i + l, j - k)) || !isBlack(image.getRGB(i - l, j - k))) {
//                                map[j][i] = 1;
//                                break one;
//                            }
//                        }
//                        !isBlack(image.getRGB(i - k, j + l))
//                        !isBlack(image.getRGB(i - k, j - l))
//                        !isBlack(image.getRGB(i + l, j + k))
//                        !isBlack(image.getRGB(i - l, j + k))
//                        !isBlack(image.getRGB(i + k, j - l))
//                        !isBlack(image.getRGB(i + k, j + l))
//                        !isBlack(image.getRGB(i + l, j - k))
//                        !isBlack(image.getRGB(i - l, j - k))
//                        if (!isBlack(image.getRGB(i - 1, j)) || !isBlack(image.getRGB(i, j - 1))) {
                        if (!isBlack(image.getRGB(i - k, j + l)) || !isBlack(image.getRGB(i - k, j - l)) || !isBlack(image.getRGB(i + l, j + k)) || !isBlack(image.getRGB(i - l, j + k)) || !isBlack(image.getRGB(i + k, j - l)) || !isBlack(image.getRGB(i + k, j + l)) || !isBlack(image.getRGB(i + l, j - k)) || !isBlack(image.getRGB(i - l, j - k))) {
                            map[j][i] = 1;
                            break one;
                        } else {
                            map[j][i] = 0;
                        }
                    }
                }
            }
        }
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (map[j][i] == 1) {
                    image.setRGB(i, j, colorToRGB(255, 255, 255, 255));
                } else if (i < r || j < r || j + r > height || i + r > width) {
                    image.setRGB(i, j, colorToRGB(255, 255, 255, 255));
                }
            }
        }
        return image;
    }

    /**
     * 清除二维码
     *
     * @param image  需要进行清除二位码的图片流
     * @param qcCode 需要被清除的二维码属性
     * @return 返回清除后的图片流
     */
    public BufferedImage remove(BufferedImage image, QCCode qcCode) {
        //多清除5个像素的边框
        aaa:
        for (int j = (qcCode.getY() - 5) > 0 ? qcCode.getY() - 5 : 0; j < qcCode.getY() + qcCode.getH() + 5; j++) {
            for (int i = (qcCode.getX() - 5) > 0 ? qcCode.getX() - 5 : 0; i < qcCode.getX() + qcCode.getW() + 5; i++) {
                if (i >= image.getWidth()) {
                    break;
                }
                if (j >= image.getHeight()) {
                    break aaa;
                }
                image.setRGB(i, j, colorToRGB(255, 255, 255, 255));
            }
        }
        return image;
    }

    /**
     * 判断是否是黑色
     *
     * @param colorInt 颜色色值ARGB
     * @return Boolean
     */
    public static boolean isBlack(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
            return true;
        }
        return false;
    }

    /**
     * 归并排序
     *
     * @param array 需要被排序的数组
     * @param left  数组的左标
     * @param right 数组的右标
     * @return void
     */
    public void sort(int[] array, int left, int right) {
        if (left >= right)
            return;
        // 找出中间索引
        int center = (left + right) / 2;
        // 对左边数组进行递归
        sort(array, left, center);
        // 对右边数组进行递归
        sort(array, center + 1, right);
        // 合并
        merge(array, left, center, right);
        // 打印每次排序结果
    }

    /**
     * 模板识别算法中所需要自动生成的模板
     *
     * @param x     单位模板放大倍数
     * @param muban 单位模板数组
     * @return 放大X倍后的模板
     */
    public int[][] copymuban(int x, int[][] muban) {
        int width = muban[0].length;
        int height = muban.length;
        int[][] bigmuban = new int[height * x][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (muban[i][j] == 0) {
                    for (int k = 0; k < x; k++) {
                        bigmuban[i * x + k][j] = 0;
                    }
                } else {
                    for (int k = 0; k < x; k++) {
                        bigmuban[i * x + k][j] = 1;
                    }
                }
            }
        }
        return bigmuban;
    }

    /**
     * 寻找图片轮廓
     *
     * @param map 需要被寻找的图片的图片矩阵
     * @return 图片轮廓矩阵
     */
    public int[][] outline(int[][] map) {
        int height = map.length;
        int width = map[0].length;
        int[][] line = new int[height][width];
        for (int j = 1; j < height - 1; j++) {
            for (int i = 1; i < width - 1; i++) {
                if (i == 0 || j == 0 || i == width - 1 || j == height - 1) {
                    if (map[j][i] == 1 && map[j - 1][i] == 1 && map[j + 1][i] == 1 && map[j][i + 1] == 1) {
                        line[j][i] = 1;
                    } else {
                        line[j][i] = 0;
                    }
                } else if (map[j][i] == 1 && map[j - 1][i] == 1 && map[j][i - 1] == 1 && map[j + 1][i] == 1 && map[j][i + 1] == 1) {
                    line[j][i] = 1;
                } else {
                    line[j][i] = 0;
                }
            }
        }

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (map[j][i] == line[j][i]) {
                    map[j][i] = 0;
                }
            }
        }
        return map;
    }

    /**
     * 二维码轮廓识别核心算法
     *
     * @param map 所需识别的含有二维码的图片的轮廓
     * @return 返回所匹配的所有坐标属性 X坐标 Y坐标 W宽度 H高度
     */
    public List<XYWH> recognition(int[][] map) {
        List<XYWH> list = new ArrayList<>();
        int height = map.length;
        int width = map[0].length;
        for (int j = 1; j < height - 1; j++) {
            for (int i = 1; i < width - 1; i++) {
                //定义一个w11,h11变量 代表二维码定位图标的最外层宽度,高度
                //再定义w10,w12变量 用来存储宽度外凸像素或者内凹像素的偏差
                //再定义h10,h12变量 用来存储高度外凸像素或者内凹像素的偏差
                int w11 = 0;
                int h11 = 0;
                int w10 = 0;
                int w12 = 0;
                int h10 = 0;
                int h12 = 0;
                if (map[j][i] == 1) {
                    w11++;
                    h11++;
                    int kong = 0;
                    Boolean type = false;
                    for (int k = 1; k < QCODE_MAX_WIDETH; k++) {
                        //轮廓偶尔内凹或者外凸的偏差
                        if (j + k < height) {
                            if (map[j + k][i] == 1) {
                                h11++;
                            } else if (map[j + k][i + 1] == 1) {
                                h12++;
                            } else if (map[j + k][i - 1] == 1) {
                                h10++;
                            } else {
                                type = true;
                                kong++;
                            }
                        }
                        if (i + k < width) {
                            if (map[j][i + k] == 1) {
                                w11++;
                            } else if (map[j + 1][i + k] == 1) {
                                w12++;
                            } else if (map[j - 1][i + k] == 1) {
                                w10++;
                            } else {
                                if (type) {
                                    break;
                                }
                                type = true;
                            }
                        } else {
                            break;
                        }
                    }
                    float wsum = w10 + w11 + w12;
                    float hsum = h10 + h11 + h12;
                    if (Math.abs(wsum - hsum) > 6) {
                        i += wsum;
                        continue;
                    }
                    if (kong > 6) {
                        i += wsum;
                        continue;
                    }
                    if (wsum < QCODE_MIN_WIDETH || wsum > QCODE_MAX_WIDETH || hsum > QCODE_MAX_WIDETH || hsum < QCODE_MIN_WIDETH) {
                        i += wsum;
                        continue;
                    }
                    int x = i, y = j;
                    if (h10 >= h11 + h12) {
                        //外凸的更多可能是该左上角落像素有缺失 所以在X轴上应该减一 所对应的长度和宽度应该进行修正
                        //i--;
                        x--;
                        wsum++;
                    }
                    if (h12 >= h11 + h10) {
                        //同理内凹
                        //i++;
                        x++;
                        wsum--;
                    }
                    if (w10 >= w11 + w12) {
                        //同理外凸凹
                        //j--;
                        y--;
                        hsum++;
                    }
                    if (w12 >= w11 + w10) {
                        //同理内凹
                        //j++;
                        y++;
                        hsum--;
                    }
                    //在此已经拿出了正确的左上角的坐标但是只能从左上角开始的两条边确定为正方形 SO
                    //有时候会出现改正方形角缺少了5个像素的情况
                    //e表示可以拥有2个像素的误差
                    //开始查询下底边长度逻辑
                    int wf10 = 0;
                    int wf11 = 0;
                    int wf12 = 0;
                    int e = QCODE_ERROR;
                    for (int k = x; k < x + wsum; k++) {
                        //因为是总高度所以当从左上角坐标下来时本身坐标加上高度会多了1 所以要减去1
                        if (map[y + (int) hsum - 1][k] == 1) {
                            //标准
                            wf11++;
                        } else if (map[y + (int) hsum - 2][k] == 1) {
                            //内凹
                            wf10++;
                        } else if (map[y + (int) hsum][k] == 1) {
                            //外凸
                            wf12++;
                        } else if (e > 0) {
                            e--;
                        } else {
                            break;
                        }
                    }
                    int wf = wf10 + wf11 + wf12;
                    //因为有3个像素的容错  所以上底边大于等于下底边则认为是个正方形
                    if (wf + e < wsum) {
                        i += wsum;
                        continue;
                    }
                    //同理遍历一下右边框
                    int hf10 = 0;
                    int hf11 = 0;
                    int hf12 = 0;
                    //重置错误像素
                    e = QCODE_ERROR;
                    for (int k = y; k < y + hsum; k++) {
                        //因为是总宽度所以当从左上角坐标下来时本身坐标加上高度会多了1 所以要减去1
                        if (map[k][x + (int) wsum - 1] == 1) {
                            hf11++;
                        } else if (map[k][x + (int) wsum - 2] == 1) {
                            //内凹
                            hf10++;
                        } else if (map[k][x + (int) wsum] == 1) {
                            //外凸
                            hf12++;
                        } else if (e > 0) {
                            e--;
                        } else {
                            break;
                        }
                    }
                    int hf = hf10 + hf11 + hf12;
                    //因为有3个像素的容错  所以左边大于等于右边则认为是个正方形
                    if (hf + e < hsum) {
                        i += wsum;
                        continue;
                    }

                    if (wf10 >= wf11 + wf12) {
                        //内凹到一定像素时 说明高度错误
                        hsum--;
                    }

                    if (wf12 >= wf11 + wf10) {
                        //外凸到一定像素时 说明高度错误
                        hsum++;
                    }

                    if (hf10 >= hf11 + hf12) {
                        //内凹到一定像素时 说明高度错误
                        wsum--;
                    }

                    if (hf12 >= hf11 + hf10) {
                        //外凸到一定像素时 说明高度错误
                        wsum++;
                    }
                    XYWH xywh = new XYWH(x, y, (int) wsum, (int) hsum);
                    list.add(xywh);
                    //根据长宽有可能造成的损失把比例在这个范围内的损失 都当作正方形来看
//                    System.out.println("i=" + x + "  j=" + y + "\r\n" + "  wsum=" + wsum + "  w10=" + w10 + "  w11=" + w11 + "  w12=" + w12 + "\r\n" + "   hsum=" + hsum + "   h10=" + h10 + "   h11=" + h11 + "   h12=" + h12 + "\r\n" + "  kong=" + kong);
                    i += wsum;
                    continue;
                }
            }
        }
        return filter(list);
    }

    /**
     * 坐标过滤
     * 最垃圾的算法配置 有大大的优化空间（没时间优化）
     * 垃圾中的战斗机
     *
     * @param lists 所匹配中的所有坐标
     * @return 返回3个关键坐标属性 X坐标 Y坐标 W宽度 H高度
     */
    public List<XYWH> filter(List<XYWH> lists) {
        int c = lists.size();
        int[] sort = new int[c];
        int i = 1;
        List<XYWH> list = new ArrayList<>();
        for (XYWH xywh : lists) {
            //去除相同数据
            Boolean type = true;
            for (int j = 0; j < i - 1; j++) {
                if (list.get(j).getX() == xywh.getX() && list.get(j).getY() == xywh.getY()) {
                    type = false;
                }
            }
            if (type) {
                XYWH coor = new XYWH(xywh.getX(), xywh.getY(), xywh.getW(), xywh.getH());
                coor.setId(i);
                list.add(coor);
                i++;
            }
        }
        //以X坐标*100+id的值进行比较X坐标
        i = 0;
        for (XYWH cpp : list) {
            sort[i] = cpp.getX() * 100 + cpp.getId();
            i++;
        }
        //排序
        int[] id = new int[3];
        sort(sort, 0, c - 1);
        zzz:
        for (int k = 0; k < c; k++) {
            if (sort[k] != 0) {
                XYWH ckk = null;
                for (XYWH cxx : list) {
                    if (cxx.getId() == sort[k] % 100) {
                        ckk = cxx;
                        break;
                    }
                }
                for (int j = 1; j < c / 2; j++) {
                    XYWH cvv = null;
                    for (XYWH cxx : list) {
                        if (cxx.getId() == sort[k + j] % 100) {
                            cvv = cxx;
                            break;
                        }
                    }
                    if (sort[k] / 100 == sort[k + j] / 100 && (Math.abs(ckk.getY() - cvv.getY()) > ckk.getW() || Math.abs(ckk.getY() - cvv.getY()) > ckk.getH())
                            && (ckk.getW() + 3 >= cvv.getW() && ckk.getW() <= 3 + cvv.getW())) {
                        id[0] = sort[k] % 100;
                        id[1] = sort[k + j] % 100;
                        break zzz;
                    }
                }
            }
//            System.out.println(sort[k]);
        }
        System.out.println(id[0] + "   " + id[1]);
        XYWH one = null;
        for (XYWH cqq : list) {
            if (cqq.getId() == id[0]) {
                one = cqq;
                break;
            }
        }
        XYWH tow = null;
        for (XYWH cqq : list) {
            if (cqq.getId() == id[1]) {
                tow = cqq;
                break;
            }
        }
        if (one == null) {
            return null;
        }
        XYWH three = null;
        for (XYWH cqq : list) {
            if (cqq.getY() == one.getY() && cqq.getX() != one.getX()) {
                id[2] = cqq.getId();
                three = cqq;
                break;
            }
        }
        System.out.println(id[0] + "   " + id[1] + "    " + id[2]);
        System.out.println("第一个坐标:\r\nX:" + one.getX() + "   Y:" + one.getY() + "   W:" + one.getW() + "   H:" + one.getH());
        System.out.println("第二个坐标:\r\nX:" + tow.getX() + "   Y:" + tow.getY() + "   W:" + tow.getW() + "   H:" + tow.getH());
        System.out.println("第三个坐标:\r\nX:" + three.getX() + "   Y:" + three.getY() + "   W:" + three.getW() + "   H:" + three.getH());
        List<XYWH> coor = new ArrayList<>();
        coor.add(one);
        coor.add(tow);
        coor.add(three);
        return coor;
    }

    /**
     * 根据关键坐标计算出二维码的实际属性
     *
     * @param one   关键坐标1属性
     * @param two   关键坐标2属性
     * @param three 关键坐标3属性
     * @return QCCode 该二维码的属性
     */
    public QCCode calculate(XYWH one, XYWH two, XYWH three) {
        int QCW = Math.abs(three.getX() - one.getX()) + ((three.getX() - one.getX()) > 0 ? three.getW() : one.getW());
        int QCH = two.getY() - one.getY() + two.getH();
        int[] sort = new int[6];
        sort[0] = one.getW();
        sort[1] = one.getH();
        sort[2] = two.getW();
        sort[3] = two.getH();
        sort[4] = three.getW();
        sort[5] = three.getH();
        //排序
        sort(sort, 0, 5);
        int b = Math.round((float) sort[5] / 7);
        int v = QCH > QCW ? Math.round((float) (QCH / b - 21) / 4) + 1 : Math.round((float) (QCW / b - 21) / 4) + 1;
        QCCode qc = new QCCode(one.getX(), one.getY(), QCW, QCH, b, v);
        System.out.println("二维码属性:\r\n" + "X:" + qc.getX() + "Y:" + qc.getY() + "W:" + qc.getW() + "H:" + qc.getH() + "B:" + qc.getB() + "V:" + qc.getV());
        return qc;
    }

    /**
     * 计算二维码应该扩大或者缩小多少倍才能替换掉第一张图中的二维码
     * 计算新二维码属性
     *
     * @param one 被替换的二维码属性
     * @param two 要替换的二维码属性
     * @return QCCode 新的二维码属性
     */
    public QCCode zooming(QCCode one, QCCode two) {
        int dw = (two.getV() - 1) * 4 + 21;
        int zoomb = one.getB();
        for (; zoomb > 0; ) {
            //允许宽有+-2个像素的误差
            if (dw * zoomb <= one.getW() + 4 && dw * (zoomb + 1) > one.getW()) {
                break;
            } else if (dw * (zoomb + 1) < one.getW()) {
                zoomb++;
            } else if (dw * zoomb > one.getW()) {
                zoomb--;
            }
        }
        //计算新插入的二维码属性
        int wh = dw * zoomb;
        int x = one.getX() + one.getW() / 2 - wh / 2;
        int y = one.getY() + one.getH() / 2 - wh / 2;
        QCCode qc = new QCCode(x, y, wh, wh, zoomb, two.getV());
        System.out.println("应该替换的新二维码新属性:\r\n" + "X:" + qc.getX() + "Y:" + qc.getY() + "W:" + qc.getW() + "H:" + qc.getH() + "B:" + qc.getB() + "V:" + qc.getV());
        return qc;
    }

    /*
     * @param map     图片数组
     * @param x       图片数组的横坐标
     * @param y       图片数组的竖坐标
     * @param width   潜在的二维码定位标记的宽度
     * @param b       二维码定位标记的倍数
     * @param mobanx  对应的放大倍数后的模板
     * @param success 匹配成功的列数
     * @param sum     匹配单列模板成功的次数
     * @param u       实际的区分模板标记（模板数组的实际横坐标）
     * @param i       匹配时图片数组的竖坐标
     * @param j       匹配时图片数组的横坐标
     * @param k       模板数组的实际横坐标
     * @param q       二维码定位黑块的黑边
     * @param w       二维码定位百块的白边
     * @param e       二维码定位中心黑块的黑边
     * @param error   列的容错像素
     */

    /**
     * 模板匹配（旧算法）（多数图片不兼容）
     * 因为二维码中像素确实一般是 会左右多出一列或者少出一列的概率要大过 行多出少出的概率
     *
     * @param map 需要进行模板匹配的图片矩阵
     */
    public void dingwei(int[][] map) {
        //遍历数组
        for (int y = 0; y < map.length; y++) {
            int width = 0;
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == 1) {
                    width += 1;
                } else {
                    //获取符合的列
                    if (width > QCODE_MIN_WIDETH && width < QCODE_MAX_WIDETH) {
                        //二维码只能是最小二维码的3-9倍
                        int b = width / 7;
                        //匹配高度算法 和宽度 对比 取最优
                        int height = 0;
                        //用4列匹配确定的高度值
                        for (int k = y; k < map[y].length; k++) {
                            if (map[k][x] == 0 && map[k][x - width] == 0 && map[k][x - width + 1] == 0 && map[k][x - 1] == 0) {
                                break;
                            }
                            height++;
                            if (height > (b + 1) * 7) {
                                break;
                            }
                        }
                        //在图片二维码中高度像素出错的概率比较小
                        //高度给正负2个像素的误差(height+2 > b * 7 && b * 7 > height - 2)
                        int error = 0;
                        if (height != b * 7) {
                            //如果高度大于宽度有可能是 第一行的宽度缺失了几个像素  所以重新计算b的值
                            if (height > width && height < (b + 1) * 7) {
//                            if (height > (b - 1) * 7 && height < (b + 1) * 7) {
                                //高度可能会少1-2行
                                b = (height + 2) / 7;
                                error = height - b * 7;
                                //回溯至前面一列 看看是不是有像素等于1 用随机数更好但是会 影响程序的执行效率 PASS
                                if (x >= width + (height - width) / 2) {
                                    if (map[y + height / 2][x - width - (height - width) / 2] == 1) {
                                        //有 说明 第一行的宽度实际有所缺失
                                        width = width + (height - width) / 2;
                                    }
                                }
                            } else if (height < (b - 1) * 7 || height > (b + 1) * 7) {
//                            } else {
                                break;
                            }
                        }
                        //生成对应模板
                        int[][] mobanx = copymuban(b, muban);
                        //模板匹配
                        //错误偏差列
//                        int error = width - b * 7;
                        //比例形参
                        float q = 0;
                        float w = 0;
                        float e = 0;
                        //该行匹配成功与否
                        int success = 0;
                        //可能存在1-2行的偏差
                        for (int i = x - width, l = 0; i < x && l < (b + 1) * 7; i++, l++) {
                            //匹配成功次数
                            int sum = 0;
                            //实际区分模板
                            int u = 0;


//                            if (l < b || l + b >= width) {
//                                for (int j = y, k = 0; j < y + b * 7 && k < b * 7; j++, k++) {
//                                    if (map[j][i] == mobanx[k][u]) {
//                                        sum += 1;
//                                    }
//                                }
//                                //如果有不符合的直接停止匹配
//                                if (sum + 4 < b * 7) {
//                                    break;
//                                }
//                                //计算最外围黑列列数
//                                q += 1;
//                            }
//                            if (l >= b && l + b < width) {
                            //中间部分全部模板分别


                            for (; u < 3; u++) {
                                //再次匹配时候sum清0
                                sum = 0;
                                for (int j = y, k = 0; j < y + b * 7 && k < b * 7; j++, k++) {
                                    if (map[j][i] == mobanx[k][u]) {
                                        sum += 1;
                                    }
                                }
                                if (sum + 4 + error >= b * 7) {
                                    if (u == 0) {
                                        q += 1;
                                    }
                                    if (u == 1) {
                                        w += 1;
                                    }
                                    if (u == 2) {
                                        e += 1;
                                    }
                                    break;
                                }
                            }
//                            }
                            //在合理误差范围内,该列匹配成功 给予3个像素的初始误差 在加上可能出现的错误偏差
                            if (sum + 4 + error >= b * 7) {
                                success += 1;
                            }
                        }
//                        if(q!=0&&w!=0&&e!=0){
//                            System.out.println("q:"+q+"w:"+w+"e:"+e+(q / w > 0.5 && q / w < 1.5 && (q > w ? e / w > 1.45 : e / q > 1.45)));
//                            System.out.println("success:"+success+"   width:"+width);
//                            System.out.println("X坐标：" + (x - width) + ",Y坐标：" + y);
//                        }
                        if (success == width && q / w > 0.5 && q / w < 1.5 && (q > w ? e / w > 1.45 : e / q > 1.45)) {
                            System.out.println("匹配成功");
                            System.out.println("X坐标：" + (x - width) + ",Y坐标：" + y + "\r\n");
                            //该列符合后直接跳过该列的多余判断 防止在匹配成功上出现的坐标 多出相差不大的坐标来
                            x = x + width;
                        }
                    }
                    width = 0;
                }

            }
        }
    }

    /**
     * 将两个数组进行归并，归并前面2个数组已有序，归并后依然有序
     *
     * @param array  数组对象
     * @param left   左数组的第一个元素的索引
     * @param center 左数组的最后一个元素的索引，center+1是右数组第一个元素的索引
     * @param right  右数组最后一个元素的索引
     */
    public void merge(int[] array, int left, int center, int right) {
        // 临时数组
        int[] tmpArr = new int[array.length];
        // 右数组第一个元素索引
        int mid = center + 1;
        // third 记录临时数组的索引
        int third = left;
        // 缓存左数组第一个元素的索引
        int tmp = left;
        while (left <= center && mid <= right) {
            // 从两个数组中取出最小的放入临时数组
            if (array[left] <= array[mid]) {
                tmpArr[third++] = array[left++];
            } else {
                tmpArr[third++] = array[mid++];
            }
        }
        // 剩余部分依次放入临时数组（实际上两个while只会执行其中一个）
        while (mid <= right) {
            tmpArr[third++] = array[mid++];
        }
        while (left <= center) {
            tmpArr[third++] = array[left++];
        }
        // 将临时数组中的内容拷贝回原数组中
        // （原left-right范围的内容被复制回原数组）
        while (tmp <= right) {
            array[tmp] = tmpArr[tmp++];
        }
    }
}

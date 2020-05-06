

/**
 * Created by Eric on 2018/4/21.
 */
public class QCCode {
    //二维码左上角X坐标
    private int x;
    //二维码左上角Y坐标
    private int y;
    //二维码实际宽度
    private int w;
    //二维码实际高度
    private int h;
    //二维码单位宽度
    private int b;
    //二维码版本
    private int v;

    public QCCode(int x,int y,int w, int h, int b, int v){
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
        this.b=b;
        this.v=v;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}

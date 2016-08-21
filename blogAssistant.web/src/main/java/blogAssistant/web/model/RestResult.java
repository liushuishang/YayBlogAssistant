package blogAssistant.web.model;

/**
 * Created by yuananyun on 2016/8/21.
 */
public class RestResult {
    private int s;
    private Object r;
    private String m;

    public RestResult(int s, Object r, String m) {
        this.s = s;
        this.r = r;
        this.m = m;
    }

    public static RestResult success(Object r)
    {
        return new RestResult(1,r,null);
    }

    public static  RestResult failure(String m)
    {
        return new RestResult(0,null,m);
    }
    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public Object getR() {
        return r;
    }

    public void setR(Object r) {
        this.r = r;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }
}

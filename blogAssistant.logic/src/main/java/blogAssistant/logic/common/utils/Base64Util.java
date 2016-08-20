package blogAssistant.logic.common.utils;

import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yuananyun on 2016/8/20.
 */
public class Base64Util {

    /**
     * @return
     * @Description: 根据文件地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     */
    public static String convertFileToBase64(String fileUrl) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            if (fileUrl.trim().toLowerCase().startsWith("http"))
                inputStream = downLoadFromUrl(fileUrl);
            else
                inputStream = new FileInputStream(fileUrl);
            data = new byte[inputStream.available()];
            inputStream.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    public static InputStream downLoadFromUrl(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        return conn.getInputStream();
    }


    public static void main(String[] args) {
        String c = convertFileToBase64("http://b.hiphotos.baidu.com/baike/c0%3Dbaike150%2C5%2C5%2C150%2C50/sign=c05506e79482d158af8f51e3e16372bd/c2fdfc039245d688c56332adacc27d1ed21b2451.jpg");
        System.out.println(c);
    }

}

package blogAssistant.logic.poster.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class BlogPosterHelper {

    /**
     * 新建一个HttpPost对象
     * @param url
     * @return
     */
    public HttpPost newHttpPost(String url) {
        HttpPost pm = new HttpPost(url);
//        URIBuilder builder = new URIBuilder(url);
//        //填入查询参数
//        if (queryParams != null && !queryParams.isEmpty()) {
//            builder.setParameters(HttpUtils.paramsConverter(queryParams));
//        }
//        pm.setURI(builder.build());
//        //填入表单参数
//        if (formParams != null && !formParams.isEmpty()) {
//            pm.setEntity(new UrlEncodedFormEntity(HttpUtils.paramsConverter(formParams)));
//        }

        //设置超时
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        pm.setConfig(requestConfig);

        pm.setHeader("User-Agent","Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1");
        pm.setHeader("Accept-Language","zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        pm.setHeader("Referer",url);

         pm.setHeader("User-Agent","Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1");
         pm.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//   pm.setHeader("Accept-Encoding","gzip, deflate");
         pm.setHeader("Accept-Language","zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
         pm.setHeader("Referer",url);
         pm.setHeader("Content-Type","application/x-www-form-urlencoded");
//        pm.getParams().setContentCharset("UTF-8"); //这个可以控制 NameValuePair的编码，具体请查看  PostMethod.generateRequestEntity();
        return pm;
    }


}

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Service
@Slf4j
public class HttpRequestTool {
    //获取连接池
    private CloseableHttpClient getPool() {
        return HttpClientPool.getSingleton();
    }

    //提交请求 顺带统一添加请求头
    private String submit(final HttpRequestBase requestBase) {
        requestBase.setHeader("Content-Type", "application/json;charset=UTF-8");
		//requestBase.addHeader("Content-type","application/json; charset=utf-8");
        //requestBase.setHeader("Accept", "application/json");
        try {
            log.info("start request : method {}, url {}", requestBase.getMethod(), requestBase.getURI().getPath());
            if (requestBase != null) {
                return this.response(getPool().execute(requestBase));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String response(HttpResponse response) {
        log.info("end request: code {}", response.getStatusLine().getStatusCode());
        try {
            HttpEntity httpEntity = response.getEntity();
            return EntityUtils.toString(httpEntity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	// 有自定义请求头的需求重载几个方法
    public String sendGet(String uriPath, List<NameValuePair> ns)
        throws URISyntaxException {
        URIBuilder uri = new URIBuilder(uriPath);
        uri.addParameters(ns);
        URI u = uri.build();
        return this.submit(new HttpGet(u));
    }

    public String sendPost(String uriPath, List<NameValuePair> ns)
        throws URISyntaxException {
        URIBuilder uri = new URIBuilder(uriPath);
        uri.addParameters(ns);
        URI u = uri.build();
        return this.submit(new HttpPost(u));
    }

    public String sendPost(String uriPath, JSONObject jsonObject) {
        HttpPost httpPost = new HttpPost(uriPath);
        StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");//解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        return this.submit(httpPost);
    }
}

@Service
public class HttpRequestTool {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestTool.class);

    private CloseableHttpClient closeableHttpClient;

    public HttpRequestTool(CloseableHttpClient closeableHttpClient) {
        this.closeableHttpClient = closeableHttpClient;
    }

    //�ύ����
    private String submit(final HttpRequestBase requestBase) {
        requestBase.setHeader("Content-Type", "application/json;charset=UTF-8");
        requestBase.setHeader("Accept", "application/json");
        String method = requestBase.getMethod();
        String url = requestBase.getURI().getPath();
        try {
            log.info("start request : method: {}, url: {}", method, url);
            return this.response(closeableHttpClient.execute(requestBase), method, url);
        } catch (ClientProtocolException e) {
            log.error("request fail method: {}, url: {}", method, url);
            log.error("ClientProtocolException: ", e);
        } catch (IOException e) {
            log.error("request fail method: {}, url: {}", method, url);
            log.error("IOException: ", e);
        }
        return null;
    }

    private String response(HttpResponse response, String method, String url) {
        log.info("end request: method: {}, url: {}, response code: {}", method, url, response.getStatusLine().getStatusCode());
        try {
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);
            log.debug("end request: method: {}, url: {}, response code: {}, response entity: {}", method, url, response.getStatusLine().getStatusCode(), result);
            return result;
        } catch (ClientProtocolException e) {
            log.error("response fail method: {}, url: {}", method, url);
            log.error("ClientProtocolException: ", e);
        } catch (IOException e) {
            log.error("response fail method: {}, url: {}", method, url);
            log.error("IOException: ", e);
        }
        return null;
    }

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
        StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");//���������������
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        return this.submit(httpPost);
    }
}
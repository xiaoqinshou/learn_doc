// 可以嵌入spring IOC 里面交由Spring Ioc 来进行管理
public class HttpClientPool {

    protected static final Logger log = LoggerFactory.getLogger(HttpClientPool.class);

    private static int MAX_CONNECTION;

    private static int DEFAULT_MAX_CONNECTION;

    private static int SOCKET_TIME_OUT;

    private static int CONNECT_TIME_OUT;

    private static int CONNECTION_REQUEST_TIME_OUT;

    private static String AGREEMENT_TYPE;

    private static int MAX_PER_ROUTE;

    private static int RETRY_NUMS;

    private static String IP;

    private static int PORT;

    static {
        log.info("initialization httpclient configuration start ...");
        Yaml yaml = new Yaml();
        try {
            Map<String, Object> data = yaml.load(new FileReader(new File(System.getProperty("spring.property.path") + "application.yml")));
            Map spring = (Map) data.get("spring");
            Map httpclent = (Map) spring.get("httpclient");
            MAX_CONNECTION = (int) httpclent.get("max-connection");
            DEFAULT_MAX_CONNECTION = (int) httpclent.get("defualt-max-connection");
            SOCKET_TIME_OUT = (int) httpclent.get("socket-time-out");
            CONNECT_TIME_OUT = (int) httpclent.get("connect-time-out");
            CONNECTION_REQUEST_TIME_OUT = (int) httpclent.get("connection-request-time-out");
            MAX_PER_ROUTE = (int) httpclent.get("max-pre-route");
            RETRY_NUMS = (int) httpclent.get("retry-nums");
            Map agreement = (Map) httpclent.get("agreement");
            AGREEMENT_TYPE = (String) agreement.get("type");
            IP = (String) agreement.get("ip");
            PORT = (int) agreement.get("port");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        log.info("initialization httpclient configuration end ...");
    }

    private static class Singleton {
        private static CloseableHttpClient httpClient;

        static {
            log.info("first loading httpclient pool");
            ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
            LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", plainsf).register("https", sslsf).build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIME_OUT).setConnectTimeout(CONNECT_TIME_OUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT).build();
            HttpHost target = new HttpHost(IP, PORT, AGREEMENT_TYPE);
            cm.setMaxTotal(MAX_CONNECTION);
            cm.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTION);
            cm.setMaxPerRoute(new HttpRoute(target), MAX_PER_ROUTE);
            HttpRequestRetryHandler httpRequestRetryHandler = (exception, executionCount, context) -> {
                if (executionCount >= RETRY_NUMS) {// 如果超过重试次数，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            };
            httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).setRetryHandler(httpRequestRetryHandler).build();
            log.info("finish loading httpclient pool");
        }

        private static CloseableHttpClient getHttpClient() {
            return httpClient;
        }
    }

    public static CloseableHttpClient getSingleton() {
        return Singleton.getHttpClient();
    }
}

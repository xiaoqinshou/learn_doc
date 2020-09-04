@Configuration
public class HttpClintConfiguration {
    @Value("${system.httpclint.http.maxTotal:500}")
    private Integer maxTotal;

    @Value("${system.httpclint.http.defaultMaxPerRoute:100}")
    private Integer defaultMaxPerRoute;

    @Value("${system.httpclint.http.connectTimeout:2000}")
    private Integer connectTimeout;

    @Value("${system.httpclint.http.connectionRequestTimeout:500}")
    private Integer connectionRequestTimeout;

    @Value("${system.httpclint.http.socketTimeout:6000}")
    private Integer socketTimeout;

    @Value("${system.httpclint.http.maxIdleTime:1}")
    private Integer maxIdleTime;

    @Bean
    public HttpClientConnectionManager initConnectionManager() {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", plainsf).register("https", sslsf).build();
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry);
        manager.setMaxTotal(maxTotal);
        manager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return manager;
    }

    @Bean
    public RequestConfig initRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).build();
    }

    @Bean
    public HttpClientBuilder httpClient(HttpClientConnectionManager manager, RequestConfig config) {
        return HttpClientBuilder.create().setConnectionManager(manager).setDefaultRequestConfig(config);
    }

    @Bean
    public CloseableHttpClient getCloseableHttpClient(HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }

    @Bean(destroyMethod = "shutdown")
    public IdleConnectionEvictor idleConnectionEvictor(HttpClientConnectionManager manager) {
        return new IdleConnectionEvictor(manager, maxIdleTime, TimeUnit.MINUTES);
    }
}
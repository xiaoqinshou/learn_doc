### Flink DEMO

#### 基本依赖
```xml
<dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-core</artifactId>
            <version>${flink.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-java</artifactId>
            <version>${flink.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-streaming-java -->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-clients_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-walkthrough-common_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
        </dependency>
```

#### demo 代码
* 废话不多说直接上代码
```java
public class demo {
    public static void main(String[] args) throws Exception {
        // 获取 flink 流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();

        // 获取一个数据流， 可以是默认初始化
        // 添加数据源的方式
        // DataStream<String> dataStream = env.addSource();
        // 创建一个读入流的方式
        // DataStream<String> dataStream = env.createInput();
        // socket 读入流的方式
        // DataStream<String> dataStream = env.socketTextStream();
        // 读取文件的方式
        // DataStream<String> dataStream = env.readFile();
        DataStream<Integer> dataStream = env.fromCollection(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8,
                9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
        OutputTag<Integer> tag = new OutputTag<Integer>("odd_out_put_tag",
                TypeInformation.of(Integer.class));
        // process 处理方法之前都是对此数据流的预处理 process 是数据流的核心处理逻辑
        SingleOutputStreamOperator<Integer> singleOutputStreamOperator = dataStream
                .filter(i -> i > 0)
                .process(new ProcessFunction<Integer, Integer>() {
                    @Override
                    public void processElement(Integer value, Context ctx,
                                               Collector<Integer> out) throws Exception {
                        if (value % 2 > 0) {
                            ctx.output(tag, value);
                        } else {
                            out.collect(value);
                        }
                    }
                })
                .name("odd_and_even_filter")
                .uid("odd_and_even_filter_id");

        /*自定义滚动策略*/
        DefaultRollingPolicy rollPolicy = DefaultRollingPolicy.builder()
                /*每隔多长时间生成一个文件*/
                .withRolloverInterval(TimeUnit.MINUTES.toMillis(2))
                /*默认60秒,未写入数据处于不活跃状态超时会滚动新文件*/
                .withInactivityInterval(TimeUnit.MINUTES.toMillis(5))
                /*设置每个文件的最大大小 ,默认是128M*/
                .withMaxPartSize(128 * 1024 * 1024)
                .build();
        /*输出文件的前、后缀配置*/
        OutputFileConfig config = OutputFileConfig
                .builder()
                .withPartPrefix("prefix")
                .withPartSuffix(".txt")
                .build();


        // 获取有效数据 并添加sink 使数据下沉到某个地方
        singleOutputStreamOperator.getSideOutput(tag)
                .filter(Objects::nonNull)
                .addSink(StreamingFileSink
                        /*forRowFormat指定文件的跟目录与文件写入编码方式，这里使用SimpleStringEncoder 以UTF-8字符串编码方式写入文件*/
                        .<Integer>forRowFormat(new Path("file:///D:\\demo"),
                                new SimpleStringEncoder<>())
                        /*这里是采用默认的分桶策略DateTimeBucketAssigner，它基于时间的分配器，每小时产生一个桶，格式如下yyyy-MM-dd--HH*/
                        .withBucketAssigner(new DateTimeBucketAssigner<>())
                        /*设置上面指定的滚动策略*/
                        .withRollingPolicy(rollPolicy)
                        /*桶检查间隔，这里设置为1s*/
                        .withBucketCheckInterval(1)
                        /*指定输出文件的前、后缀*/
                        .withOutputFileConfig(config)
                        .build())
                /* 设置数据下沉时的并行度 */
                .setParallelism(1)
                .name("odd_sink")
                .uid("odd_sink_id");

        env.execute();
    }
}
```
* 从官网介绍以及代码可以大概看出来，Flink使用时的三个核心
  1. source(数据源)：用与转化为流的原始数据
  2. dataStearm: 暴露出来的流处理核心
  3. sink: 流处理完的数据下沉核心

#### 本地运行
* 每次上传到Flink很麻烦。
* 因此编写代码的时候，获取运行环境的时候，直接在本地创建
* 然后再 IDEA 项目中本地引入，对应版本的Flink的 dist 包
* 可以在官网下载的Flink，解压后的lib目录下找到

### 整合spring boot的Flink任务
* Spring IOC 无非就是一些bean的创建，管理。并没有直接影响程序。
* 所以，spring IOC 是能整合进Flink中的，把它当作一个普通的插件来使用即可
* 看上面例子的变种：
* 依赖：
  ```xml
  <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
  ```

* 启动类
  ```java
  @Slf4j
  @SpringBootApplication
  public class SpringFlinkDemo {

    public static void main(String[] args) {
        SpringApplication.run(SpringFlinkDemo.class, args);
    }
  }
  ```

* 任务bean
  ```java
  @Component
  public class OddFilterFunction {

      @PostConstruct
      private void run() throws Exception {
          odd();
      }

      public void odd() throws Exception {
          // 获取 flink 流执行环境
          StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();

          // 获取一个数据流， 可以是默认初始化
          // 添加数据源的方式
          // DataStream<String> dataStream = env.addSource();
          // 创建一个读入流的方式
          // DataStream<String> dataStream = env.createInput();
          // socket 读入流的方式
          // DataStream<String> dataStream = env.socketTextStream();
          // 读取文件的方式
          // DataStream<String> dataStream = env.readFile();
          DataStream<Integer> dataStream = env.fromCollection(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8,
                  9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
          OutputTag<Integer> tag = new OutputTag<Integer>("odd_out_put_tag",
                  TypeInformation.of(Integer.class));
          // process 处理方法之前都是对此数据流的预处理 process 是数据流的核心处理逻辑
          SingleOutputStreamOperator<Integer> singleOutputStreamOperator = dataStream
                  .filter(i -> i > 0)
                  .process(new ProcessFunction<Integer, Integer>() {
                      @Override
                      public void processElement(Integer value, Context ctx,
                                                Collector<Integer> out) throws Exception {
                          if (value % 2 > 0) {
                              ctx.output(tag, value);
                          } else {
                              out.collect(value);
                          }
                      }
                  })
                  .name("odd_and_even_filter")
                  .uid("odd_and_even_filter_id");

          /*自定义滚动策略*/
          DefaultRollingPolicy rollPolicy = DefaultRollingPolicy.builder()
                  /*每隔多长时间生成一个文件*/
                  .withRolloverInterval(TimeUnit.MINUTES.toMillis(2))
                  /*默认60秒,未写入数据处于不活跃状态超时会滚动新文件*/
                  .withInactivityInterval(TimeUnit.MINUTES.toMillis(5))
                  /*设置每个文件的最大大小 ,默认是128M*/
                  .withMaxPartSize(128 * 1024 * 1024)
                  .build();
          /*输出文件的前、后缀配置*/
          OutputFileConfig config = OutputFileConfig
                  .builder()
                  .withPartPrefix("prefix")
                  .withPartSuffix(".txt")
                  .build();


          // 获取有效数据 并添加sink 使数据下沉到某个地方
          singleOutputStreamOperator.getSideOutput(tag)
                  .filter(Objects::nonNull)
                  .addSink(StreamingFileSink
                          /*forRowFormat指定文件的跟目录与文件写入编码方式，这里使用SimpleStringEncoder 以UTF-8字符串编码方式写入文件*/
                          .<Integer>forRowFormat(new Path("file:///D:\\demo"),
                                  new SimpleStringEncoder<>())
                          /*这里是采用默认的分桶策略DateTimeBucketAssigner，它基于时间的分配器，每小时产生一个桶，格式如下yyyy-MM-dd--HH*/
                          .withBucketAssigner(new DateTimeBucketAssigner<>())
                          /*设置上面指定的滚动策略*/
                          .withRollingPolicy(rollPolicy)
                          /*桶检查间隔，这里设置为1s*/
                          .withBucketCheckInterval(1)
                          /*指定输出文件的前、后缀*/
                          .withOutputFileConfig(config)
                          .build())
                  /* 设置数据下沉时的并行度 */
                  .setParallelism(1)
                  .name("odd_sink")
                  .uid("odd_sink_id");

          env.execute();
      }
  }
  ```

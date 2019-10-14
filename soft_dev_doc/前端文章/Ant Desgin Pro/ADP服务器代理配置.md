## [Ant Design Pro](https://pro.ant.design/) 前端配置服务器代理
> 在config.js文件中可找到注释掉的

```json
// proxy: {
  //   '/server/api/': {
  //     target: 'https://preview.pro.ant.design/',
  //     changeOrigin: true,
  //     pathRewrite: { '^/server': '' },
  //   },
  // },
```
> 取消注释，并做出相应的修改

```json
proxy: {
    '/api': {
      target: 'http://localhost:8080/',
      changeOrigin: true,
      pathRewrite: { '^/api': '' },
    },
  },
```
页面请求路径 /api/login/account 会映射到http://localhost:8888/login/account
注意:
* 映射后没有前面的/api
* 不要忘记写协议

启动：npm run start:no-mock

* 启用全局代理之后请求进入 [Spring MVC](https://baike.baidu.com/item/spring%20MVC/5627187?fr=aladdin) 后台控制器，参数无法获取准确的值。

> 是因为 [Ant Design Pro](https://pro.ant.design/) 的request方法里默认使用的 [Content-Type:application/json; charset=utf-8](https://baike.baidu.com/item/contentType/1938445)会导致后台无法拿到数据
所以提供两个解决方案：
* 解决方法1.修改请求Content-Type为application/x-www-form-urlencoded
* 解决方法2.在后端接口增加@requestBody注解

这里就用解决方法1。经过看 umi-request 源码接口。
```javascript
/**
 * 增加的参数
 * @param {string} requestType post类型, 用来简化写content-Type, 默认json
 * @param {*} data post数据
 * @param {object} params query参数
 * @param {string} responseType 服务端返回的数据类型, 用来解析数据, 默认json
 * @param {boolean} useCache 是否使用缓存,只有get时有效, 默认关闭, 启用后如果命中缓存, response中有useCache=true. 另: 内存缓存, 刷新就没.
 * @param {number} ttl 缓存生命周期, 默认60秒, 单位毫秒
 * @param {number} timeout 超时时长, 默认未设, 单位毫秒
 * @param {boolean} getResponse 是否获取response源
 * @param {function} errorHandler 错误处理
 * @param {string} prefix 前缀
 * @param {string} suffix 后缀
 * @param {string} charset 字符集, 默认utf8
 */
export interface RequestOptionsInit extends RequestInit {
  charset?: 'utf8' | 'gbk';
  requestType?: 'json' | 'form';
  data?: any;
  params?: object;
  responseType?: ResponseType;
  useCache?: boolean;
  ttl?: number;
  timeout?: number;
  errorHandler?: (error: ResponseError) => void;
  prefix?: string;
  suffix?: string;
}
```
看了注释只需将 [Ant Design Pro](https://pro.ant.design/) Utils 包下的 request 封装包 加上requestType: 'form'即可。
```javascript
/**
 * 配置request请求时的默认参数
 */
const request = extend({
  errorHandler, // 默认错误处理
  credentials: 'include', // 默认请求是否带上cookie
  requestType: 'form',
});
```

> 参考于 [my_wings](https://blog.csdn.net/my_wings/article/details/85618692) 的博客记录
> 参考于 [_平凡的自我_](https://blog.csdn.net/zhh123sy001/article/details/86327538) 的博客记录
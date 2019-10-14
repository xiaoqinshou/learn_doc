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


> 参考于 [my_wings](https://blog.csdn.net/my_wings/article/details/85618692) 的博客记录
> 参考于 [_平凡的自我_](https://blog.csdn.net/zhh123sy001/article/details/86327538) 的博客记录
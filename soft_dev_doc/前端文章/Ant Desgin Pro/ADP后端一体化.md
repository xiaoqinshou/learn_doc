## [Ant Design Pro](https://pro.ant.design/index-cn) 后端一体部署小试牛刀

1. 将 [Ant Design Pro](https://pro.ant.design/index-cn) 与后端 [Spring Boot](https://spring.io/projects/spring-boot) 合并打包发布。

研究了好些天的 [Ant Design Pro](https://pro.ant.design/index-cn) 把它相关的，一些技术都补了一遍，顺带还看了一下微服务。最终因为业务需求还是强行把它俩，凑在一起一并发布。
> ------------

实际动手起来有点难，本人学了也没多久，目前选用了几个最简单暴力的方法将它们俩打包凑在一起发布。

首先是前台路由与服务器代理配置，之前写了一篇相关博客的，但是实际动手起来，没啥用，当凑到一起的时候，服务器代理不起作用。

接下来讲一下原理，原理就是前端所有框架再牛逼，生成的源码终究还是 [CSS+HTML+JS](#) 代码，最简单方法，将生成的源码直接丢进 [Spring Boot](https://spring.io/projects/spring-boot) 项目中的/resource/static目录下这就初步实现了，同构吧。虽然还有一些其他的花里胡哨的同构，像什么 [webpack](https://webpack.github.io/) 之类的，因为暂时不会，也没有太多时间学，以后有机会接触再写。。。

顺带提一下，其实[JHipster](https://www.jhipster.tech/)中生成的，[React](https://react.docschina.org/) 项目中使用到了 [webpack](https://webpack.github.io/) 进行打包，其中原理也是很简单将生成的源码动态打包进项目运行的tager目录下。在这里我选用的是手动简单暴力一点的做法。省时间。

***
同构之后，会出现两个问题。
1. 在地址栏直接输入前端静态路由时，浏览器会直接请求后端，访问后端路由，当后端不存在这个路由时就会报服务器错误，请求地址不存在，404等错误。

解决方案： 在前端对应的每一个静态路由都创建一个相应的重定向后端路由。
```java
@GetMapping("/user/login")
    public String Test() {
        return "redirect:/#/user/login";
    }
```
这里就牵扯到一个 [React-router](#) 中的 [HashRouter](#) 和 [BrowserHistory](#)，简单的概括一下。
* [HashRouter](#) 可以访问虚拟路径不发起后台请求，/#/user/login 这里相当于只是做了一个前端HTML跳转
* [BrowserHistory](#) 必须得发起后台请求需要后台 [API](http://baike.baidu.com/link?url=uYOPrAkll3cftn0MkQl82rtQGPNDeFHuTluQIdxTcIt-wOhhwI9aQEJMm5KnmRX6kv-izr2BZ8XPhCo_rCRJpK) 暴露支持，例如 /user/login 就直接访问了后端API，当后端不存在此API时，就会报错。
***
其次就是之前所说到的，实际打包之后的源码中，Ant Design Pro中的服务器代理是不生效的。
解决方案：根据实际情况，抛弃代理使用 JS 的相对路径即可。直接在前端项目的 service 层中，给出相对路径即可。

以后有时间我会，相对的再整理一篇关于 [webpack](https://webpack.github.io/) 前端项目打包的教程吧。有助于[Ant Design Pro](https://pro.ant.design/index-cn) 与后端 [Spring Boot](https://spring.io/projects/spring-boot) 真正的结合，让我们再也不用手动复制粘贴了。
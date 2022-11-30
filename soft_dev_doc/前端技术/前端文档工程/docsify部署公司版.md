# docsify 部署
* [官方文档](https://docsify.js.org/)

## 概述
> docsify 可以快速帮你生成文档网站。不同于 GitBook、Hexo 的地方是它不会生成静态的 .html 文件，所有转换工作都是在运行时。如果你想要开始使用它，只需要创建一个 index.html 就可以开始编写文档并直接部署在 GitHub Pages。

## 工作原理
1. 通过监听路由器的hash路由的变化，进行markdown文件的请求
2. 请求文件后拿到markdown内容，将内容交由marked初步解析
3. 解析的结构明了后，再由内部的各类解析器(高亮语法解析器，表格解析器等等)进行解析转为Html Dom元素
4. 页面的呈现

* 工作原理其实很简单

## 部署
* 常用部署方式，nginx， tomcat等web服务器做个静态资源代理即可
* 参考原理：它内部是有一套完整的运行渲染解析体系的，只需要服务器提供对应的静态文件内容，http请求返回功能即可

* 常用配置如下注释：需要其他的配置需要去官方文档中查看
* 只需在服务器(例如：nginx)上的某一个文件夹下根结点部署该html文件即可
```html
<!DOCTYPE html>
<!DOCTYPE html>
<html>

<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <meta charset="UTF-8">
  <link rel="stylesheet" href="./css/vue.css">
</head>

<body>
  <div id="app"></div>
  <script>
    window.$docsify = {
      auto2top: true, // 切换页面后是否自动跳转到页面顶部。
      basePath: '/ht_doc/docs', //文档加载的根路径，可以是二级路径或者是其他域名的路径。
      loadSidebar: true, //加载自定义侧边栏，设置为 true 后会加载 _sidebar.md 文件，也可以自定义加载的文件名。
      alias: { // 别名
        '/.*/_sidebar.md': '/_sidebar.md'
      },
      mergeNavbar: true, //小屏设备下合并导航栏到侧边栏。
      subMaxLevel: 3, // 自定义侧边栏后默认不会再生成目录，你也可以通过设置生成目录的最大层级开启这个功能。
      // autoHeader: true,
      name: '宏图BI大屏使用文档', // 文档标题，会显示在侧边栏顶部
      search: {
        paths: 'auto', // 开启搜索
      }
    }
  </script>
  <script src="./js/docsify.min.js"></script>
  <!-- 全文搜索插件 -->
  <script src="./js/search.min.js"></script>
  <!-- 图片缩放插件 -->
  <script src="./js/zoom-image.min.js"></script>
  <!-- 代码拷贝插件 -->
  <script src="./js/docsify-copy-code.min.js"></script>
  <!-- Docsify v4 -->
  <!-- <script src="./js/docsify-v4.min.js"></script> -->
</body>
</html>
```

## 文档结构描述
* 文档结构应当如下：
```tree
.
├── css
│   └── vue.css // 网站主题样式
├── doc_bak // 文档备份文件夹
├── docs // 具体文档内容
│   ├── README.md // 首页内容， 对应黄色部分，尚未点击菜单栏任何一项时所展示的内容
│   ├── _sidebar.md // 菜单渲染文件， 对应图片红色部分
│   ├── chartComponent
│   │   ├── arrange-alternative.md
│   │   ├── arrange-radio.md
│   │   ├── bars-fore.md
│   │   ├── blocks-map.md
│   │   ├── composite-index.md
│   │   ├── date-radio.md
│   │   ├── date-range.md
│   │   ├── dial-indicators.md
│   │   ├── drop-down-choice.md
│   │   ├── fore-bars.md
│   │   ├── forms.md
│   │   ├── funnel-figure.md
│   │   ├── nested-pie.md
│   │   ├── ordinary-pie.md
│   │   ├── radar-map.md
│   │   ├── scatter-diagram.md
│   │   ├── tab-selection.md
│   │   ├── text-input.md
│   │   ├── toggle-button.md
│   │   └── tree-down.md
│   ├── image // 统一的文档图片地址
│   ├── 产品基础
│   │   ├── 产品介绍.md
│   │   └── 核心概念介绍.md
│   ├── 快速入门
│   │   ├── 制作大屏.md
│   │   └── 制作报表.md
│   ├── 操作指南
│   │   ├── 平台管理
│   │   │   ├── 大屏管理.md
│   │   │   ├── 报表管理.md
│   │   │   ├── 模板管理.md
│   │   │   ├── 空间管理.md
│   │   │   └── 组织管理.md
│   │   ├── 用户试用.md
│   │   ├── 可视化分析
│   │   │   ├── 数据排序
│   │   │   │   ├── 字段数据排序.md
│   │   │   │   ├── 度量字段排序.md
│   │   │   │   ├── 维度字段排序.md
│   │   │   │   ├── 额外字段排序.md
│   │   │   │   └── 表格的更多设置.md
│   │   │   ├── 图表下钻.md
│   │   │   └── 图表联动.md
│   │   ├── 连接数据源.md
│   │   ├── 创建数据模型
│   │   │   ├── 自定义SQL视图.md
│   │   │   ├── 多表关联.md
│   │   │   ├── 数据模型.md
│   │   │   ├── 数据筛选.md
│   │   │   ├── 数据脱敏.md
│   │   │   ├── 添加数据表.md
│   │   │   └── 维度和度量是什么.md
│   │   └── 制作可视化页面
│   │       ├── 组件操作
│   │       │   ├── 组件交互配置.md
│   │       │   ├── 组件基础配置.md
│   │       │   ├── 组件数据配置.md
│   │       │   └── 组件高级配置.md
│   │       └── 编辑界面.md
│   ├── 更新日志
│   │   └── updateLog.md
│   └── 第三方接入
│       └── third-help.md
├── index.html // 核心静态文档页面
└── js // index.html 需要引用的JS
    ├── docsify-copy-code.min.js
    ├── docsify-v4.min.js
    ├── docsify.min.js
    ├── search.min.js
    └── zoom-image.min.js

```
![](./images/2022-10-20-01-57-03.png)

## 更新
* 初始化部署配置好了之后, 后续只需替换 docs 下面的文件内容, 以及_sidebar.md 菜单渲染文件即可

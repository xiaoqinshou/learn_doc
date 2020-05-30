# React-Router 使用
[toc]
### 前言
&emsp;`React-Router` 只是相当于一个调配者，调配项目中的一个或多个组件，将多个组件组合在一起，呈现出一个完整的更高级的组件，而前端路由就是调配者所收到的渲染组件的指令。

### 简述前后端路由的区别
#### 后端路由
多页应用中，一个URL对应一个HTML页面，一个Web应用包含很多HTML页面，在多页应用中，页面路由控制由服务器端负责，这种路由方式称为后端路由。

> 多页应用中,每次页面切换都需要向服务器发送一次请求，页面使用到的静态资源也需要重新加载，存在一定的浪费。而且，页面的整体刷新对用户体验也有影响，因为不同页面间往往存在共同的部分，例如导航栏、侧边栏等，页面整体刷新也会导致共用部分的刷新。

#### 前端路由
在单面应用中，URL发生并不会向服务器发送新的请求，所以“逻辑页面”的路由只能由前端负责，这种路由方式称为前端路由。

> 目前，国内的搜索引擎大多对单页应用的SEO支持的不好，因此，对于 SEO 非常看重的 Web
应用(例如，企业官方网站，电商网站等)，一般还是会选择采用多页面应用。React 也并非只能用于开发单页面应用。


### 版本问题
react-router 有多个版本，2.x/3.x  - 4.x版本有比较大的改动，并且互相不兼容，2.x/3.x 和 4.x 版本的语法有非常大的不同。并且 react-router 和 react 的某些版本也会有冲突，目前接触过的项目，以及自我学习接触的都是3.x 版本，对于4.x 就很陌生了。

### 安装
创建 Web应用，使用
```npm install react-router-dom```
创建 navtive 应用，使用
``` npm install react-router-native```

### 路由器
React Router 通过 Router 和 Route 两个组件完成路由功能。Router 可以理解成路由器，一个应用中需要一个 Router 实例，所有跌幅配置组件 Route 都定义为 Router 的子组件。在 Web应用中，我们一般会使用对 Router 进行包装的 BrowserRouter 或 HashRouter 两个组件 BrowserRouter使用 HTML5 的 history API（pushState、replaceState等）实现应用的 UI 和 URL 的同步。HashRouter 使用 URL 的 hash 实现应用的 UI 和 URL 同步。
***BrowserRouter 创建的 URL 形式如下：***
```
http://example.com/some/path
```
***HashRouter 创建的 URL 形式如下：***
```
http://example.com/#/some/path
```
使用 BrowserRouter 时，一般还需要对服务器进行配置，让服务器能正确地处理所有可能的URL。例如，当浏览器发生 http://example.com/some/path 和 http://example.com/some/path2 两个请求时，服务器需要能返回正确的 HTML 页面（也就是单页面应用中唯一的 HTML 页面）

HashRouter 则不存在这个问题，因为 hash 部分的内容会被服务器自动忽略，真正有效的信息是 hash 前端的部分，而对于单页应用来说，这部分是固定的。

Router 会创建一个 history 对象，history 用来跟踪 URL, 当URL 发生变化时， Router,的后代组件会重新渲染。React Router 中提供的其他组件可以通过 context 获取 history 对象，这也隐含说明了 React Router 中其他组件必须作为 Router 组件后代使用。但 Router 中只能唯一的一个子元素，例如：
```ts
// 正确
ReactDOM.render(
  (
  <BrowserRouter>
    <App />
  </BrowserRouter>),
  document.getElementById('root')
)
//错误，Router 中包含两个子元素
ReactDOM.render(
  (
    <BrowserRouter>
      <App1 />
      <App2 />
    </BrowserRouter>),
  document.getElementById('root')
)
```

### 用法
Route 是 React Router中用于配置路由信息的组件，也是 React Router 中使用频率最高的组件。每当有一个组件需要根据 URL 决定是否渲染时，就需要创建一个 Route。

#### 1. path
每个 Route 都需要定义一个 path 属性，当使用 BrowserRouter 时，path 用来描述这个Router匹配的 URL 的pathname;当使用 HashRouter时，path 用来描述这个 Route 匹配的 URL 的 hash。例如，使用 BrowserRouter 时，<Route path=''foo' /> 会匹配一个 pathname 以 foo 开始的 URL （如: http://example.com/foo）。当 URL 匹配一个 Route 时，这个 Route 中定义的组件就会被渲染出来。

#### 2. match
当 URL 和 Route匹配时，Route 会创建一个 match 对象作为 props 中的一个 属性传递给被渲染的组件。这个对象包含以下4个属性。

1. params: Route的 path 可以包含参数，例如 <Route path="/foo/:id" 包含一个参数 id。params就是用于从匹配的 URL 中解析出 path 中的参数，例如，当 URL = 'http://example.ocm/foo/1' 时，params= {id: 1}。

2. isExact: 是一个布尔值，当 URL 完全匹时，值为 true; 当 URL 部分匹配时，值为 false.例如，当 path='/foo'、URL="http://example.com/foo" 时，是完全匹配; 当 URL="http://example.com/foo/1" 时，是部分匹配。

3. path: Route 的 path 属性，构建嵌套路由时会使用到。

4. url: URL 的匹配的方式

#### 3. Route 渲染组件的方式
1.component
component 的值是一个组件，当 URL 和 Route 匹配时，Component属性定义的组件就会被渲染。例如：
```ts
<Route path='/foo' component={Foo} >
```
当 URL = "http://example.com/foo" 时，Foo组件会被渲染。
2. render
render 的值是一个函数，这个函数返回一个 React 元素。这种方式方便地为待渲染的组件传递额外的属性。例如：
```ts
<Route path='/foo' render={(props) => {
  <Foo {...props} data={extraProps} />
}}>
</Route>
```
Foo 组件接收了一个额外的 data 属性。

3. children
children 的值也是一个函数，函数返回要渲染的 React 元素。 与前两种方式不同之处是，无论是否匹配成功， children 返回的组件都会被渲染。但是，当匹配不成功时，match 属性为 null。例如:
```
<Route path='/foo' render={(props) => {
  <div className={props.match ? 'active': ''}>
    <Foo {...props} data={extraProps} />
  </div>
}}>
</Route>
```
如果 Route 匹配当前 URL，待渲染元素的根节点 div 的 class 将设置成 active.

4. Switch 和 exact
当URL 和多个 Route 匹配时，这些 Route 都会执行渲染操作。如果只想让第一个匹配的 Route 沉浸，那么可以把这些 Route 包到一个 Switch 组件中。如果想让 URL 和 Route 完全匹配时，Route才渲染，那么可以使用 Route 的 exact 属性。Switch 和 exact 常常联合使用，用于应用首页的导航。例如：
```ts
<Router>
 <Switch>
    <Route exact path='/' component={Home}/>
    <Route exact path='/posts' component={Posts} />
    <Route exact path='/:user' component={User} />
  </Switch>
</Router>
```
如果不使用 Switch,当 URL 的 pathname 为 "/posts" 时，`<Route path='/posts' />` 和 `<Route path=':user' />` 都会被匹配，但显然我们并不希望 `<Route path=':user' />` 被匹配，实际上也没有用户名为 posts 的用户。如果不使用 exact， "/" "/posts" "/user1"等几乎所有 URL 都会匹配第一个 Route,又因为Switch 的存在，后面的两个 Route永远不会被匹配。使用 exact,保证 只有当 URL 的 pathname 为 '/'时，第一个Route才会匹配。

5. 嵌套路由
嵌套路由是指在Route 渲染的组件内部定义新的 Route。例如，在上一个例子中，在 Posts 组件内再定义两个 Route:
```ts
const Posts = ({match}) => {
  return (
    <div>
      {/* 这里 match.url 等于 /posts */}
      <Route path={`${match.url}/:id`} component={PostDetail} />
      <Route exact path={match.url} component={PostList} />
    </div>
  )
}
```

### 链接
Link 是 React Router提供的链接组件，一个 Link 组件定义了当点击该 Link 时，页面应该如何路由。例如：
```ts
const Navigation = () => {
  <header>
    <nav>
      <ul>
        <li><Link to='/'>Home</Link></li>
        <li><Link to='/posts'>Posts</Link></li>
      </ul>
    </nav>
  </header>
}
```
Link 使用 to 属性声明要导航到的URL地址。to 可以是 string 或 object 类型，当 to 为 object 类型时，可以包含 pathname、search、hash、state 四个属性，例如:
```ts
<Link to={{
  pathname: '/posts',
  search: '?sort=name',
  hash:'#the-hash',
  state: { fromHome: true}
}}>
</Link>
```
除了使用`Link`外，我们还可以使用 `history` 对象手动实现导航。`history` 中最常用的两个方法是 `push(path,[state])` 和 `replace(path,[state])`,`push`会向浏览器记录中新增一条记录，`replace` 会用新记录替换记录。例如：
```ts
history.push('/posts');
history.replace('/posts');
```

### 路由设计
路由设计的过程可以分为两步：

为每一个页面定义有语义的路由名称(path)
组织 Route 结构层次
1. 定义路由名称
我们有三个页面，按照页面功能不难定义出如下的路由名称：
* 登录页： /login
* 帖子列表页: /posts
* 帖子详情页: /posts/:id(id代表帖子的ID)
但是这些还不够，还需要考虑打开应用时的默认页面，也就是根路径"/"对应的页面。结合业务场景，帖子列表作为应用的默认页面为合适，因此，帖子列表对应两个路由名称: '/posts'和 '/'

2. 组织 Route 结构层次
React Router 4并不需要在一个地方集中声明应用需要的所有 Route, Route实际上也是一个普通的 React 组件，可以在任意地方使用它（前提是，Route必须是 Router 的子节点）。当然，这样的灵活性也一定程度上增加了组织 Route 结构层次的难度。
我们先考虑第一层级的路由。登录页和帖子列表页(首页)应该属于第一层级：
```ts
<Router>
  <Switch>
    <Route exact path="/" component={Home}></Route>
    <Route exact path="/login" component={Login}></Route>
    <Route exact path="/posts" component={Home}></Route>
  </Switch>
</Router>
```
第一个Route 使用了 exact 属性，保证只有当访问根路径时，第一个 Route 才会匹配成功。Home 是首页对应组件，可以通过 "/posts" 和 “/” 两个路径访问首页。注意，这里并没有直接渲染帖子列表组件，真正渲染帖子列表组件的地方在 Home 组件内，通过第二层级的路由处理帖子列表组件和帖子详情组件渲染，components/Home.js 的主要代码如下：

```ts
class Home extends Component {
  /**省略其余代码 */
  render() {
    const {match, location } = this.props;
    const { username } = this.state;
    return(
      <div>
        <Header
          username = {username}
          onLogout={this.handleLogout}
          location = {location}
        >
        </Header>
        {/* 帖子列表路由配置 */}
        <Route
          path = {match.url}
          exact
          render={props => <PostList username={username} {...this.props}></PostList>}
        ></Route>
      </div>
    )
  }
}
```
Home的render内定义了两个 Route,分别用于渲染帖子列表和帖子详情。PostList 是帖子列表组件，Post是帖子详情组件，代码使用Router 的render属性渲染这两个组件，因为它们需要接收额外的 username 属性。另外，无论访问是帖子列表页面还是帖子详情页面，都会共用相同 Header 组件。

### 代码分片
默认情况下，当在项目根路径下执行 npm run build 时 ,create-react-app内部使用 webpack将 src路径下的所有代码打包成一个 JS 文件和一个 Css 文件。

当项目代码量不多时，把所有代码打包到一个文件的做法并不会有什么影响。但是，对于一个大型应用，如果还把所有的代码都打包到一个文件中，显然就不合适了。

create-react-app 支持通过动态 import() 的方式实现代码分片。import()接收一个模块的路径作为参数，然后返回一个 Promise 对象， Promise 对象的值就是待导入的模块对象。例如
```ts
// moduleA.js

const moduleA = 'Hello'
export { moduleA };

// App.js

import React, { Component } from 'react';

class App extends Component {
  handleClick = () => {
    // 使用import 动态导入 moduleA.js
    import('./moduleA')
      .then(({moduleA}) => {
        // 使用moduleA
      })
      .catch(err=> {
        //处理错误
      })
  };
  render() {
    return(
      <div>
        <button onClick={this.handleClick}>加载 moduleA</button>
      </div>
    )
  }
}

export default App;
```
上面代码会将 moduleA.js 和它所有依赖的其他模块单独打包到一个chunk文件中，只有当用户点击加载按钮，才开始加载这个 chunk 文件。
当项目中使用 React Router 是，一般会根据路由信息将项目代码分片，每个路由依赖的代码单独打包成一个chunk文件。我们创建一个函数统一处理这个逻辑：
```ts
import React, { Component } from 'react';
// importComponent 是使用 import()的函数
export default function asyncComponent(importComponent) {
  class AsyncComponent extends Component {
    constructor(props) {
      super(props);
      this.state = {
        component:  null //动态加载的组件
      }
    }
    componentDidMount() {
      importComponent().then((mod) => {
        this.setState({
          // 同时兼容 ES6 和 CommonJS 的模块
          component: mod.default ? mod.default : mod;
        });
      })
    }
    render() {
      // 渲染动态加载组件
      const C = this.state.component;
      return C ? <C {...this.props}></C> : null
    }
  }

  return AsyncComponent;
}
```
asyncComponent接收一个函数参数 importComponent, importComponent 内通过import()语法动态导入模块。在AsyncComponent被挂载后，importComponent就会阴调用，进而触发动态导入模块的动作。
下面利用 asyncComponent 对上面的例子进行改造，代码如下:
```ts
import React, { Component } from 'react';
import { ReactDOM, BrowserRouter as Router, Switch, Route } from 'react-dom';
import asyncComponent from './asyncComponent'
//通过asyncComponent 导入组件，创建代码分片点
const AsyncHome = asyncComponent(() => import("./components/Home"))
const AsyncLogin = asyncComponent(() => import("./components/Login"))

class App extends component {
  render() {
    return(
      <Router>
        <Switch>
          <Route exact path="/" component={AsyncHome}></Route>
          <Route exact path="/login" component={AsyncLogin}></Route>
          <Route exact path="/posts" component={AsyncHome}></Route>
        </Switch>
      </Router>
    )
  }
}

export default App;
```
这样，只有当路由匹配时，对应的组件才会被导入，实现按需加载的效果。

这里还有一个需要注意的地方，打包后没有单独的CSS文件了。这是因为 CSS样子被打包到各个 chunk 文件中，当 chunk文件被加载执行时，会有动态把 CSS 样式插入页面中。如果希望把 chunk 中的 css打包到一个单独的文件中，就需要修改 webpack 使用的 ExtractTextPlugin 插件的配置，但 create-react-app 并没有直接把 webpack 的配置文件暴露给用户，为了修改相应配置，需要将 create-react-app 管理的配置文件“弹射”出来，在项目根路径下执行：

```npm run eject```

项目中会多出两个文件夹：config和 scripts,scrips中包含项目启动、编译和测试的脚本，config 中包含项目使用的配置文件，
webpack配置文件 就在这个路径下，打包 webpack.config.prod.js 找到配置 ExtractTextPlugin 的地方，添加 allChunks:true 这项配置：
```ts
new ExtractTextPlugin({
  filename: cssFilename,
  allChunks: true
})
```

然后重新编译项目，各个chunk 文件 使用的 CSS 样式 又会统一打包到 main.css 中。

### 感悟
&emsp;使用后原理感觉就是，通过监听 window.location（浏览器地址栏）的路由信息，渲染不同的组件，然后再通过 React Content 用法，使之路由命令全局可见。
&emsp;没看过源码，不能保证以上见解就是正确的。

### 原文地址
[前端小智](https://segmentfault.com/a/1190000016421036#item-1)

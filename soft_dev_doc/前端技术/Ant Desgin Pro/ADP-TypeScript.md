## 前置技术
1. TypeScript
了解 TypeScript 语法，推荐此处学习 [快速入门](https://ts.xcatliu.com/),[进阶之路](https://typescript.bootcss.com/)

2. React - TypeScript最基础组件式开发
```typeScript
import React, { Component } from 'react';


interface IHomeState {
  title: string;
  name: string;
}

interface TwoItem {
  title: string;
}

class Home extends Component<IHomeState> {
  public item: TwoItem = {
    title: 'test',
  };

  public state: IHomeState = {
    title: 'home state title',
    name: 'props name',
  };

  constructor(state: IHomeState, item: TwoItem) {
    super(state, item);
    console.log(this.props);
    console.log(this.state);
    console.log(this.item);
    console.log(this);
  }
  // 这种类型的方法，不知道为啥其实 this 指向的是undefined 并不是该类本身
//  public changeTitle(): void {
//    console.log(this);
//    this.setState({
//      title: 'change title',
//    });
//  }
  // 必须使用这种赋值变量，形式的方发声明，this 才会指向当前类
  public changeTitle: any = (): void => {
    console.log(this);
    this.setState({
      title: 'change title',
    });
    // 上面的方法是React定义的 修改State 组件的方法中途会触发一个重新渲染
    // 下面两个虽然会直接修改当前类的值，但是并不会触发重新渲染，所以页面不会改变
    // this.state.title = 'change title';
    // this.item.title = 'change title';
  };

  render() {
    return (
      <div className="home-component-root">
        <p>{this.state.title}</p>
        <p>{this.state.name}</p>
        <p>{this.item.title}</p>
        <button onClick={this.changeTitle}>this test button</button>
      </div>
    );
  }
}
export default Home;
// -----------结果-----------
// 其实就是调用当前组件时的各种属性，例如路由啊，标题，hash值，搜索值等等
this.props = {
    children: null
    computedMatch: {path: "/editor/test", url: "/editor/test", isExact: true, params: {…}}
    history: {length: 5, action: "POP", location: {…}, createHref: ƒ, push: ƒ, …}
    location: {pathname: "/editor/test", search: "", hash: "", query: {…}, state: undefined, …}
    match: {path: "/editor/test", url: "/editor/test", isExact: true, params: {…}}
    route: {name: "test", path: "/editor/test", component: ƒ, exact: true}
    staticContext: undefined
    }
this.state = {
    name: "props name"
    title: "home state title"
}
this.item = {
    title: "test"
}
// 从this指向值可以看得出，当前类的局部变量，以及方法都属于这个js 对象里面
this = {
    changeTitle: ƒ ()
    context: {}
    item: {title: "test"}
    props: {match: {…}, location: {…}, history: {…}, staticContext: undefined, computedMatch: {…}, …}
    refs: {}
    state: {title: "change title", name: "props name"}
    updater: {isMounted: ƒ, enqueueSetState: ƒ, enqueueReplaceState: ƒ, enqueueForceUpdate: ƒ}
    _reactInternalFiber: FiberNode {tag: 1, key: null, elementType: ƒ, type: ƒ, stateNode: Home, …}
    _reactInternalInstance: {_processChildContext: ƒ}
}
```
其实，这里从多个角度分析，将changeTitle方法分别改成public ，protected, private 时，其实google浏览器中打印出来的this,还是一样，没什么区别。

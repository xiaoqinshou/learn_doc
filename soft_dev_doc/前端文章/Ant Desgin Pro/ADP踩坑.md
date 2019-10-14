## [Ant Design Pro](https://pro.ant.design/index-cn) 填坑之路

#### 1. 概述
&emsp;当把 [Ant Design Pro](https://pro.ant.design/index-cn) clone下来到本地运行，看着这鬼框架，熟悉了一些 [React](http://react-china.org/)，[umi](https://umijs.org/)，[dva](https://dvajs.com/guide/)等前置技术栈后。直接使用虽然代码看得懂了，但是还是会无从下手，这 demo 封装的太紧凑了，真的是无从下手，稍微改一点都可能一发不可收拾。
&emsp;当然，了解了这些前置技术栈，是远远不够的。最多只能达到你能看得懂 [Ant Design Pro](https://pro.ant.design/index-cn) 的代码。想改？还需要进一步挖掘这个框架的，运行原理，以及一些配置文件的用途，但是官方特别小气，大部分配置文件都没提。或许在原有页面上进行一点组件上添加删除的微改不是问题，但是新建页面那就头疼了。这个坑踩了我一天，从demo页面复制出的700行代码稍微整改一下，结果页面一直报错，逻辑，语法，看起来都没问题，随处打印日志是一个都没打印，最后删成Hello World代码，都是一直在报错。
* ‘is using incorrect casing. Use PascalCase for React components, or lowercase for HTML elements.’
* Can't perform a React state update on an unmounted component. This is a no-op, but it indicates a memory leak in your application. To fix, cancel all subscriptions and asynchronous tasks in the componentWillUnmount method.

这两个F12的JS报错简直就是我的噩梦。

#### 2. [Ant Design Pro](https://pro.ant.design/index-cn)  页面加载机制
![加载机制](https://img-blog.csdnimg.cn/20181120171847604.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3psMXpsMnpsMw==,size_16,color_FFFFFF,t_70)
> 图片引用于 [zl1zl2zl3](https://blog.csdn.net/zl1zl2zl3/article/details/81357146)的博客，如涉侵权，请联系我删除

#### 3. [Ant Design Pro](https://pro.ant.design/index-cn) [MVC](https://baike.baidu.com/item/MVC%E6%A1%86%E6%9E%B6/9241230?fromtitle=MVC&fromid=85990&fr=aladdin) 各模块通讯机制
##### 3.1 概述
&emsp;官方文档是表明，Model 层控制数据，View 视图展示，Controller 获取数据。此 Model 是基于 [DVA](https://dvajs.com/) 中的概念而来，感兴趣的小伙伴，可以去深入挖掘一下 [dva.js](https://dvajs.com/) ，这里主要是为了记录并且降低 [Ant Design Pro](https://pro.ant.design/index-cn) 此框架的上手难度，就不普及其余那么多的知识了。
##### 3.2 Model 结构
&emsp;但是经过个人理解,将这个 MVC 理解成 M(C)VS 更好。首先来解释一下 Model。
&emsp;Model 层主要是用于保存数据，由上一章的页面加载机制可以知道，View 层在加载时，会访问 Model 层，获取该绑定好的 Model 中 state 数据。进行渲染实际的页面展示。
&emsp;讲解一下 Model 层的结构。
```typescript
export default {
  namespace: 'Model名称',

  state: {
      '该Model所存放的数据,任意对象格式,数组对象嵌套皆可'
  },

/**
* 相当于控制器，调用Service层异步发起请求，获取数据，可有多个控制器方法。
* 参数 { call, put } 是固定形参 yield call发起异步请求。
* yield put 就是调用同步块reducers 中的方法。
*/
  effects: {
    * getData({ payload }, { call, put }){
        //发起异步请求
        const response = yield call(Service, payload);
        //发起同步保存
        yield put({
        type: 'save',
        payload: this.state.data,
      });
    }
  },

/**
* 至于该属性，暂未用到，暂未理解以后再填
*/
  subscriptions: {

  }

/**
* 同步代码块，将结果保存入该 Model 的 State 中
*/
  reducers: {
    save(state, action) {
      return {
        ...state,
        data: action.payload,
      };
    },
  },
}
```
##### 3.3 View 中引入 Model 并与控制器交互
&emsp;导入该 Model 只需利用装饰器 @connect 在类前装饰即可
```typescript
//View层 加载 Model
@connect(({ modelName, loading }) => ({
  modelName,
  loading: loading.models.faker,
}))

/**
   * 调用 Model 中的 Controller
   * 它会自动根据modelName 找到该Model 
   * 并且调用 Model 下 effects块下的 对应方法名的方法
   * */
  eventName = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'modelName/getData',
      payload: {
        // 所需要传入数据 
      },
    });
  };
```
##### 3.4 View层的结构
&emsp;因为此框架是基于 React 所开发出来的，React本人也就随便看了一点，View 作为视图层，太多变了我自己也很模糊，就随便讲一点， [Ant Design Pro](https://pro.ant.design/index-cn) 中的规范。
&emsp;随意讲一下  [Ant Design Pro](https://pro.ant.design/index-cn) 中 View 层的结构。
```typescript
//装饰器为此类装载一个Model
@connect(({ modelName, loading }) => ({
  modelName,
  loading: loading.models.faker,
}))
//视图类
class ViewName extends Component {
//而且这个是必须的
  state = {
    //InitData 初始化数据，相当于该类的局部变量
  };

/**
   * 类似于构造函数，
   * */
  constructor(props){
        
    }

  /**
   * 讲虚拟DOM元素实体化之后
   * 调用此方法，例如在这里获取一次页面初始化时的数据请求等
   * 可写可省略
   * */
  componentDidMount() {
    //其中更新类局部变量 只能使用this.setState方法去更新类的变量  
    this.setState({

    });
  }
  /**
   * 必须，为此类返回一个视图
   * 其中Model中的数据被装载之后都会在
   * 全局变量 this.props 中
   * */
  render() {
    const {
      modelName: { data },
      loading,
    } = this.props;
    return (
      <div>hello world!</div>
    );
  }
}
//返回一个视图类组件
export default ViewName;

```

##### 3.5 View 的渲染机制
&emsp;View 的渲染机制，主要是当局部类变量state中有变量变化，它会重新渲染一次这个组件，至于是全部重新渲染，还是局部渲染，我只能说我母鸡。当调用了
```typescript
this.setState({
    key: newData
});
```
更新当前类的局部变量时，该视图层会重新渲染。

&emsp;当 Model 中的 同步块（reducers）中的 save 方法调用且更新了 Model 中的 state 数据时，View会重新渲染，这里也是同样的回答，是全部重新渲染，还是局部渲染，而且，再抛个问题是 Model 中的方法被调用了，视图会重新渲染，还是只有当 state 中的数据有变化，还是两者皆有时视图层会重新渲染。我只能说我母鸡。

##### 3.6 Service 的简介
&emsp;Service 层还真没啥好说的。看代码：
```typescript
export async function query(params) {
  return request('/api/list', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function remove(params) {
  return request(`/api/delete?id=${params.id}`);
}
```
&emsp;基本就这样传入所需要传的数据数组，给好后台控制器就行。具体逻辑处理在 Model 中的控制器中处理

#### 4. React 生命周期
##### 4.1 概述
&emsp;生命周期还是很重要的，它包含一个人的出生，到平常生活，直至死亡，大部分人了解的只是生活的过程吧，对于一个人的出生以及死亡，毫不关心，这样就不能合理的利用这个人的一生。
&emsp;就例如我了解它的出生，我可以让他一出生就变成王思聪，或是夭折，了解他的死亡，我可以在他死亡时，令它重生。只有了解全部，理解全部才能更好的利用它。

##### 4.2 详述生命周期
![组件之间运行生命周期](https://img-blog.csdn.net/20180904094453195?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NzE5MDM5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
> 图片引用于 [天蒙蒙亮](https://blog.csdn.net/qq_38719039/article/details/82378434)的博客，如涉侵权，请联系我删除

注意，如果在shouldComponentUpdate里面返回false可以提前退出更新路径。

##### 4.3 生命周期测试
```typescript
class LifeCycle extends React.Component {
    constructor(props) {
        super(props);
        alert("Initial render");
        alert("constructor");
        this.state = {str: "hello"};
    }
 
    componentWillMount() {
        alert("componentWillMount");
    }
 
    componentDidMount() {
        alert("componentDidMount");
    }
 
    componentWillReceiveProps(nextProps) {
        alert("componentWillReceiveProps");
    }
 
    shouldComponentUpdate() {
        alert("shouldComponentUpdate");
        return true;        // 记得要返回true
    }
 
    componentWillUpdate() {
        alert("componentWillUpdate");
    }
 
    componentDidUpdate() {
        alert("componentDidUpdate");
    }
 
    componentWillUnmount() {
        alert("componentWillUnmount");
    }
 
    setTheState() {
        let s = "hello";
        if (this.state.str === s) {
            s = "HELLO";
        }
        this.setState({
            str: s
        });
    }
 
    forceItUpdate() {
        this.forceUpdate();
    }
 
    render() {
        alert("render");
        return(
            <div>
                <span>{"Props:"}<h2>{parseInt(this.props.num)}</h2></span>
                <br />
                <span>{"State:"}<h2>{this.state.str}</h2></span>
            </div>
        );
    }
}
 
class Container  extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            num: Math.random() * 100
        };
    }
 
    propsChange() {
        this.setState({
            num: Math.random() * 100
        });
    }
 
    setLifeCycleState() {
        this.refs.rLifeCycle.setTheState();
    }
 
    forceLifeCycleUpdate() {
        this.refs.rLifeCycle.forceItUpdate();
    }
 
    unmountLifeCycle() {
        // 这里卸载父组件也会导致卸载子组件
        React.unmountComponentAtNode(document.getElementById("container"));
    }
 
    parentForceUpdate() {
        this.forceUpdate();
    }
 
    render() {
        return (
            <div>
                <a href="javascript:;" className="weui_btn weui_btn_primary" onClick={this.propsChange.bind(this)}>propsChange</a>
                <a href="javascript:;" className="weui_btn weui_btn_primary" onClick={this.setLifeCycleState.bind(this)}>setState</a>
                <a href="javascript:;" className="weui_btn weui_btn_primary" onClick={this.forceLifeCycleUpdate.bind(this)}>forceUpdate</a>
                <a href="javascript:;" className="weui_btn weui_btn_primary" onClick={this.unmountLifeCycle.bind(this)}>unmount</a>
                <a href="javascript:;" className="weui_btn weui_btn_primary" onClick={this.parentForceUpdate.bind(this)}>parentForceUpdateWithoutChange</a>
                <LifeCycle ref="rLifeCycle" num={this.state.num}></LifeCycle>
            </div>
        );
    }
}
 
ReactDom.render(
    <Container></Container>,
    document.getElementById('container')
);
```
##### 4.4 单组件的详细生命周期
![单组件详细生命周期](https://img-blog.csdn.net/20180202142556319?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMTEzNTg4Nw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
> 图片引用于 [流川枫与苍井小姐](https://blog.csdn.net/u011135887/article/details/79239328)的博客，如涉侵权，请联系我删除
#### 5. 随便说说
&emsp;我主攻后端，前端只是顺带的，所以此文章只是做个记录提醒自己快速上手而已，也是给和我同样情况的码农，快速上手的一篇攻略吧。
&emsp;有不好的地方，请指教。



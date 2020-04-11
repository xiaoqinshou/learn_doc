## 问题
> 因为需求需要开发一个设计器，为了省事选择了 [React-Grid-Layout](https://github.com/STRML/react-grid-layout) 这个拖拽组件，也的确没让我失望挺好用的，但是它的 ResponsiveGridLayout 拓展形式，初次加载时我发现宽度不变，导致我嵌入式布局，实际编辑区域，要比我所给区域要宽很多。
> 然后呢？我使用第一次渲染固定宽度赋值，发现 width 属性完全不管用。
> 无果，尝试着看了看源码，然后发现 WidthProvider 的源码挺简单的。
> 以下丢出实际操作

### 1.下载 [React-Grid-Layout](https://github.com/STRML/react-grid-layout) 源码

### 2. 对应源码
&emsp;&emsp;它自适应的原理就是，装饰器装饰 GridLayout 拓展出一些回调方法，以及监听 浏览器的 onresize 事件。并且根本没使用传进来的 width (怪不得我怎么传都不生效)，它内部自定义了一个 width = 1280。所以第一次加载出来，永远都是 1280，虽然源码中：
```jsx
componentDidMount() {
      this.mounted = true;

      window.addEventListener("resize", this.onWindowResize);
      // Call to properly set the breakpoint and resize the elements.
      // Note that if you're doing a full-width element, this can get a little wonky if a scrollbar
      // appears because of the grid. In that case, fire your own resize event, or set `overflow: scroll` on your body.
      this.onWindowResize();
    }

componentWillUnmount() {
      this.mounted = false;
      window.removeEventListener("resize", this.onWindowResize);
    }

onWindowResize = () => {
      if (!this.mounted) return;
      // eslint-disable-next-line react/no-find-dom-node
      const node = ReactDOM.findDOMNode(this); // Flow casts this to Text | Element
      if (node instanceof HTMLElement)
        this.setState({ width: node.offsetWidth });
    };
```
&emsp;&emsp;它是默认运行一次获取宽度，但是不知道为什么我怎么改第一次都是1280的可实际编辑区域，非要触发一次 onresize 事件才能正常。

### 3. 修改后的源码
```jsx
 constructor(props) {
      super(props);
      this.state = {
        width: props.width
      };
    }

    // 宽度同时交给更上层控制
componentWillReceiveProps(nextProps, nextContext) {
      const width = nextProps.width;
      if (width > 0) {
        this.setState({
          width
        });
      }
    }
```
删除了 onresize 监听事件，并且扩展宽度交给上层控制，因为我上层组件给它画了一个背景板，同时上层创建了一个 onresize 监听 同时调整背景板以及 拖拽组件效果会更好。

### 4. 编译
&emsp;&emsp;因为长期在Windows 环境下开发，对于各位大佬的，各种打包方式，实在是孤陋寡闻，这里也知道了算是一个叫 Makefile 的打包工具吧。
&emsp;&emsp;看了一下源码中自带的 Makefile 文件，还行，半懂不懂，还是直接用吧，也找到了一个能用的打包命令 `make build-js`。它自动的将jsx代码全部转化并打包成了 JS 代码。
### 4.1 Windows 环境下使用 make 命令
&emsp;&emsp;程序从官网下载, 并配置到path路径上去
&emsp;&emsp;http://gnuwin32.sourceforge.net/packages/make.htm
### 5. 打包
&emsp;&emsp;将所需的文件提出来到一个文件夹中，使用 `npm pack` 命令打包即可。

### 6. 本地安装 
&emsp;&emsp;在所需要导入的项目中安装即可。`npm install tgz文件路径`

### 7. 说两句
&emsp;&emsp;虽然不知道是不是这么打包的，感觉这么打包也行，没出什么问题，虽然有点繁琐。我是对着线上打包好的文件，来匹配那些文件需要打包，那些文件不需要打包。

### 8. 相关资料
[Makefile教程（绝对经典，所有问题看这一篇足够了）](https://blog.csdn.net/weixin_38391755/article/details/80380786)
[[快速掌握]Node.js模块封装及本地使用以及发布](https://blog.csdn.net/wcslb/article/details/53004313)
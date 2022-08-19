# React-Redux 使用
[toc]
### 前言
`React-Redux`是`Redux`的官方`React`绑定库。它能够使你的`React`组件从`Redux store`中读取数据，并且向`store`分发`actions`以更新数据

### 安装
```powershell
$ npm install --save react-redux
$ yarn add react-redux
```

### React-Redux connect原理
* 大佬写的代码，就是简单易懂。说白了就是读取全局的store 然后进行筛选，组合到装饰后的 React 组件当中
```jsx
// connect() is a function that injects Redux-related props into your component.
// You can inject data and callbacks that change that data by dispatching actions.
function connect(mapStateToProps, mapDispatchToProps) {
  // It lets us inject component as the last step so people can use it as a decorator.
  // Generally you don't need to worry about it.
  return function (WrappedComponent) {
    // It returns a component
    return class extends React.Component {
      render() {
        return (
          // that renders your component
          <WrappedComponent
            {/* with its props  */}
            {...this.props}
            {/* and additional props calculated from Redux store */}
            {...mapStateToProps(store.getState(), this.props)}
            {...mapDispatchToProps(store.dispatch, this.props)}
          />
        )
      }

      componentDidMount() {
        // it remembers to subscribe to the store so it doesn't miss updates
        this.unsubscribe = store.subscribe(this.handleChange.bind(this))
      }

      componentWillUnmount() {
        // and unsubscribe later
        this.unsubscribe()
      }

      handleChange() {
        // and whenever the store state changes, it re-renders.
        this.forceUpdate()
      }
    }
  }
}

// This is not the real implementation but a mental model.
// It skips the question of where we get the "store" from (answer: <Provider> puts it in React context)
// and it skips any performance optimizations (real connect() makes sure we don't re-render in vain).

// The purpose of connect() is that you don't have to think about
// subscribing to the store or perf optimizations yourself, and
// instead you can specify how to get props based on Redux store state:

const ConnectedCounter = connect(
  // Given Redux state, return props
  state => ({
    value: state.counter,
  }),
  // Given Redux dispatch, return callback props
  dispatch => ({
    onIncrement() {
      dispatch({ type: 'INCREMENT' })
    }
  })
)(Counter)
```

### React-Redux 使用
* 筛选方法如下：这个函数允许我们将 store 中的数据作为 props 绑定到组件上。
```ts
mapStateToProps(state, ownProps) : stateProps
// 通常使用
const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        increment: (...args) => dispatch(actions.increment(...args)),
        decrement: (...args) => dispatch(actions.decrement(...args))
    }
}
```
使用起来实际是穿进去了一个方法，然后再 provide 组件中将 store 通过这个方法解析出来，在注入到这个组件中。
### 总结
> 高级组件的一种应用形式，简单理论是这样，实际要比这复杂的多，目前还没必要深入了解。
> 取之用尽，不浪费。花太多时间学太深，搞搞后端或者其他东西又会忘记，没必要。

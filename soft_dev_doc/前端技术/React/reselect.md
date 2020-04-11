# reselect 使用
[toc]

### 前言
> `.Redux`是`React`的一个数据层,`React`组件的`state`有关逻辑处理都被单独放到`Redux`中来进行,在`state`的操作流程中衍生了很多中间件,`Reselect`这个中间件要解决的问题是:在组件交互操作的时候,`state`发生变化的时候如何减少渲染的压力.在`Reselect`中间中使用了缓存机制,这个机制可以在`javascript`的模式设计中看到介绍,这里就不详细说了[smartphp](https://www.jianshu.com/p/6e38c66366cd).

### 为什么要用到 reselect
#### 遇到的问题
先看下下面的一个组件：
```ts
import React, { Component } from 'react'
import { connect } from 'react-redux'

class UnusedComp extends Component {
    render() {
        const { a, b, c, fab, hbc, gac, uabc } = this.props
        return (
            <div>
                <h6>{a}</h6>
                <h6>{b}</h6>
                <h6>{c}</h6>
                <h6>{fab}</h6>
                <h6>{hbc}</h6>
                <h6>{gac}</h6>
                <h6>{uabc}</h6>
            </div>
        )
    }
}

function f(x, y) {
    return a + b
}

function h(x, y) {
    return x + 2 * y
}

function g(x, y) {
    return 2 * x + y
}

function u(x, y, z) {
    return x + y + z
}
```
这个UnusedComp 组件关心这样的几个props： a, b, c, f(a,b), h(b, c), g(a, c), u(a, b, c), 其中f, h, g, u分别是一个函数。 关于这几个计算的值， 我们应该怎么处理呢？
#### 把数据直接计算在redux
第一种， 我们把所有值存在redux， 所有store的结构大概是这样的：
```ts
store = {
    a:1,
    b:1,
    c:1,
    fab: 2, // a + b
    hbc: 3, // b + 2c
    gac: 3, // 2a + c
    uabc: 3 // a + b + c
}
```
这样我们的组件简单了， 只需要直接取值渲染就好 const { a, b, c, fab, hbc, gac, uabc } = this.props 。 那么问题来了， reducer的函数应该怎么处理呢？ 对应的如下：
```ts
switch(action.type) {
    case 'changeA': {
        return {
            ...state,
            a: action.a,
            fab: f(action.a, state.b),
            gac: g(action.a, state.c)
            uabc: u(action.a, state.b, state.c)
        }
    }
    case 'changeB': {
        ...
    }
    case 'changeC': {
        ...
    }
}
```
我们的reducer 函数非常复杂了， 我们每更新一个状态值。 都得维护与这个值相关的值， 不然就会有数据不一致。
#### reducer 只存最基本状态
为了保证数据流的清晰， 更新的简单。 我们只把最基本的状态存储在redux。store的结构和redcuer函数如下：
```ts
store = {
    a:1,
    b:1,
    c:1,
}
...
switch(action.type) {
    case 'changeA': {
        return {
            ...state,
            a: action.a
        }
    }
    ...
}
```
此刻组件可能是这样的：
```ts
class UnusedComp extends Component {
    render() {
        const { a, b, c } = this.props
        const fab = f(a, b)
        const hbc = h(b, c)
        const gac = g(a, c)
        const uabc = u(a, b, c)
        return (
            <div>
                <h6>{a}</h6>
                <h6>{b}</h6>
                <h6>{c}</h6>
                <h6>{fab}</h6>
                <h6>{hbc}</h6>
                <h6>{gac}</h6>
                <h6>{uabc}</h6>
            </div>
        )
    }
}
```
或者这样的：
```
class UnusedComp extends Component {
    componentWillReciveProps(nextProps) {
        const { a, b, c } = this.props
        this.fab = f(a, b)
        this.hbc = h(b, c)
        this.gac = g(a, c)
        this.uabc = u(a, b, c)
    }
    

    render() {
        const { a, b, c } = this.props
        return (
            <div>
                <h6>{a}</h6>
                <h6>{b}</h6>
                <h6>{c}</h6>
                <h6>{this.fab}</h6>
                <h6>{this.hbc}</h6>
                <h6>{this.gac}</h6>
                <h6>{this.uabc}</h6>
            </div>
        )
    }
}
```
对于第一种情况， 当组件ownProps（组件自身属性， 非redux传递）, 或者setState 的时候 都会执行计算。
对于第二钟情况， 当组件ownProps 变化的时候， 会执行计算。
而且这两种都违背了 我们的基本原则： **保持组件逻辑简单**

让数据逻辑离开组件！
```ts
// 可以写成函数式组件
class UnusedComp extends Component {
    render() {
        const { a, b, c, fab, hbc, gac, uabc } = this.props
        return (
            <div>
                <h6>{a}</h6>
                <h6>{b}</h6>
                <h6>{c}</h6>
                <h6>{fab}</h6>
                <h6>{hbc}</h6>
                <h6>{gac}</h6>
                <h6>{uabc}</h6>
            </div>
        )
    }
}
function mapStateToProps(state) {
    const {a, b, c} = state
    return {
        a,
        b,
        c,
        fab: f(a,b),
        hbc: h(b,c),
        gac: g(a,c),
        uabc: u(a, b, c)
    }
}
UnusedComp = connect(mapStateToProps)(UnusedComp)
```

组件很简单， 接收数据展示就可以了。 看似很美好！ 我们知道当store数据被改变的时候， 会通知所有connect的组件（前提是没被销毁）。
所有假设页面上还有 A， B， C三个组件， 这三个组件任意状态（存在redux的状态）的改变， 都会出发这里的 f, h, g, u的执行。。。听起来很扯！！！的确很扯！（在redner里面， willReciveProps里面计算是这里是不会引起函数执行的）。 但是这通常不是问题， 因为我们一般每个页面只有一个 容器组件 和redux交互， 其他子组件通过props的方式获取数据和action。 而且react-router在切换路由的时候， 是会销毁掉前一个路由的组件。 这样同一个时间只会有 一个 容器组件。

在考虑一种情况， 假设UnusedComp还有 x, y, z 状态属性， 存在redux。 这3个属性就是简单的3个值， 只用来展示。 可是当x， y， z改变的时候，也会触发计算。 这里发生的计算不管是在render里面计算， 还是willReciveProps, 还是mapStateToProps里 都无法避免。

#### 精确控制计算
仿佛我们依据找到了 方法：
1. redux只存基本状态
2. react-router + 单 容器组件 组件

现实很残酷！ 实际上x, y, z这种属性， 一定大量存在。 光是这一点就会导致大量的无效计算。 之前讨论的3种方式 （render， willRecive，mapStateToProps）无法避免这种计算。

另外mapStateToProps 还会被其他store的值改变影响 ，毕竟react-router + 单 容器组件 组件 这种组织方式只是最美好的情况。 我们有些业务就是处于性能的考虑，没有销毁之前路由的组件， 用我们自己的路由。有些页面也不是 单容器组件，尴尬！！

明显的， 我们是知道 x， y， z的变化是不需要计算的， 而a，b， c变化是需要计算的。 如何描述给程序呢？另外 mapStateToProps 这种方式还带来了好处， 我们在描述的时候，不会侵入组件！！。

最原始的描述：
```ts
let memoizeState = null
function mapStateToProps(state) {
    const {a, b, c} = state
    if (!memoizeState) { 
       memoizeState =  {
            a,
            b,
            c,
            fab: f(a,b),
            hbc: h(b,c),
            gac: g(a,c),
            uabc: u(a, b, c)
        }
    } else {
        if (!(a === memoizeState.a && b === memoizeState.b) ) {
            // f should invoke
            memoizeState.fab = f(a, b)
        }
        if (!(b === memoizeState.b && c === memoizeState.c) ) {
            // h should invoke
            memoizeState.hbc = h(b, c)
        }
        if (!(a === memoizeState.a && c === memoizeState.c) ) {
            // g should invoke
            memoizeState.gac = g(a, c)
        }
        if (!(a === memoizeState.a && b === memoizeState.b && c === memoizeState.c) ) {
            // u should invoke
            memoizeState.uabc = u(a, b, c)
        }
        memoizeState.a = a
        memoizeState.b = b
        memoizeState.c = c
    }
    
    return memoizeState
}
```
首选， 我们知道fab的值与a,b 有关， 所以当a, b 有变化的时候，f需要重新执行。 其他同理， 这样的话函数一定是只在必要的时候执行。

#### 使用reselect

reselect 解决了我们上面的那个问题， 我们也不必每次用这个最原始的描述了， 对应的reselect描述是这样的
```ts
import { createSelector } from 'reselect'

fSelector = createSelector(
    a => state.a,
    b => state.b,
    (a, b) => f(a, b)
)
hSelector = createSelector(
    b => state.b,
    c => state.c,
    (b, c) => h(b, c)
)
gSelector =  createSelector(
    a => state.a,
    c => state.c,
    (a, c) => g(a, c)
)
uSelector = createSelector(
    a => state.a,
    b => state.b,
    c => state.c,
    (a, b, c) => u(a, b, c)
)

...
function mapStateToProps(state) {
    const { a, b, c } = state
    return {
        a,
        b,
        c,
        fab: fSelector(state),
        hbc: hSelector(state),
        gac: gSelector(state),
        uabc: uSelector(state)
    }
}
```
在 createSelector 里面我们先定义了 input-selector 函数， 最后定义了 值是如何计算出来的。 selector保证了，当input-selector 返回结果相等的时候，不会计算。

#### 最后
如果 你是react-router 并且是 单 容器组件。 那么可能在 mapStateToProps里面计算，性能问题并不大。 而且性能不应该是我们第一要考虑的东西， 我们首先要考虑的是简单性，尤其是组件的简单性。 当我们的业务复杂到需要考虑性能的时候， reselect是我们不错的选择！

### 安装
```
npm install reselect
```

### 深入用法
#### 在React Props中接入Selectors
> selectors接收store的state作为一个参数,其实一个selector也可以接受props.在

这里是一个`App`组件,渲染出三个`VisibleTodoList`组件,每一个组件有`ListId`属性.
`components/App.js`
```ts
import React from 'react'
import Footer from './Footer'
import AddTodo from '../containers/AddTodo'
import VisibleTodoList from '../containers/VisibleTodoList'

const App = () => (
  <div>
    <VisibleTodoList listId="1" />
    <VisibleTodoList listId="2" />
    <VisibleTodoList listId="3" />
  </div>
)
```

每一个`VisibleTodoListcontainer`应该根据各自的`listId`属性获取`state`的不同部分.所以我们修改一下`getVisibilityFilter`和`getTodos`,便于接受一个属性参数

`selectors/todoSelectors.js`
```ts
import { createSelector } from 'reselect'

const getVisibilityFilter = (state, props) =>
  state.todoLists[props.listId].visibilityFilter

const getTodos = (state, props) =>
  state.todoLists[props.listId].todos //这里是为二维数组了

const getVisibleTodos = createSelector(
  [ getVisibilityFilter, getTodos ],
  (visibilityFilter, todos) => {
    switch (visibilityFilter) {
      case 'SHOW_COMPLETED':
        return todos.filter(todo => todo.completed)
      case 'SHOW_ACTIVE':
        return todos.filter(todo => !todo.completed)
      default:
        return todos
    }
  }
)

export default getVisibleTodos
```
`props`可以从`mapStateToProps`传递到`getVisibleTodos`：
```ts
const mapStateToProps = (state, props) => {
  return {
    todos: getVisibleTodos(state, props)
  }
}
```
**但是还有个问题**
当`getVisibleTodosselector`和`VisibleTodoListcontainer`的多个实例一起工作的时候,记忆功能就不能正常的运行:
`containers/VisibleTodoList.js`
```ts
import { connect } from 'react-redux'
import { toggleTodo } from '../actions'
import TodoList from '../components/TodoList'
import { getVisibleTodos } from '../selectors'

const mapStateToProps = (state, props) => {
  return {
    // WARNING: THE FOLLOWING SELECTOR DOES NOT CORRECTLY MEMOIZE
    //⚠️下面的selector不能正确的记忆
    todos: getVisibleTodos(state, props)
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    onTodoClick: (id) => {
      dispatch(toggleTodo(id))
    }
  }
}

const VisibleTodoList = connect(
  mapStateToProps,
  mapDispatchToProps
)(TodoList)

export default VisibleTodoList
```
使用`createSelector`创建的`selector`时候,如果他的参数集合和上一次的参数机会是一样的,仅仅返回缓存的值.如果我们交替渲染`<VisibleTodoList listId="1" />` 和`<VisibleTodoList listId="2" />`时,共享的`selector`将会交替接受`{listId：1}`和`{listId:2}`作为他的`props`的参数.这将会导致每一次调用的时候的参数都不同,因此selector每次都会重新来计算而不是返回缓存的值.下一部分我们将会介绍怎么解决这个问题.

#### 跨越多个组件使用selectors共性props
> 这一部分的实例需要React Redux v4.3.0或者更高版本的支持.

在多个`VisibleTodoList`组件中共享`selector`,同时还要保持记忆性,每一个组件的实例需要他们自己的`selector`备份.

现在让我们创建一个函数`makeGetVisibleTodos`,这个函数每次调用的时候返回一个新的`getVisibleTodos`的拷贝:
`selectors/todoSelectors.js`
```ts
import { createSelector } from 'reselect'

const getVisibilityFilter = (state, props) =>
  state.todoLists[props.listId].visibilityFilter

const getTodos = (state, props) =>
  state.todoLists[props.listId].todos

const makeGetVisibleTodos = () => {
  return createSelector(
    [ getVisibilityFilter, getTodos ],
    (visibilityFilter, todos) => {
      switch (visibilityFilter) {
        case 'SHOW_COMPLETED':
          return todos.filter(todo => todo.completed)
        case 'SHOW_ACTIVE':
          return todos.filter(todo => !todo.completed)
        default:
          return todos
      }
    }
  )
}

export default makeGetVisibleTodos
```
我们也需要设置给每一个组件的实例他们各自获取私有的selector方法.`mapStateToProps`的`connect`函数可以帮助完成这个功能.

**如果`mapStateToProps`提供给`connect`不返回一个对象而是一个函数,他就可以被用来为每个组件`container`创建一个私有的`mapStateProps`函数.**

在下面的实例中,`mapStateProps`创建一个新的`getVisibleTodosselector`,他返回一个`mapStateToProps`函数,这个函数能够接入新的`selector`.

如果我们把`makeMapStateToprops`传递到`connect`,每一个`visibleTodoListcontainer`将会获得各自的含有私有`getVisibleTodosselector`的`mapStateToProps`的函数.这样一来记忆就正常了,不管`VisibleTodoListcontainers`的渲染顺序怎么样.

```ts
import { connect } from 'react-redux'
import { toggleTodo } from '../actions'
import TodoList from '../components/TodoList'
import { makeGetVisibleTodos } from '../selectors'

const makeMapStateToProps = () => {
  const getVisibleTodos = makeGetVisibleTodos()
  const mapStateToProps = (state, props) => {
    return {
      todos: getVisibleTodos(state, props)
    }
  }
  return mapStateToProps
}

const mapDispatchToProps = (dispatch) => {
  return {
    onTodoClick: (id) => {
      dispatch(toggleTodo(id))
    }
  }
}

const VisibleTodoList = connect(
  makeMapStateToProps,
  mapDispatchToProps
)(TodoList)

export default VisibleTodoList
```

### API
#### createSelector(…inputSelectors|[inputSelectors],resultFunc)
接受一个或者多个selectors,或者一个selectors数组,计算他们的值并且作为参数传递给`resultFunc`.
`createSelector`通过判断`input-selector`之前调用和之后调用的返回值是否全等于，从而决定是否运行`resultFunc`,如果全等于则不运行`resultFunc`直接引用缓存,否则调用`resultFunc`.

经过`createSelector`创建的`Selectors`有一个缓存,大小是1.这意味着当一个`input-selector`变化的时候,他们总是会重新计算`state`,因为`Selector`仅仅存储每一个`input-selector`前一个值.
```ts
const mySelector = createSelector(
  state => state.values.value1,
  state => state.values.value2,
  (value1, value2) => value1 + value2
)

// You can also pass an array of selectors
//可以出传递一个selector数组
const totalSelector = createSelector(
  [
    state => state.values.value1,
    state => state.values.value2
  ],
  (value1, value2) => value1 + value2
)
```
在selector内部获取一个组件的props非常有用.当一个selector通过connect函数连接到一个组件上,组件的属性作为第二个参数传递给selector:
```ts
const abSelector = (state, props) => state.a * props.b

// props only (ignoring state argument)
const cSelector =  (_, props) => props.c

// state only (props argument omitted as not required)
const dSelector = state => state.d

const totalSelector = createSelector(
  abSelector,
  cSelector,
  dSelector,
  (ab, c, d) => ({
    total: ab + c + d
  })
)
```

#### defaultMemoize(func, equalityCheck = defaultEqualityCheck)
`defaultMemoize`能记住通过`func`传递的参数.这是`createSelector`使用的记忆函数.

`defaultMemoize` 通过调用`equalityCheck`函数来决定一个参数是否已经发生改变.因为`defaultMemoize`设计出来就是和`immutable`数据一起使用,默认的`equalityCheck`使用引用全等于来判断变化:
```ts
function defaultEqualityCheck(currentVal, previousVal) {
  return currentVal === previousVal
}
```
`defaultMemoize`和`createSelectorCreator`去配置`equalityCheck`函数.

#### createSelectorCreator(memoize,…memoizeOptions)
`createSelectorCreator`用来配置定制版本的`createSelector`.

`memoize`参数是一个有记忆功能的函数,来代替`defaultMemoize`.
`…memoizeOption`展开的参数是0或者更多的配置选项,这些参数传递给`memoize.func`
`selectors.resultFunc`作为第一个参数传递给`memoize`,`memoizeOptions`作为第二个参数:
```ts
const customSelectorCreator = createSelectorCreator(
  customMemoize, // function to be used to memoize resultFunc,记忆resultFunc
  option1, // option1 will be passed as second argument to customMemoize 第二个参数
  option2, // option2 will be passed as third argument to customMemoize 第三个参数
  option3 // option3 will be passed as fourth argument to customMemoize   第四个参数
)

const customSelector = customSelectorCreator(
  input1,
  input2,
  resultFunc // resultFunc will be passed as first argument to customMemoize  作为第一个参数传递给customMomize
)
```
在`customSelecotr`内部调用`memoize`的函数的代码如下:
```ts
customMemoize(resultFunc, option1, option2, option3)
```
下面是几个可能会用到的`createSelectorCreator`的实例:
**为`defaultMemoize`配置`equalityCheck`**
```ts
import { createSelectorCreator, defaultMemoize } from 'reselect'
import isEqual from 'lodash.isEqual'

// create a "selector creator" that uses lodash.isEqual instead of ===
const createDeepEqualSelector = createSelectorCreator(
  defaultMemoize,
  isEqual
)

// use the new "selector creator" to create a selector
const mySelector = createDeepEqualSelector(
  state => state.values.filter(val => val < 5),
  values => values.reduce((acc, val) => acc + val, 0)
)
```

**使用`loadsh`的`memoize`函数来缓存未绑定的缓存.**
```ts
import { createSelectorCreator } from 'reselect'
import memoize from 'lodash.memoize'

let called = 0
const hashFn = (...args) => args.reduce(
  (acc, val) => acc + '-' + JSON.stringify(val),
  ''
)
const customSelectorCreator = createSelectorCreator(memoize, hashFn)
const selector = customSelectorCreator(
  state => state.a,
  state => state.b,
  (a, b) => {
    called++
    return a + b
  }
)
```

#### createStructuredSelector({inputSelectors}, selectorCreator = createSelector)
如果在普通的模式下使用`createStructuredSelector`函数可以提升便利性.传递到`connect`的`selector`装饰者(这是js设计模式的概念,可以参考相关的书籍)接受他的`input-selectors`,并且在一个对象内映射到一个键上.

```ts
const mySelectorA = state => state.a
const mySelectorB = state => state.b

// The result function in the following selector
// is simply building an object from the input selectors 由selectors构建的一个对象
const structuredSelector = createSelector(
   mySelectorA,
   mySelectorB,
   mySelectorC,
   (a, b, c) => ({
     a,
     b,
     c
   })
)
```
`createStructuredSelector`接受一个对象,这个对象的属性是`input-selectors`,函数返回一个结构性的`selector`.这个结构性的`selector`返回一个对象,对象的键和`inputSelectors`的参数是相同的,但是使用`selectors`代替了其中的值.
```ts
const mySelectorA = state => state.a
const mySelectorB = state => state.b

const structuredSelector = createStructuredSelector({
  x: mySelectorA,
  y: mySelectorB
})

const result = structuredSelector({ a: 1, b: 2 }) // will produce { x: 1, y: 2 }
```
结构性的selectors可以是嵌套式的:
```ts
const nestedSelector = createStructuredSelector({
  subA: createStructuredSelector({
    selectorA,
    selectorB
  }),
  subB: createStructuredSelector({
    selectorC,
    selectorD
  })
})
```


### 原文链接
[ykforerlang](https://segmentfault.com/a/1190000011936772)
[smartphp](https://www.jianshu.com/p/6e38c66366cd)
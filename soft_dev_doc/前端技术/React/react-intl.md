# react-intl 国际化组件
[toc]
### 描述
> React Intl 用于国际化 React 组件，提供 React 组件和 API 来格式化日期，数字，字符串（包括单复数和翻译）

### 安装
`npm install react-intl --save`

### 用法
```typescript
// import { addLocaleData } from 'react-intl'
// import en from 'react-intl/locale-data/en'
// import zh from 'react-intl/locale-data/zh'
// addLocaleData([...en, ...zh]);  // 引入多语言环境的数据
// 两种方式都行一样的效果
const addLocaleData = require('react-intl').addLocaleData
const enLocaleData = require('react-intl/locale-data/en')
const zhLocaleData = require('react-intl/locale-data/zh')
addLocaleData(enLocaleData)
addLocaleData(zhLocaleData)
```

1. 然后在需要用到国际化的最外层新增 `<IntlProvider>` 组件包裹起来。一般整个项目都有需要用到国际化，所以一般都是放在项目结构中的最外层，包含整个软件项目。例如
```tsx
//app.js
import { AppContainer } from 'react-hot-loader'
import Main from './components/Main'
//... ...
const render = Component => {
    ReactDOM.render(
        <AppContainer>
            <IntlProvider>
                <Component />
            </IntlProvider>
        </AppContainer>,
        document.getElementById('app')
    )
}
```
2. 添加多语言对应文本。需要建立对应的 json 文件或 js 文件
```js
// en_US.js
const en_US = {
    hello: "Hello!"，
    //... ...
}
export default en_US;
```
```js
// zh_CN.js
const zh_CN = {
    hello: "你好！"，
    //... ...
}
export default zh_CN;
```
3. 然后引入这两个文件。
4. 全局配置当前的语言，和相对应的文本。即配置`<IntlProvider>`组件的两个属性locale和messages。例如：
```tsx
render(){
    let messages = {}
    messages['en'] = en_US;
    messages['zh'] = zh_CN;
    return (
        <IntlProvider locale={this.state.lang} messages={messages[this.state.lang]}>
            //··· ···
        </IntlProvider>
    )
}
```
5. 接下来，添加翻译的文本到页面中。
基本只需要使用到一个组件：<FormattedMessage>。这个组件默认生成一个<span>，内容是翻译后的文本，也就是 messages中对应字段的值。
在需要添加国际化文本的组件中，引入FormattedMessage组件。
```tsx
import { FormattedMessage  } from 'react-intl'; /* react-intl imports */
//... ...
<FormattedMessage id="hello" />
```
当前语言为`en`时，生成结果：
```js
<span>Hello!</span>
```
### 更进一步的用法
#### 文本中添加变量
```js
// en_US.js
const en_US = {
    helloSomeone: "Hello, {name}!"
}
// zh_CN.js
const zh_CN = {
    helloSomeone: "{name}，你好！"
}
```
```jsx
<FormattedMessage id="helloSomeone" values={{name:"Evelyn"}}/>
```

#### 自定义标签名
不生成<span>。比如生成 <p>。
```jsx
<FormattedMessage id="hello" tagName="p" />
```
#### 生成的文本中包含富文本
在messages中直接包含富文本无效，不会被解析。可以通过values传值时，加上富文本，比如：
```jsx
<FormattedMessage
  id="helloSomeone"
  tagName="div"
  values={{
    name:<p className="name">Evelyn</p>
  }} />
```
注意此处name不是字符串，而是 React 元素。结果为：
```html
<div>Hello, <p class="name">Evelyn</p>!</div>
```
#### 自定义生成的节点
比如，生成一个按钮：
```jsx
<FormattedMessage id='hello'>
    {(txt) => (
      <input type="button"
        className="btn-hello"
        onClick={this.handleClickHello.bind(this)}
        value={txt} />
    )}
</FormattedMessage>
```
`txt`对应`messages`中的文本。当语言为en时生成结果：
```html
<input type="button" class="btn-hello" value="Hello!">
```
此时再定义`tagName`属性是无效的。
### 感悟
&emsp;就从使用的角度上来考虑，这个组件底层原理应该时是使用了，部分高级 `React` 用法，其中感觉有 `Context` 的影子。

### 参考阅读
1. [Evelynzzz](https://www.jianshu.com/p/574f6cea4f26)
2. [Davinci 源码](https://github.com/edp963/davinci)
3. [React Intl](https://github.com/formatjs/react-intl/blob/master/docs/API.md)

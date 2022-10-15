### [Material UI](https://mui.com/)
> Material UI is a library of React UI components that implements Google's Material Design.
* 谷歌家出品的一个 `React` 组件库

### 原因
* 使用对比 [Ant Design](https://ant.design/index-cn)后发现, [Ant Design](https://ant.design/index-cn)会对样式造成污染, 并且内部样式也不好修改, 在引入时难以适配之前项目的 UI 风格, 体积也挺大的.

* 完善的文档, 可通过 `style-engine` + `styled-components` 快速定制不同的主题

### 安装使用
```shell
# 安装默认emotion样式
# npm
npm install @mui/material @emotion/react @emotion/styled
# yarn
yarn add @mui/material @emotion/react @emotion/styled

# `style-engine` + `styled-components` 定制主题
# npm
npm install @mui/material @mui/styled-engine-sc styled-components
# yarn
yarn add @mui/material @mui/styled-engine-sc styled-components
```
* 我这里使用的是后者, 为了后期可以随意更改主题风格, 所预留缺口
* 因为这里我开发工具使用的是vite, 在@mui内部的包引用的 `@mui/styled-engine` 这个路径, 所以在打包编译时时需要打包编译工具修改路径映射
```js
// https://vitejs.dev/config/
export default defineConfig({
  resolve: {
    alias: {
      '@mui/styled-engine': '@mui/styled-engine-sc',
    },
  },
  plugins: [react()]
})
```
* typescript 项目也需要修改路径映射
```json5
{
   "compilerOptions": {
+    "paths": {
+      "@mui/styled-engine": ["./node_modules/@mui/styled-engine-sc"]
+    }
   },
 }
```
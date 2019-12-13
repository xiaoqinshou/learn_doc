## Dva + TypeScript + Antd + React 项目创建

### 安装 dva-cli
>dva-cli 是基于 roadhog工具上而封装出来的轻量级应用框架
```powershell
$ npm install dva-cli -g
$ dva -v
dva-cli version 0.9.1
```

### 创建新应用
```
$ dva new dva-quickstart
```

### 使用 Antd 
通过 `npm` 安装 `antd` 和 `babel-plugin-import` 。`babel-plugin-import` 是用来按需加载 `antd` 的脚本和样式.
```
$ npm install antd babel-plugin-import --save
```
编辑 `.webpackrc`，使 `babel-plugin-import` 插件生效。
```git
{
+  "extraBabelPlugins": [
+    ["import", { "libraryName": "antd", "libraryDirectory": "es", "style": "css" }]
+  ]
}
```

### 引入React, ES, TypeScript, umi-request请求工具等
> 大多开发工具都引用于 `umi` 框架中
```json
"dependencies": {
    "umi-request": "^1.2.6",
    "antd": "^3.19.5",
    "react": "^16.8.6",
    "react-dom": "^16.8.6",
    "dva": "^2.4.1"
  },
  "devDependencies": {
    "@types/classnames": "^2.2.7",
    "@types/jest": "^23.3.12",
    "@types/lodash": "^4.14.139",
    "@types/react": "^16.9.16",
    "@types/react-dom": "^16.9.4",
    "@types/react-grid-layout": "^0.16.7",
    "@types/react-test-renderer": "^16.0.3",
    "@types/reactcss": "^1.2.3",
    "babel-eslint": "^9.0.0",
    "babel-plugin-dva-hmr": "^0.3.2",
    "babel-plugin-import": "^1.13.0",
    "eslint": "^5.4.0",
    "eslint-config-umi": "^1.4.0",
    "eslint-plugin-flowtype": "^2.50.0",
    "eslint-plugin-import": "^2.14.0",
    "eslint-plugin-jsx-a11y": "^5.1.1",
    "eslint-plugin-react": "^7.11.1",
    "husky": "^0.14.3",
    "less": "^3.10.3",
    "less-loader": "^5.0.0",
    "lint-staged": "^7.2.2",
    "react-test-renderer": "^16.7.0",
    "redbox-react": "^1.4.3",
    "roadhog": "^2.5.0-beta.4",
    "ts-lint": "^4.5.1",
    "ts-loader": "^6.2.1",
    "tslint": "^5.12.0",
    "tslint-eslint-rules": "^5.4.0",
    "tslint-react": "^3.6.0"
  }
```
### 将原有的JS文件 改为TypeScript 文件
在随意一个文件夹下添加一个页面：
```typescript
import * as React from 'react';

export interface IAppProps {
  name?: string;
};

const Home: React.SFC<IAppProps> = (props: IAppProps): JSX.Element => {
  return (
    <div>
      <h1>
        Hello {props.name ? props.name : "123"}
      </h1>
    </div>
  );
};

export default Home;
```
src根目录下，分别添加 `router.tsx` 路由注册文件，以及 `index.tsx` 项目入口文件
```typescript
export default function RouterConfig({history}: RouterProps) {
  return (
    <Router history={history}>
      <Switch>
        <Route path="/" exact component={Home}/>
      </Switch>
    </Router>
  );
}
```
```typescript
const app = dva({
  history: createhistory(),
});
app.model(require('./models/modelname').default);
app.router( require('./router').default );

app.start('#root');
```

### 配置非相对路径
在根目录下新建 `webpack.config.js` 文件，加入地址映射：
```javascript
module.exports = (webpackConfig, env) => {
  webpackConfig.resolve.alias = {
    '@': `${__dirname}/src`,
  };
  return webpackConfig
};
```

### tsconfig.json ts相关配置
> 引用于 `umi` 框架中
```json
{
  "compilerOptions": {
    "target": "esnext",
    "module": "esnext",
    "moduleResolution": "node",
    "importHelpers": true,
    "jsx": "react",
    "esModuleInterop": true,
    "sourceMap": true,
    "baseUrl": ".",
    "strict": true,
    "paths": {
      "@/*": ["src/*"]
    },
    "allowSyntheticDefaultImports": true
  },
  "exclude": [
    "node_modules",
    "lib",
    "es"
  ]
}
```


### tslint.json
> 引用于 `umi` 框架中的文件
```
{
  "extends": [
    "tslint:latest",
    "tslint-react"
  ],
  "linterOptions": {
    "exclude": [
      "node_modules/**/*.ts",
      "src/**/*.{ts,tsx}"
    ]
  },
  "rules": {
    "object-literal-sort-keys": false,
    "jsx-no-lambda": false,
    "eofline": false,
    "no-consecutive-blank-lines": false,
    "no-var-requires": false,
    "quotemark": false,
    "space-within-parents": false,
    "no-submodule-imports": false,
    "no-implicit-dependencies": false,
    "ordered-imports": [ false ],
    "jsx-boolean-value": false,
    "no-trailing-whitespace": false,
    "semicolon": false,
    "trailing-comma": false,
    "space-in-parents": false,
    "typedef-whitespace": [ false ],
    "whitespace": [ true ],
    "interface-over-type-literal": true,
    "no-duplicate-imports": false,
    "no-namespace": true,
    "no-internal-module": true
  }
}
```
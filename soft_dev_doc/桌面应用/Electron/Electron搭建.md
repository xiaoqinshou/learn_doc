# Electron 程序搭建
[官方文档](http://www.electronjs.org/docs)
## 安装 node,npm
[点此安装](https://nodejs.org/en/download/)

## 初始化项目
```shell
$ npm init

This utility will walk you through creating a package.json file.
It only covers the most common items, and tries to guess sensible defaults.

See `npm help json` for definitive documentation on these fields
and exactly what they do.

Use `npm install <pkg>` afterwards to install a package and
save it as a dependency in the package.json file.

Press ^C at any time to quit.
package name: (uml2mdtool)
version: (1.0.0)
description: uml转换markdown
entry point: (index.js) main.js
test command:
git repository:
keywords:
author: lance
license: (ISC)
About to write to E:\Code\MyProject\uml2mdTool\package.json:

{
  "name": "uml2mdtool",
  "version": "1.0.0",
  "description": "uml转换markdown",
  "main": "main.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "author": "lance",
  "license": "ISC"
}


Is this OK? (yes) yes
```

### 添加启动命令
* package.json
```json
# electron 程序
{
  "scripts": {
    "start": "electron ."
  }
}
```

### 安装 Electron
```shell
npm install --save-dev electron

# 国内用户还是乖乖进 淘宝镜像吧
# 下载 cnpm
npm install -g cnpm --registry=https://registry.npm.taobao.org

# cnpm 下载electron
cnpm install --save-dev electron
```

## Hello World
* main.js
```js
const { app, BrowserWindow } = require('electron')

function createWindow () {
  // 创建浏览器窗口
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: true
    }
  })

  // 并且为你的应用加载index.html
  win.loadFile('index.html')

  // 打开开发者工具
  win.webContents.openDevTools()
}

// Electron会在初始化完成并且准备好创建浏览器窗口时调用这个方法
// 部分 API 在 ready 事件触发后才能使用。
app.whenReady().then(createWindow)

//当所有窗口都被关闭后退出
app.on('window-all-closed', () => {
  // 在 macOS 上，除非用户用 Cmd + Q 确定地退出，
  // 否则绝大部分应用及其菜单栏会保持激活。
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', () => {
  // 在macOS上，当单击dock图标并且没有其他窗口打开时，
  // 通常在应用程序中重新创建一个窗口。
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow()
  }
})

// 您可以把应用程序其他的流程写在在此文件中
// 代码 也可以拆分成几个文件，然后用 require 导入。
```

* index.html
```html
<html>
  <head>
    <meta charset="UTF-8">
    <title>Hello World!</title>
    <!-- https://electronjs.org/docs/tutorial/security#csp-meta-tag -->
    <meta http-equiv="Content-Security-Policy" content="script-src 'self' 'unsafe-inline';" />
  </head>
  <body>
    <h1>Hello World!</h1>
    We are using node <script>document.write(process.versions.node)</script>,
    Chrome <script>document.write(process.versions.chrome)</script>,
    and Electron <script>document.write(process.versions.electron)</script>.
  </body>
</html>
```

然后运行即可。

## 总结
&emsp;本来是想做个桌面应用工具，给自己使用。但是好久没写JS，代码和普通的 HTML 代码了没写起来有点不习惯，这篇文章就到这。打算按照自己的技术栈，整理一个 `Electron` + `React` + `React-redux` + `TypeScript` 的脚手架出来。
这样用起来也比较得心应手。

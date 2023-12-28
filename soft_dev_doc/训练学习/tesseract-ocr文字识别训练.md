## 前言
* 业务需求需要使用文字识别，识别图片营业执照上的信息实现自动填充
* 由于不想使用后端算力实现识别功能，顾采用tesseract这个框架，提供了javascript的实现，天生可以放在浏览器客户端进行识别。

### tesseract-ocr
* [官方文档](https://tesseract-ocr.github.io/tessdoc/#introduction)
* 由于自带的 `tesseract.js` 没经过训练，对特定场景下的文字识别效果不是很好，所以需要`tessract-ocr`进行训练，将训练调优后的参数给`tesseract.js`进行使用。

### 小插曲
* 由于我本地安装 `homebrew` 的是国内的镜像源，一堆毛病，所以推荐直接装源镜像，一次到位。删除切换真的很痛苦，使用 `brew` 安装过的东西都得重新安装一遍了。
* 说三遍：
* 千万别碰国内源，水很深，普通人把握不住
* 千万别装国内源，水很深，普通人把握不住
* 千万别用国内源，水很深，普通人把握不住

1. 删除重新安装 homebrew
```shell
# 删除
$ sudo rm -rf /opt/homebrew
# 安装，使用github官方源，需翻墙
$ /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"
# 重新添加 taps
$ brew tap homebrew/core
$ brew tap homebrew/cask
$ brew tap homebrew/services
# 更新 
$ brew update
```
2. 官方版本真的很让人安心, 安装时一路畅通无阻

### 安装
* 初始不自带macos版本，所以得自己编译安装
* [编译安装原文档](https://tesseract-ocr.github.io/tessdoc/Compiling.html)
* 官方提供了三种编译方式，`Fink`（已弃用）、`MacPorts`、`Homebrew`。
* 我系统安装了 `Homebrew`,所以直接使用它编译安装
#### Homebrew 编译安装
1. 安装依赖
```sh
# Packages which are always needed.
$ brew install automake autoconf libtool
$ brew install pkgconfig
# icu4c 是 keg-only 状态
$ brew install icu4c
$ brew install leptonica
# Packages required for training tools.
$ brew install pango
# Optional packages for extra features.
# libarchive 是 keg-only 状态
$ brew install libarchive
# Optional package for builds using g++.
$ brew install gcc
```
* [keg-only](https://zhuanlan.zhihu.com/p/196667957) 放在内部环境使用，不会干扰原生系统所提供的包

2. 编译
```sh
git clone https://github.com/tesseract-ocr/tesseract/
cd tesseract
./autogen.sh
mkdir build
cd build
# Optionally add CXX=g++-8 to the configure command if you really want to use a different compiler.
../configure PKG_CONFIG_PATH=/usr/local/opt/icu4c/lib/pkgconfig:/usr/local/opt/libarchive/lib/pkgconfig:/usr/local/opt/libffi/lib/pkgconfig
make -j
# Optionally install Tesseract.
sudo make install

# 清空编译 安装训练工具
# Optionally build and install training tools.
# 回到上级目录
cd ..
./autogen.sh
mkdir training
cd training
../configure
make training
sudo make training-install
```

#### 直接安装
* 一开始不清楚我用的是编译安装的 dev 版
```sh
$ brew install tesseract
```

#### 安装jTessBoxEditorFX
* 用于辅助 tesseract-ocr 训练的可视化工具，使用javaFx开发的，天生可跨平台。
* [jTessBoxEditorFX Release](https://github.com/nguyenq/jTessBoxEditorFX/releases)
* 由于用了JAVAFX开发，所以得安装 [JAVAFX](https://gluonhq.com/products/javafx/)
```sh
# 修改启动命令，将安装的JAVAFX 放入启动命令中
$ cd yourpath/jTessBoxEditorFX
$ vi train
# 加入下面这行
$ ADDJAVAFX="--module-path /Users/lanceyang/macos-soft/javafx-sdk-21.0.1/lib --add-modules javafx.controls,javafx.fxml,javafx.web"
# 把引入javaFx的命令，加入到最终的启动命令中，保存退出
$ $JAVACMD -Xms128m -Xmx1024m $ADDJAVAFX -jar "$PROGDIR/$PROGRAM.jar" $@
# 赋予执行权限。
$ chmod 755
# 启动即可
$ ./train
```
* 该训练之能训练字体识别的成功率，可以训练不同的字体进行识别
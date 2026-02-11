
## 安装jupyter

Jupyter Notebook是一个开源的web应用程序,一个交互式笔记本，支持运行 40 多种编程语言。
#### 安装
```sh
# 安装
pip3 install jupyter
# 帮助
jupyter notebook -h
# 生成默认配置
jupyter notebook --generate-config
# 启动
jupyter notebook
```

### Jupyter Notebook代码提示/自动补全/代码格式化
* 安装 nbextensions
```sh
pip install jupyter_contrib_nbextensions -i https://pypi.mirrors.ustc.edu.cn/simple

jupyter contrib nbextension install --user

pip install jupyter_nbextensions_configurator

jupyter nbextensions_configurator enable --user

```

* 安装autopep8
```sh
pip install autopep8
```
* 进入jupyter notebook 找到Nbextensions 勾选Hinterland,autopep8两项
* 测试,可以代码补全和代码格式化

## 安装jupyterlab
### homebrew 安装
```sh
# 默认安装最新版，目前是4.0.9
$ brew install jupyterlab
# 成功日志，这写日志很重要
==> Pouring jupyterlab--4.0.9_2.arm64_sonoma.bottle.tar.gz
==> Caveats
Additional kernels can be installed into the shared jupyter directory
  /opt/homebrew/etc/jupyter

zsh completions have been installed to:
  /opt/homebrew/share/zsh/site-functions
==> Summary
🍺  /opt/homebrew/Cellar/jupyterlab/4.0.9_2: 4,460 files, 88.8MB
==> Running `brew cleanup jupyterlab`...
Disable this behaviour by setting HOMEBREW_NO_INSTALL_CLEANUP.
Hide these hints with HOMEBREW_NO_ENV_HINTS (see `man brew`).
# 启动
$ jupyter lab
...
2023-12-28 16:48:25,677 CST - ERROR - pylsp_ruff.plugin - Error running ruff: /opt/homebrew/Cellar/jupyterlab/4.0.9_2/libexec/bin/python: No module named ruff
...
# 可以看到实际使用的是内部python在独立运行jupyter相关程序
```
* 由于使用homebrew安装是独立环境python 也是内置的，所以使用全局的pip3再安装各种插件它都无法自动集成

### 中文包
* 为了不浪费时间，我还是装了个中文包
```sh
# 安装到指定路径下 /opt/homebrew/Cellar/jupyterlab/4.0.9_2/libexec/lib/python3.12/site-packages 这个地方才是jupyter全部的依赖
$ pip3 install jupyterlab_language_pack_zh_CN -t /opt/homebrew/Cellar/jupyterlab/4.0.9_2/libexec/lib/python3.12/site-packages
# 再重启 jupyterlab 就好了
```

### 多内核环境隔离
#### 机器学习环境
```sh
# 查看 brew 安装的jupyterlab 所依赖的 python 环境
# 打开依赖命令
$ cd /opt/homebrew/Cellar/jupyterlab/4.0.9_2/libexec/bin
% ls -l python*
lrwxr-xr-x  1 lance  admin  87  2  2  2020 python -> ../../../../../opt/python@3.12/Frameworks/Python.framework/Versions/3.12/bin/python3.12
lrwxr-xr-x  1 lance  admin  87  2  2  2020 python3 -> ../../../../../opt/python@3.12/Frameworks/Python.framework/Versions/3.12/bin/python3.12
lrwxr-xr-x  1 lance  admin  87  2  2  2020 python3.12 -> ../../../../../opt/python@3.12/Frameworks/Python.framework/Versions/3.12/bin/python3.12
# 全都指向了，依赖的python环境，
# 同时也是自带的 python@3.12 brew 默认安装好，直接使用即可
% python3.12 --version
Python 3.12.1
# 所以接下来需要用 python3.12 去创建内核
# 查看 python 环境
# 创建共享内核文件
$ mkdir /opt/homebrew/etc/jupyter
# 查看内核环境
$ jupyter kernelspec list
# 安装多环境管理工具
$ brew install pyenv
# 查看配置pyenv方式
$ pyenv init 
# 查看安装版本
% pyenv install -l
# 装个 anaconda3-2023.09-0
% pyenv install anaconda3-2023.09-0
# 查看安装环境
% pyenv versions                   
* system (set by /Users/lance/.pyenv/version)
  anaconda3-2023.09-0
# 激活环境，是系统
% pyenv version 
system (set by /Users/lance/.pyenv/version)
# 将之前安装的环境添加到内核中
# 进入 anaconda3-2023.09-0 的 pip
% cd /Users/lance/.pyenv/versions/anaconda3-2023.09-0/bin
# 使用对应环境的pip3进行下载 ipykernel
% ./pip3 install ipykernel
# 他其实自带的，只是我没找到
# 同理使用anaconda3-2023.09-0的python3 
# 将该python内核添加进jupyterlab中
# 先查看虚拟环境列表
% ./conda env list 
# conda environments:
#
base                     /Users/lance/.pyenv/versions/anaconda3-2023.09-0
# 再将基础环境列表加入到jupyterlab中
# 安装到对应环境的自己下面
% ./python3 -m ipykernel install --name=base --user --display-name "机器学习环境"
# 安装成功
% jupyter kernelspec list
0.00s - Debugger warning: It seems that frozen modules are being used, which may
0.00s - make the debugger miss breakpoints. Please pass -Xfrozen_modules=off
0.00s - to python to disable frozen modules.
0.00s - Note: Debugging will proceed. Set PYDEVD_DISABLE_FILE_VALIDATION=1 to disable this validation.
Available kernels:
  python3    /opt/homebrew/Cellar/jupyterlab/4.0.9_2/libexec/lib/python3.12/site-packages/ipykernel/resources
  base       /Users/lance/Library/Jupyter/kernels/base
# 启动jupytelab即可
% jupyter lab
```

#### 基础环境
```sh
% pyenv install -l
# 装个 3.12.1
% pyenv install 3.12.1
# 调整全局
% pyenv global 3.12.1
# 使用对应环境的pip3进行下载 ipykernel
% pip3 install ipykernel
# 再将基础环境列表加入到jupyterlab中
# 安装到对应环境的自己下面
% python3 -m ipykernel install --user --display-name "基础环境"
# 安装成功,同时挤掉了，jupyter所使用的环境
 % jupyter kernelspec list
0.00s - Debugger warning: It seems that frozen modules are being used, which may
0.00s - make the debugger miss breakpoints. Please pass -Xfrozen_modules=off
0.00s - to python to disable frozen modules.
0.00s - Note: Debugging will proceed. Set PYDEVD_DISABLE_FILE_VALIDATION=1 to disable this validation.
Available kernels:
  base       /Users/lance/Library/Jupyter/kernels/base
  python3    /Users/lance/Library/Jupyter/kernels/python3
# 启动jupytelab即可
% jupyter lab
```

#### 带Tkinter环境
```sh
# 1. 安装 tcl-tk
brew install tcl-tk 
# 2. 查看安装路径
brew info tcl-tk
==> tcl-tk: stable 9.0.1 (bottled)
Tool Command Language
https://www.tcl-lang.org
Conflicts with:
  page (because both install `page` binaries)
  the_platinum_searcher (because both install `pt` binaries)
Installed
/opt/homebrew/Cellar/tcl-tk/9.0.1 (3,150 files, 38MB) *
  Poured from bottle using the formulae.brew.sh API on 2025-02-05 at 18:30:05
From: https://github.com/Homebrew/homebrew-core/blob/HEAD/Formula/t/tcl-tk.rb
License: TCL
==> Dependencies
Required: libtommath ✔, openssl@3 ✔
==> Caveats
The sqlite3_analyzer binary is in the `sqlite-analyzer` formula.
==> Analytics
install: 31,789 (30 days), 121,959 (90 days), 430,605 (365 days)
install-on-request: 18,097 (30 days), 70,628 (90 days), 186,581 (365 days)
build-error: 35 (30 days)

# 安装新的python 环境并链接tk

PYTHON_CONFIGURE_OPTS="--with-tcltk-includes=$(brew --prefix tcl-tk)/include --with-tcltk-libs=$(brew --prefix tcl-tk)/lib" \
pyenv install 3.13.1  # 你可以替换成想用的 Python 版本

pyenv versions
  system
  3.10.13
* 3.12.1 (set by /Users/lance/.pyenv/version)
  3.13.1
  anaconda3-2023.09-0

# 进入安装的新版本
cd /Users/lance/.pyenv/versions/3.13.1/bin

# 使用对应环境的pip3进行下载 ipykernel
% ./pip3 install ipykernel

# 再将基础环境列表加入到jupyterlab中
# 安装到对应环境的自己下面
% ./python3 -m ipykernel install --name=python-tk --user --display-name "tk环境"
# 安装成功
% jupyter kernelspec list
```

### pyenv 常用指令
* 借鉴[知乎-青灯抽丝](https://zhuanlan.zhihu.com/p/664786383)
```sh
# 查看安装包 
pyenv install --list
pyenv install -l

# 安装指定版本，抄查出来的即可
pyenv install <version>
pyenv install 3.11.1

# 查看已安装的Python版本
pyenv versions
# 显示当前活动的Python版本
pyenv version

# 卸载指定版本，抄已安装的即可
pyenv uninstall <version>
pyenv uninstall 3.11.1

# 以下用的比较少
# 优先级 shell > local > global
# 主要搭配jupyterlab做内核隔离，几乎不需要用到版本切换
# 设置 全局的Python版本
pyenv global <version>
# 查看 全局的Python版本设置
pyenv global

# 设置 当前目录下的Python版本
pyenv local <version>
# 显示 当前目录下的Python版本设置
pyenv local
# 取消 当前目录下的Python版本设置
pyenv local --unset

# 设置 当前Shell会话的Python版本
pyenv shell <version>
# 查看 当前Shell会话的Python版本设置
pyenv shell
# 取消 当前Shell会话的Python版本设置
pyenv shell --unset
```
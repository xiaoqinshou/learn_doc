
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

## Jupyter Notebook代码提示/自动补全/代码格式化
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

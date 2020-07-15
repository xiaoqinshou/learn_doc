# 403 forbidden

```shell
# 打开路gitlab auth 日志， 并找到被封 IP
$ cd /var/log/gitlab/gitlab-rails
$ tail -F auth.log
{"severity":"ERROR","time":"2020-07-02T03:00:04.794Z","correlation_id":null,"message":"Rack_Attack","env":"blacklist","remote_ip":"0.0.0.0","request_method":"GET","path":"/users/sign_in"}
{"severity":"ERROR","time":"2020-07-02T03:00:04.850Z","correlation_id":null,"message":"Rack_Attack","env":"blacklist","remote_ip":"0.0.0.0","request_method":"GET","path":"/favicon.ico"}

# 修改配置文件 添加被封IP为白名单
$ vi /etc/gitlab/gitlab.rb

# 重新刷新配置文件或重启
$ gitlab-ctl reconfigure
$ gitlab-ctl restart
```

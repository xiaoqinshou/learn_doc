## 坑

```ts
private buildTimingScript = (task: Function, sharedUri: string, delay: number) => `
  self.onmessage = function(event) {
    var sharedWorker = new SharedWorker('${sharedUri}');
    sharedWorker.port.onmessage = function(e) {
      console.log(e, 'Message received from DaemonWorker');
      self.postMessage(e.data + \`. this worker (${task}) is close\`)
      return close()
    }
    sharedWorker.port.postMessage([${delay}])
    const args = event.data.message.args
    if (args) {
      self.postMessage((${task}).apply(null, args))
      return close()
    }
    self.postMessage((${task})())
    return close()
  }`

private buildTimingScript = (task: Function, sharedUri: string, delay: number) => `
  self.onmessage = function(event) {
    var sharedWorker = new Worker('${sharedUri}');
    sharedWorker.onmessage = function(e) {
      console.log(e, 'Message received from DaemonWorker');
      self.postMessage(e.data + \`. this worker (${task}) is close\`)
      sharedWorker.terminate()
      return close()
    }
    sharedWorker.postMessage([${delay}])
    const args = event.data.message.args
    if (args) {
      self.postMessage((${task}).apply(null, args))
      return close()
    }
    self.postMessage((${task})())
    return close()
  }`
```
1. 专用worker中无法访问到, SharedWorker, 以及window, 虽然文档中有写, 但是还是试了一下
2. 专用worker中虽然能生成子worker, 但是收到onmessage消息还是无法及时触发, 会埋在task任务之后, 所以监听计时无法及时回调关闭该线程

* 总结: 虽说是worker其实每个worker内部其实还是js那套容器的单线程
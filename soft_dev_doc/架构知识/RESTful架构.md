# RESTful 架构
## 前言
&emsp;整理一下 `RESTful` 架构的由来，以及整体规范。因为发现经过一段时间工作发现，周围的大多同事对 `RESTful` 架构的认知，就仅仅只是一个名称作为 `RESTful` 的名词而已，大致在与他们的沟通中了解到的 `RESTful` 也就只是提供服务暴露接口供其他用户，或者前端请求进行访问，并没有遵循任何属于 `RESTful` 的规范(没有使用响应HTTP的状态码，路径，接口参数随性而定)。
&emsp;所以，才想整理一下 `RESTful` 架构，给自己加深影响，别被带坑了。同时也略微希望能推动公司，代码质量，以及采用 `RESTful` 架构上的规范化。

## 起源
&emsp;REST 这个词，是[Roy Thomas Fielding](http://en.wikipedia.org/wiki/Roy_Fielding)在他2000年的[博士论文](https://www.ics.uci.edu/~fielding/pubs/dissertation/top.htm)中提出的。
&emsp;`Fielding`是一个非常重要的人，他是HTTP协议（1.0版和1.1版）的主要设计者、`Apache`服务器软件的作者之一、`Apache`基金会的第一任主席。所以，他的这篇论文一经发表，就引起了关注，并且立即对互联网开发产生了深远的影响。
> "本文研究计算机科学两大前沿----软件和网络----的交叉点。长期以来，软件研究主要关注软件设计的分类、设计方法的演化，很少客观地评估不同的设计选择对系统行为的影响。而相反地，网络研究主要关注系统之间通信行为的细节、如何改进特定通信机制的表现，常常忽视了一个事实，那就是改变应用程序的互动风格比改变互动协议，对整体表现有更大的影响。我这篇文章的写作目的，就是想在符合架构原理的前提下，理解和评估以网络为基础的应用软件的架构设计，得到一个功能强、性能好、适宜通信的架构。"
(This dissertation explores a junction on the frontiers of two research disciplines in computer science: software and networking. Software research has long been concerned with the categorization of software designs and the development of design methodologies, but has rarely been able to objectively evaluate the impact of various design choices on system behavior. Networking research, in contrast, is focused on the details of generic communication behavior between systems and improving the performance of particular communication techniques, often ignoring the fact that changing the interaction style of an application can have more impact on performance than the communication protocols used for that interaction. My work is motivated by the desire to understand and evaluate the architectural design of network-based application software through principled use of architectural constraints, thereby obtaining the functional, performance, and social properties desired of an architecture. )

## 名称
&emsp;Fielding将他对互联网软件的架构原则，定名为`REST`，即`Representational State Transfer`(表现层状态转化)的缩写。

&emsp;如果一个架构符合`REST`原则，就称它为`RESTful`架构。

&emsp;**要理解RESTful架构，最好的方法就是去理解Representational State Transfer这个词组到底是什么意思，它的每一个词代表了什么涵义。** 如果你把这个名称搞懂了，也就不难体会`REST`是一种什么样的设计。

## 资源（Resources）
&emsp;`REST`的名称"表现层状态转化"中，省略了主语。"表现层"其实指的是"资源"（Resources）的"表现层"。

**所谓"资源"，就是网络上的一个实体，或者说是网络上的一个具体信息。** 它可以是一段文本、一张图片、一首歌曲、一种服务，总之就是一个具体的实在。你可以用一个`URI`（统一资源定位符）指向它，每种资源对应一个特定的`URI`。要获取这个资源，访问它的`URI`就可以，因此`URI`就成了每一个资源的地址或独一无二的识别符。

所谓"上网"，就是与互联网上一系列的"资源"互动，调用它的URI。

## 表现层（Representation）
"资源"是一种信息实体，它可以有多种外在表现形式。**我们把"资源"具体呈现出来的形式，叫做它的"表现层"（Representation）。**

比如，文本可以用txt格式表现，也可以用`HTML`格式、`XML`格式、`JSON`格式表现，甚至可以采用二进制格式；图片可以用`JPG`格式表现，也可以用`PNG`格式表现。

`URI`只代表资源的实体，不代表它的形式。严格地说，有些网址最后的".html"后缀名是不必要的，因为这个后缀名表示格式，属于"表现层"范畴，而`URI`应该只代表"资源"的位置。它的具体表现形式，应该在`HTTP`请求的头信息中用`Accept`和`Content-Type`字段指定，这两个字段才是对"表现层"的描述。

## 状态转化（State Transfer）

访问一个网站，就代表了客户端和服务器的一个互动过程。在这个过程中，势必涉及到数据和状态的变化。

互联网通信协议HTTP协议，是一个无状态协议。这意味着，所有的状态都保存在服务器端。因此，如果客户端想要操作服务器，必须通过某种手段，让服务器端发生"状态转化"（State Transfer）。而这种转化是建立在表现层之上的，所以就是"表现层状态转化"。

客户端用到的手段，只能是HTTP协议。具体来说，就是HTTP协议里面，四个表示操作方式的动词：`GET、POST、PUT、DELETE`。它们分别对应四种基本操作：`GET`用来获取资源，`POST`用来新建资源（也可以用于更新资源），`PUT`用来更新资源，`DELETE`用来删除资源。

## 综述

综合上面的解释，我们总结一下什么是`RESTful`架构：
1. 每一个`URI`代表一种资源；
2. 客户端和服务器之间，传递这种资源的某种表现层；
3. 客户端通过四个`HTTP`动词，对服务器端资源进行操作，实现"表现层状态转化"。

## 误区

`RESTful`架构有一些典型的设计误区。

最常见的一种设计错误，就是`URI`包含动词。因为"资源"表示一种实体，所以应该是名词，`URI`不应该有动词，动词应该放在`HTTP`协议中。

举例来说，某个`URI`是`/posts/show/1`，其中`show`是动词，这个`URI`就设计错了，正确的写法应该是`/posts/1`，然后用`GET`方法表示`show`。

如果某些动作是`HTTP`动词表示不了的，你就应该把动作做成一种资源。比如网上汇款，从账户1向账户2汇款500元，错误的URI是：
```http
POST /accounts/1/transfer/500/to/2 HTTP/1.1
```
正确的写法是把动词`transfer`改成名词`transaction`，资源不能是动词，但是可以是一种服务：
```http
POST /transaction HTTP/1.1
Host: 127.0.0.1
　　
from=1&to=2&amount=500.00
```
另一个设计误区，就是在URI中加入版本号：
```http
http://www.example.com/app/1.0/foo

http://www.example.com/app/1.1/foo

http://www.example.com/app/2.0/foo
```
因为不同的版本，可以理解成同一种资源的不同表现形式，所以应该采用同一个`URI`。版本号可以在`HTTP`请求头信息的`Accept`字段中进行区分（参见Versioning REST Services）：
```http
Accept: vnd.example-com.foo+json; version=1.0

Accept: vnd.example-com.foo+json; version=1.1

Accept: vnd.example-com.foo+json; version=2.0
```

# REST四个基本原则
1. 使用HTTP动词：GET POST PUT DELETE；
2. 无状态连接，服务器端不应保存过多上下文状态，即每个请求都是独立的；
3. 为每个资源设置URI；
4. 通过XML JSON进行数据传递。

实现上述原则的架构即可称为RESTFul架构。
1. 互联网环境下，任何应用的架构和API可以被快速理解；
2. 分布式环境下，任何请求都可以被发送到任意服务器；
3. 异构环境下，任何资源的访问和使用方式都统一。

# RESTful API 设计指南
## 协议
API与用户的通信协议，总是使用HTTPs协议。
可略微降低为HTTP协议

## 域名
应该尽量将API部署在专用域名之下。
```http
https://api.example.com
```
如果确定API很简单，不会有进一步扩展，可以考虑放在主域名下。
```http
https://example.org/api/
```

## 版本（Versioning）
应该将API的版本号放入URL。
```http
https://api.example.com/v1/
```
另一种做法是，将版本号放在HTTP头信息中，但不如放入URL方便和直观。Github采用这种做法。

## 路径（Endpoint）
路径又称"终点"（endpoint），表示API的具体网址。

在RESTful架构中，每个网址代表一种资源（resource），所以网址中不能有动词，只能有名词，而且所用的名词往往与数据库的表格名对应。一般来说，数据库中的表都是同种记录的"集合"（collection），所以API中的名词也应该使用复数。

举例来说，有一个API提供动物园（zoo）的信息，还包括各种动物和雇员的信息，则它的路径应该设计成下面这样。
```http
https://api.example.com/v1/zoos
https://api.example.com/v1/animals
https://api.example.com/v1/employees
```

## HTTP动词
对于资源的具体操作类型，由HTTP动词表示。

常用的HTTP动词有下面五个（括号里是对应的SQL命令）。
```http
GET（SELECT）：从服务器取出资源（一项或多项）。
POST（CREATE）：在服务器新建一个资源。
PUT（UPDATE）：在服务器更新资源（客户端提供改变后的完整资源）。
PATCH（UPDATE）：在服务器更新资源（客户端提供改变的属性）。
DELETE（DELETE）：从服务器删除资源。
```

还有两个不常用的HTTP动词。

```http
HEAD：获取资源的元数据。
OPTIONS：获取信息，关于资源的哪些属性是客户端可以改变的。
```
下面是一些例子。

``` http
GET /zoos：列出所有动物园 HTTP/1.1
POST /zoos：新建一个动物园 HTTP/1.1
GET /zoos/ID：获取某个指定动物园的信息 HTTP/1.1
PUT /zoos/ID：更新某个指定动物园的信息（提供该动物园的全部信息） HTTP/1.1
PATCH /zoos/ID：更新某个指定动物园的信息（提供该动物园的部分信息） HTTP/1.1
DELETE /zoos/ID：删除某个动物园 HTTP/1.1
GET /zoos/ID/animals：列出某个指定动物园的所有动物 HTTP/1.1
DELETE /zoos/ID/animals/ID：删除某个指定动物园的指定动物 HTTP/1.1
```

## 过滤信息（Filtering）
如果记录数量很多，服务器不可能都将它们返回给用户。API应该提供参数，过滤返回结果。

下面是一些常见的参数。
```http
?limit=10：指定返回记录的数量
?offset=10：指定返回记录的开始位置。
?page=2&per_page=100：指定第几页，以及每页的记录数。
?sortby=name&order=asc：指定返回结果按照哪个属性排序，以及排序顺序。
?animal_type_id=1：指定筛选条件
```

参数的设计允许存在冗余，即允许`API`路径和`URL`参数偶尔有重复。比如，`GET /zoo/ID/animals` 与 `GET /animals?zoo_id=ID` 的含义是相同的。

## 错误处理（Error handling）
如果状态码是4xx，就应该向用户返回出错信息。一般来说，返回的信息中将error作为键名，出错信息作为键值即可。
```json
{
    error: "Invalid API key"
}
```

## 返回结果
针对不同操作，服务器向用户返回的结果应该符合以下规范。
```http
GET /collection：返回资源对象的列表（数组） HTTP/1.1
GET /collection/resource：返回单个资源对象 HTTP/1.1
POST /collection：返回新生成的资源对象 HTTP/1.1
PUT /collection/resource：返回完整的资源对象 HTTP/1.1
PATCH /collection/resource：返回完整的资源对象 HTTP/1.1
DELETE /collection/resource：返回一个空文档 HTTP/1.1
```

## Hypermedia API
`RESTful API`最好做到`Hypermedia`，即返回结果中提供链接，连向其他`API`方法，使得用户不查文档，也知道下一步应该做什么。

比如，当用户向`api.example.com`的根目录发出请求，会得到这样一个文档。
```json
{"link": {
  "rel":   "collection https://www.example.com/zoos",
  "href":  "https://api.example.com/zoos",
  "title": "List of zoos",
  "type":  "application/vnd.yourformat+json"
}}
```

上面代码表示，文档中有一个`link`属性，用户读取这个属性就知道下一步该调用什么`API`了。`rel`表示这个`API`与当前网址的关系（`collection`关系，并给出该`collection`的网址），`href`表示`API`的路径，`title`表示`API`的标题，`type`表示返回类型。

`Hypermedia API`的设计被称为`HATEOAS`。`Github`的`API`就是这种设计，访问`api.github.com`会得到一个所有可用API的网址列表。
```json
{
  "current_user_url": "https://api.github.com/user",
  "authorizations_url": "https://api.github.com/authorizations",
  // ...
}
```

从上面可以看到，如果想获取当前用户的信息，应该去访问`api.github.com/user`，然后就得到了下面结果。
```json
{
  "message": "Requires authentication",
  "documentation_url": "https://developer.github.com/v3"
}
```

上面代码表示，服务器给出了提示信息，以及文档的网址。

## 其他
1. API的身份认证应该使用OAuth 2.0框架。
2. 服务器返回的数据格式，应该使用JSON。

## 补充
1. 接口参数采用下划线分割单词的形式
2. 摒弃大小写，大小写容易在编写文档时进行混淆

# 总结
&emsp;没查阅资料之前，我对 RESTful 架构还以为只是一套开发API的规范，通过了解 RESTful 架构，更能正视这一套架构的流程，以及遵守的规范，如若不遵守规范，千里之堤毁于蚁穴。

# 引用
[阮一峰老师的理解RESTful架构](http://www.ruanyifeng.com/blog/2011/09/restful.html)
[阮一峰老师的RESTful API 设计指南](http://www.ruanyifeng.com/blog/2014/05/restful_api.html)

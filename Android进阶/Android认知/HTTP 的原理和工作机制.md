### HTTP 的原理和工作机制

#### HTTP 到底是什么

- HyperText Transfer Reotocal 超文本传输协议；
- 超文本：在电脑中显示的、含有可以指向其他文本的链接的文本；

#### HTTP 的工作方式

- URl 示例

<img src="http://baihonghua.cn/Http%E7%9A%84Url%E7%A4%BA%E4%BE%8B.png">

- 请求报文格式 Request

<img src="http://baihonghua.cn/Request%E7%9A%84%E6%8A%A5%E6%96%87%E6%A0%BC%E5%BC%8F.png">

- 响应报文格式

<img src="http://baihonghua.cn/Response%E5%93%8D%E5%BA%94%E6%A0%BC%E5%BC%8F.png">

- 请求方法

<img src="http://baihonghua.cn/%E8%AF%B7%E6%B1%82%E6%96%B9%E6%B3%95.png">

> 幂等指的是反复调用多次时会得到相同的结果；

|请求方法|意义|
|:--:|:--|
|GET|用于获取资源、不对服务器数据进行修改、不发送 Body|
|POST|用于增加或修改资源、发送给服务器的内容写在 Body 里面|
|PUT|用于修改资源、发送给服务器的内容写在 Body 里面|
|DELETE|用于删除资源、不发送 Body|
|HEAD|和 GET 使用方法完全相同、和 GET 唯一区别在于，返回的响应报文中没有 Body|


- 状态码

|状态码|意义|
|:--:|:--|
|4XX|表示客户端错误，例如客户端请求的报文信息错误，造成服务端无法识别；|
|304|表示重定向的内容没有改变；|
|100|表示客户端发送分块数据给服务器的过程中，服务端发送给客户端暂时的响应成功的状态码；|
|101|表示服务端支持 HTTP2.0 协议|

<img src="http://baihonghua.cn/%E7%8A%B6%E6%80%81%E7%A0%81.png">

- Header

> 作用：HTTP 消息的元数据或者称为 Body 的数据属性（metadata）；

|元数据|意义|
|:--:|:--|
|Host|服务器的主机地址，不是用来寻址用的，而是用来表示服务器的对应的主机|
|Content-Length|内容的长度（为了方便二进制内容的分割）|
|Content-Type|文本（text/html）、json（application/json）、表单（application/x-www-form-urlencoded）多部分形式（multipart/form-data）|
|boundary|分界线，分割 header 以及 body，以及 body 各个属性之间；|
|Location|重定向的路径；|
|User-Agent|用户代理，指的我们的浏览器；|
|Range/Accept-Range|指定 Body 的内容范围，断点续传|

- - Chunked Transfer Encoding（分块传输编码）

<img src="http://baihonghua.cn/chunkedtransferEncoding.png">

- - 其他种类的 Header

<img src="http://baihonghua.cn/%E5%85%B6%E4%BB%96%E9%83%A8%E5%88%86header.png">

- Cache

|种类|意义|
|:--:|:--|
|Cache|主要是为了加快响应速度|
|Buffer|主要是为了工作流，如视频流|

- REST

对 HTTP 进行限制的一种架构风格；

|种类|意义|
|:--:|:--|
|Server-Client architecture||
|Statelessness|无状态|
|Cacheability|可缓存的|
|Layered System|分层系统|
|Code on demand||

- RESTful HTTP

> 正确地使用 HTTP；

- 小结

<img src="http://baihonghua.cn/L01%E5%B0%8F%E7%BB%93.png">









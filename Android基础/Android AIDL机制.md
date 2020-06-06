#### 前言

前一段时间，除了忙在公司项目的重构和优化，然后就主要把时间花在 Java 的一些基础进行回顾。按照我的计划，接下来的一段时间里面，我会把时间主要花在 Android 的 Framework 层，当然也计划了 Kotlin 的协程呀、Jetpack 组件呀这些的深入理解....

##### # 目录结构

![](http://baihonghua.cn/20200420171137.png)

##### # AIDL

IMyAidlInterface.aidl 文件内容：

![](http://baihonghua.cn/20200420171411.png)

Word.aidl 文件内容：

![](http://baihonghua.cn/20200420171323.png)

Word.java 文件内容：

![](http://baihonghua.cn/20200420171544.png)

其中，在 `IMyAidlInterface.aidl` 文件中，允许通过一个或多个方法来声明接口：

- AIDL 默认支持 Java 的八种基本类型；

- AIDL 还支持 `String`、`CharSequence`、`List`、`Map` 类型，但在 `List`、`Map` 中的元素必须是八种基本类型、或 AIDL 生成的接口、或 `Parcelable` 类型；

- List 支持泛型，而 Map 不支持泛型；

- 如果 AIDL 使用到自定义的 `Parcelable` 对象，那么需要新建一个和需要使用到的 AIDL 文件同名、并位于同一个包内，同时需要通过 `import` 关键字导入到需要使用的 AIDL 文件中；

- AIDL 除了使用到的基本类型，其他类型都需要标上方向：`in`、`out`、`inout`；

新建好上面的三个文件，随后通过 Android Studio 可以编译生成一个 `IMyAidlInterface.java` 文件（文件里面的内容过多，我在这里就不详细引入了）。然后依次编写 AIDL 的 Client 端和 Server 端，如下：

AIDLService.java

![](http://baihonghua.cn/20200420182524.png)

AndroidManifest.xml

![](http://baihonghua.cn/20200420182617.png)

MainActivity.java

![](http://baihonghua.cn/20200420182817.png)

在 `MainActivity` 和 `AIDLService` 中编写的代码都是拿到 Binder 引用，进行 IPC 的跨进程通信。其中在 `AIDLService` 中，是通过：

![](http://baihonghua.cn/20200420183442.png)

`new IWordManager.Stub()` 的创建对象的方式，拿到 `Binder` 引用，然后把该引用通过 `onBind()` 方法返回。其中，`Stub` 是一个接口，并且继承 `android.os.Binder` 和实现 `com.example.aidl.IWordManager`, 然后把该 Binder 对象（也可以看作 `IWordManager`）的引用传到 `Client` 端，即 `MainActivity` ：

![](http://baihonghua.cn/20200420185619.png)

上面的 `val iWordManager = IWordManager.Stub.asInterface(service)` 拿到了 `Server` 端传过来的 `Binder` 对象，然后转换成 `IWordManager`, 最后在 `AIDLService` 和 `MainActivity` 都持有了 `IWordManager` 的引用，也就意味着可以调用 `IWordManager` 里面的方法。

为什么会在 `AIDLService` 和 `MainActivity` 都可以看作持有 `IWordManager` 的引用呢，因为 AIDL 也是一个 C/S 模型的通信模式，`Client` 端和 `Server` 端都是相对而言的。在 `AIDLService` 请求 `MainActivity` 的内容的时候，`AIDLService` 可以看作 `Server` 端,当 `MainActivity` 接收 `MainActivity` 内容的时候，`AIDLService` 可以看作 `Client` 端。

那么在 `AIDLService` 中是如何把 `IWordManager` 对象（也就是 Binder）引用传到 `MainActivity` 中的呢？我们可以通过分析源码：

![](http://baihonghua.cn/carbon.png)

我们通过查看注释 1 的代码：

![](http://baihonghua.cn/IWordManager源码.png)

在继续往下阅读源码的时候，需要明白以下几个知识点，不然容易会看得云里雾里：

- 关于 `_data`、`_reply` 两个 `android.os.Parcel` 对象, `_data` 主要是用来存放 `Client` 端需要发送的数据，而 `_reply` 主要是用来存放用来接收的数据。对于 `android.os.Parcel` 是一个可以通过 `Binder` 传输的数据结构，简单来说，`Binder` 之间的通信交流数据是以 `android.os.Parcel` 作为载体，而数据通过序列化后存在载体 `android.os.Parcel` 上（这里需要注意的是，`parcel` 和 `Parcelable` 是两个不同的概念，而 `Parcelable` 和 `Serializable` 是两个类似的概念）。

- `Binder#transact()` 是客户端和服务端的核心方法，通过调用该方法，客户端会暂时挂起，等待服务端的数据写入 `_reply` 返回，对于 `Binder#transact()` 的四个方法参数（code、data、reply、flags）需要以下的几点说明：

    - **code** 指的是代表 AIDL 接口文件中定义的方法的 ID，因为在客户端和服务端中的 AIDL 文件中，定义了一一对应的方法。因而在 AIDL 文件转成 `.java` 文件的时候，系统会为每个方法生成对应的 ID，通过阅读源码，可以知道方法的 ID 是从 1 开始的；

    - **data**、**reply** 对应上文的 `_data` 和 `_reply`;

    - 第四个参数 **flags** 当为 0 的时候，表示全双工的 RPC 机制，也就是 **data** 发送数据，而 **reply** 接收数据；当为 1 的时候，表示半双工的 RPC 机制，也就是 **data** 发送数据，而 **reply** 是没有数据的。

通过阅读 `IWordManager.java#Proxy` 源码，我们发现, AIDL 是通过：

![](http://baihonghua.cn/Proxy.png)

到此，我们可以得出结论：

- 在 `_data` 中存入客户端需要发送的数据；

- 通过 `mRemote#transact()` 把 `_data` 传给服务端；

- 通过接收 `_reply` 接收数据，从中取出服务端发送过来的数据；

上述两个语句通过 mRemote 去发起调用 transact() 方法的，同时我们通过查看代码，知道 `mRemote` 就是 Binder 对象。通过继续阅读 `Binder#transact()` 方法，可以发现其最终是调用 `Binder#onTransact(int, Parcel, Parcel,int)` 方法，因为在 `IWordManager#Stub` 是继续了 `Binder`, 以此调用的是 `Stub#onTransact()` 方法：

![](http://baihonghua.cn/onTransact.png)

上面的代码可以看出，当接收到 `onTransact()` 的调用后，直接来个 `swith` 语句进行 `case`，这里我以 `getWord()` 方法为例，分析 `_reply` 是如何从中拿出服务端的数据的：

![](http://baihonghua.cn/onTransact_detail.png)

可以发现，在 `Proxy#transact()` 发起调用时候，在 `Stub#onTransact()` 中响应调用，看起来好像就是在本地 `Proxy#transact()` "直接"调用了 `Stub#onTransact()`。其实这个 "直接" 的过程是 Binder 为我们封装了细节，让我们感受不到其中的通信的曲折，我想这也是 Binder 跨进程通信最初设计的目的吧。

#### 小结

现在我们已经完成了 Wordmanager.java 的分析，下面我附上两张图片来做一个小总结：

![](http://baihonghua.cn/20200421182152.png)

![](http://baihonghua.cn/20200421115326.png)

这篇分析 AIDL 机制是基于 Binder 的应用层面的分析，如果我们需要深入了解 Binder 的跨进程原理，我们仍然需要深入到 Binder 的 Framework 层和 Native 层等进行源码的分析。
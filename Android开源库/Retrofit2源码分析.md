![](http://baihonghua.cn/Snipaste_2020-06-05_21-35-37.png)

#### 前言

相信使用过 Retrofit2 网络请求库的童鞋都会对上面的图片有一种熟悉的感觉。是的，上面的图片便是来源自 [Retrofit](https://square.github.io/retrofit/) 的官网的截图。官网上使用了一句话对 Retrofit 进行了扼要的介绍： ***A type-safe HTTP client for Android and Java***，我对这句话的理解是：Retrofit 是一个可以使用在 Android 和 Java 上面的 ***类型安全的 HTTP 客户端***。为什么说 Retrofit 是一个类型安全的的 HTTP 客户端呢？我会在文中提及、以及文后小结出来的。


#### 网络请求的一般步骤

![](http://baihonghua.cn/Snipaste_2020-06-05_22-26.png)

上图是利用 Retrofit 在 Android 实现的一般网络请求的步骤，执行的结果是返回 Github 上的 ***octocat*** 用户的仓库列表。以下是对上图的解析说明：

- 标注 1 处的 `GitHubService` 接口主要是为了声明 API 方法， API 方法必须需要声明在接口里面、该接口不可以带有类型参数、而且该接口不可以继承其他的接口，如果不遵守以上约定，代码在运行期间都会抛出异常的。同时，如果该接口定义了 JDK8 及以上支持的 `default` 方法、静态方法，这些新特性的方法是不被执行的。***在这里，通过对 `GitHubService` API 接口、以及 API 方法的验证，保证了在进行网络请求前的类型安全。***

- 标注 2 处是通过一个典型的构建者模式构建出一个 `Retrofit` 对象，在构建对象的过程中，根据项目的需要，可以通过 `Retrofit#addConverterFactory()` 方法和 `Retrofit#addCallAdapterFactory()` 方法等构建出特性差异的 `Retrofit` 对象，这也是把构建者模式的好处淋漓尽致地表现了出来。至于以上两个方法具体的作用，我会在下文具体描述。

- 标注 3 处是把接口 `GitHubService` 通过 `Retrofit#create()` 方法，内部通过动态代理模式，利用字节码技术在内存里面生成一个 `GitHubService` 的代理对象，通过代理对象去调用 API 方法，然后底层通过反射调用执行真正的 API 方法，达到在程序运行期间，根据接口模板的不同，动态地生成对应的代理对象，去执行 API 方法。这里也是非常好地诠释了动态代理模式的作用。

- 标注 4 处是通过一个 `Call` 接口的实现，去调用 `Call<T>#enqueue()` 方法。`Call<T>enqueue()` 是一个异步地处理网络请求，并通过接口回调 `Callback` 返回请求的响应体，或者发生的错误等信息。同时，还可以通过 `Call<T>#execute()` 同步地发送请求和返回响应体。

#### 网络请求的流程分析

这里的网络请求的流程分析，我会仍然围绕上图进行展开。一般来说，阅读源码的时候，带着问题去驱动阅读，会比较容易弄明白源码的逻辑，不容易迷失在代码的细节里面。

那么，首先抛出的问题：***上图中，代码标注 3 执行到标注 4 的过程，代码经历了那些操作呢？***

- ###### Retrofit#create()

![](http://baihonghua.cn/20200606113938.png)

我们通过阅读 `Retrofit#create()` 源码，发现其内部主要是做了对 API 接口的验证、以及使用动态代理，根据验证后的 API 接口生成返回接口的实现对象，这里生成的 `GitHubService` 代理对象名字一般为 `$Proxy0`。这里呢，我就不继续展开对动态代理的详细描述了，如果你阅读到这里发现自己不太理解动态代理模式的话，可以去看看我写的 [动态代理原理分析](https://hndroid.github.io/2020/03/19/%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86%E5%8E%9F%E7%90%86%E5%88%86%E6%9E%90/) 一文，或者在网络上搜索其他描述动态代理的文章。

在标注 2 处主要的逻辑是做 Java 版本的验证，以及不同版本的 Java 版本执行不同的方法，这主要是为了兼容不同的版本平台。

- ##### Retrofit#loadServiceMethod()

![](http://baihonghua.cn/20200606131524.png)

在上图中，`Retrofit#loadServiceMethod()` 方法主要是检查缓存 `serviceMethodCache` 里面是否已经存在需要被执行的 API 方法，如果存在则直接返回，如果不存在则通过执行 标注2 处的方法进行解析返回 API 方法。为什么需要这个检查缓存的功能呢？因为在解析 API 的过程中，我们通过查看 标注2 处的方法，来到 `ServiceMethod<T>#RequestFactory.parseAnnotations()` 的方法处，那么我们通过阅读 标注3 处代码如下图过程：

![](http://baihonghua.cn/20200606132137.png)

如上图，最终我们来到 标注1 处，发现解析 API 方法需要解析方法的注解、解析方法的泛型的类型参数、以及解析方法参数的注解，这些在代码里面都会是比较耗时的操作，而在移动端的网络请求的过程中，过多执行耗时的操作，显然是不被我们所接受的，我们都希望网络请求的结果可以更快地呈现在我们的手机屏幕上。因而，便有了以上的读取缓存的操作。

- ##### ServiceMethod<T>#HttpServiceMethod.parseAnnotations()

我们回到上图 标注4 处，这里继续对 API 方法进行解析。我们都知道，对注解进行解析，往往会使用到反射，正如该方法的官方注释写道： ***This requires potentially-expensive reflection so it is best to build each service method only once and reuse it.*** 这同时也验证了上文读取缓存的必要性。

![](http://baihonghua.cn/20200606143045.png)

上图中的方法主要有三处，我分别给标注了出来。其他省略的部分主要是对 API 方法类型的检查，使得请求网络的时候类型是安全的。

- 标注 1 处主要是与上文提及的 `Retrofit#addCallAdapterFactory()` 方法相关，默认地，如果我们不手动调用该方法，那么 `Retrofit` 也会手动添加一个 `DefaultCallAdapterFactory` 对象, 如果是 JDK8 的平台，还会默认添加一个 `CompletableFutureCallAdapterFactory` 对象。`CallAdapter` 在 `Retrofit` 的作用主要是什么呢？
简单来说，就是把网络请求返回来的响应体，包装一层形成新的返回对象。如在接口 API 方法里面，原本默认返回的是 `Call<ResponseBody>`，如果调用 `Retrofit#addCallAdapterFactory()` 方法，添加对 RxJava 的支持，如添加 `RxJava2CallAdapterFactory.create()`, 那么在接口 API 方法可以返回的是 `Observable<ResponseBody>`。

    ![](http://baihonghua.cn/20200606152018.png)

- 标注 2 处则是直接把请求体或相应体转换成需要的格式。

最终，我们来到标注 3 处，这里是上文 `Retrofit#loadServiceMethod()` 调用之后返回 `CallAdapted` 的对象：

![](http://baihonghua.cn/20200606154500.png)

这时，便会调用 `ServiceMethod<T>#invoke()` 方法，通过阅读源码，我们得知，`CallAdapted` 是继承自 `HttpServiceMethod<ResponseT, ReturnT>` 类，而 `HttpServiceMethod<ResponseT, ReturnT>` 类又继承自 `ServiceMethod<ReturnT>` 类，那么这里调用实际是 `CallAdapted` 的对象。我们可以通过找到 `CallAdapted` 类阅读发现，`incoke()` 方法最终是调用了 `CallAdapted` 类里面的 `CallAdapted#adapt()`:

![](http://baihonghua.cn/20200606155934.png)

经过阅读代码，我们最终来到了 标注1 处的代码，并且发现，此时的 `callAdapter.adapt(call)` 中的 `callAdapter` 就是上文提到的 `Retrofit#addCallAdapterFactory()` 中的 `Adapter`。假设我们在构建 Retrofit 的时候，没有调用该方法，那么 Retrofit 就会默认地调用到 `DefaultCallAdapterFactory` 类中的 `DefaultCallAdapterFactory#adapt()`:

![](http://baihonghua.cn/20200606161331.png)

当 ***Retrofit#create()*** 执行完，并且调用 API 方法之后，返回了一个 `ExecutorCallbackCall` 对象。通过执行`ExecutorCallbackCall#enqueue()` 去调用真正进行网络请求的 `OkHttpCall#enqueue()` 方法。在这里源码里面，还使用到了一个设计模式：适配器模式。

适配器模式可以细分为两种：类适配器和对象适配器

- 类适配器使用继承关系来实现；

- 对象适配器使用组合来实现；

通过分析源码，可以发现，`CallAdapted` 采用的适配器模式是对象适配器的。到此，就完整地回答了在文章开头提出的： ***代码标注 3 执行到标注 4 的过程，代码经历了那些操作呢？*** 。总结来说，`Retrofit` 通过构建者模式构造出其实例，在构建的过程中，根据项目的需要，可以添加响应体适配器、以及请求和返回内容的转换器等，然后通过动态代理生成代理对象，去调用 API 接口方法，通过同步或异步返回响应体。

下图是 Retrofit 进行网络请求的时序图：

![](http://baihonghua.cn/20200606170356.png)

#### 小结

在上文，还有一个问题 ***为什么说 Retrofit 是一个类型安全的的 HTTP 客户端呢？*** 在对 `Retrofit` 源码的分析的过程中，我们会发现 `Retrofit` 会对 API 接口、以及 API 接口方法进行严格的检查，从而避免了 `Retrofit` 在网络请求的过程中，会发生 API 接口、接口方法类型错误的问题。

> 在文末，如果你可以看到这段话，超级感谢你可以耐心阅读完整一篇文章。首先，为了写这一篇文章，我从阅读 Retrofit 的源码，到写出这边文章，花了整整一周的时间。因为我觉得，既然要把文章写出来，就不可以随随便便去应付去写。在写文章的同时，需要确保自己写的东西的准确性，不可以写出来误导了别人。写作是一种能力，同时写出的文章让别人看得懂，那就是另外一种更高层次的能力了。在这里我希望，你可以读完我的文章后，可以去验证我文章的观点，因为我觉得，无论谁提出的观点，都需要自己带有一种批判的思维去看待。只有经历的起自己的推敲的观点，才是真真正正正确的。
>
> 同时，我希望如果你发现我写的文章中存在不足或错误的地方，希望你可以指出，我真的会很感激你的帮助的。毕竟，如果一个人连错误都不敢承担，那么他该如何成长呢？
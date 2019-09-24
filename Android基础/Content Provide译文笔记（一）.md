### Content provider 译文笔记（一）

Content providers can help an application manage access to data stored by itself, stored by other apps, and provide a way to share data with other apps. They encapsulate the data, and provide mechanisms for defining data security. Content providers are the standard interface that connects data in one process with code running in another process. Implementing a content provider has many advantages. Most importantly you can configure a content provider to allow other applications to securely access and modify your app data as illustrated in figure 1.

`content provider` 可以允许其他的 APP 访问自身储存的数据，`content provider` 通过提供一种访问数据的途径，允许其他的 APP 把通过 `content provider` 访问得到的数据储存到其他的 APP 自身。`content provider` 提供机制去封装、以及保护应用的数据。`content provider` 是将一个进程中的数据与另一个进程通过运行的代码连接起来的标准接口。实现一个 `content provider` 有很多的好处。最有意义的是你可以通过配置 `content provider` 去允许其他的应用安全地访问、以及修改你自身应用储存的数据，如下图 1：

![Overview diagram of how content providers manage access to storage](http://baihonghua.cn/content-provider-overview.png)

Use content providers if you plan to share data. If you don’t plan to share data, you may still use them because they provide a nice abstraction, but you don’t have to. This abstraction allows you to make modifications to your application data storage implementation without affecting other existing applications that rely on access to your data. In this scenario only your content provider is affected and not the applications that access it. For example, you might swap out a SQLite database for alternative storage as illustrated in figure 2.

你可以通过 `content provider` 共享你应用的数据。如果你不打算共享数据，你仍然可以使用 `content providers`，因为它们提供了很好的抽象，但您不必这样做。此抽象允许你对应用程序数据存储实现进行修改，而不会影响依赖于访问数据的其他现有应用程序。在这种情况下，只有您的内容提供商受到影响，而不是访问它的应用程序。例如，你可以将SQLite数据库换成备用存储，如图2所示。

![Illustration of migrating content provider storage.](http://baihonghua.cn/content-provider-migration.png)

A number of other classes rely on the ContentProvider class:

- `AbstractThreadedSyncAdapter`

- `CursorAdapter`

- `CursorLoader`

If you are making use of any of these classes you also need to implement a content provider in your application. Note that when working with the sync adapter framework you can also create a stub content provider as an alternative. For more information about this topic, see Creating a stub content provider. In addition, you need your own content provider in the following cases:

你需要实现 `content provider` 在你的应用程序，如果你正在使用上述的类。需要注意的是，当你使用同步的适配器框架的时候，你可以创建一个 `stub content provider` 作为一个替代方案，有关这个主题的更多的信息，请参考创建 `stub content provider`，补充来说，在以下的情况下面，你需要自己的 `content provider`:

- You want to implement custom search suggestions in your application

- You need to use a content provider to expose your application data to widgets

- You want to copy and paste complex data or files from your application to other applications

The Android framework includes content providers that manage data such as audio, video, images, and personal contact information. You can see some of them listed in the reference documentation for the android.provider package. With some restrictions, these providers are accessible to any Android application.

Android 的框架管理数据例如音频、视频、图片、和个人的联系信息都包含 `content providers`。你可以在 `android.provider` 包的参考文档中看到它们。除了有一些限制，任何Android应用程序都可以访问这些的 `content providers`，从而获取到它们对应的数据。

#### Advantages of content providers

> content providers 的优点

Content providers offer granular control over the permissions for accessing data. You can choose to restrict access to a content provider from solely within your application, grant blanket permission to access data from other applications, or configure different permissions for reading and writing data. For more information on using content providers securely, see Security tips for storing data, as well as Content provider permissions.

`Content providers` 提供对访问数据的权限的精细控制。你可以选择仅在应用程序内限制对 `Content providers` 的访问，授予从其他应用程序访问数据的一揽子权限，或配置读取和写入数据的不同权限。有关安全使用 `Content providers` 的详细信息，请参阅存储数据的安全提示以及内容提供程序权限。

You can use a content provider to abstract away the details for accessing different data sources in your application. For example, your application might store structured records in a SQLite database, as well as video and audio files. You can use a content provider to access all of this data, if you implement this development pattern in your application.

你可以使用 `Content providers` 抽象出用于访问应用程序中不同数据源的详细信息。例如，你的应用程序可能会在SQLite数据库中存储结构化记录，以及视频和音频文件。如果在应用程序中实现此开发模式，则可以使用 `Content providers` 访问所有这些数据。

Also note that CursorLoader objects rely on content providers to run asynchronous queries and then return the results to the UI layer in your application. For more information on using a CursorLoader to load data in the background, see Running a query with a CursorLoader.

另外值得注意的是，`CursorLoader` 对象依赖 `Content providers` 来运行异步查询，然后将结果返回到应用程序中的UI层。有关使用 `CursorLoader` 在后台加载数据的更多信息，请参阅使用 `CursorLoader` 运行查询。

> [原文链接](https://developer.android.com/guide/topics/providers/content-providers.html)


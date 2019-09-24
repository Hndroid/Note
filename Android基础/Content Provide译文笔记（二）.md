### Content provider译文笔记（二）


#### Content provider basics

> Content provider 基础知识

A content provider manages access to a central repository of data. A provider is part of an Android application, which often provides its own UI for working with the data. However, content providers are primarily intended to be used by other applications, which access the provider using a provider client object. Together, providers and provider clients offer a consistent, standard interface to data that also handles inter-process communication and secure data access.

`content provider` 管理对数据库的访问。`content provider` 作为 Android 应用的一部分，去为渲染 UI 时提供内容数据的支持，也就是可以使得 UI 渲染后有内容数据的显示。然而，`content providers` 主要被其他看作客户端的应用程序使用。`content provider` 的服务端暴露数据访问的接口，而`content provider` 的客户端使用已经给暴露的数据访问接口，服务端和客户端提供了一致的、标准的接口，而且该接口还处理了进程间访问和安全数据的访问。

Typically you work with content providers in one of two scenarios; you may want to implement code to access an existing content provider in another application, or you may want to create a new content provider in your application to share data with other applications. This topic covers the basics of working with existing content providers. To learn more about implementing content providers in your own applications, see Creating a content provider.

通常，你在下面的两种的方案中使用 `content provider`；第一种是你可能想通过编写代码去访问存在着 `content provider` 的其他程序，第二种是你可能添加一个 `content provider` 在你的应用程序中，把你的应用程序中的数据访问接口暴露出来，以便其他的应用程序访问。下面的文章讲述的主题是基于应用中已经存在了 `content provider`，而需要学习如何通过代码的编写，在你的应用程序中实现 `content provider`，可以参考 [Creating a content provider](https://developer.android.com/guide/topics/providers/content-provider-creating?hl=en)

This topic describes the following:

- How content providers work.

- The API you use to retrieve data from a content provider.

- The API you use to insert, update, or delete data in a content provider.

- Other API features that facilitate working with providers.

#### Overview

A content provider presents data to external applications as one or more tables that are similar to the tables found in a relational database. A row represents an instance of some type of data the provider collects, and each column in the row represents an individual piece of data collected for an instance.

`content provider` 将数据作为一个或多个表格呈现给外部应用程序，这些表格与关系数据库中的表格类似。行表示提供程序收集的某种类型数据的实例，行中的每个列表示为实例收集的单个数据。

A content provider coordinates access to the data storage layer in your application for a number of different APIs and components as illustrated in figure 1, these include:

`content provider` 协调对应用程序中数据存储层的访问，以获取一系列不同的API和组件，如图1所示，其中包括：

- Sharing access to your application data with other applications;
和其他的应用共享你的应用数据的访问;

- Sending data to a widget;
  发送数据到组件

- Returning custom search suggestions for your application through the search framework using `SearchRecentSuggestionsProvider`;
  使用 `SearchRecentSuggestionsProvider` 通过搜索框架​​返回应用程序的自定义搜索建议;

- Synchronizing application data with your server using an implementation of `AbstractThreadedSyncAdapter`;
  使用 `AbstractThreadedSyncAdapter` 的实现将应用程序数据与服务器同步;

- Loading data in your UI using a `CursorLoader`;


![Relationship between content provider and other components.](http://baihonghua.cn/content-provider-tech-stack.png)

#### Accessing a provider

When you want to access data in a content provider, you use the ContentResolver object in your application's Context to communicate with the provider as a client. The ContentResolver object communicates with the provider object, an instance of a class that implements ContentProvider. The provider object receives data requests from clients, performs the requested action, and returns the results. This object has methods that call identically-named methods in the provider object, an instance of one of the concrete subclasses of ContentProvider. The ContentResolver methods provide the basic "CRUD" (create, retrieve, update, and delete) functions of persistent storage.

如果要访问内容提供程序中的数据，可以使用应用程序上下文中的 `ContentResolver` 对象作为客户端与提供程序进行通信。`ContentResolver` 对象与提供程序对象进行通信，提供程序对象是实现 `ContentProvider` 的类的实例。提供者对象从客户端接收数据请求，执行请求的操作，并返回结果。此对象具有在提供程序对象中调用具有相同名称的方法的方法，该对象是 `ContentProvider` 的一个具体子类的实例。`ContentResolver` 方法提供持久存储的基本“CRUD”（创建，检索，更新和删除）功能。

A common pattern for accessing a ContentProvider from your UI uses a CursorLoader to run an asynchronous query in the background. The Activity or Fragment in your UI call a CursorLoader to the query, which in turn gets the ContentProvider using the ContentResolver. This allows the UI to continue to be available to the user while the query is running. This pattern involves the interaction of a number of different objects, as well as the underlying storage mechanism, as illustrated in figure 2.

从UI访问 `ContentProvider` 的常用模式使用 `CursorLoader` 在后台运行异步查询。UI中的 Activity 或 Fragment 调用 `CursorLoader` 到查询，然后使用`ContentResolver` 获取 `ContentProvider`。这允许在查询运行时UI继续可供用户使用。该模式涉及许多不同对象的交互，以及底层存储机制，如图2所示。

![](http://baihonghua.cn/content-provider-interaction.png)

One of the built-in providers in the Android platform is the user dictionary, which stores the spellings of non-standard words that the user wants to keep. Table 1 illustrates what the data might look like in this provider's table:

Android平台中的一个内置提供程序是用户词典，用于存储用户想要保留的非标准单词的拼写。表1说明了此提供程序表中数据的外观：

|word|app id|frequency|locale|_ID|
|:--:|:--:|:--:|:--:|:--:|
|mapreduce|user1|100|en_US|1|
|precompiler|user14|200|fr_FR|2|
|applet|user2|225|fr_CA|3|

In table 1, each row represents an instance of a word that might not be found in a standard dictionary. Each column represents some data for that word, such as the locale in which it was first encountered. The column headers are column names that are stored in the provider. To refer to a row's locale, you refer to its locale column. For this provider, the _ID column serves as a "primary key" column that the provider automatically maintains.

在表1中，每行表示可能在标准字典中找不到的单词的实例。每列代表该单词的一些数据，例如首次遇到它的语言环境。列标题是存储在提供程序中的列名。要引用行的语言环境，请参阅其语言环境列。对于此提供程序，_ID列用作提供程序自动维护的“主键”列。

To get a list of the words and their locales from the User Dictionary Provider, you call ContentResolver.query(). The query() method calls the ContentProvider.query() method defined by the User Dictionary Provider. The following lines of code show a ContentResolver.query() call:

要从 User Dictionary Provider 获取单词及其语言环境的列表，你可以调用 ContentResolver.query(),
query() 方法调用 User Dictionary Provider 定义的 ContentProvider.query() 方法。以下代码行显示ContentResolver.query（）调用：

```java
// Queries the user dictionary and returns results
cursor = getContentResolver().query(
    UserDictionary.Words.CONTENT_URI,   // The content URI of the words table
    projection,                        // The columns to return for each row
    selectionClause,                   // Selection criteria
    selectionArgs,                     // Selection criteria
    sortOrder);                        // The sort order for the returned rows
```

Query() compared to SQL query:

|query() argument|SELECT keyword/parameter|Notes|
|:--:|:--:|:--|
|Uri|FROM table_name|Uri maps to the table in the provider named table_name.|
|projection|col,col,col,...|projection is an array of columns that should be included for each row retrieved.|
|selection|WHERE col = value|selection specifies the criteria for selecting rows.|
|selectionArgs|(No exact equivalent. Selection arguments replace ? placeholders in the selection clause.)|
|sortOrder|ORDER BY col,col,...|sortOrder specifies the order in which rows appear in the returned Cursor.|

#### Content URIs

A content URI is a URI that identifies data in a provider. Content URIs include the symbolic name of the entire provider (its authority) and a name that points to a table (a path). When you call a client method to access a table in a provider, the content URI for the table is one of the arguments.

内容URI是标识提供者中的数据的URI。内容URI包括整个提供程序的符号名称（其权限）和指向表（路径）的名称。当您调用客户端方法来访问提供程序中的表时，该表的内容URI是其中一个参数。

In the preceding lines of code, the constant CONTENT_URI contains the content URI of the user dictionary's "words" table. The ContentResolver object parses out the URI's authority, and uses it to "resolve" the provider by comparing the authority to a system table of known providers. The ContentResolver can then dispatch the query arguments to the correct provider.

在前面的代码行中，常量 CONTENT_URI 包含用户词典的“单词”表的内容URI。ContentResolver 对象解析URI的权限，并通过将权限与已知提供程序的系统表进行比较来使用它来“解析”提供程序。然后，ContentResolver 可以将查询参数分派给正确的提供程序。

The ContentProvider uses the path part of the content URI to choose the table to access. A provider usually has a path for each table it exposes.

ContentProvider 使用内容 URI 的路径部分来选择要访问的表。提供者通常为其公开的每个表都有一个路径。

In the previous lines of code, the full URI for the "words" table is:

```java
content://user_dictionary/words
```

where the user_dictionary string is the provider's authority, and the words string is the table's path. The string content:// (the scheme) is always present, and identifies this as a content URI.













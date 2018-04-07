#### Context 上下文对象

- Activity、Service、Application 都是继承于 Context。
- Android 应用程序在以下的时间点的时候会创建对应的 Context 对象：
    - 创建 Application
    - 创建 Activity
    - 创建 Service
- 当 Android 应用程序在第一次创建的时候，都会创建一个 Application 对象，同时也就创建了该应用程序的 Appliaction Context 对象，也就意味着整一个应用程序在其整一个生命周期里面，都会拥有着一个 Context 对象;
- 
 
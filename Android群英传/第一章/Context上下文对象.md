#### Context 上下文对象

- Activity、Service、Application 都是继承于 Context。
- Android 应用程序在以下的时间点的时候会创建对应的 Context 对象：
    - 创建 Application
    - 创建 Activity
    - 创建 Service
- 当 Android 应用程序在第一次创建的时候，都会创建一个 Application 对象
 
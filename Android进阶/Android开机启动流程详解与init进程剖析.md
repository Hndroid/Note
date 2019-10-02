### Android开机启动流程详解与init进程剖析

#### 手机启动发生的事情

![](http://baihonghua.cn/init%E8%BF%9B%E7%A8%8B.png)

#### Init进程

- init进程的定义：它是 Linux 内核启动的第一个用户级进程。Init 进程主要任务为启动 Android 关键服务，守护关键服务进程，确保不被杀死；

- init与Android关系：当 Linux 内核启动之后，通过启动用户级程序 init 来完成引导 Android Framework 的建立。因此，init 总是第一个进程（它的进程号总是 1 ）

#### 系统服务

![](http://baihonghua.cn/%E7%B3%BB%E7%BB%9F%E6%9C%8D%E5%8A%A1.png)

- 系统服务不会给系统 kill 掉；
- 有的系统服务被意外 kill 掉，init 进程会重新拉起该服务；
- 有的系统服务被意外 kill 掉，init 进程拉不起该服务，系统会被关掉；

init.rc 是一个配置文件，配置系统级的服务；
如果想配置指定的服务，则需要在 init.rc 文件中配置自己的服务；

#### init进程如何守护系统关键服务

![](http://baihonghua.cn/init%E8%BF%9B%E7%A8%8B%E5%A6%82%E4%BD%95%E5%AE%88%E6%8A%A4%E7%B3%BB%E7%BB%9F%E5%85%B3%E9%94%AE%E5%85%B3%E9%94%AE%E6%9C%8D%E5%8A%A1.png)

#### Android系统启动总结




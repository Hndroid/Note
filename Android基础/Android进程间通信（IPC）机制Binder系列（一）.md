### Android进程间通信（IPC）机制Binder系列笔记（一）概述

> Android 中不同进程的 Activity 或 Service 之间是如何通信的呢？这个问题就是本系列讲解的重点。

#### Android系统基于Linux内核

Linux 系统继承和兼容了丰富的 Unix 系统进程间通信（IPC）机制。

Linux 通信方式|定义
:--:|:---
管道（pipe）|一种最基本的IPC机制。内核使用环形队列机制，借助内核缓冲区（4K）实现。
信号（signal）|信号是进程间通信机制中唯一的异步通信机制，可以看作是异步通知，通知接收信号的进程有哪些事情发生。
跟踪（trace）|常用来跟踪进程执行时的系统调用和所接收的信号。在Linux系统中，进程不能直接访问硬件设备，当进程需要访问硬件设备(比如读取磁盘文件，接收网络数据等等)时，必须由用户态模式切换至内核态模式，通过系统调用访问硬件设备。strace可以跟踪到一个进程产生的系统调用,包括参数，返回值，执行消耗的时间。

#### Binder机制组成

分别是 Client、Server、Service Manager和 Binder 驱动程序。其中，Client、Server、Service Manager 运行在用户空间，而Binder 驱动程序运行在内核空间。

<img style="margin-left:auto; margin-right:auto; display:block" src="http://baihonghua.cn/Binder%E8%BF%9B%E7%A8%8B%E9%97%B4%E9%80%9A%E4%BF%A1.png" alt="Binder机制组成">

#### 概述

- **Client、Server、Service Manager 运行在用户空间，而 Binder 驱动程序运行在内核空间。**
- **Service Manager 和 Binder 已经在 Android 系统中实现，开发者只需要按照约定好的规范实现 Client 和 Service；**
- Binder 驱动程序提供设备文件 /dev/binder 与用户空间交互，Client、Service 和 Service Manager 通过 open 和 ioctl 文件操作函数与 Binder 驱动程序进行通信；
- Client、Server 进程间地通信是通过 binder 驱动程序简介实现的；
- Service Manager 是一个守护进程，用来管理 Service,并向 Client 提供查询 Service 接口的能力；

#### 参考资料

[Android进程间通信（IPC）机制Binder简要介绍和学习计划](https://blog.csdn.net/luoshengyang/article/details/6618363)



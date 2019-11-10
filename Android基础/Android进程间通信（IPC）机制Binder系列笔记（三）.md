#### 前言

Binder 是 Android 开发者阅读 Android 源码、Linux Kernel 层源码的时候，必须要弄懂的一个知识点。Binder 它出现在 Android Driver 层、Android Native 层、Android Framework 层，当然，在 Android 的 App 层使用的 Binder 框架，也是需要依赖以上三层才可以实现的。

#### Binder 在不同角度上的定义

- IPC：Binder 是 Android 系统中的一种基于 C/S（Client/Server）、全双工的一种跨进程通信方式，该通信方式在 Linux 系统中是没有，是 Android 系统独有的；

- Android Driver：Binder 可以理解为一种虚拟的物理设备，它的设备驱动是 /dev/binder。在 Linux 系统中，一切皆文件。驱动层位于 Linux Kernel 中，它提供了最底层的数据传递、对象标识、线程管理、调用过程控制等功能。驱动层是整个 Binder 机制的核心；

- Android Native：Binder 是创建 Service Manager 以及 BpBinder/BBinder 模型，搭建与 Binder 驱动的桥梁；

- Android Framework：Binder 是各种 Manager（ActivityManager、WindowManager 等）和相应的 xxxManagerService 的桥梁；

- APP：Binder 是客户端和服务端进行通信的媒介，当调用 bindService 的时候，服务端会返回一个包含了服务端业务方法调用的 Binder 对象，通过这个 binder 对象，客户端就可以调用服务端的 Binder 对象，去获取服务端提供的服务或者数据，这里的服务包含普通服务和基于 AIDL（Android Interface Define Language）的服务；

#### BpBinder/BBinder 模型

在上面的 Android Native 层，我们提及到了 BpBinder/BBinder 模型 。BpBinder/BBinder 是 Binder 通信的的 "双子星"，都是实现了 IBinder 接口，表示具有跨进程的能力。BpBinder 是 Client 端与 Service 交互的代理类，而 BBinder 则代理了 Service 端。BpBinder 和 BBinder 是一一对应的，BpBinder 通过 Hander 找到对应的 BBinder。在 ServiceManager 中创建了 BpBinder，通过 Handler（值为0）可以找到对应的 BBinder。

![](http://baihonghua.cn/BpBinder%E5%92%8CBBinder.png)

#### Binder 驱动源码分析

- `binder_init()` 函数主要是驱动设备的初始化；

- `binder_open()` 函数是打开 Binder 驱动设备；

- `binder_mmap()` 该函数是 Binder 的内存地址映射函数。首先在内核虚拟地址空间，申请一块与用户虚拟内存相同大小的内存块。然后再申请 1 个 page 大小的物理内存，再将同一块物理内存分别映射到内核虚拟地址和用户虚拟内存空间，从而实现了用户空间的 Buffer 和内核空间的 Buffer 同步操作的功能。

- `binder_ioctl()` 该函数主要是用于数据操作；

Binder 再进程中进行数据通信的时候，Client 端向 Server 端发送数据时，Client 先从自己的进程空间把 IPC 通信数据 copy_from_user 拷贝到内核空间，而 Server 端与内核共享数据，不再需要拷贝数据，而是通过内存地址空间的偏移量，即可以获取到对应的内核空间的地址，整个过程只发生一次内存的拷贝；

#### ServiceManager 源码分析

ServiceManager 位于 Android Native 层的 `/frameworks/native/cmds/servicemanager/service_manager.c`，调用的是 Kernel 层的 `binder.c` 驱动。
![](http://baihonghua.cn/service_manager_main%28%29.png)
获取 Service Manager 是通过调用 `defaultServiceManager()` 方法来完成，当进程注册服务 `addService` 或获取服务 `getService` 的过程之前，都需要通过调用 `defaultServiceManager()` 方法来获取 `gDefaultServiceManager` 对象。对于 `gDefaultServiceManager` 对象，如果存在则直接返回；如果不存在则创建该对象，创建过程包括调用 `open()` 打开 `binder` 驱动设备，利用 `mmap()` 映射内核的地址空间; 并通过 `do_add_service()` 注册服务、`do_find_service()` 查询服务

#### framework 层源码分析

- 注册 Binder：建立了 Binder 类在 Native 层与 Framework 层之间的相互调用的桥梁；

- 注册 BinderInternal：建立了 BinderInternal 类在 Native 层与 Framework 层之间的相互调用的桥梁；

- 注册BinderProxy：建立了 BinderProxy 类在 Native 层与 Framework 层之间的相互调用的桥梁；

#### 后言

这次主要是就 Binder 的底层的驱动初始化、Binder 底层驱动如何被上层调用的流程做了一个宏观的描述。





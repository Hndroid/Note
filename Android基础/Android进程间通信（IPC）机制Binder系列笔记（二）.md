### Android进程间通信（IPC）机制Binder系列笔记（二）

#### Binder 在不同的角度上的定义

* 从设备的角度来说，Binder 是一种虚拟的物理设备驱动，位于 /dev/binder 目录下；
* 从 Android 的代码上来说，Binder 是 Android 中的一个类，实现了 IBinder 接口；
* 从 IPC 角度来说，Binder 是 Android 中的的一种跨进程的通信方式，在 Linux 系统上没有；
* 从Android Framework 角度来说，Binder 是 ServiceManager 连接各种 Manager 和相应的 ManagerService 的桥梁；
* 从 Android 应用层来说，Binder 是 client 和 service 进行通信的媒介，当 bindService() 的时候，service端会返回一个包含 service 端业务调用的 Binder 对象，通过这个 Binder 对象，client 端就可以获取到 service 端提供的服务或者数据了；

#### Android 采用 Binder 的原因

Android 是基于 Linux 系统上的，理论上应当采用 Linux 系统的 IPC 通信方式。Linux 系统中的 IPC 方式共有 5 中，大体划分为三种 socket、管道、共享内存。而 Android 并没有采用 Linux 中的 IPC 方式，而采用称为 Binder 的 IPC 方式。

Binder 是一种基于 Client-Service 通行模式的通信方式，传输过程只需要一次拷贝，可以为发送方添加 UID/PID 标记，支持实名 Binder 和匿名 Binder，安全性高；

下表描述 Linux 中的 IPC 的特性，来与 Android 中的 Binder 对比，以解析采用 Binder 的原因：

|特性||
:--:|:--
|可靠性|Android 中希望得到的是一种 Client-Service 的通信方式，而 Linux 中符合这种通信方式的只有 Socket。虽然可以在其他四种方式的基础上添加协议来控制，但这样会增加系统的复杂性，在手机这种条件复杂、资源稀缺的环境下，也难以保证可靠；|
|传输性能|虽然 Socket 是一个数据传输接口，但是性能开销大，适合网络传输、大文件传输这种重量级的传输环境；消息队列和管道采用的是存储-转发的形式，先把需要转发的数据拷贝到新开辟的内存缓存，然后再从内存缓存中把数据拷贝到接收方，经历了两次数据拷贝；共享内存虽然不用经过数据拷贝，多个进程共用一块内存，但是对于 Android 这种多应用进程、安全保密性高的场合，共享内存明显不合适；|
|安全性|Android 作为一个多应用的系统，各种应用的来源不同，确保各种应用的安全性是非常重要的。Linux 中传统的 IPC 完全没有任何的安全措施，完全依赖上层的协议来确保，具体有两种体现：第一，传统的 IPC 接收方无法获知发送方可靠的 UID/PID（用户ID/进程IP），从而无法鉴别对方的身份，使用传统的 IPC 只能由用户填入 UID/PID ，但这样不可靠，容易被恶意的程序利用；第二，传统IPC的访问接入点是开放的，无法建立私有通信，只要知道这些接入点的程序都可以和对端建立连接，这样无法阻止恶意程序通过猜测接收方的地址获得连接。|



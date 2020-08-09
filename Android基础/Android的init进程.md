#### 前言

Init 进程除了完成系统的初始化之外，本身也是一个守护进程。从系统的角度看 Android 的启动过程可以分为 bootloader 引导、装载和启动 Linux 内核、启动 Android 系统三大阶段。其中的 Android 系统的启动还可以细分为启动 Init 进程、启动 Zegote 进程、启动 SystemService、启动 SystemServer、启动 Home 等多阶段。

![](http://baihonghua.cn/20200714135802.png)

在 Linux 系统内核加载完以后，会首先启动
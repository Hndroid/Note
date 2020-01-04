#### 开机启动流程概述

 Linux 操作系统从开机加电到执行 main 函数的执行可以分三步完成，其目的是实现从启动盘加载操作操作系统程序，完成执行 main 函数所需要的准备工作。第一步，启动 BIOS，准备实模式下的中断向量表和中断服务程序；第二步，从启动盘加载操作系统程序到内存，加载操作系统程序的工作就是利用第一步准备的中断服务程序实现的；第三步，为执行 32 位的 main 函数做过度工作。

 Android 操作系统是基于 Linux Kernel 实现的。Android 从开机加电的时候，首先会启动 BootLoader 这个引导程序，通过这个引导程序启动 `Linux Kernel`，这时候会给操作系统分配内存、以及加载操作系统所需要的驱动。然后通过找到 `init.rc` 这个文件，首先启动 init 进程，并且 init 进程的 id 为 1。在 init 进程启动以后，init 进程会去启动 zygote 进程，而 zygote 进程会去启动 JVM、`SystemServer` 等关键的服务。然后 `SystemServer` 会去启动 Binder线程池、`SystemServiceManager`、`ActivityManagerService`等其他各种服务。再然后由 `ActivityManagerService` 去启动 Launcher APP。

 ![](http://baihonghua.cn/App%E5%90%AF%E5%8A%A8.png)

 #### APP的冷、热、暖启动

 |方式|定义|
 |:--|:--|
 |冷启动|程序从头开始，系统没有为该程序创建进程。一般场景：程序安装后的第一次启动、或者应用程序被系统完全终止后再打开。|
 |热启动|此时程序仍然驻留在内存中，只是被系统从后台带到前台，因此程序可以避免重复对象初始化，加载布局和渲染。需要注意的是，如果程序的某些内存被系统清除，比如调用了 `onTrimMemory` 方法，则需要重新创建这些对象以响应热启动事件。|
 |暖启动|它包含热启动和冷启动一系列的操作子集，比热启动的消耗稍微多一点。它与热启动最大的区别在于，它必须通过调用 onCreate 方法开始重新创建活动，也可以从传传递给 onCreate 方法中保存的实例状态中获得某些对象的恢复。|

 #### 冷启动流程

 - 加载并启动 APP；
 - 启动后立即为该 APP 显示一个空白启动窗口；
 - 创建 APP 进程；（创建应用程序对象）
 - 创建主 Activity；
 - 加载布局，绘制；

 #### APP启动总结

 APP 从被系统调用，再到第一个页面渲染到手机屏幕。我们通常只需要关注 Application 中的 onCreate 方法，第一个 Activity 中 onCreate、onStart、onResume 方法；

 注意：如果在 APP 启动第一个 Activity 时，该 Activity 不但有自己的逻辑，还在 onCreate、onStart、或者 onResume 方法中直接又跳转到了其他 Activity 页面，那么跳转后的 Activity 方法也需要进行优化；

 #### 黑白屏原因

 根据前面的冷启动流程，我们知道当系统加载并且启动 App 的时候，需要耗费相应的时间，即使时间不到 1s，用户也会感觉到当点击 App 图标时会有 "延迟" 现象，为了解决这一个问题，Google 的做法是在 App 创建的过程中，先展示一个空白页面，让用户体会到点击图标以后立马就有响应；而这个空白页面的颜色则是根据我们在 Manifest 文件中配置的主题颜色来决定的；一般默认为白色；


#### 解决黑白屏以及App启动优化

![](http://baihonghua.cn/%E9%BB%91%E7%99%BD%E5%B1%8F%E4%BC%98%E5%8C%96.png)

如上图，通过修改 AppTheme 的方式。即在应用默认的 AppTheme 中，设置 `android:windowDisablePreview` (系统取消预览空白窗体) 为 true，或者通过设置空白窗体为透明；

则两种方式属于同一种方案：将 Theme 的背景改为透明，这样用户从视图上就无法看出黑白屏的存在；

![](http://baihonghua.cn/%E9%BB%91%E7%99%BD%E5%B1%8F%E5%90%AF%E5%8A%A8.png)

第二种黑白屏的解决方案是自定义继承自 AppThemme 的主题，然后将启动的 Activity 的 theme 设置为自定义的主题，然后在启动的 Activity 的 onCreate 和 setContentView 方法之前调用 setTheme 方法，将主题设置为最初的 AppTheme；

方案二实现的效果类似于网易云音乐 App 启动的效果；

方案二实现的原理：主要是优化 App 启动时候的空白窗体，而不是像方案一那样子直接把 App 启动时候的黑白窗体取消、或者变透明的处理方案，方案二的方式更加贴近 Google 官方提供的处理方案，便于后续的启动页面的优化；








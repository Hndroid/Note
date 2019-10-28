#### 前言

我们知道，在 Android 的 Handler 消息机制里面，Looper 扮演着一个消息泵的角色。在 Looper.loop() 的方法中，通过一个死循环不断地从 MessageQueue 里面取出消息，然后通过 Handler.dispatchMessage(Message msg) 把消息发送到对应的线程中，并通过 handleMessage(Message msg) 去处理消息。

> 扩展：为什么 Looper.loop() 里面的死循环不会造成 ANR？

同时，我们知道每一条线程里面维护着一个 Looper 消息泵，
### Android 消息机制

#### Handle、MessageQueue、Looper、Message 之间的关系

|||
:--:|:--:
|Handler|Handler 持有 mqueue 的实例（MessageQeue mqueue = Looper.mqueue）|
|MessageQueue|入队 enqueueMessage / 出队next|
|Looper|MessageQueue mqueue|
|Message||


    

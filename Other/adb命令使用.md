### adb 使用命令

### Android SDK 目录查看

<img src="http://baihonghua.cn/SDK%E7%9B%AE%E5%BD%95.png" style="margin-left:auto; margin-right:auto; display:block"/>

### 通过 adb 命令查看程序的任务栈

##### 查看程序的任务栈中 Activity 的名字

```
adb shell <enter> dumpsys activity | grep -i run
``` 

```
adb shell dumpsys activity activities | grep -i run
```

---

#### 其他的 adb 命令

For the list of recent tasks

```
adb shell dumpsys activity recents
```

For the list of Services running

```
adb shell dumpsys activity services
```

For the list of current Content Providers

```
adb shell dumpsys activity providers
```

For the list of Broadcast state

```
adb shell dumpsys activity broadcasts
```

For the list of Pending Intents

```
adb shell dumpsys activity intents
```

For the list of permissions

```
adb shell dumpsys activity permissions
```

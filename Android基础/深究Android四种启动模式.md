### 理解Activity任务栈

#### 两种方式指定启动模式

Android 系统允许开发者定义 Activity 的启动模式通过两个方式：

方式|定义
:--:|:--:
使用 manifest 文件|通过 android:launchMode="" 指定
使用 Intent Flags|在调用 startActivity() 时，把需要传进的 Intent 参数设置一个 flag 的方式指定

> 通过 manifest 文件定义的启动模式，可以被使用 Intent Flag指定的方式覆盖。

---

#### Standard

谁启动了该模式的 Activity ，该 Activity 就属于启动它的 Activity 的任务栈中。

#### SingleTop（栈顶复用）

在栈顶复用该模式的 Activity 的时候，系统不会回调 onCreate()、onStart() 方法，但是会回调 onResume() 方法，同时会回调 onNewIntent() 方法，该方法会转入一个新的 intent。

#### SingleTask（）

#### SingleInstance（可以理解作系统单例模式）
该模式启动下的 Activity 在系统中具有全局唯一性。并在这个 Activity 启动的新的 Activity 实例都会在新的任务栈中打开。

#### 异同点
||Standard|SingleTop|SingTask|SingleInstance
:--:|:--:|:---:|:--:|:--:
任务栈中没有实例|新建|新建|新建|新创建一个任务栈，并新建该模式的Activity,放进该新建的任务栈中。
任务栈存在该实例，并位于栈顶|新建|复用栈顶，调用onResume()、onNewIntent(Intent intent)方法|复用栈顶，调用onPuse()方法，然后再调用onResume()方法|
任务栈中存在该实例，但不位于栈顶|新建|新建|把位于该模式的Activity之上的Activity弹出，并把该Activity置于栈顶，调用onStart()、onResume()方法|

> 当当前位于栈顶的 Activity 处理新 Intent 的时候，在新的 Intent 到达 onNewintent() 之前，用户无法按返回键返回活动状态；

---

#### Intent Flags

##### FLAG_ACTIVITY_NEW_TASK

对应 SingleTask 启动模式；

##### FLAG_ACTIVITY_SINGLE_TOP

对应 SingleTop 启动模式；

##### FLAG_ACTIVITY_CLEAR_TOP

---

下面三个属性描述用户离开当前任务栈，任务栈的变化：

#### alwaysRetainTaskState

如果在任务的根活动中将此属性设置为“true”，则不会发生刚刚描述的默认行为。即使经过很长一段时间，任务仍会保留堆栈中的所有活动。

#### clearTaskOnLaunch

如果在任务的根活动中将此属性设置为“true”，则只要用户离开任务并返回到该任务，就会将堆栈清除为根活动。换句话说，它与alwaysRetainTaskState相反。即使在离开任务片刻之后，用户也始终返回初始状态的任务。

#### finishOnTaskLaunch

此属性类似于clearTaskOnLaunch，但它在单个活动上运行，而不是在整个任务上运行。它还可以导致任何活动消失，包括根活动。当它设置为“true”时，活动仍然只是当前会话的任务的一部分。如果用户离开然后返回任务，则它不再存在。
















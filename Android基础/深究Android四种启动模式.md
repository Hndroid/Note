### Android四种启动模式

#### Standard

谁启动了该模式的 Activity ，该 Activity 就属于启动它的 Activity 的任务栈中。

#### SingleTop（栈顶复用）

在栈顶复用该模式的 Activity 的时候，系统不会回调 onCreate()、onStart() 方法，但是会回调 onResume() 方法，同时会回调 onNewIntent() 方法，该方法会转入一个新的 intent。

#### Singletask（栈内复用模式，又称栈内单例模式）

#### SingleInstance（可以理解作系统单例模式）
该模式启动下的 Activity 在系统中具有全局唯一性。

#### 异同点
||Standard|SingleTop|SingTask|SingleInstance
:--:|:--:|:---:|:--|:--
任务栈中没有实例|新建|新建|新建|新创建一个任务栈，并新建该模式的Activity,放进该新建的任务栈中。
任务栈存在该实例，并位于栈顶|新建|复用栈顶，调用onResume()、onNewIntent(Intent intent)方法|复用栈顶，调用onPuse()方法，然后再调用onResume()方法|
任务栈中存在该实例，但不位于栈顶|新建|新建|把位于其上的Activity弹出并销毁，再把该Activity置于栈顶，调用onStart()、onResume()方法|


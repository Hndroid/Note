#### 前言

在工作的过程中，因为使用的项目是单个 `Activity` + 多个 `Fragment` 的模式，然后在此过程中也是遇到了挺多的坑的，然后在平常的学习中，记录下的对 `Fragment` 使用。

<!--more-->

#### 为什么要使用Fragment

##### # 单个 Activity + 多个 Fragment

- 一个 App 仅有一个 `Activity`，然后界面都是 `Fragment`，`Activity` 作为 App 的容器使用。

- 优点：性能高、速度快。参考：新版的知乎、Google 系 App；

##### # 多模块 Activity + 多个 Fragment

- 优点：速度快，先比较单个 `Activity` + 多个 `Fragment` 来说，多模块的 `Activity` + 多 `Fragment` 更加容易维护；

##### # 无视图的 Fragment 保存数据

- Activity 的 `onSaveInstanceState(Bundle outState)` 能保存的数据有限，当有大量数据要保存的时候用无视图的 Fragment 来保存数据；

- Activity 异常销毁的时候，`onSaveInstanceState(Bundle outState)` 可以保存的数据有限，数据过大容易 OOM。所以哦我们可以通过再 `onSaveInstanceState(Bundle outState)` 时 `attach` 一个 `Fragment`，并将要存储的大数据保存到这个 `Fragment` 中。当 Activity 重建时找到该 `Fragment`，再从中取出那些数据；

#### FragmentPagerAdapter与FragmentStatePagerAdapter区别

- `FragmentPagerAdapter` 对于暂时不需要的 `Fragment，`***会选择调用 `detach` 方法，仅销毁视图，并不会销毁 Fragment 实例***；

- `FragmentStatePagerAdapter` 会销毁不需要的 `Fragment`，当当前的事务提交以后，会彻底将 `Fragment` 从当前的 `FragmentManager` 中 ***移除***;

- 针对上面的情况，`FragmentStatePagerAdapter` 适合创建多的 `Fragment` 的页面，而 `FragmentPagerAdapter` 适合创建数量少的 `Fragment` 的情况；

#### Fragment的生命周期

- `onAttach()` 当在 Fragment 与 `Activity` 发生关联的时候调用；

- `onCreateView(LayoutInflater, ViewGroup, Bundle)` 创建该 `Fragment` 视图的时候调用；

- `onActivityCreate(Bundle)` 当 Activity 在调用完 `onCreate()` 方法时调用；

- `onDestoryView()` 与 `onCreateView()` 对应，当该 `Fragment` 的视图被移除的时候调用；

- `onDetach()` 方法与 `onAttach()` 相对应，当 `Fragment` 与 `Activity` 关联被取消时调用； 

##### # Fragment生命周期的其他细节

- `onHiddenChanged` 在使用 `add` + `show` 跳转新的 Fragment 的时候，旧的 Fragment 会回调 `onHiddenChanged` 方法，而不会回调 `onStop` 等生命方法；

#### FragmentManage使用

##### # 获取 FragmentManage 的方式

通过调用 `getFragmentManager` 获取；

##### # V4 包中，获取 FragmentManage 的方式

通过调用 `getSupportFragmentManager` 获取；

#### FragmentTransaction使用

##### # 开启 FragmentTransaction 事务

`FragmentTransaction transaction = fm.benginTransatcion();`

##### # FragmentTransaction#add

往 Activity 中添加一个 Fragment；

##### # FragmentTransaction#remove

从 `Activity` 中移除一个 `Fragment`，如果被移除的 `Fragment` 没有添加到回退栈，这个 `Fragment` 实例将会被销毁；

##### # FragmentTransaction#replace

使用另一个 Fragment 替换当前的 Fragment，实际上就是 `FragmentTransaction#remove` 和 `FragmentTransaction#add` 的结合体；

##### # FragmentTransaction#hide

隐藏当前的 Fragment，仅仅是设为不可见，并不会销毁；

##### # FragmentTransaction#show

显示之前隐藏的 Fragment；

##### # FragmentTransaction#detach

会把 View 从当前的 Fragment 解除绑定，和 `remove` 不同的是，此时的 `Fragment` 状态依然由 `FragmentManage` 维护；

##### # FragmentTransaction#attach

重建 view 视图，附加到 UI 上并显示；

##### # FragmentTransaction#commit

提交一个事务；

> 有一个需要注意的点：`commit` 方法需要在 `Activity#onSaveInstance` 之前调用，不然可能出现 `State loss` 的异常错误；

#### Arguments

`setArguments` 方法必须在 `Fragment` 创建以后，添加给 `Activity` 前完成（ `Fragment` 添加给 `Activity` 调用 `attach` 方法）；

#### Fragment与Activity通信

- `Fragment` 交互后数据应该通过接口返回给 `Activity`;
- 如果 `Activity` 中包含自己管理的 `Fragment` 的引用，可以通过引用直接访问所有的 `Fragment` 的 `public` 方法进行通信；
- 如果 `Activity` 中未保存任何 `Fragment` 的引用，那么没关系，每个 `Fragment` 都有一个唯一的 TAG 或 者 ID,可以通过 `getFragmentManager.findFragmentByTag()` 或者 `findFragmentById()` 获得任何
 `Fragment` 实例，然后进行操作。

#### # getActivity() 为空异常

***原因:*** 在 `Fragment` 里面使用 `getActivity` 的时候，当该 `Fragment` 执行了 `Fragment#detach` 生命周期方法之后，位于该 `Fragment` 里面的 `getActivity` 仍然被调用，便会使得 `getActivity() == null`。

***解决:***

![](http://baihonghua.cn/20200711214821.png)

#### # FragmentTransaction#commit提交异常

![](http://baihonghua.cn/20200711215717.png)

对上图的解释：在 `Activity` 调用 `onSaveInstanceState(Bundle, PersistableBundle)` 之后，再去调用 `FragmentTransaction#commit` 方法，会抛出如上图的异常。一般场景是在子线程中调用 `FragmentTransaction#commit` 造成的。

#### # Fragment重叠

***原因:*** 当承载着 `Fragment` 的 `Activity` 在显示了的情况下，当该 `Activity` 在如横竖屏切换的情况下，导致 `Activity` 的生命周期方法重新走一次，然后把 `Fragment` 重新 `add` 和 `commit``，但与此同时，Fragment` 自动有一个恢复机制，在系统异常销毁 Fragment 后又会重新渲染一次，因而以上的原因导致了 `Fragment` 的重叠问题（我想主要是围绕 `Fragment` 的自动恢复机制避免）。

> 内存重启：由于内存紧张导致系统杀死不位于系统前台的 APP。

***解决:***

- 使在 `onSaveInstanceState` 在 `Activity` 异常的时候，不存储销毁前的数据；

- 在 `onCreate(Bundle savedInstanceState)` 中判断 `savedInstanceState` 是否为空来判断是否 `add` 和 `commit` 该 `Fragment`；

#### # Fragment常见问题

![](http://baihonghua.cn/20200711231138.png)

上图中，`containerViewId`、以及 `tag` 是用来通过 `findFragmentById`、或 `findFragmentByTag` 来获取到指定的 `Fragment`。

对于 `Fragment` 的传参问题，不要写有参数的构造函数，因为在内存重启的时候，系统会自动调用 `Fragment` 的默认无参构造函数。

#### # add 与 replace 的区别

- `add` 往 `containerViewId` 的容器里面添加 `Fragment` `view`;

- `replace` 会将 `containerViewId` 的容器里面之前添加的 `view` 全部清空；

#### # 利用Fragment保留Activity内存重启时的数据

![](http://baihonghua.cn/20200712003702.png)

![](http://baihonghua.cn/20200712004155.png)
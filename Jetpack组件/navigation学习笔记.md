![](http://baihonghua.cn/20200418095745.png)

上图的 Fragment 表示 Navigation 主的导航容器，它需要一个 `id` 和 `name`，其中 `name` 是有系统限定的 `androidx.navigation.fragment.NavHostFragment` 表示导航的总的容器；

![](http://baihonghua.cn/20200418101353.png)

在 `app:defaultNavHost` 表示拦截系统的 back 键，在 `fragment` 中可以通过 back 回退到上一个 `Fragment`；

在 `app:navGraph` 属性中，表示导航的界面，需要在 `res` 目录下新建的 `navigation` 文件夹：

![](http://baihonghua.cn/20200418101749.png)

![](http://baihonghua.cn/20200418101910.png)

#### 前言

该篇文章主要是记录开发 Android Studio 插件的步骤。

#### 步骤

开发 AS 插件的编译器选择的是 IntelliJ IDEA，按照正常的新建 project 后，选择 IntellJ Platform Plugin，然后点击 next 进入下一步。

![](http://baihonghua.cn/20200731174007.png)

新建好 project 的目录如下：

![](http://baihonghua.cn/20200731175023.png)

新建好 project 以后，需要到 `\resources\META-INF\` 目录下配置 `plugin.xml` 文件，在 `plugin.xml` 文件里面，文件的节点还是挺容易理解的，但里面的有两个节点如下图：

![](http://baihonghua.cn/20200731180113.png)

对上图标注的说明：

- 标注 1 处，指的是

- 标注 2 处，每一个 `action` 指的是鼠标点击编译器菜单栏、或者右键点击弹出的内容框，如下图：![](http://baihonghua.cn/20200731180925.png)

创建 action 的步骤（new -> Plugin DevKit -> action）：

![](http://baihonghua.cn/20200731211301.png)

![](http://baihonghua.cn/20200731213056.png)

以上的步骤生成的 action 类如下：

![](http://baihonghua.cn/20200731215127.png)

同时，会在 `plugin.xml` 文件中自动生成以下代码：

![](http://baihonghua.cn/20200731215618.png)

然后点击运行的时候，会重新打开 IntelliJ，对 IDEA 的 UI 布局重新布局，运行的结果如下图：

![](http://baihonghua.cn/20200731215946.png)

![](http://baihonghua.cn/20200731220030.png)

![](http://baihonghua.cn/20200731235920.png)

生成的 jar 包如下图：














#### 领域特定语言DSL

groovy 语言是领域特定语言 DSL 的一个分支。DSL（Domain Specific Language）的语言还有 UML、HTML 等。DSL 与通用的编程语言的区别可以用一句话概括起来：求专不求全，解决特定问题。

#### groovy概述

groovy 是一种基于 JVM 的敏捷开发语言，结合了 Python、Ruby 和Smalltalk 的许多强大的特性。groovy 可以与 Java 完美结合，而且可以使用 Java 所有的库。

#### groovy语法

![](http://baihonghua.cn/groovy_1.png)

上图说明，groovy 中的基本类型会转换成 Java 中的包装类型；

![](http://baihonghua.cn/groovy_2.png)

通过上面两个的图片对比，说明 groovy 中存在两种的类型定义方式。上图一表示定义强类型的变量，在继续使用变量的后面，不可以变更变量的类型；而通过图二 `def` 定义的变量，可以变更变量的类型。

![](http://baihonghua.cn/groovy_3.png)

![](http://baihonghua.cn/groovy_4.png)

#### groovy闭包

在 groovy 中，一个闭包是一个花括号围起来的语句块，为了传递参数给闭包，闭包有一组可选的参数列表，通过 "->" 表示列表的结束。

闭包在 groovy 的类型是 groovy.lang.Closure，如下代码创建了一个使用 closure 来处理 Range[1, 2, ..., num] 的函数：

![](http://baihonghua.cn/groovy_5.png)

![](http://baihonghua.cn/groovy_6.png)

groovy闭包有 3 个相关对象：

|对象|定义|
|:--:|:--:|
|this|表示闭包定义所在的类|
|owner|表示闭包定义所在的对象或闭包|
|delegate|表示闭包中引用的第三方对象|
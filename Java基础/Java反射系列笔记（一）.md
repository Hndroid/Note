### Java反射系列笔记（一）

#### 前言

最近在接触 Android 的组件化原理的时候，发现自己对 Java 反射的机制理解并不是很深，只能利用周末的时间把 Java 反射机制回炉重铸。

#### 什么是Java反射

反射机制是 Java 语言提供的一种基础功能，赋予了 Java 程序在运行时的自省（introspect，官方用语）的能力。通过 Java 的反射机制，程序员可以在 Java 程序在运行态的时候操作任意的类或者对象的属性、方法。利用 Java 的反射机制，可以做到以下：

- 在程序的运行态可以获取对象所属的类；
- 在程序的运行态可以构造类的对象实例；
- 在程序的运行时可以获取，或者修改类的成员属性；
- 在程序的运行态可以调用某个类，或者对象的方法；
- 在程序的运行态可以获取类的其他信息，比如描述修饰符、父类信息等；

> 对文中的"自省"的理解："自省"应该仅指程序在运行时对自身信息（元数据）的检测，而反射机制不仅仅需要在运行时对程序的自身数据进行检测，还需要根据检测到的数据修改程序的状态或者方法。

用于操作反射的相关的 5 个类：

- java.lang.Class：代表类；
- java.lang.reflect.Constructor：代表类的构造方法；
- java.lang.reflect.Field：代表类的属性；
- java.lang.reflect.Method：代表类的方法；
- java.lang.reflect.Modifier：代表类、方法、属性的修饰符；

Constructor、Field、Method 这三个类都继承 AccessibleObject，该对象有一个非常重要的方法 `AccessibleObject#setAccessible​(boolean flag)`，这里的所谓 accessible 可以理解成修饰成员的 public、protected、private，这意味着我们可以在程序运行时修改类成员的访问限制。

在类 Object 中有 `Object#getClass()`、`Object#hashCode()`、`Object#equals(Object obj)`、`Object#clone()`、`Object#toString()`、`Obect#notify()`、`Object#notifyAll()`、`Object#wait()` 等 public 权限的方法。而 `Object#getClass()` 方法则是返回程序运行时的 Class 类的对象实例。CLass 类也是同样继承 Object 类，拥有相应的方法。Class 类的类表示正在运行的 Java 应用程序中的类和接口。枚举是一种类，一个注解是一个接口。每个数组还属于一个反映为Class对象的类，该对象由具有相同元素类型和维数的所有数组共享。Java 的基本类型 boolean、byte、char、short、int、long、float、double，和关键字 void 也表示为 Class 对象。

Class 没有 public 类型的构造器。Java 虚拟机会在加载类时以及通过在类加载器中调用 `ClassLoader#defineClass()` 方法来自动构造 Class 对象。

Modifier 类提供了 static 方法和常量来解码类和成员访问修饰符。修饰符集合被表示为具有不同修饰符的不同位置的整数。表示的范围有 abstract、final、interface、native、private、protected、public、strict、synchronized、transient、volatile。

#### Constructor

> java.lang.reflect.Constructor<T>

Constructor 提供了一个类的单个构造函数的信息和访问。Constructor 允许在将实际参数与 newINstance() 与底层构造函数的形式参数进行匹配时进行扩展转换，如果发生缩小转换，则抛出 `IllegalArgumentException`。

|方法|含义|
|:--|:--|
|Constructor<?>[] getConstructors()|返回包含一个 Constructor 对象的数组，元素表示为所指定的类的所有的 public 权限的构造函数|
|getDeclaredConstructors()|表示返回包含 Constructor 对象的数组，元素表示为指定类的构造函数，包含非 public 权限的|
|getConstructor(class<?>... parameterTypes)|返回一个 Constructor 对象，表示指定参数的类的 public 权限的构造函数|
|getDeclaredConstructor(class<?>... parameterTypes)|表示返回一个表示 Constructor 对象，表示指定参数的构造函数，包含非 public 权限的|


#### Field

> java.lang.reflect.Field

 Field 提供有关类或接口的单个字段的信息和动态访问。 反射的字段可以是类（静态）字段或实例字段。Field 允许在获取或设置访问操作期间扩展转换，但如果发生缩小转换，则抛出 `IllegalArgumentException`。 

|方法|含义|
|:--|:--|
|getFields()|返回包含一个数组 Field 对象，表示的类或接口的所有可访问的公共字段|
|getDeclaredFields()|返回包含一个数组 Field 对象，表示的类或接口声明的所有字段|
|getField(String name)|返回包含一个数组 Field 对象，表示的类或接口指定的可访问的公共字段|
|getDeclaredField(String name)|返回包含一个数组 Field 对象，表示的类或接口指定的任意权限的公共字段|

#### Method

> java.lang.reflect.Method

Method 提供有关类和接口上单一方法的信息和访问权限。 反映的方法可以是类方法或实例方法（包括抽象方法）。

|方法|含义|
|:--|:--|
|getMethods()|获取类所有的 public 方法|
|getMethod(String name, class<?>... parameterTypes)|获取类特定的 public 方法|
|getDeclaredMethods()|获取类所有的方法|
|getDeclaredMethod(String name, class<?>... parameterTypes)|获取类特定的方法|

#### 反射机制原理

Java 虚拟机可以通过称为运行时类型信息（RTTI, Run Time Type Information）的技术在运行时检查任何类，这是通过一种称为 Class 对象的特殊对象完成的，该对象包含有关类的信息。

虚拟机为每个类管理一个独一无二的 Class 对象。也就是说，每个类都有一个 Class 对象实例。在运行程序的时候，JVM 首先需要会去检测所需加载的类的 Class 是否已经完成加载。如果没有加载在 JVM 中，那么 JVM 回去寻找对应类名的 .class 文件，完成对 Class 对象的加载。通过 Class 对象，我们可以实例化对应的 Class 类对象，调用其构造器（Constructor）、调用类的成员方法（Method）、访问或者修改类的成员属性（Field）。通过 `AccessibleObject#setAccessible​(boolean flag)` 可以访问到类的非 public 权限的其他成员，在上文提到通过 `AccessibleObject#setAccessible​(boolean flag)` 可以在程序运行时修改类成员的访问限制。实际上，`AccessibleObject#setAccessible​(boolean flag)` 关闭了权限的访问检查，使得通过 `Class#invoke()` 可以访问到任意权限的类成员。

#### Java反射实践

![](http://baihonghua.cn/Reflect_1.png)

![](http://baihonghua.cn/Reflect_2.png)

#### Java反射调用内部类

Java 的内部类可分为普通内部类、静态内部类。JVM 在编译含有普通内部类的时候，默认会在构造方法中传入外部类对象的引用，这也是为什么内部类对象会持有外部类的引用。我们可以通过解析 .class 字节码来验证这一推论。

![](http://baihonghua.cn/Reflect_4.png)

有上面的基础，我们可以推断出，在反射调用普通内部类的成员的时候，我们需要在普通内部类的构造方法中传入外部类的对象引用。而静态内部类由于不持有外部类的引用，因而不需要在其构造方法中传入外部类的引用。

举个例子。有一个类 `OutClass`，然后 `OutClass` 含有一个普通内部类 `InnerClass`、以及静态内部类 `StaticClass`。我们通过反射分别实例化它们的时候，如下图:

![](http://baihonghua.cn/Reflect_6.png)

反射调用普通内部类和静态内部类，只是在实例化的构造器的时候有区别，对于调用内部类的 Field、Method、Constructor，其过程是和调用普通类的过程时一样的，在这里就不一一细述了（主要因为我懒）。

#### 小结

Java 的反射机制用起来挺复杂的。但 Java 的反射机制在 Android 组件化中解耦合起到了很大的作用。可以在程序运行时访问类的成员属性或修改属性、执行方法、以及执行构造方法。并且在 Android 的许多源码中，有很多的属性、方法被标记了 @hide，但通过 Java 的反射，仍然可以访问这些属性、方法。













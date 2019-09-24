### Java注解系列笔记（一）

Annotation Processing 的理解可以为，在一些文献中的右上角的角标所对应的注解。

#### 注解的定义

```Java
puplic @interface TestAnnotation {}
```

#### 元注解

元注解可以理解为注解在注解上面的注解，或者是如在化学反应中，原子不可以再分，元注解也同样，在 Annotation Processing 中，元注解不可再分。

元注解的种类：@Retention、@Documented、@Target、@Inherited、@Repeatable

|@Retention|意义|
|:--:|:--|
|RetentionPolicy.SOURCE|注解只在源码阶段保留，在编译器进行编译时它将被丢弃忽略|
|RetentionPolicy.CLASS|注解被保留到编译进行的时候，它并不会加载到 JVM 中|
|RetentionPolicy.RUNTIME|注解可以保留到程序运行的时候，它会被加载进入 JVM 中，所以在程序运行时可以获取到它们|

理解：@Retention 定义了注解的有效时长；

|@Documented|意义|
|:--:|:--|
||作用是把将注解中的元素包含到 JavaDoc 中去|

|@Target|意义|
|:--:|:--|
|ElementType.ANNOTATION_TYPE|可以给一个注解进行注解|
|ElementType.CONSTRUCTOR|可以给构造方法进行注解|
|ElementType.FIELD|可以给属性进行注解|
|ElementType.LOCAL_VARIABLE|可以给局部变量进行注解|
|ElementType.METHOD|可以给方法进行注解|
|ElementType.PACKAGE|可以给一个包进行注解|
|ElementType.PARAMETER|可以给一个方法内的参数进行注解|
|ElementType.TYPE|可以给一个类型进行注解，比如类、接口、枚举|

理解：@Target 定义注解的注解的范围；

|@Inherited|意义|
|:--:|:--|
||作用是：如果被该元注解注解的超类，且其子类不被其他的注解所注解的话，该子类继承超类的注解；|

|@Repeatable|意义|
|:--:|:--|
|||


#### Java 预置的注解

|注解|意义|
|:--:|:--|
|@Deprecated|这个注解用来标记过时的元素；编译器在编译阶段遇到这个注解时会发出提醒警告，告诉开发者正在调用一个过时的元素比如过时的方法、过时的类、过时的成员变量。|
|@SuppressWarnings|阻止警告的意思。之前说过调用被 @Deprecated 注解的方法后，编译器会警告提醒，而有时候开发者会忽略这种警告，他们可以在调用的地方通过 @SuppressWarnings 达到目的。|
|@Override||
|@SafeVarargs|参数安全类型注解。它的目的是提醒开发者不要用参数做一些不安全的操作,它的存在会阻止编译器产生 unchecked 这样的警告。|

#### 注解的提取

### 注解与反射

可以通过 Class 对象的 isAnnotationPresent() 方法判断它是否应用了某个注解：

```Java
public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {}
```

通过 getAnnotation() 方法来获取 Annotation 对象：

```java
public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {}
```

或者：

```Java
public Annotation[] getAnnotations() {}
```
前一种方法返回指定类型的注解，后一种方法返回注解到这个元素上的所有注解。





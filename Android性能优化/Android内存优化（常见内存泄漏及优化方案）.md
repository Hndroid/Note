### 内存泄漏

#### 定义

如果一个无用对象（不需要再使用的对象）仍然被其他的对象持有引用，导致系统无法回收，以致该对象在堆中所占用的内存单元无法被释放而造成内存空间的浪费，这种情况就是内存泄漏；

#### 单例导致内存泄漏

单例的静态特性使得单例对象的生命周期同应用的生命周期一样长，如果一个传入的对象已经没有用处了，但是单例还是持有它的引用，那么在整个应用程序的生命周期它都不能正常被回收，从而导致内存泄漏；

如在单例模式下，需要传入一个上下文对象，但是传入的是某一个 Activity、或 Service 的实例，但在 Activity、或 Service 销毁的时候，该单例的对象还持有该引用，导致在应用的整个的生命周期内，该 Activity、或 Service 都不可以正常回收；

> 解决：尽量避免使用 Activity、或 Service 的生命周期，而是使用 Application 的生命周期；

#### 静态变量导致内存泄漏

静态变量初始化的时候，如传入了生命周期不一致的 Activity 的对象实例，导致在 Activity 退出的时候，静态变量仍然持有 Activity 的对象的引用，导致 Activity 不能被回收，导致内存泄漏；

> 解决：在创建静态变量持有变量的时候，需要多考虑各个成员之间的引用关系，考虑他们的生命周期是否可以一致。或者在适当的时候，可以把静态变量的引用置为 null，这样也可以避免内存泄漏；

---

#### 非静态内部类（匿名内部类）导致内存泄漏

非静态内部类默认就会持有外部类的引用，当非静态内部类的生命周期比其外部类的生命周期长的时候，会导致内存泄漏；

---

> 非静态内部类持有外部类的引用的原因：

```java
public class A {
    private class B {}
    public static void main(String[] args) {
        B b = new A().new B();
    }
}
```
查看 A$B.class 的字节码
```java
class A$B {
    private A$B(A var1) {
        this.this$0 = var1;
    }
}
```
由上面的两端代码可以看出，当 B 为 A 的内部类的时候，初始化 B 的时候，在 B 的构造器中，编译器会默认传入 A 类型的实例，赋值给 this.this$0，这就解析了为什么非静态内部类会持有外部类的引用；

---

***例子***
- ***在 Activity 里面直接实例化使用 Handler；***
- ***在 Activity 直接使用 new Thead()；***
- ***在 Activity 直接使用 new AsyncTask()；***

解决方法：Handler 使用 Activity 的软引用，依次类推。

#### 未取消注册或回调导致内存泄漏

如在 Activity 中使用广播；如使用 Retrofit + RxJava 注册网络请求的观察者回调，同样作为匿名内部类持有外部引用，所以需要记得不使用或者销毁的时候，记得取消注册；

#### Timer 和 TimerTask 导致内存泄漏

如在 Activity 内使用 Timer 和 Timertask 实现的无限轮播图，当该 Activity 销毁的时候，而 Timer 还在等待 Timertask，这样会导致内存泄漏；

#### 集合中的对象未清理造成内存泄漏

如集合中持有不再使用、需要销毁的对象，会导致内存泄漏；或静态的集合中持有不再使用的对象，这样更会导致内存泄漏；

#### 资源未关闭或释放导致内存泄漏

#### 属性动画造成内存泄漏

动画同样是一个耗时任务，比如在 Activity 中启动属性动画，在 Activity 销毁的时候，没有调用 cancle() 方法；

#### WebView 导致的内存泄漏

#### 小结

|内存泄漏|优化方案|
:--:|:--:
|单例导致内存泄漏|构造单例的时候尽量别用Activity 的引用|
|静态变量导致内存泄漏|静态变量注意置空引用或尽量少用静态变量|
|非静态内部类（匿名内部类）导致内存泄漏|使用静态内部类+软引用的方式代替非静态内部类|
|未取消注册或回调导致内存泄漏|取消注册广播或观察者模式|
|Timer 和 TimerTask 导致内存泄漏|耗时任务|
|集合中的对象未清理造成内存泄漏||
|资源未关闭或释放导致内存泄漏||
|属性动画造成内存泄漏|属性动画在Activity 销毁时记得cancel|
|WebView 导致的内存泄漏|Activity 销毁时 WebView 的移除和销毁|












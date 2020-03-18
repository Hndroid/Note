#### Kotlin的变量、函数和类型

Java 的字段（Field）在 Kotlin 里面被隐藏了，取而代之的是属性（property）；

Kotlin 的变量是没有默认值的，而 Java 的字段有默认值，但 Java 的局部变量同样也是没有默认值的；

Kotlin 的空安全：通过 IDE 的提示，来避免 null 对象的调用，从而避免 NullPointerException；

在 var 之前添加 lateinit 表示（延迟初始化）：让 IDE 不要对这个变量检查初始化和报错；另一种语意就是：确认该变量在使用之前，已经完成初始化；因为在 Kotlin 中，声明的变量不可以赋值为 null 的；

如果在 var 变量的类型后面添加 `？`，说明解除该变量的非空限制，也就是说，该变量可以像 Java 那样子赋值为 Null；但是可空变量在直接使用的时候，Kotlin 需要进行非空校验，不然编译器会报错；

在 Kotlin 中，还有一种只读变量，即 `val`，它只能被赋值一次，后续不可以修改；`var` 是 variable 的缩写，而 `val` 是 value 的缩写；

在 Kotlin 中，Kotlin 里变量默认就是 public 的；






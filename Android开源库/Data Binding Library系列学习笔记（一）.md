### Data Binding Library系列学习笔记（一）

`Data Binding Library` 有比较好的灵活性和兼容性：

- 灵活性：可以与传统的通过代码的方式把数据刷新到 xml 的方式并存；

- 兼容性：可以运行最低的 Android 4.0（API level 14）或者更高的 Android 版本；

使用 `Data Binding Library` 的时候推荐使用最新的 `Gradle Android` 插件，但 `Data Binding Library` 可以支持到最低的 1.5.0 版本；

#### 初始化 Data Binding Library 的使用环境

在使用 `Data Binding Library` 之前，需要在对应使用的 `module` 里面的 `build.gradle` 添加 `dataBinding` 元素，如下代码：

```java
android {
    ...
    dataBinding {
        enabled = true
    }
}
```

#### Android Studio 对 dataBinding 库的支持

Android Studio 对 dataBinding 有良好的支持，如：

- 语法高亮

- 标记表达式语法的错误

- 语法补全等

布局编辑器中的“预览”窗格显示数据绑定表达式的默认值（如果提供）。例如，“预览”窗格在以下示例中声明的TextView小部件上显示my_default值：

```java
<TextView android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@{user.firstName, default=my_default}"/>
```

#### 新的数据绑定编译器

Gradle 插件版本 `3.1.0-alpha06` 包含了用于生成新 `binding classes` 的数据绑定编译器，主要是为了加快构建 `binding classes` 的过程，主要是可以在构建成 APK 前就可以把 `binding classes` 给编译出来，防止在编译成 APK 的过程中，编译器没有找到所需要的 `binding classes`。

为了使新的 `data binding` 编译器有效，可以在 `gradle.properties` 加上下面的配置：

```java
android.databinding.enableV2=true
```
或者在 Gradle 命令中加入下面的配置：

```java
-Pandroid.databinding.enableV2=true
```

> Note:新的 data binding compiler 在 Gradle 版本 3.1 以后是不向后兼容的。3.2 的版本是默认使用新的 data binding compiler。



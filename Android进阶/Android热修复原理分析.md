### Android热修复原理学习系列笔记（一）

> 热修复：让应用能够在无需重新安装的情况下实时更新，帮助应用快速建立动态修复能力；

##### 热修复流程

![](http://baihonghua.cn/%E7%83%AD%E4%BF%AE%E5%A4%8D%E5%BC%80%E5%8F%91%E6%B5%81%E7%A8%8B.png)

##### ClassLoader介绍

任何一个 Java 程序都是有一个或者多个 class 文件组成，在程序运行时，需要将 class 文件加载到虚拟机中才可以使用，负责加载这些 class 文件的就是 Java 的类加载机制。`Classloader` 的作用简单来说就是加载 class 文件，提供给程序运行时使用。每个 Class 对象的内部都有一个 `Classloader` 字段来标识自己是由哪个 `Classloader` 加载的。

```Java
class Class<T> {
    ...
    private transient ClassLoader classLoader;
    ...
}
```

`Classloader` 是一个抽象类，而它的主要实现类主要有：

|实现类|作用|
|:--:|:--|
|`BootClassLoader`|用于加载 Android Framework 层 class 文件|
|`PathClassLoader`|用于 Android 应用程序类加载器。可以加载指定的 dex，以及 jar、zip、apk 中的 classes.dex|
|`DexClassLoader`|用于加载指定的 dex，以及 jar、zip、apk 中的 classes.dex|

> PathClassLoader 和 DexClassLoader 可以加载 sdcard 中的 dex 文件；


##### `DexClassLoader` 源码：

```Java
package dalvik.system;

import java.io.File;

public class DexClassLoader extends BaseDexClassLoader {
    public DexClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super((String)null, (File)null, (String)null, (ClassLoader)null);
        throw new RuntimeException("Stub!");
    }
}
```

##### `PathClassLoader` 源码：
```Java
package dalvik.system;

import java.io.File;

public class PathClassLoader extends BaseDexClassLoader {
    public PathClassLoader(String dexPath, ClassLoader parent) {
        super((String)null, (File)null, (String)null, (ClassLoader)null);
        throw new RuntimeException("Stub!");
    }

    public PathClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
        super((String)null, (File)null, (String)null, (ClassLoader)null);
        throw new RuntimeException("Stub!");
    }
}
```
##### `BaseDexClassLoader` 源码
```Java
package dalvik.system;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

public class BaseDexClassLoader extends ClassLoader {
    public BaseDexClassLoader(String dexPath, File optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        throw new RuntimeException("Stub!");
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        throw new RuntimeException("Stub!");
    }

    protected URL findResource(String name) {
        throw new RuntimeException("Stub!");
    }

    protected Enumeration<URL> findResources(String name) {
        throw new RuntimeException("Stub!");
    }

    public String findLibrary(String name) {
        throw new RuntimeException("Stub!");
    }

    protected synchronized Package getPackage(String name) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}
```

由以上的 `PathClassLoader` 源码、`PathClassLoader` 源码和 `BaseDexClassLoader` 源码分析可得知：

![](http://baihonghua.cn/ClassLoader%E7%BB%A7%E6%89%BF%E5%85%B3%E7%B3%BB.png)




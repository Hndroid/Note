#### 前言

> [JavaPoet](https://github.com/square/javapoet) is a Java API for generating .java source files.

`JavaPoet` 是一个用于生成 `.java` 源文件的 `Java API`, 在许多如 `ButterKnift`、`ARoute` 等使用了 APT 技术的开源库中，都可以看到 `JavaPoet` 的影子。在解析注解生成 Java 源文件的过程中，`JavaPoet` 起的作用很大。

![](http://baihonghua.cn/JavaPoet%E6%96%87%E4%BB%B6%E7%BB%93%E6%9E%84.png)

上图是 `JavaPoet` 库的文件结构：

- `AnnotationSpec` 用于生成注解相关的 API；

- `FieldSpec` 用于生成属性变量字段相关的 API；

- `MethodSpec` 用于生成类的构造器、或者方法相关的 API；

- `ParameterSpec` 用于生成方法参数相关的 API；

- `TypeSpec` 用于生成 `class` 类、接口、或者 `enum` 相关的 API；

- `className` 用于声明在 `.java` 文件中通过 `import` 关键字导入

#### 例子

在 Android Studio 中新建一个 Java 库，然后在 `.gradle` 文件中添加 `JavaPoet` 的依赖，效果如下：

![](http://baihonghua.cn/JavaPoet%E4%BE%9D%E8%B5%96%E5%BA%93.png)

在了解 `JavaPoet` 生成 `.java` 文件的规则之前，有必要先了解 `JavaPoet` 的字符串格式化规则:

|符号|意义|
|:--:|:--|
| `$L` |表示字面量，如: `int value = $L`|
| `$S` |表示字符串|
| `$T` |可以表示类、接口|
| `$N` |表示变量、函数名，和 `$S` 区分，主要是 `$N` 有实际的意义|

##### # 例子_1

如下，如果我们知道我们要生成的类格式如下：

```java
package com.example.helloworld;

import java.lang.String;
import java.lang.System;

public final class HelloWorld {
  public static void main(String[] args) {
    System.out.println("Hello, JavaPoet!");
  }
}
```

通过 `JavaPoet` 库，我们可以通过如下 API ：

```java
// 生成 main 方法
MethodSpec mainMethod = MethodSpec.methodBuilder("main")
        .addParameter(String[].class, "args")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
        .build();

// 生成 HelloWorld 类
TypeSpec helloWorldClass = TypeSpec.classBuilder("HelloWorld")
        .addMethod(mainMethod)
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .build();

// 生成 .java 文件
JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorldClass)
        .build();

try {
    // 把生成的 .class 文件内容写出到标准控制台
    javaFile.writeTo(System.out);
} catch (IOException e) {
    e.printStackTrace();
}
```

效果如下：

![](http://baihonghua.cn/JavaPoet-HelloWorld.png)

##### # 例子_2

```java
void main() {
    long now = System.currentTimeMillis();
    if (System.currentTimeMillis() < now) {
    System.out.println("Time travelling, woo hoo!");
    } else if (System.currentTimeMillis() == now) {
    System.out.println("Time stood still!");
    } else {
    System.out.println("Ok, time still moving forward");
    }
}
```
可以通过 `JavaPoet` 生成：

```java
MethodSpec mainMethod = MethodSpec.methodBuilder("main")
                .addStatement("long now = $T.currentTimeMillis()", System.class)
                .beginControlFlow("if ($T.currentTimeMillis() < now)", System.class)
                .addStatement("$T.out.println($S)", System.class, "Time travelling, woo hoo!")
                .nextControlFlow("else if ($T.currentTimeMillis() == now)", System.class)
                .addStatement("$T.out.println($S)", System.class, "Time stood still!")
                .nextControlFlow("else")
                .addStatement("$T.out.println($S)", System.class, "Ok, time still moving forward")
                .endControlFlow()
                .build();
```

对于上面的 `beginControlFlow()`、`nextControlFlow()`、`endControlFlow()`, 一般可以用在循环、异常捕获、条件判断等。注意，在循环、异常捕获、条件判断的花括号最后要加上 `endControlFlow()`。

##### # 例子_3

ClassName类型它可以标识任何声明的类。声明的类型只是Java丰富类型系统的开始：我们还具有数组，参数化类型，通配符类型和类型变量。JavaPoet具有用于构建以下每个类的类：

```java
ClassName hoverboard = ClassName.get("com.mattel", "Hoverboard");
ClassName list = ClassName.get("java.util", "List");
ClassName arrayList = ClassName.get("java.util", "ArrayList");
TypeName listOfHoverboards = ParameterizedTypeName.get(list, hoverboard);

MethodSpec beyond = MethodSpec.methodBuilder("beyond")
    .returns(listOfHoverboards)
    .addStatement("$T result = new $T<>()", listOfHoverboards, arrayList)
    .addStatement("result.add(new $T())", hoverboard)
    .addStatement("result.add(new $T())", hoverboard)
    .addStatement("result.add(new $T())", hoverboard)
    .addStatement("return result")
    .build();
```

生成的 `.java` 文件如下:

```java
package com.example.helloworld;

import com.mattel.Hoverboard;
import java.util.ArrayList;
import java.util.List;

public final class HelloWorld {
  List<Hoverboard> beyond() {
    List<Hoverboard> result = new ArrayList<>();
    result.add(new Hoverboard());
    result.add(new Hoverboard());
    result.add(new Hoverboard());
    return result;
  }
}
```

##### # 例子_4

JavaPoet支持导入静态。它通过显式收集类型成员名称来实现。

```java
...
ClassName namedBoards = ClassName.get("com.mattel", "Hoverboard", "Boards");

MethodSpec beyond = MethodSpec.methodBuilder("beyond")
    .returns(listOfHoverboards)
    .addStatement("$T result = new $T<>()", listOfHoverboards, arrayList)
    .addStatement("result.add($T.createNimbus(2000))", hoverboard)
    .addStatement("result.add($T.createNimbus(\"2001\"))", hoverboard)
    .addStatement("result.add($T.createNimbus($T.THUNDERBOLT))", hoverboard, namedBoards)
    .addStatement("$T.sort(result)", Collections.class)
    .addStatement("return result.isEmpty() ? $T.emptyList() : result", Collections.class)
    .build();

TypeSpec hello = TypeSpec.classBuilder("HelloWorld")
    .addMethod(beyond)
    .build();

JavaFile.builder("com.example.helloworld", hello)
    .addStaticImport(hoverboard, "createNimbus")
    .addStaticImport(namedBoards, "*")
    .addStaticImport(Collections.class, "*")
    .build();
```

生成的 `.java` 文件如下:

```java
package com.example.helloworld;

import static com.mattel.Hoverboard.Boards.*;
import static com.mattel.Hoverboard.createNimbus;
import static java.util.Collections.*;

import com.mattel.Hoverboard;
import java.util.ArrayList;
import java.util.List;

class HelloWorld {
  List<Hoverboard> beyond() {
    List<Hoverboard> result = new ArrayList<>();
    result.add(createNimbus(2000));
    result.add(createNimbus("2001"));
    result.add(createNimbus(THUNDERBOLT));
    sort(result);
    return result.isEmpty() ? emptyList() : result;
  }
}
```

##### # $N

生成的代码通常是自引用的。使用 `$N` 通过其名称引用另一个生成的声明。这是一个调用另一个的方法：

```java
public String byteToHex(int b) {
  char[] result = new char[2];
  result[0] = hexDigit((b >>> 4) & 0xf);
  result[1] = hexDigit(b & 0xf);
  return new String(result);
}

public char hexDigit(int i) {
  return (char) (i < 10 ? i + '0' : i - 10 + 'a');
}
```

在生成上面的代码时，我们使用 `$N` 将 `hexDigit()` 方法作为参数传递给 `byteToHex()` 方法：

```java
MethodSpec hexDigit = MethodSpec.methodBuilder("hexDigit")
    .addParameter(int.class, "i")
    .returns(char.class)
    .addStatement("return (char) (i < 10 ? i + '0' : i - 10 + 'a')")
    .build();

MethodSpec byteToHex = MethodSpec.methodBuilder("byteToHex")
    .addParameter(int.class, "b")
    .returns(String.class)
    .addStatement("char[] result = new char[2]")
    .addStatement("result[0] = $N((b >>> 4) & 0xf)", hexDigit)
    .addStatement("result[1] = $N(b & 0xf)", hexDigit)
    .addStatement("return new String(result)")
    .build();
```

##### # 构造函数

使用 `MethodSpec` 也可以生成类的构造函数。

```java
MethodSpec flux = MethodSpec.constructorBuilder()
    .addModifiers(Modifier.PUBLIC)
    .addParameter(String.class, "greeting")
    .addStatement("this.$N = $N", "greeting", "greeting")
    .build();

TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
    .addModifiers(Modifier.PUBLIC)
    .addField(String.class, "greeting", Modifier.PRIVATE, Modifier.FINAL)
    .addMethod(flux)
    .build();
```
生成的代码如下：

```java
public class HelloWorld {
  private final String greeting;

  public HelloWorld(String greeting) {
    this.greeting = greeting;
  }
}
```

##### # 参数

使用 `ParameterSpec.builder()` 或 `MethodSpec` 的 API `addParameter()` 在方法和构造函数上声明参数：

```java

ParameterSpec android = ParameterSpec.builder(String.class, "android")
    .addModifiers(Modifier.FINAL)
    .build();

MethodSpec welcomeOverlords = MethodSpec.methodBuilder("welcomeOverlords")
    .addParameter(android)
    .addParameter(String.class, "robot", Modifier.FINAL)
    .build();
```

生成的代码如下：

```java
void welcomeOverlords(final String android, final String robot) {
}
```

##### # 字段

使用 `FieldSpec#builder()` 或 `TypeSpec#addField()` 生成字段。

```java
FieldSpec android = FieldSpec.builder(String.class, "android")
    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
    .build();

TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
    .addModifiers(Modifier.PUBLIC)
    .addField(android)
    .addField(String.class, "robot", Modifier.PRIVATE, Modifier.FINAL)
    .build();
```

生成的代码如下：

```java
public class HelloWorld {
  private final String android;

  private final String robot;
}
```

##### # 接口

对于 `JavaPoet` 的接口，请注意，接口方法必须始终为 `PUBLIC ABSTRACT`，并且接口字段必须始终为 `PUBLIC STATIC FINAL`。定义接口时，必须使用以下修饰符：

```java
TypeSpec helloWorld = TypeSpec.interfaceBuilder("HelloWorld")
    .addModifiers(Modifier.PUBLIC)
    .addField(FieldSpec.builder(String.class, "ONLY_THING_THAT_IS_CONSTANT")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", "change")
        .build())
    .addMethod(MethodSpec.methodBuilder("beep")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .build())
    .build();
```

但是，在生成代码时将省略这些修饰符。

```java
public interface HelloWorld {
  String ONLY_THING_THAT_IS_CONSTANT = "change";

  void beep();
}
```

##### # 枚举

```java
TypeSpec helloWorld = TypeSpec.enumBuilder("Roshambo")
    .addModifiers(Modifier.PUBLIC)
    .addEnumConstant("ROCK")
    .addEnumConstant("SCISSORS")
    .addEnumConstant("PAPER")
    .build();
```

生成的代码如下：

```java
public enum Roshambo {
  ROCK,

  SCISSORS,

  PAPER
}
```

#### 小结

`JavaPoet` 还有挺多细节的生成规则，建议感兴趣的小伙伴可以阅读 `JavaPoet` 的官方文档。








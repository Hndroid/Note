#### 前言

JavaPoet是用于生成.java源文件的Java API。

在执行诸如批注处理或与元数据文件（例如，数据库模式，协议格式）交互之类的操作时，源文件的生成可能非常有用。
通过生成代码，您无需编写样板文件，同时还保留了元数据的唯一真实来源。

#### 实践

```java
// 需要输出的模板
package com.example.hellopoet;

import java.lang.String;
import java.lang.System;

public final class HelloPoet {
  public static void main(String[] args) {
    System.out.println("Hello JavaPoet");
  }
}


// 从内到外的方式

// 1.构建方法
MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello JavaPoet")
                .build();
// 2.构建类
TypeSpec helloPoet = TypeSpec.classBuilder("HelloPoet")
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addMethod(main)
        .build();

// 3.构建文件
JavaFile javaFile = JavaFile.builder("com.example.hellopoet", helloPoet)
        .build();

try {
    javaFile.writeTo(System.out);
} catch (IOException e) {
    e.printStackTrace();
}

// 输出结果
//package com.example.hellopoet;
//
//import java.lang.String;
//import java.lang.System;
//
//public final class HelloPoet {
//    public static void main(String[] args) {
//        System.out.println("Hello JavaPoet");
//    }
//}
```

为了声明 main 方法，我们创建了一个 MethodSpec"main"，配置了修饰符，返回类型，参数和代码语句。

我们将 main 方法添加到 HelloPoet 类，然后将其添加到 HelloPoet.java 文件。

在这种情况下，我们将文件写入 System.out，但是我们也可以将其作为字符串（JavaFile.toString（））或将其写入文件系统（JavaFile.writeTo（））。

#### 代码与控制流程

JavaPoet 的大多数 API 使用普通的老式不可变 Java 对象。还有使 API 友好的构建器，方法链和 varargs。JavaPoet 提供了用于类和接口（TypeSpec），字段（FieldSpec），方法和构造函数（MethodSpec），参数（ParameterSpec）和注释（AnnotationSpec）的模型。但是方法和构造函数的主体未建模。没有表达式类，语句类或语法树节点。相反，JavaPoet使用字符串作为代码块：

```java
MethodSpec main = MethodSpec.methodBuilder("main")
    .addCode(""
        + "int total = 0;\n"
        + "for (int i = 0; i < 10; i++) {\n"
        + "  total += i;\n"
        + "}\n")
    .build();

// 输出结果
void main() {
  int total = 0;
  for (int i = 0; i < 10; i++) {
    total += i;
  }
}
```

手动分号，换行和缩进非常繁琐，因此 JavaPoet 提供了 API 来简化此操作。有 addStatement() 负责分号和换行符，以及 beginControlFlow() + endControlFlow() 一起用于括号，换行符和缩进：

```java
MethodSpec main = MethodSpec.methodBuilder("main")
    .addStatement("int total = 0")
    .beginControlFlow("for (int i = 0; i < 10; i++)")
    .addStatement("total += i")
    .endControlFlow()
    .build();
```

这个例子很 la 脚，因为生成的代码是恒定的！假设我们不希望仅将0加到10，而是要使操作和范围可配置。这是一个生成方法的方法：

```java
private MethodSpec computeRange(String name, int from, int to, String op) {
  return MethodSpec.methodBuilder(name)
      .returns(int.class)
      .addStatement("int result = 1")
      .beginControlFlow("for (int i = " + from + "; i < " + to + "; i++)")
      .addStatement("result = result " + op + " i")
      .endControlFlow()
      .addStatement("return result")
      .build();
}            
```



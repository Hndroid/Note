
##### # all()

```java
private void learnRxAll() {

    Observable.just("1", "2", "3", "4")
            .all(new Predicate<String>() {
                @Override
                public boolean test(String pS) throws Throwable {
                    return !pS.equals("c");
                }
            }).subscribe(new Consumer<Boolean>() {
        @Override
        public void accept(Boolean pBoolean) throws Throwable {
            Log.d("条件型操作符 all", "accept: ----> " + pBoolean);
        }
    });

//        2020-04-04 22:54:14.364 25017-25017/com.example.learnrxjava D/条件型操作符 all: accept: ----> true
}
```

条件型操作符 `all()` 只有上游发射的事件都满足 `true` 才完下游发射 `true`, 如果有一个事件不满足，则返回 `false`。

##### # any()

```java

 private void learnRxAny() {

    Observable.just("JavaSE", "C++", "C", "Java")
            .any(new Predicate<String>() {
                @Override
                public boolean test(String pS) throws Throwable {
                    return pS.equals("Android");
                }
            })
            .subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean pBoolean) throws Throwable {
                    Log.d("条件型操作符 any", "accept: ----> " + pBoolean);
                }
            });

    //04-05 17:46:12.981 9714-9714/com.example.learnrxjava D/条件型操作符 any: accept: ----> false
}

```

条件型操作符 `any()` 全部为 `false` 才是 `false`, 只有一个为 `true` 就是 `true`。如果在上游使用了条件操作符，那么在下游接收的类型是条件类型（boolean）。

![](http://baihonghua.cn/%E5%BE%AE%E4%BF%A1%E5%85%AC%E4%BC%97%E5%8F%B7%E4%BA%8C%E7%BB%B4%E7%A0%81.png)




#### 前言

在 RxJava 中，上游和下游的界限有时候并不是很明显。我的理解是：RxJava 的上下游的区分需要以观察者为比较的参照物，在观察者的之前的，都是可以看着上游。

如下面的 `map()` 变换操作符，`just()` 和 `map()` 操作符都可以看着上游，因为观察者订阅事件是发生在 `Consumer` 匿名对象中。

#### 变换型操作符实战

##### # map()

```java
private void learnRxMap() {

    Observable.just(1, 2, 3, 4)// 生产事件
            // 变换事件
            .map(new Function<Integer, String>() {
                @Override
                public String apply(Integer pInteger) throws Throwable {
                    return "[ " + pInteger + " ]";
                }
            })
            // 观察者订阅事件
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String pS) throws Throwable {
                    Log.d("变换型操作符 map ", "accept: ----> " + pS);
                }
            });

//        2020-04-04 16:11:18.227 9596-9596/com.example.learnrxjava D/变换型操作符 map: accept: ----> [ 1 ]
//        2020-04-04 16:11:18.227 9596-9596/com.example.learnrxjava D/变换型操作符 map: accept: ----> [ 2 ]
//        2020-04-04 16:11:18.228 9596-9596/com.example.learnrxjava D/变换型操作符 map: accept: ----> [ 3 ]
//        2020-04-04 16:11:18.228 9596-9596/com.example.learnrxjava D/变换型操作符 map: accept: ----> [ 4 ]

}
```
操作符 `map()` 可以看着上下游的之间的节点，把上游的 `Integer` 类型的变量，变换为 `String` 类型的字符串，而 `map()` 作为中间的节点可以变换上游的事件性质；

##### # flatMap()

```java

private void learnRxFlatMap() {

    String[] lStrings = {"learn ", "RxJava's ", "Op"};

    Observable.fromArray(lStrings)
            .flatMap(new Function<String, ObservableSource<String>>() {
                @Override
                public ObservableSource<String> apply(final String pS) throws Throwable {

                    return new Observable<String>() {
                        @Override
                        protected void subscribeActual(@NonNull Observer<? super String> observer) {
                            observer.onNext(pS + "// ");
                            observer.onNext(pS + "== ");
                        }
                    };
                }
            })
            .subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(@NonNull String pS) {
                    Log.d("变换型操作符 onNext", "onNext: ----> " + pS);
                }

                @Override
                public void onError(@NonNull Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });

//        2020-04-04 16:49:36.806 26443-26443/? D/变换型操作符 onNext: onNext: ----> learn // 
//        2020-04-04 16:49:36.806 26443-26443/? D/变换型操作符 onNext: onNext: ----> learn == 
//        2020-04-04 16:49:36.806 26443-26443/? D/变换型操作符 onNext: onNext: ----> RxJava's // 
//        2020-04-04 16:49:36.806 26443-26443/? D/变换型操作符 onNext: onNext: ----> RxJava's == 
//        2020-04-04 16:49:36.806 26443-26443/? D/变换型操作符 onNext: onNext: ----> Op// 
//        2020-04-04 16:49:36.806 26443-26443/? D/变换型操作符 onNext: onNext: ----> Op==
}

```

操作符 `flatMap` 作为 RxJava 的中介节点，可以像 `map()` 操作一样，变换上游的事件性质，同时在还可以通过 `ObservableSource<?>>()` 在变换事件性质的同时，同时向下游多次发射事件。

```java
private void learnRxFlatMap() {

        String[] lStrings = {"learn ", "RxJava's ", "Op"};

        Observable.fromArray(lStrings)
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final String pS) throws Throwable {

                        List<String> lStringList = new ArrayList<>();

                        for (int i = 0; i < 5; i++) {
                            lStringList.add(pS + "下标 " + "[" + i + "]");
                        }
                        // 模拟网络延迟
                        return Observable.fromIterable(lStringList).delay(5, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String pS) {
                        Log.d("变换型操作符 onNext", "onNext: ----> " + pS);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

//        2020-04-04 17:08:55.488 27958-27989/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> learn 下标 [0]
//        2020-04-04 17:08:55.489 27958-27989/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> learn 下标 [1]
//        2020-04-04 17:08:55.490 27958-27989/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> learn 下标 [2]
//        2020-04-04 17:08:55.491 27958-27989/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> learn 下标 [3]
//        2020-04-04 17:08:55.492 27958-27989/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> learn 下标 [4]
//        2020-04-04 17:08:55.493 27958-27990/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> RxJava's 下标 [0]
//        2020-04-04 17:08:55.494 27958-27990/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> RxJava's 下标 [1]
//        2020-04-04 17:08:55.494 27958-27991/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> Op下标 [0]
//        2020-04-04 17:08:55.495 27958-27990/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> RxJava's 下标 [2]
//        2020-04-04 17:08:55.495 27958-27991/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> Op下标 [1]
//        2020-04-04 17:08:55.496 27958-27990/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> RxJava's 下标 [3]
//        2020-04-04 17:08:55.497 27958-27991/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> Op下标 [2]
//        2020-04-04 17:08:55.497 27958-27991/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> RxJava's 下标 [4]
//        2020-04-04 17:08:55.498 27958-27991/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> Op下标 [3]
//        2020-04-04 17:08:55.499 27958-27991/com.example.learnrxjava D/变换型操作符 onNext: onNext: ----> Op下标 [4]
    }
```

操作符 `flatMap()` 在把上游的事件变换为多个事件继续向下游发射的同时，并不是顺序向下游发射的。

##### # concatMap()

```java
private void learnRxConcatMap() {
    Observable.just("AA", "BBBB", "CVD")
            .concatMap(new Function<String, ObservableSource<String>>() {
                @Override
                public ObservableSource<String> apply(String pS) throws Throwable {
                    List<String> lStringList = new ArrayList<>();

                    for (int i = 0; i < 5; i++) {
                        lStringList.add(pS + "下标 " + "[" + i + "]");
                    }
                    return Observable.fromIterable(lStringList).delay(5, TimeUnit.SECONDS);
                }
            })
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String pS) throws Throwable {
                    Log.d("变换型操作符 concatMap", "accept: ----> " + pS);
                }
            });

//        2020-04-04 17:18:22.629 29058-29091/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> AA下标 [0]
//        2020-04-04 17:18:22.630 29058-29091/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> AA下标 [1]
//        2020-04-04 17:18:22.630 29058-29091/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> AA下标 [2]
//        2020-04-04 17:18:22.630 29058-29091/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> AA下标 [3]
//        2020-04-04 17:18:22.630 29058-29091/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> AA下标 [4]
//        2020-04-04 17:18:27.665 29058-29095/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> BBBB下标 [0]
//        2020-04-04 17:18:27.667 29058-29095/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> BBBB下标 [1]
//        2020-04-04 17:18:27.667 29058-29095/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> BBBB下标 [2]
//        2020-04-04 17:18:27.668 29058-29095/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> BBBB下标 [3]
//        2020-04-04 17:18:27.669 29058-29095/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> BBBB下标 [4]
//        2020-04-04 17:18:32.706 29058-29105/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> CVD下标 [0]
//        2020-04-04 17:18:32.707 29058-29105/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> CVD下标 [1]
//        2020-04-04 17:18:32.708 29058-29105/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> CVD下标 [2]
//        2020-04-04 17:18:32.709 29058-29105/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> CVD下标 [3]
//        2020-04-04 17:18:32.709 29058-29105/com.example.learnrxjava D/变换型操作符 concatMap: accept: ----> CVD下标 [4]
}
```

变换操作符 `concatMap()` 和 `flatMap()` 操作符的功能一样，也是可以把上游的事件变换成多个事件向下游发送，但是 `concatMap()` 在多次发射事件的时候，是排序向下游发送的。

##### # groupBy()

```java
private void learnRxGroupBy() {

    Observable.just(10, 20, 30, 40, 50)
            .groupBy(new Function<Integer, String>() {
                @Override
                public String apply(Integer pInteger) throws Throwable {
                    return pInteger > 30 ? "中年程序员" : "年轻的程序员";
                }
            })
            .subscribe(new Consumer<GroupedObservable<String, Integer>>() {
                @Override
                public void accept(final GroupedObservable<String, Integer> pGroupedObservable) throws Throwable {

                    Log.d("分类的 Key ", "accept: " + pGroupedObservable.getKey());

                    pGroupedObservable.subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer pInteger) throws Throwable {
                            Log.d("变换操作符 groupBy", "accept: ----> " + pGroupedObservable.getKey() + "年龄 ：" + pInteger);
                        }
                    });
                }
            });

//        2020-04-04 17:32:21.656 3023-3023/com.example.learnrxjava D/分类的 Key: accept: 年轻的程序员
//        2020-04-04 17:32:21.656 3023-3023/com.example.learnrxjava D/变换操作符 groupBy: accept: ----> 年轻的程序员年龄 ：10
//        2020-04-04 17:32:21.657 3023-3023/com.example.learnrxjava D/变换操作符 groupBy: accept: ----> 年轻的程序员年龄 ：20
//        2020-04-04 17:32:21.657 3023-3023/com.example.learnrxjava D/变换操作符 groupBy: accept: ----> 年轻的程序员年龄 ：30
//        2020-04-04 17:32:21.657 3023-3023/com.example.learnrxjava D/分类的 Key: accept: 中年程序员
//        2020-04-04 17:32:21.657 3023-3023/com.example.learnrxjava D/变换操作符 groupBy: accept: ----> 中年程序员年龄 ：40
//        2020-04-04 17:32:21.658 3023-3023/com.example.learnrxjava D/变换操作符 groupBy: accept: ----> 中年程序员年龄 ：50
}
```

操作符 `groupBy()` 的作用主要是在其节点中，把上游的事件按照一定的标准分类成 `group`，然后再发送往下游。但下游需要按照一定的模板才可以把 `group` 分类出来。

##### # buffer()

```java
  private void learnRxBuffer() {
    Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
            for (int lI = 0; lI < 100; lI++) {
                emitter.onNext(lI);
            }
            emitter.onComplete();
        }
    })
            .buffer(20)
            .subscribe(new Consumer<List<Integer>>() {
                @Override
                public void accept(List<Integer> pIntegers) throws Throwable {
                    Log.d("变换操作符 buffer", "accept: ----> " + pIntegers);
                }
            });

//        2020-04-04 17:49:38.264 13267-13267/com.example.learnrxjava D/变换操作符 buffer: accept: ----> [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
//        2020-04-04 17:49:38.264 13267-13267/com.example.learnrxjava D/变换操作符 buffer: accept: ----> [20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39]
//        2020-04-04 17:49:38.265 13267-13267/com.example.learnrxjava D/变换操作符 buffer: accept: ----> [40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59]
//        2020-04-04 17:49:38.265 13267-13267/com.example.learnrxjava D/变换操作符 buffer: accept: ----> [60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79]
//        2020-04-04 17:49:38.266 13267-13267/com.example.learnrxjava D/变换操作符 buffer: accept: ----> [80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99]
}
```

对于上游有大量的事件发送下来的时候，如果需要先把事件缓存到指定的数量后，再往下游发送，则变换操作符 `buffer()` 可以做到先缓存事件到指定的数量，然后再把缓存的事件一起发送往下游。

![](http://baihonghua.cn/%E5%BE%AE%E4%BF%A1%E5%85%AC%E4%BC%97%E5%8F%B7%E4%BA%8C%E7%BB%B4%E7%A0%81.png)
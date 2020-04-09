#### 前言

在实战 RxJava 之前，先谈谈我自己对 RxJava 的理解，我们知道 RxJava 是基于观察者模式的，我们可以把 RxJava 的使用看着整体的一条链：

- 在链的最上游：是生成、发送、或者发射事件的 `Observable`；
- 在链的最下游：是根据上游的生成、发送、或者发射事件来做出相对应的动作的 `Observer`;
- 在链的中游：作为中介的节点，变动上下游的性质，如线程的切换等；

我想，挺多的小伙伴可能在刚接触 RxJava 的时候，会感到疑惑：为什么是被观察者（Observable）订阅（subscribe）观察者（Observer），而不是观察者订阅被观察者？

我认为可以这么理解吧：首先你需要知道 Android 是基于事件驱动模型的，也就是说程序的执行流程是根据用户的动作（例如用户的屏幕触摸）触发的事件决定的。程序的执行流程在 RxJava 中就好比作观察者做出的动作，用户触发的事件就好比作被观察者产生的事件，程序的执行流程是代码固死的了，而用户的动作则是不确定的，程序代码根据用户操作执行相应的代码（如用户点击声量按钮只会调整音量，而不会让手机关机）。

然后为什么不是观察者订阅被观察者，其实这只是这样的写法更加符合事件驱动编程，先把观察者的代码加载进内存，以等待被观察者的到来后（如网络请求后的响应），执行对应的代码，因为，你永远不知道你的明天会怎样，唯一的只能先做好准备去面对。

#### 创建操作符实践

##### # create()

```java
/**
* 创建型操作符: create
*/
private void learnRxCreate() {
    Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onComplete();
        }
    }).subscribe(new Observer<Integer>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d("创建型操作符: create", "onSubscribe: ----> " + d.isDisposed());
        }

        @Override
        public void onNext(@NonNull Integer integer) {
            Log.d("创建型操作符: create", "onNext: ----> " + integer);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d("创建型操作符: create", "onError: ----> " + e);
        }

        @Override
        public void onComplete() {
            Log.d("创建型操作符: create", "onComplete: ");
        }
    });

     /*********************************打印的结果***************************************/
//    2020-04-03 14:37:48.543 16088-16088/com.example.learnrxjava D/创建型操作符: create: onSubscribe: ----> false
//    2020-04-03 14:37:48.543 16088-16088/com.example.learnrxjava D/创建型操作符: create: onNext: ----> 1
//    2020-04-03 14:37:48.543 16088-16088/com.example.learnrxjava D/创建型操作符: create: onNext: ----> 2
//    2020-04-03 14:37:48.543 16088-16088/com.example.learnrxjava D/创建型操作符: create: onComplete:
}
```

创建类型的 `Observable#create()` 可以自己作为事件的生产者，往下游发送事件；

- 在上游的 `onComplete()` 或 `onError()` 方法执行完以后，再通过 `onNext()` 发送事件，下游不在接收上游的事件；

- 上游已经发送了 `onComplete()` 以后再发送 `onError()`, RxJava 会报错；

- 上游先发送 `onError()`, 然后再发送 `onComplete()`，RxJava 不会报错，但下游不再接收 `onComplete()` 事件；

##### # just()

```java
/**
* 创建型操作符： just
*/
private void learnRxJust() {
        Observable.just("test", "just")
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d("创建型操作符: just", "onSubscribe: ----> " + d.isDisposed());
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.d("创建型操作符: just", "onNext: ----> " + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("创建型操作符: just", "onError: ----> " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d("创建型操作符: just", "onComplete: ");
                    }
                });

//        2020-04-03 16:09:01.604 20917-20917/com.example.learnrxjava D/创建型操作符: just: onSubscribe: ----> false
//        2020-04-03 16:09:01.604 20917-20917/com.example.learnrxjava D/创建型操作符: just: onNext: ----> test
//        2020-04-03 16:09:01.605 20917-20917/com.example.learnrxjava D/创建型操作符: just: onNext: ----> just
//        2020-04-03 16:09:01.605 20917-20917/com.example.learnrxjava D/创建型操作符: just: onComplete:
    }
```

创建型操作符 `Observable#just()` 在自己的内部发送事件，下游接收事件；

##### # fromArray()

```java
/**
* 创建型操作符： fromArray
*/
private void learnRxFromArray() {

        String[] str = {"1", "2", "3"};

        // 简化版的下游接受者
        Observable.fromArray(str)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Throwable {
                        Log.d("简化版的下游观察者 Consumer", "accept: ----> " + s);
                    }
                });

//        2020-04-03 16:20:17.701 27695-27695/com.example.learnrxjava D/简化版的下游观察者 Consumer: accept: ----> 1
//        2020-04-03 16:20:17.701 27695-27695/com.example.learnrxjava D/简化版的下游观察者 Consumer: accept: ----> 2
//        2020-04-03 16:20:17.701 27695-27695/com.example.learnrxjava D/简化版的下游观察者 Consumer: accept: ----> 3

        Observable.fromArray(str)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d("创建型操作符 fromArray", "onSubscribe: ----> " + d.isDisposed());
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.d("创建型操作符 fromArray", "onNext: ----> " + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("创建型操作符 fromArray", "onError: ----> " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d("创建型操作符 fromArray", "onComplete:");
                    }
                });

//        2020-04-03 16:23:49.046 28351-28351/com.example.learnrxjava D/创建型操作符 fromArray: onSubscribe: ----> false
//        2020-04-03 16:23:49.046 28351-28351/com.example.learnrxjava D/创建型操作符 fromArray: onNext: ----> 1
//        2020-04-03 16:23:49.046 28351-28351/com.example.learnrxjava D/创建型操作符 fromArray: onNext: ----> 2
//        2020-04-03 16:23:49.046 28351-28351/com.example.learnrxjava D/创建型操作符 fromArray: onNext: ----> 3
//        2020-04-03 16:23:49.046 28351-28351/com.example.learnrxjava D/创建型操作符 fromArray: onComplete:
    }
```
创建型操作符 `Observable#fromArray()` 在自己的内部发射事件，但发射的对象需要是数组类型，因为在阅读 `Observable#fromArray()` 的源码可以发现，`Observable#fromArray()` 接收的参数是可变类型参数：

```java
public static <T> Observable<T> fromArray(@NonNull T... items) {
        Objects.requireNonNull(items, "items is null");
        if (items.length == 0) {
            return empty();
        }
        if (items.length == 1) {
            return just(items[0]);
        }
        return RxJavaPlugins.onAssembly(new ObservableFromArray<>(items));
    }
```

##### # empty()

```java
/**
* 创建型操作符： empty
*/
private void learnRxEmpty() {
        Observable.empty()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d("创建型操作符 empty", "onSubscribe: ----> " + d.isDisposed());
                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                        Log.d("创建型操作符 empty", "onNext: ----> " + o);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("创建型操作符 empty", "onError: ----> " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d("创建型操作符 empty", "onComplete");
                    }
                });

//        2020-04-03 16:35:15.460 29156-29156/? D/创建类型操作符 empty: onSubscribe: ----> true
//        2020-04-03 16:35:15.460 29156-29156/? D/创建类型操作符 empty: onComplete
    }
```

创建型操作符 `Observable#empty()` 内部自己发送事件，下游默认是 Object，无法发出有值的事件，会发送 `onComplete()`；

##### # range()

```java
private void learnRxRange() {
    Observable.range(10, 5)
            .subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Throwable {
                    Log.d("创建型操作符 range", "accept: ----> " + integer);
                }
            });

//        2020-04-03 17:04:07.195 3103-3103/com.example.learnrxjava D/创建型操作符 range: accept: ----> 10
//        2020-04-03 17:04:07.195 3103-3103/com.example.learnrxjava D/创建型操作符 range: accept: ----> 11
//        2020-04-03 17:04:07.195 3103-3103/com.example.learnrxjava D/创建型操作符 range: accept: ----> 12
//        2020-04-03 17:04:07.195 3103-3103/com.example.learnrxjava D/创建型操作符 range: accept: ----> 13
//        2020-04-03 17:04:07.195 3103-3103/com.example.learnrxjava D/创建型操作符 range: accept: ----> 14
    }
```

创建型操作符  `Observable#range()` 会发送指定范围的 `int` 值的数值，如上面代码，开始为 `10`, 每次累加 `1`, 最后结果为: `10 11 12 13 14` 的 5 个值。

#### Consumer和Observer

可能有的小伙伴可能分不清 `Consumer` 和 `Observer` 两个类，其实可以把 `Consumer` 看着 `Observer` 简版的观察者，也是根据上游被观察者的行为变化而变化。

![](http://baihonghua.cn/%E5%BE%AE%E4%BF%A1%E5%85%AC%E4%BC%97%E5%8F%B7%E4%BA%8C%E7%BB%B4%E7%A0%81.png)
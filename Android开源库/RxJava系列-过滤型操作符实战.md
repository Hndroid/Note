##### # filter()

```java
private void learnRxFilter() {

    Observable.just("C++", "Java", "C#")
            .filter(new Predicate<String>() {
                @Override
                public boolean test(String pS) throws Throwable {

                    if ("Java".equals(pS)) {
                        return false;
                    }

                    return true;
                }
            })
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String pS) throws Throwable {
                    Log.d("过滤型操作符 filter", "accept: ----> " + pS);
                }
            });

//        2020-04-04 21:40:41.583 6754-6754/com.example.learnrxjava D/过滤型操作符 filter: accept: ----> C++
//        2020-04-04 21:40:41.583 6754-6754/com.example.learnrxjava D/过滤型操作符 filter: accept: ----> C#
}
```

过滤操作符 `filter()` 作为中间的节点，回去拦截上游发送出来的事件，然后根据 `false` 或 `true` 条件继续发送往下游。`false` 表示事件被拦截不再往下发射。

##### # take()

```java
private void learnRxTake() {

    Observable.interval(0, TimeUnit.SECONDS)
            .take(8)
            .subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long pLong) throws Throwable {
                    Log.d("过滤型操作符 take ", "accept: ----> " + pLong);
                }
            });

//        2020-04-04 22:00:16.109 9270-9302/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 0
//        2020-04-04 22:00:16.109 9270-9302/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 1
//        2020-04-04 22:00:16.109 9270-9302/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 2
//        2020-04-04 22:00:16.110 9270-9302/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 3
//        2020-04-04 22:00:16.110 9270-9302/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 4
//        2020-04-04 22:00:16.110 9270-9302/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 5
//        2020-04-04 22:00:16.110 9270-9302/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 6
//        2020-04-04 22:00:16.110 9270-9302/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 7
}
```

过滤操作符 `take()` 主要是在定时器的基础上，才可以体现 `take()` 过滤操作符的意义。

然后下面代码实战实现一个倒计时的小栗子，分别用到了定时器 `interval()`、`take()`、`map()`，代码如下：

```java
private void learnRxTake() {

    final int num = 10;

    Observable.interval(0, 1, TimeUnit.SECONDS)
            .take(num)
            .map(new Function<Long, Long>() {
                @Override
                public Long apply(Long pLong) throws Throwable {
                    return num - pLong;
                }
            })
            .subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long pLong) throws Throwable {
                    Log.d("过滤型操作符 take ", "accept: ----> " + pLong);
                }
            });

//        2020-04-04 22:22:32.687 16087-16129/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 10
//        2020-04-04 22:22:33.693 16087-16129/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 9
//        2020-04-04 22:22:34.725 16087-16129/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 8
//        2020-04-04 22:22:35.727 16087-16129/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 7
//        2020-04-04 22:22:36.697 16087-16129/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 6
//        2020-04-04 22:22:37.724 16087-16129/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 5
//        2020-04-04 22:22:38.719 16087-16129/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 4
//        2020-04-04 22:22:39.719 16087-16129/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 3
//        2020-04-04 22:22:40.727 16087-16129/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 2
//        2020-04-04 22:22:41.727 16087-16129/com.example.learnrxjava D/过滤型操作符 take: accept: ----> 1
}
```

##### # distinct()

```java
private void learnRxDistinct() {

    Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onNext(4);
            emitter.onNext(4);
            emitter.onNext(4);
            emitter.onNext(4);
        }
    }).distinct()
            .subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer pInteger) throws Throwable {
                    Log.d("过滤型操作符 distinct", "accept: " + pInteger);
                }
            });

//        2020-04-04 22:32:18.745 21380-21380/com.example.learnrxjava D/过滤型操作符 distinct: accept: 1
//        2020-04-04 22:32:18.745 21380-21380/com.example.learnrxjava D/过滤型操作符 distinct: accept: 2
//        2020-04-04 22:32:18.745 21380-21380/com.example.learnrxjava D/过滤型操作符 distinct: accept: 3
//        2020-04-04 22:32:18.746 21380-21380/com.example.learnrxjava D/过滤型操作符 distinct: accept: 4
}
```

过滤操作符 `distinct()` 主要的作用是把上游重复的事件过滤。

##### # elementAt()

```java
private void learnRxElementAt() {

    Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
            emitter.onNext("C");
            emitter.onNext("C++");
            emitter.onNext("Java");
            emitter.onNext("Java");
            emitter.onNext("Kotlin");
            emitter.onNext("Kotlin");
            emitter.onComplete();
        }
    }).elementAt(0)
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String pS) throws Throwable {
                    Log.d("过滤型操作符 elementAt ", "accept: ----> " + pS);
                }
            });

//        2020-04-04 22:42:15.205 23692-23692/com.example.learnrxjava D/过滤型操作符 elementAt: accept: ----> C
}
```

过滤操作符 `elementAt()` 用于输出指定下标的事件，还可以指定默认的值，如果指定的下标的事件不存在，则输出指定的默认值。

![](http://baihonghua.cn/%E5%BE%AE%E4%BF%A1%E5%85%AC%E4%BC%97%E5%8F%B7%E4%BA%8C%E7%BB%B4%E7%A0%81.png)




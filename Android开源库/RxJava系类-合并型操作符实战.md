##### # startWith()

```java
private void learnRxStartWait() {
    Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
            emitter.onNext(10);
            emitter.onNext(20);
            emitter.onNext(30);
            emitter.onNext(40);
            emitter.onComplete();
        }
    }).startWith(Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
            emitter.onNext(1000);
            emitter.onNext(2000);
            emitter.onNext(3000);
            emitter.onNext(4000);
            // 发射事件完成以后，必须加上 emitter.onComplete()，不然不会执行合并操作另外一个被观察者
            emitter.onComplete();
        }
    })).subscribe(new Consumer<Integer>() {
        @Override
        public void accept(Integer pInteger) throws Throwable {
            Log.d("合并型操作符 startWith ", "accept: ----> " + pInteger);
        }
    });

//        04-05 20:48:21.479 29474-29474/com.example.learnrxjava D/合并型操作符 startWith: accept: ----> 1000
//        04-05 20:48:21.479 29474-29474/com.example.learnrxjava D/合并型操作符 startWith: accept: ----> 2000
//        04-05 20:48:21.479 29474-29474/com.example.learnrxjava D/合并型操作符 startWith: accept: ----> 3000
//        04-05 20:48:21.479 29474-29474/com.example.learnrxjava D/合并型操作符 startWith: accept: ----> 4000
//        04-05 20:48:21.479 29474-29474/com.example.learnrxjava D/合并型操作符 startWith: accept: ----> 10
//        04-05 20:48:21.479 29474-29474/com.example.learnrxjava D/合并型操作符 startWith: accept: ----> 20
//        04-05 20:48:21.479 29474-29474/com.example.learnrxjava D/合并型操作符 startWith: accept: ----> 30
//        04-05 20:48:21.479 29474-29474/com.example.learnrxjava D/合并型操作符 startWith: accept: ----> 40
}
```

合并操作符 `startWith()` 可以看着：`Observable_1.startWith(Observable_2)`, 其执行的顺序是先发射 `Observable_2` 里面的事件，在调用完 `onComplete()` 方法以后，再去发射 `Observable_1` 里面的事件。


##### # concatWith()

```java
private void learnRxConcatWait() {
    Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
            emitter.onNext(10);
            emitter.onNext(20);
            emitter.onNext(30);
            emitter.onNext(40);
            emitter.onComplete();
        }
    }).concatWith(Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
            emitter.onNext(1000);
            emitter.onNext(2000);
            emitter.onNext(3000);
            emitter.onNext(4000);
            emitter.onComplete();
        }
    })).subscribe(new Consumer<Integer>() {
        @Override
        public void accept(Integer pInteger) throws Throwable {
            Log.d("合并型操作符", "accept: ----> " + pInteger);
        }
    });

//        04-05 20:59:25.607 620-620/com.example.learnrxjava D/合并型操作符: accept: ----> 10
//        04-05 20:59:25.607 620-620/com.example.learnrxjava D/合并型操作符: accept: ----> 20
//        04-05 20:59:25.607 620-620/com.example.learnrxjava D/合并型操作符: accept: ----> 30
//        04-05 20:59:25.607 620-620/com.example.learnrxjava D/合并型操作符: accept: ----> 40
//        04-05 20:59:25.607 620-620/com.example.learnrxjava D/合并型操作符: accept: ----> 1000
//        04-05 20:59:25.607 620-620/com.example.learnrxjava D/合并型操作符: accept: ----> 2000
//        04-05 20:59:25.607 620-620/com.example.learnrxjava D/合并型操作符: accept: ----> 3000
//        04-05 20:59:25.607 620-620/com.example.learnrxjava D/合并型操作符: accept: ----> 4000
}
```

合并操作符 `concatWith()` 可以看着表达式: `Observable_1.concatWith(Observable_2)`, 与上面的 `startWith()` 不同的是，`concatWith()` 会先发射 `Observable_1` 里面的事件，然后再发射 `Observable_2` 里面的事件。

##### # concat()

```java
private void learnRxConcat() {
    Observable.concat(
            Observable.just(10)
            ,
            Observable.just(20)
            ,
            Observable.just(30)
            ,
            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                    emitter.onNext(40);
                    emitter.onComplete();
                }
            })
    ).subscribe(new Consumer<Integer>() {
        @Override
        public void accept(Integer pInteger) throws Throwable {
            Log.d("合并型操作符 concat ", "accept: ----> " + pInteger);
        }
    });

//        04-05 21:10:42.198 5139-5139/com.example.learnrxjava D/合并型操作符 concat: accept: ----> 10
//        04-05 21:10:42.198 5139-5139/com.example.learnrxjava D/合并型操作符 concat: accept: ----> 20
//        04-05 21:10:42.198 5139-5139/com.example.learnrxjava D/合并型操作符 concat: accept: ----> 30
//        04-05 21:10:42.198 5139-5139/com.example.learnrxjava D/合并型操作符 concat: accept: ----> 40
}
```

合并型操作符 `concat()` 是把被观察者按顺序执行的，但最多只能同时四个被观察者进行合并。

##### # merge()

```java
private void learnRxMerge() {
        
    @NonNull Observable<Long> lObservable_1 = Observable.intervalRange(1, 5, 1, 2, TimeUnit.SECONDS);

    @NonNull Observable<Long> lObservable_2 = Observable.intervalRange(6, 5, 1, 2, TimeUnit.SECONDS);

    @NonNull Observable<Long> lObservable_3 = Observable.intervalRange(11, 5, 1, 2, TimeUnit.SECONDS);

    Observable.merge(lObservable_1, lObservable_2, lObservable_3)
            .subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long pLong) throws Throwable {
                    Log.d("合并型操作符 merge ", "accept: ----> " + pLong);
                }
            });

//        04-05 21:25:52.151 10863-10912/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 1
//        04-05 21:25:52.154 10863-10914/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 11
//        04-05 21:25:52.155 10863-10913/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 6
//        04-05 21:25:54.148 10863-10912/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 2
//        04-05 21:25:54.149 10863-10913/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 7
//        04-05 21:25:54.149 10863-10914/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 12
//        04-05 21:25:56.148 10863-10912/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 3
//        04-05 21:25:56.148 10863-10913/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 8
//        04-05 21:25:56.149 10863-10914/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 13
//        04-05 21:25:58.148 10863-10912/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 4
//        04-05 21:25:58.149 10863-10913/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 9
//        04-05 21:25:58.149 10863-10914/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 14
//        04-05 21:26:00.148 10863-10912/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 5
//        04-05 21:26:00.149 10863-10913/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 10
//        04-05 21:26:00.150 10863-10914/com.example.learnrxjava D/合并型操作符 merge: accept: ----> 15

}
```

合并型操作符 `merge()` 和 `concat()` 一样，也是最多可以合并四个被观察者，但是被合并的被观察者发射的事件是并发发射的。

##### # zip()

```java
private void learnRxZip() {

    @NonNull Observable<String> lStringObservable_1 = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
            emitter.onNext("Android开发");
            emitter.onNext("Java开发");
            emitter.onNext("Web开发");
            emitter.onComplete();
        }
    });

    @NonNull Observable<String> lStringObservable_2 = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
            emitter.onNext("12K");
            emitter.onNext("13K");
            emitter.onNext("14K");
            // 在 lStringObservable_1 没有职位与 15K 对应
            emitter.onNext("15K");
            emitter.onComplete();
        }
    });

    Observable.zip(lStringObservable_1, lStringObservable_2, new BiFunction<String, String, StringBuffer>() {
        @Override
        public StringBuffer apply(String pS, String pS2) throws Throwable {

            return new StringBuffer().append("职位: ").append(pS).append(" -----> ").append("工资：").append(pS2);
        }
    }).subscribe(new Consumer<StringBuffer>() {
        @Override
        public void accept(StringBuffer pStringBuffer) throws Throwable {
            Log.d("合并型操作符 zip ", "accept: ----> " + pStringBuffer.toString());
        }
    });

//        04-05 21:38:03.910 14403-14403/com.example.learnrxjava D/合并型操作符 zip: accept: ----> 职位: Android开发 -----> 工资：12K
//        04-05 21:38:03.910 14403-14403/com.example.learnrxjava D/合并型操作符 zip: accept: ----> 职位: Java开发 -----> 工资：13K
//        04-05 21:38:03.910 14403-14403/com.example.learnrxjava D/合并型操作符 zip: accept: ----> 职位: Web开发 -----> 工资：14K
}
```
合并型操作符 `zip()` 最多可以同时合并 9 个被观察者，但每个被观察者里面的事件需要数量相同，换而言之，就是需要被观察者里面的事件一一对应。

![](http://baihonghua.cn/%E5%BE%AE%E4%BF%A1%E5%85%AC%E4%BC%97%E5%8F%B7%E4%BA%8C%E7%BB%B4%E7%A0%81.png)



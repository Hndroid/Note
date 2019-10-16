### View 的绘制流程

> 基于 Android 源码 5.1 分析

#### 把布局添加进 DecorView

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //-->源码 1
    setContentView(R.layout.activity_main);
}

/**
* Set the activity content from a layout resource.  The resource will be
* inflated, adding all top-level views to the activity.
*
* @param layoutResID Resource ID to be inflated.
*
* @see #setContentView(android.view.View)
* @see #setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
*/
//源码 1
public void setContentView(@LayoutRes int layoutResID) {
    //获取 Window 的对象，并把布局资源放进 window 里面；
    //-->源码 2
    getWindow().setContentView(layoutResID);
    initWindowDecorActionBar();
}

//位于 PhoneWindow 源码
//源码 2
@Override
public void setContentView(View view, ViewGroup.LayoutParams params) {
    // Note: FEATURE_CONTENT_TRANSITIONS may be set in the process of installing the window
    // decor, when theme attributes and the like are crystalized. Do not check the feature
    // before this happens.
    // mContentParent 是 window 的顶级的 View，是 DecorView ；
    if (mContentParent == null) {
        //如果 PhoneWindow 实例的 DecorView 实例为空，则创建DecorView 实例，--->源码 3
        installDecor();
    } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        //如果 PhoneWindow 实例的 DecorView 实例不为空，则移除所有的 View；
        mContentParent.removeAllViews();
    }

    if (hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        view.setLayoutParams(params);
        final Scene newScene = new Scene(mContentParent, view);
        transitionTo(newScene);
    } else {
        mContentParent.addView(view, params);
    }
    final Callback cb = getCallback();
    if (cb != null && !isDestroyed()) {
        cb.onContentChanged();
    }
}

//源码 3
//主要是创建一个 DecorView 实例
private void installDecor() {

    if (mDecor == null) {
        // 1. 生成DecorView --->源码4
        mDecor = generateDecor(); 
        mDecor.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        mDecor.setIsRootNamespace(true);
        if (!mInvalidatePanelMenuPosted && mInvalidatePanelMenuFeatures != 0) {
            mDecor.postOnAnimation(mInvalidatePanelMenuRunnable);
        }
    }
    // 2. 为 DecorView 设置布局格式并且返回 mContentParent
    if (mContentParent == null) {
        //--->> 源码 5
        mContentParent = generateLayout(mDecor); 
        ...
        } 
    }
}

/**
  * 源码 4：generateDecor()
  * 作用：生成DecorView
  */
protected DecorView generateDecor() {
    return new DecorView(getContext(), -1);
}

/**
*源码 5 generateLayout()
*/
protected ViewGroup generateLayout(DecorView decor) {
        // Apply data from current theme.
        //1. 获取该 window 主题对应的属性
        TypedArray a = getWindowStyle();

        // Inflate the window decor.
        //2. 加载窗口的布局特性
        int layoutResource;
        int features = getLocalFeatures();
        // System.out.println("Features: 0x" + Integer.toHexString(features));
        
        // 3. 加载layoutResource
        View in = mLayoutInflater.inflate(layoutResource, null);

        // 4. 往DecorView中添加子View
        // 即文章开头介绍DecorView时提到的布局格式，那只是一个例子，根据主题样式不同，加载不同的布局。
        decor.addView(in, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)); 
        mContentRoot = (ViewGroup) in;

        // 5. 这里获取的是mContentParent = 即为内容栏（content）对应的DecorView = FrameLayout子类
        ViewGroup contentParent = (ViewGroup)findViewById(ID_ANDROID_CONTENT); 

        return contentParent;
}
```

#### 小结

1. 创建 Window 抽象类的子类 PhoneWindow 类的实例对象；
2. 为 PhoneWindow 类对象设置 WindowManager 对象；
2. 为 PhoneWindow 类对象创建 DecorView 对象；
3. 为 DecorView 类对象中的 content 增加 Activity 中的布局文件；

#### 执行绘制三大步前的准备

```java
//源码 1
//位于 ActivityThread 中，也就是在主线程创建的时候，会调用 handleResumeActivity()
final void handleResumeActivity(IBinder token, boolean clearHide, boolean isForward, boolean reallyResume) {
    ...
    // If the window hasn't yet been added to the window manager,
    // and this guy didn't finish itself or start another activity,
    // then go ahead and add the window.
    if (r.window == null && !a.mFinished && willBeVisible) {
        //1. window 是 PhoneWindow 的实例;
        r.window = r.activity.getWindow();
        //2. r.window.getDecorView() 返回的是 DecorView 的实例;
        View decor = r.window.getDecorView();
        //3. DecorView 对用户不可见;
        decor.setVisibility(View.INVISIBLE);
        ViewManager wm = a.getWindowManager();
        WindowManager.LayoutParams l = r.window.getAttributes();
        a.mDecor = decor;
        l.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;
        l.softInputMode |= forwardBit;

        // 3. DecorView被添加进WindowManager了,此时，不可见
        if (a.mVisibleFromClient) {
            a.mWindowAdded = true;
            //--->源码 2
            wm.addView(decor, l);
        }

    // If the window has already been added, but during resume
    // we started another activity, then don't yet make the
    // window visible.
    } else if (!willBeVisible) {
        if (localLOGV) Slog.v(
            TAG, "Launch " + r + " mStartedActivity set");
        r.hideForNow = true;
    }

    // The window is now visible if it has been added, we are not
    // simply finishing, and we are not starting another activity.
    if (!r.activity.mFinished && willBeVisible
            && r.activity.mDecor != null && !r.hideForNow) {
        //4. DecorView 对用户可见
        if (r.activity.mVisibleFromClient) {
            r.activity.makeVisible();
        }
    }

    ...

}
```

```java
//源码 2
//位于 WindowManagerImpl 中，实现了 WindowManager
/**
* @param view The view to be added to this window.  传入的是 DecorView 的实例；
* @param params The LayoutParams to assign to view.  传入的是 PhoneWindow 的 params
*/
@Override
public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
    applyDefaultToken(params);
    //mGlobal 是 WindowManagerGlobal 的实例，--->源码 3
    mGlobal.addView(view, params, mDisplay, mParentWindow);
}
```

```java
//源码 3
//位于 WindowManagerGlobal 中
public void addView(View view, ViewGroup.LayoutParams params,Display display, Window parentWindow) {
    ...

    // do this last because it fires off messages to start doing things
    //最后执行此操作是因为它会触发启动操作的消息
    try {
        //--->源码 4，这里的 root 表示 ViewRootImpl 的实例；然后 wparams 表示的是 PhoneWindow 的实例；
        root.setView(view, wparams, panelParentView);
    } catch (RuntimeException e) {
        // BadTokenException or InvalidDisplayException, clean up.
        synchronized (mLock) {
            final int index = findViewLocked(view, false);
            if (index >= 0) {
                removeViewLocked(index, true);
            }
        }
        throw e;
    }

}
```

```java
//位于 ViewRootlmpl 
//源码 4
/**
* We have one child
*/
public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
    synchronized (this) {
        if (mView == null) {
            mView = view;
            ···
            // Schedule the first layout -before- adding to the window
            // manager, to make sure we do the relayout before receiving
            // any other events from the system.
            //--->源码 5
            requestLayout();
            ···
        }
    }
}
```

```java
//位于 ViewRootlmpl
//源码 5
@Override
public void requestLayout() {
    if (!mHandlingLayoutInLayoutRequest) {
        //判断是否是当前线程
        checkThread();
        mLayoutRequested = true;
        //--->源码 6
        scheduleTraversals();
    }
}
```

```java
//位于 ViewRootlmpl
//源码 6
void scheduleTraversals() {
    if (!mTraversalScheduled) {
        mTraversalScheduled = true;
        mTraversalBarrier = mHandler.getLooper().postSyncBarrier();
        // 通过mHandler.post（）发送一个runnable，在run()方法中去处理绘制流程
        // 与ActivityThread的Handler消息传递机制相似
        mChoreographer.postCallback(
            //--->源码 7，主要往接口 mTraversalRunnable 看
                Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
        if (!mUnbufferedInputDispatch) {
            scheduleConsumeBatchedInput();
        }
        notifyRendererOfFramePending();
    }
}
```

```java
//位于 ViewRootlmpl
//源码 7
··· 

final TraversalRunnable mTraversalRunnable = new TraversalRunnable(); 

···

final class TraversalRunnable implements Runnable {
    @Override
    public void run() {
        //--->源码 8
        doTraversal();
    }
}

```

```java
//源码 8
void doTraversal() {
    if (mTraversalScheduled) {
        mTraversalScheduled = false;
        mHandler.getLooper().removeSyncBarrier(mTraversalBarrier);

        if (mProfile) {
            Debug.startMethodTracing("ViewAncestor");
        }

        Trace.traceBegin(Trace.TRACE_TAG_VIEW, "performTraversals");
        try {
            //--->源码 9
            performTraversals();
        } finally {
            Trace.traceEnd(Trace.TRACE_TAG_VIEW);
        }

        if (mProfile) {
            Debug.stopMethodTracing();
            mProfile = false;
        }
    }
}
```

```java
//源码 9
//performTraversals() 进入了 performMeasure()、performLayout()、performDraw() 绘制的的三大步；
private void performTraversals() {
    // cache mView since it is used so much below...
        if (!mStopped) {
            boolean focusChangedDueToTouchMode = ensureTouchModeLocally(
                    (relayoutResult&WindowManagerGlobal.RELAYOUT_RES_IN_TOUCH_MODE) != 0);
            if (focusChangedDueToTouchMode || mWidth != host.getMeasuredWidth()
                    || mHeight != host.getMeasuredHeight() || contentInsetsChanged) {
                //根据 DecorView 的宽高、和 DecorView 的布局参数合成 ChildMeasureSpec；
                int childWidthMeasureSpec = getRootMeasureSpec(mWidth, lp.width);
                int childHeightMeasureSpec = getRootMeasureSpec(mHeight, lp.height);

                //传入的参数说明，View 的绘制流程从顶级 DecorView 开始，然后一层一层从 ViewGoroup 至子 View 遍历测绘； 
                performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);

                // Implementation of weights from WindowManager.LayoutParams
                // We just grow the dimensions as needed and re-measure if
                // needs be
                int width = host.getMeasuredWidth();
                int height = host.getMeasuredHeight();
                boolean measureAgain = false;

                if (lp.horizontalWeight > 0.0f) {
                    width += (int) ((mWidth - width) * lp.horizontalWeight);
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
                            MeasureSpec.EXACTLY);
                    measureAgain = true;
                }
                if (lp.verticalWeight > 0.0f) {
                    height += (int) ((mHeight - height) * lp.verticalWeight);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                            MeasureSpec.EXACTLY);
                    measureAgain = true;
                }

                if (measureAgain) {
                    if (DEBUG_LAYOUT) Log.v(TAG,
                            "And hey let's measure once more: width=" + width
                            + " height=" + height);
                    performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                }

                layoutRequested = true;
            }
        }
    } else {
        // Not the first pass and no window/insets/visibility change but the window
        // may have moved and we need check that and if so to update the left and right
        // in the attach info. We translate only the window frame since on window move
        // the window manager tells us only for the new frame but the insets are the
        // same and we do not want to translate them more than once.

        // TODO: Well, we are checking whether the frame has changed similarly
        // to how this is done for the insets. This is however incorrect since
        // the insets and the frame are translated. For example, the old frame
        // was (1, 1 - 1, 1) and was translated to say (2, 2 - 2, 2), now the new
        // reported frame is (2, 2 - 2, 2) which implies no change but this is not
        // true since we are comparing a not translated value to a translated one.
        // This scenario is rare but we may want to fix that.

        final boolean windowMoved = (mAttachInfo.mWindowLeft != frame.left
                || mAttachInfo.mWindowTop != frame.top);
        if (windowMoved) {
            if (mTranslator != null) {
                mTranslator.translateRectInScreenToAppWinFrame(frame);
            }
            mAttachInfo.mWindowLeft = frame.left;
            mAttachInfo.mWindowTop = frame.top;
        }
    }

    final boolean didLayout = layoutRequested && !mStopped;
    boolean triggerGlobalLayoutListener = didLayout
            || mAttachInfo.mRecomputeGlobalAttributes;
    if (didLayout) {
        performLayout(lp, desiredWindowWidth, desiredWindowHeight);
    }

    if (!cancelDraw && !newSurface) {
        if (!skipDraw || mReportNextDraw) {
            if (mPendingTransitions != null && mPendingTransitions.size() > 0) {
                for (int i = 0; i < mPendingTransitions.size(); ++i) {
                    mPendingTransitions.get(i).startChangingAnimations();
                }
                mPendingTransitions.clear();
            }

            performDraw();
        }
    } else {
        if (viewVisibility == View.VISIBLE) {
            // Try again
            scheduleTraversals();
        } else if (mPendingTransitions != null && mPendingTransitions.size() > 0) {
           
        }
    }
    mIsInTraversal = false;
}
```

#### 源码流程总结

1. 把 DecorView 对象实例装进 WindowManger 里面；
2. WindowManager 再把 DecorView 对象实例装进到 ViewRootImpl 里面；
3. ViewRootImpl 对象通过 Handler 向主线程发送信息，通知 doTraversal() --> performTraversals() 开始执行绘制的三大步；

#### 绘制前的准备

![](http://baihonghua.cn/%E7%BB%98%E5%88%B6%E5%89%8D%E7%9A%84%E5%87%86%E5%A4%87.png)

> ViewRootImpl 对象中接收各种的变化（如来自 WMS 的窗口属性变化、来自控件树的尺寸变化、重绘请求等都会引发 performTraversals() 的调用）并且在 ViewRootImpl 的对象中完成；

#### DecorView 添加到窗口 Window 的过程

![](http://baihonghua.cn/DecorView%E6%B7%BB%E5%8A%A0%E5%88%B0%E7%AA%97%E5%8F%A3Window%E7%9A%84%E8%BF%87%E7%A8%8B.png)














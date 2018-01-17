package com.bmw.peek2slite.utils.singleThreadUtil;

/**
 * Created by admin on 2016/10/21.
 */

import java.util.concurrent.ExecutorService;


/**
 * 自定义线程池里的newSingleThreadExecutor方法需要的一些类
 *
 *
 */
public class FinalizableDelegatedExecutorService extends DelegatedExecutorService {
    public FinalizableDelegatedExecutorService(ExecutorService executor) {
        super(executor);
    }

    protected void finalize() {
        super.shutdown();
    }
}
package com.bmw.peek2slite.model.model;

import com.bmw.peek2slite.presenter.EnvironmentListener;

/**
 * Created by admin on 2016/9/19.
 */
public interface EnvironmentMode {
    void getDatas(EnvironmentListener environmentListener);
    void realese();
}

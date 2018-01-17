package com.bmw.peek2slite.presenter;

import com.bmw.peek2slite.model.Environment;

import java.util.List;

/**
 * Created by admin on 2016/9/19.
 */
public interface EnvironmentListener {
    void success(List<Environment> list);
    void failure();

}

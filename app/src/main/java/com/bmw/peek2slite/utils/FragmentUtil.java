package com.bmw.peek2slite.utils;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by admin on 2017/5/19.
 */

public class FragmentUtil {

    public static void showDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment, String tag){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if(fragment != null){
            fragmentTransaction.remove(fragment);
        }
        dialogFragment.show(fragmentTransaction,tag);
    }

}

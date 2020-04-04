package com.syy.expression.permission;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class PermissionAvoidManager {

    public static final String TAG = "TAG";

    private AvoidFragment avoidFragment;


    public PermissionAvoidManager(@NonNull FragmentActivity activity) {
        avoidFragment = getAvoidFragment(activity);
    }

    public PermissionAvoidManager(@NonNull Fragment context) {
        if (context.getActivity() != null)
            avoidFragment = getAvoidFragment(context);
    }

    /**
     * 添加Fragment到当前启动Activity
     *
     * @param activity
     * @return、
     */
    private AvoidFragment getAvoidFragment(FragmentActivity activity) {

        AvoidFragment fragment = (AvoidFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new AvoidFragment();
            FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
            supportFragmentManager.executePendingTransactions();
            supportFragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commitNowAllowingStateLoss();
        }
        return fragment;
    }

    /**
     * 添加Fragment到当前启动Fragment
     *
     * @return、
     */
    private AvoidFragment getAvoidFragment(Fragment context) {

        AvoidFragment fragment = (AvoidFragment) context.getChildFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new AvoidFragment();
            FragmentManager supportFragmentManager = context.getChildFragmentManager();
            supportFragmentManager.executePendingTransactions();
            supportFragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commitNowAllowingStateLoss();
        }
        return fragment;
    }

    // 请求权限通用
    public void requestPermission(String[] permission, OnCommonPermissionCallBack onCommonPermissionCallBack) {
        avoidFragment.setOnPermissionCallback(new AvoidFragment.OnPermissionCallback() {

            @Override
            public void onRequestPermissionsResult(boolean result, boolean shouldShowAllRequestPermissionRationale) {
                onCommonPermissionCallBack.onRequestPermissionsResult(result, shouldShowAllRequestPermissionRationale);
            }
        });
        avoidFragment.requestPermission(permission);
    }


    // 请求每一个权限通用
    public void requestPermission2(String[] permission, OnCommonPermissionCallBack2 onCommonPermissionCallBack2) {
        avoidFragment.setOnPermissionCallback2(new AvoidFragment.OnPermissionCallback2() {

            @Override
            public void onRequestPermissionsResult(String permission, boolean result, boolean shouldShowAllRequestPermissionRationale) {
                onCommonPermissionCallBack2.onRequestPermissionsResult(permission, result, shouldShowAllRequestPermissionRationale);
            }
        });
        avoidFragment.requestPermission2(permission);
    }


    /**
     * 业务层通用权限请求回调
     */
    public interface OnCommonPermissionCallBack {

        void onRequestPermissionsResult(boolean hasPermission, boolean shouldShowAllRequestPermissionRationale);
    }

    /**
     * 业务层请求每一个权限都会回调
     */
    public interface OnCommonPermissionCallBack2 {

        void onRequestPermissionsResult(String permission, boolean hasPermission, boolean shouldShowAllRequestPermissionRationale);
    }

    /**
     * 判断是否缺少权限
     */
    private static boolean hasPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断权限集合
     * permissions 权限数组
     */
    public static boolean hasPermissions(Context mContexts, String[] mPermissions) {
        for (String permission : mPermissions) {
            if (!hasPermission(mContexts, permission)) {
                return false;
            }
        }
        return true;

    }
}

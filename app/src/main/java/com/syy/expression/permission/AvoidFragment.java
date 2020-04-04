package com.syy.expression.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.syy.expression.base.BaseFragment;

public class AvoidFragment extends BaseFragment {


    public static final int REQ_CAMERA = 100;

    private static final boolean SHOULD_REQUEST = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    private int mRequestCode = 1;
    private String[] mPermissionArray;
    private OnPermissionCallback mCallback;
    private OnPermissionCallback2 mCallback2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    /**
     * 设置请求回调
     *
     * @param callback
     */
    public void setOnPermissionCallback(OnPermissionCallback callback) {
        this.mCallback = callback;
    }

    /**
     * 设置请求回调
     * 多个权限请求 每一个都添加回调返回值
     */
    public void setOnPermissionCallback2(OnPermissionCallback2 callback2) {
        this.mCallback2 = callback2;
    }

    /**
     * 请求权限
     *
     * @param permissions
     */
    public void requestPermission(String[] permissions) {

        this.mPermissionArray = permissions;
        if (activity == null || mCallback == null) return;
        if (!SHOULD_REQUEST || mPermissionArray == null || mPermissionArray.length == 0) {
            mCallback.onRequestPermissionsResult(true, false);
            return;
        }

        if (checkAllSelfPermissions(activity, mPermissionArray)) {
            mCallback.onRequestPermissionsResult(true, false);
            return;
        }
        requestPermissions(mPermissionArray, mRequestCode);
    }


    /**
     * 请求权限
     *
     * @param permissions
     */
    public void requestPermission2(String[] permissions) {

        this.mPermissionArray = permissions;
        if (activity== null || mCallback2 == null) return;
        if (!SHOULD_REQUEST || mPermissionArray == null || mPermissionArray.length == 0 || checkAllSelfPermissions(activity, mPermissionArray)) {
            for (String permission : permissions) {
                mCallback2.onRequestPermissionsResult(permission, true, false);
            }
            return;
        }
        requestPermissions(mPermissionArray, mRequestCode);
    }


    /**
     * 验证权限结果
     *
     * @param grantResults
     */
    public void verifyPermissions(int[] grantResults) {

        if (!SHOULD_REQUEST) {
            mCallback.onRequestPermissionsResult(true, shouldShowAllRequestPermissionRationale());
            return;
        }
        if (grantResults == null || grantResults.length < 1) {
            mCallback.onRequestPermissionsResult(false, shouldShowAllRequestPermissionRationale());
        } else {
            boolean result = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    result = false;
                    break;
                }
            }
            mCallback.onRequestPermissionsResult(result, shouldShowAllRequestPermissionRationale());
        }

    }

    /**
     * 每一条验证权限结果
     *
     * @param grantResults
     */
    public void verifyPermissions(String[] permissions, int[] grantResults) {

        boolean hasPermission = false;
        for (int i = 0; i < permissions.length; i++) {
            if (!SHOULD_REQUEST)
                hasPermission = true;
            else if (grantResults == null || grantResults.length < 1)
                hasPermission = false;
            else
                hasPermission = grantResults[i] == PackageManager.PERMISSION_GRANTED;

            mCallback2.onRequestPermissionsResult(permissions[i], hasPermission,
                    shouldShowAllRequestPermissionRationale());
        }

    }

    /**
     * 检测所有权限是否都通过
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean checkAllSelfPermissions(Context context, String... permissions) {
        if (SHOULD_REQUEST) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 第一次打开App时	false
     * 上次弹出权限点击了禁止（但没有勾选“下次不在询问”）	true
     * 上次选择禁止并勾选：下次不在询问	false
     * <p>
     * true 表明用户没有彻底禁止弹出权限请求
     * false 表明用户已经彻底禁止弹出权限请求
     */
    private boolean shouldShowAllRequestPermissionRationale() {
        for (String permission : mPermissionArray) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 权限处理回调
     */
    public interface OnPermissionCallback {
        void onRequestPermissionsResult(boolean result, boolean shouldShowAllRequestPermissionRationale);
    }

    public interface OnPermissionCallback2 {
        void onRequestPermissionsResult(String permissin, boolean result, boolean shouldShowAllRequestPermissionRationale);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mRequestCode == requestCode) {
            if (mCallback != null)
                verifyPermissions(grantResults);
            if (mCallback2 != null)
                verifyPermissions(permissions, grantResults);
        }

    }

}

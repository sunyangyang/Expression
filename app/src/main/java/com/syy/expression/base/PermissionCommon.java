package com.syy.expression.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionCommon {

    private static final boolean SHOULD_REQUEST = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    private Activity mActivity;
    private int mRequestCode;
    private String[] mPermissionArray;
    private OnPermissionCallback mCallback;

    public PermissionCommon(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 设置请求回调
     * @param callback
     */
    public void setOnPermissionCallback(OnPermissionCallback callback) {
        this.mCallback = callback;
    }

    /**
     * 请求权限
     * @param requestCode
     * @param permissions
     */
    public void requestPermission(int requestCode, String[] permissions) {
        this.mRequestCode = requestCode;
        this.mPermissionArray = permissions;
        if (mActivity == null || mCallback == null) return;
        if (!SHOULD_REQUEST || mPermissionArray == null || mPermissionArray.length == 0) {
            onResult(true);
            return;
        }
        if (checkAllSelfPermissions(mActivity, mPermissionArray)) {
            onResult(true);
            return;
        }
        if (shouldShowAllRequestPermissionRationale()) {
            // 需要显示权限请求对话框
        }
        ActivityCompat.requestPermissions(mActivity, mPermissionArray, mRequestCode);
    }

    /**
     * 验证权限结果
     * @param grantResults
     */
    public void verifyPermissions(int[] grantResults) {
        if (!SHOULD_REQUEST) {
            onResult(true);
            return;
        }
        if (grantResults == null || grantResults.length < 1) {
            onResult(false);
        } else {
            boolean result = true;
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    result = false;
                    break;
                }
            }
            onResult(result);
        }
    }

    /**
     * 检测所有权限是否都通过
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

    // 是否需要显示权限请求对话框
    private boolean shouldShowAllRequestPermissionRationale() {
        for (String permission : mPermissionArray) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                return true;
            }
        }
        return false;
    }

    // 结果处理
    private void onResult(boolean hasPermissions) {
        mCallback.onPermissionCallback(mRequestCode, hasPermissions);
    }

    /**
     * 权限处理回调
     */
    public interface OnPermissionCallback {
        void onPermissionCallback(int requestCode, boolean hasPermissions);
    }

}

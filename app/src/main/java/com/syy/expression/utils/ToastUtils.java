package com.syy.expression.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {

    private static Toast toast;

    private ToastUtils(Toast mToast) {
        toast = mToast;
    }

    public static class Builder {

        private Context context;
        private View customView;
        private int gravity = Gravity.CENTER;
        private int duration = Toast.LENGTH_SHORT;
        private int xOffset = 0;
        private int yOffset = 0;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCustomView(View customView) {
            this.customView = customView;
            return this;
        }

        public Builder setCustomView(int customViewId) {
            this.customView = LayoutInflater.from(context).inflate(customViewId, null);
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder setXOffset(int offset) {
            xOffset = offset;
            return this;
        }

        public Builder setYOffset(int offset) {
            yOffset = offset;
            return this;
        }

        public ToastUtils build() {
            return new ToastUtils(getToast());
        }

        private Toast getToast() {
            if (toast != null) {
                toast.cancel();
            }
            Toast mToast = new Toast(context);
            mToast.setGravity(gravity, xOffset, yOffset);
            mToast.setDuration(duration);
            mToast.setView(customView);
            return mToast;
        }
    }

    public void show() {
        if (toast != null) {
            toast.show();
        }
    }

    private static final ViewGroup.LayoutParams M_LAYOUT_PARAMS = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    private static GradientDrawable mGradientDrawable;

    private static GradientDrawable getViewGradientDrawable(int radius) {
        if (mGradientDrawable == null) {
            mGradientDrawable = new GradientDrawable();
            mGradientDrawable.setCornerRadius(radius);
            mGradientDrawable.setColor(Color.argb(180, 0, 0, 0));
        }
        return mGradientDrawable;
    }

    public static void showText(Context context, CharSequence cs) {
        showText(context, cs, Toast.LENGTH_SHORT, Gravity.CENTER);
    }

    public static void showText(Context context, CharSequence cs, int duration, int gravity) {
        if (TextUtils.isEmpty(cs)) return;
        if (toast != null) {
            toast.cancel();
        }
        Builder builder = new Builder(context);
        builder.duration = duration;
        builder.gravity = gravity;

        TextView textView = new TextView(context);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            int paddingBottom = DeviceUtils.dp2px(context, 10);
            int paddingTop = DeviceUtils.dp2px(context, 10);
            int paddingStart = DeviceUtils.dp2px(context, 24);
            int paddingEnd = DeviceUtils.dp2px(context, 24);
            int cornerRadius = DeviceUtils.dp2px(context, 5);
            M_LAYOUT_PARAMS.width = display.getWidth() - cornerRadius;

            textView.setLayoutParams(M_LAYOUT_PARAMS);
            textView.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
            textView.setLineSpacing(DeviceUtils.dp2px(context, 4), 1.0f);
            textView.setBackgroundDrawable(getViewGradientDrawable(cornerRadius));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setText(cs);

            builder.setCustomView(textView);
            ToastUtils toastUtils = builder.build();
            toastUtils.show();
        }
    }

}

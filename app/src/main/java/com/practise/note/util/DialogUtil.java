package com.practise.note.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.practise.note.R;

/**
 * Author: Pepper
 * Date :  2018/5/16
 * Summary:${ToDo}.
 */

public class DialogUtil extends Dialog {
    public static DialogUtil dialog;
    //窗口位置设置
    private static int gravity = Gravity.CENTER;
    //布局视图
    private static View view;
    //点击返回是否关闭dialog
    public static boolean isDismiss = true;
    //是否全屏
    public static boolean isFullScreen = false;
    //是否阴影
    public static boolean noBackground = false;
    public static void setNoBackGround() {
        noBackground = true;
    }
    public static void setFullScreen() {
        isFullScreen = true;
    }
    public static void setGravity(int youGravity) {
        gravity = youGravity;
    }

    public static void setBackNoDismiss() {
        isDismiss = noBackground;
    }
    public static View show(Context context, @LayoutRes int youLayout) {
        if (dialog != null) dialog.dismiss();
        Window window;
        if (isFullScreen) {
            if (noBackground) {
                dialog = new DialogUtil(context, R.style.dialog);
            } else {
                dialog = new DialogUtil(context, R.style.list_dialog);
            }
            window = dialog.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.FILL_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        } else {
            dialog = new DialogUtil(context, R.style.list_dialog);
            window = dialog.getWindow();
            window.getDecorView().setPadding(100,0,100,0); //根据需求进行设置，可无
        }

        view = dialog.getLayoutInflater().inflate(youLayout, null);

        window.setGravity(gravity);
        window.setWindowAnimations(R.style.dialogStyle);
        if (noBackground) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }


        dialog.show();
        return view;
    }
    /**
     * 关闭对话框
     */
    public static void dismisss() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                clear();
            }
        }
    }
    static void clear() {
        gravity = Gravity.CENTER;
        isDismiss = true;
        isFullScreen = false;
        noBackground = false;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isDismiss) {
            clear();
            return super.onKeyDown(keyCode, event);
        } else {
            return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
    }
    public DialogUtil(@NonNull Context context) {
        super(context);
    }

    public DialogUtil(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogUtil(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}


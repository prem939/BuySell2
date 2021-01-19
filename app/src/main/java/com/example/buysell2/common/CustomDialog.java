package com.example.buysell2.common;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;


import com.example.buysell2.Activity.BaseActivity;
import com.example.buysell2.Activity.LoginActivity;
import com.example.buysell2.Activity.MainActivity;
import com.example.buysell2.Activity.UserActivity;
import com.example.buysell2.R;

import java.lang.ref.WeakReference;

/**
 * class to create the Custom Dialog
 **/
public class CustomDialog extends Dialog {
    //initializations
    boolean isCancellable = true;
    /**
     * Constructor
     *
     * @param context
     * @param view
     */
    private WeakReference<BaseActivity> baseActivity;
    private WeakReference<LoginActivity> loginActivity;
    private WeakReference<MainActivity> mainActivity;
    private WeakReference<UserActivity> userActivity;

    public CustomDialog(Context context, View view) {
        super(context, R.style.Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        if (context instanceof BaseActivity)
            this.baseActivity = new WeakReference<BaseActivity>((BaseActivity) context);
        else if (context instanceof MainActivity)
            this.mainActivity = new WeakReference<MainActivity>((MainActivity) context);
        else if (context instanceof UserActivity)
            this.userActivity = new WeakReference<UserActivity>((UserActivity) context);
        else if (context instanceof LoginActivity)
            this.loginActivity = new WeakReference<LoginActivity>((LoginActivity) context);
    }

    /**
     * Constructor
     *
     * @param context
     * @param view
     * @param lpW
     * @param lpH
     */
    public CustomDialog(Context context, View view, int lpW, int lpH) {
        this(context, view, lpW, lpH, true);
        if (context instanceof BaseActivity)
            this.baseActivity = new WeakReference<BaseActivity>((BaseActivity) context);
        else if (context instanceof MainActivity)
            this.mainActivity = new WeakReference<MainActivity>((MainActivity) context);
        else if (context instanceof UserActivity)
            this.userActivity = new WeakReference<UserActivity>((UserActivity) context);
        else if (context instanceof LoginActivity)
            this.loginActivity = new WeakReference<LoginActivity>((LoginActivity) context);
    }

    /**
     * Constructor
     *
     * @param context
     * @param view
     * @param lpW
     * @param lpH
     * @param isCancellable
     */
    public CustomDialog(Context context, View view, int lpW, int lpH, boolean isCancellable) {
        super(context, R.style.Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view, new LayoutParams(lpW, lpH));
        this.isCancellable = isCancellable;
        if (context instanceof BaseActivity)
            this.baseActivity = new WeakReference<BaseActivity>((BaseActivity) context);
        else if (context instanceof MainActivity)
            this.mainActivity = new WeakReference<MainActivity>((MainActivity) context);
        else if (context instanceof UserActivity)
            this.userActivity = new WeakReference<UserActivity>((UserActivity) context);
        else if (context instanceof LoginActivity)
            this.loginActivity = new WeakReference<LoginActivity>((LoginActivity) context);
    }

    public CustomDialog(Context context, View view, int lpW, int lpH, boolean isCancellable, int style) {
        super(context, R.style.Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view, new LayoutParams(lpW, lpH));
        this.isCancellable = isCancellable;
        if (context instanceof BaseActivity)
            this.baseActivity = new WeakReference<BaseActivity>((BaseActivity) context);
        else if (context instanceof MainActivity)
            this.mainActivity = new WeakReference<MainActivity>((MainActivity) context);
        else if (context instanceof UserActivity)
            this.userActivity = new WeakReference<UserActivity>((UserActivity) context);
        else if (context instanceof LoginActivity)
            this.loginActivity = new WeakReference<LoginActivity>((LoginActivity) context);
    }

    @Override
    public void onBackPressed() {
        if (isCancellable)
            super.onBackPressed();
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    public void showCustomDialog(Context context) {
        try {
            if (context instanceof BaseActivity && baseActivity.get() != null && !baseActivity.get().isFinishing())
                show();
            else if (context instanceof MainActivity && mainActivity.get() != null && !mainActivity.get().isFinishing())
                show();
            else if (context instanceof UserActivity && userActivity.get() != null && !userActivity.get().isFinishing())
                show();
            else if (context instanceof LoginActivity && loginActivity.get() != null && !loginActivity.get().isFinishing())
                show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

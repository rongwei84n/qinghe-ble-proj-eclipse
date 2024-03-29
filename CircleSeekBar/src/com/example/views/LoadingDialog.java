package com.example.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.circleseekbar.R;
import com.example.utils.ToastUtil;


/**
 * 加载对话框
 * Created by weiming.zeng on 2017/7/25.
 */

public class LoadingDialog extends Dialog {

    private TextView mMessage;
    private ImageView mLoading;
    public static long DURATION = 10 * 1000 + 800;
    private ObjectAnimator mAnim;
    private Context mContext;
    private String errorText;
    Handler mHandler = new Handler();
    Runnable loadingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mAnim.cancel();
                dismiss();
                if (TextUtils.isEmpty(errorText)) {
                    ToastUtil.show(mContext, errorText);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public LoadingDialog(Context context) {
        this(context, null);
    }

    public LoadingDialog(Context context, String message) {
        super(context, R.style.DialogStyle);
        mContext = context;
        initView(message);
    }

    public LoadingDialog(Context context, int resId) {
        super(context, R.style.DialogStyle);
        mContext = context;
        String message = resId == 0 ? context.getString(R.string.loading_text) : context.getString(resId);
        initView(message);
    }

    /**
     * @param context
     * @param message  加载框显示信息
     * @param duration 加载框显示时长
     */
    public LoadingDialog(Context context, String message, long duration) {
        super(context, R.style.DialogStyle);
        this.DURATION = duration;
        initView(message);
    }

    /**
     * 初始化界面
     *
     * @param message 加载框的提示消息，如果为空，则显示默认的
     */
    private void initView(String message) {
        setCancelable(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_loading);
        mMessage = (TextView) findViewById(R.id.tv_message);
        if (!TextUtils.isEmpty(message)) {
            mMessage.setText(message);
        }
        mLoading = (ImageView) findViewById(R.id.iv_loading);
        //播放旋转动画
        mAnim = ObjectAnimator.ofFloat(mLoading, "rotation", 0f, 360f);
        mAnim.setDuration(1000);
        mAnim.setRepeatCount(ValueAnimator.INFINITE);
        mAnim.setRepeatMode(ValueAnimator.RESTART);
        mAnim.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        try {
            mAnim.cancel();
            mHandler.removeCallbacks(loadingRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(String message, long duration) {
        this.show(message, duration, null);
    }

    /**
     * 该show方法不显示提示文字，如需要显示提示文字，用另一个方法
     *
     * @param message
     * @param duration
     * @param error
     */
    public void show(String message, long duration, final String error) {
        if (!TextUtils.isEmpty(message)) {
//            this.mMessage.setVisibility(View.VISIBLE);
            this.mMessage.setVisibility(View.INVISIBLE);
            this.mMessage.setText(message);
        } else {
            this.mMessage.setVisibility(View.INVISIBLE);
        }
        this.errorText = error;
        mHandler.postDelayed(loadingRunnable, duration);
        mAnim.start();
        super.show();
    }

    /**
     * 该方法可以决定是否显示提示文字
     *
     * @param message
     * @param duration
     * @param showMsg
     */
    public void show(String message, long duration, boolean showMsg) {
        if (showMsg && !TextUtils.isEmpty(message)) {
            this.mMessage.setVisibility(View.VISIBLE);
            this.mMessage.setText(message);
        } else {
            this.mMessage.setVisibility(View.INVISIBLE);
        }
        mHandler.postDelayed(loadingRunnable, duration);
        mAnim.start();
        super.show();
    }

    public void show(int resId, long duration) {
        String message = resId == 0 ? mContext.getString(R.string.loading_text) : mContext.getString(resId);
        this.show(message, duration, null);
    }

    @Override
    public void show() {
        try {
            mHandler.postDelayed(loadingRunnable, DURATION);
            this.show(null, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

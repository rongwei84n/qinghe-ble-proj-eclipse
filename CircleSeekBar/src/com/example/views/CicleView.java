package com.example.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.lee.circleseekbar.R;
import com.example.utils.LogUtils;

public class CicleView extends View {
    //    定义画笔
    Paint paint;
    private int mColor;


    //在View的构造方法中通过TypedArray获取
    public CicleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.circleview);
        String color = ta.getString(R.styleable.circleview_circle_color);
        LogUtils.d("sandy", "color: " + color);
        ta.recycle();

        initColor(color);
    }

    private void initColor(String color){
        if (TextUtils.isEmpty(color)){
            mColor = Color.RED;
            return;
        }
        if ("blue".equalsIgnoreCase(color)){
            mColor = Color.BLUE;
        }else {
            mColor = Color.RED;
        }
    }

    //    重写draw方法
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

//        实例化画笔对象
        paint = new Paint();
//        给画笔设置颜色
        paint.setColor(mColor);
//        设置画笔属性
//        paint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
        paint.setStyle(Paint.Style.STROKE);//画笔属性是空心圆
        paint.setStrokeWidth(16);//设置画笔粗细

        /*四个参数：
                参数一：圆心的x坐标
                参数二：圆心的y坐标
                参数三：圆的半径
                参数四：定义好的画笔
                */
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 200, paint);


//        float x = (getWidth() - getHeight() / 2) / 2;
        float y = getHeight() / 2;
        float x = getWidth()/4;

        RectF oval = new RectF( x, y, x+x, y+x);
        paint.setColor(Color.RED);
        canvas.drawArc(oval,120,300,false,paint);

    }
}

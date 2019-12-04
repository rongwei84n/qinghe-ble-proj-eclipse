package com.example.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.lee.circleseekbar.R;

import java.util.ArrayList;

public class MeterView extends View {

    private int preWidth;
    private int preHeight;
    private Paint quenLinePaint;
    private Paint paint1;
    private Paint paint2;
    private float row;
    private float temp;
    private Bitmap bmp;
    private Paint paintPoint;
    private Paint paintDu;
    private Paint paintwendu;
    private Path pathDu;
    private int sideLength;
    private Shader mShader;
    private float minNumb;
    private float maxNumb;
    private float span;

    public MeterView(Context context) {
        this(context, null);
    }

    public MeterView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        quenLinePaint = new Paint();
        quenLinePaint.setAntiAlias(true);

        paint1 = new Paint();
        paint1.setColor(Color.WHITE);
        paint1.setAntiAlias(true);

        paint2 = new Paint();
        paint2.setAntiAlias(true);

        paintPoint = new Paint();
        paintPoint.setColor(Color.WHITE);
        paintPoint.setStrokeWidth(10);
        paintPoint.setAntiAlias(true);
        paintPoint.setStyle(Paint.Style.FILL);

        paintDu = new Paint();
        paintDu.setTextAlign(Paint.Align.RIGHT);
        paintDu.setTextSize(24);
        paintDu.setAntiAlias(true);
        paintDu.setColor(Color.BLACK);

        paintwendu = new Paint();
        paintwendu.setAntiAlias(true);
        paintwendu.setTextSize(40);

        pathDu = new Path();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        preWidth = MeasureSpec.getSize(widthMeasureSpec);
        preHeight = MeasureSpec.getSize(heightMeasureSpec);
        int max = Math.max(preWidth, preHeight);
        if (max<240){
            sideLength = 240;//保证刻度清晰可见,设置边长下限
        }else{
            sideLength =max;
        }
        //颜色过渡,这里采用线性过渡
        mShader = new LinearGradient(20, sideLength, sideLength-20, sideLength,
                new int[] { getResources().getColor(R.color.green),getResources().getColor(R.color.blue),getResources().getColor(R.color.red)
                }, null, Shader.TileMode.CLAMP);
        quenLinePaint.setShader(mShader);
        paint2.setShader(mShader);

        //通过path绘制棱形表盘指针
        bmp = Bitmap.createBitmap(20, sideLength/2+50, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint6 = new Paint();
        paint6.setAntiAlias(true);
        paint6.setColor(getResources().getColor(R.color.blue));
        Path path = new Path();
        path.moveTo(10,0);
        path.lineTo(20,50);
        path.lineTo(10,sideLength/2+50);
        path.lineTo(0,50);
        path.lineTo(10,0);
        canvas.drawPath(path,paint6);
        canvas.drawBitmap(bmp, 170,10, paint6);
        canvas.save();//Canvas.ALL_SAVE_FLAG
        canvas.restore();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF oval2 = new RectF(0, 0, sideLength, sideLength);//绘制区域
        canvas.drawArc(oval2, 135, 270, true, quenLinePaint);//绘制圆弧从135度开始绘制270度
        canvas.drawCircle(sideLength/2, sideLength/2, sideLength/2-20, paint1);//绘制圆,重叠达到环形边框的效果
        //绘制刻度线,通过两次不同大小圆的遮罩,达到刻度的长短粗细效果
        RectF oval3 = new RectF(20, 20, sideLength-20, sideLength-20);//
        float i1 = (270.0f-110) / 99;
        float startAngle = 135;
        ArrayList<Float> floats = new ArrayList<Float>();
        for (int i = 0; i < 100; i++) {
            if (String.valueOf(i+1).contains("0")||i==0){
                floats.add(startAngle);
                startAngle = startAngle+i1+2;
            }else{
                canvas.drawArc(oval3, startAngle, 1, true, paint2);
                startAngle = startAngle+i1+1;
            }

        }
        canvas.drawCircle(sideLength/2, sideLength/2,  sideLength/2-40, paint1);
        for (int i = 0; i < floats.size(); i++) {
            canvas.drawArc(oval3, floats.get(i), 2, true, quenLinePaint);
        }
        canvas.drawCircle(sideLength/2, sideLength/2, sideLength/2-50, paint1);
        //刻度数绘制,通过path确定位置,然后通过drawTextOnPath绘制text
        RectF oval4 = new RectF(30, 30, sideLength-30, sideLength-30);//
        float pathstart = 135-20;
        for (int i = 0; i < 11; i++) {
            pathDu.reset();
            pathDu.addArc(oval4,pathstart,25);
            canvas.drawTextOnPath(""+(i*(int)span/10+(int)span),pathDu,0,50,paintDu);
            pathstart+=27;
        }

//        绘制表盘指针,以及旋转效果的实现
//        方式一:绘制一条直线,通过旋转画布的方式实现指针的旋转效果
//        canvas.save();//先保存之前的
//        canvas.rotate(row,200, 200);//延中心点旋转角度
//        canvas.drawLine(200,200,200,390,paint);//画线
//        canvas.restore();//恢复

//         方式二:通过圆函数,计算旋转任意角度后的圆上一点的坐标,然后绘制圆心到圆上一点的半径,就是指针的位置,
//        x1   =   x0   +   r   *   cos(ao   *   3.14   /180   )
//        y1   =   y0   +   r   *   sin(ao   *   3.14   /180   )
//        float lineX = (float)(200+170*Math.cos(row*3.14f/180));
//        float lineY = (float)(200+170*Math.sin(row*3.14f/180));
//        canvas.drawLine(200,200,lineX,lineY,paint);

//      这里我采用的原理与方式一类似,只不过绘制的不是简单的直线而是多边形位图(bitmap)
        canvas.drawText(temp+"℃",sideLength/2-50,sideLength-50,paintwendu);
        canvas.save();
        canvas.rotate(row,sideLength/2, sideLength/2);
        canvas.drawBitmap(bmp,sideLength/2-10,sideLength/2-60,paintwendu);
        canvas.drawCircle(sideLength/2,sideLength/2,6,paintPoint);
        canvas.restore();
    }

    /**
     *
     * @param minNumb 表盘最小值
     * @param maxNumb 最大值
     * @param temp 实际温度
     */
    public void setData(float minNumb,float maxNumb,float temp){
        this.minNumb = minNumb;//
        this.maxNumb = maxNumb;//
        span = maxNumb-minNumb;//跨度
        this.temp = temp;
        float v = 100.0f / span;
        row = 2.7f*(temp-minNumb)*v+45; //计算出的旋转角度,由于前面绘制指针控件的角度是垂直向下的,表盘的起始角度是135度,所有加45度
        postInvalidate();//子线程模拟调用此方法重绘
    }
}

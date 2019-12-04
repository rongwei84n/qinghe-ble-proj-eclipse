package com.example.activity.status;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import com.lee.circleseekbar.R;
import com.example.activity.BaseActivity;
import com.example.views.DashboardView;
import com.example.views.HighlightCR;
//import com.github.mikephil.charting.animation.Easing;
//import com.github.mikephil.charting.charts.PieChart;
//import com.github.mikephil.charting.components.Legend;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.PieData;
//import com.github.mikephil.charting.data.PieDataSet;
//import com.github.mikephil.charting.formatter.PercentFormatter;
//import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OBDYiBiaoActivity extends BaseActivity {
//    private PieChart mPieChart;
    private DashboardView mDashboardView;

    @Override
    public void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_yibiao);
    }

    @Override
    public void afterInitView() {
        mTvTitle.setText("仪表");
        mDashboardView = (DashboardView) findViewById(R.id.dbv_licheng);

        List<HighlightCR> highlight2 = new ArrayList<HighlightCR>();
        highlight2.add(new HighlightCR(170, 140, Color.BLUE));
        highlight2.add(new HighlightCR(310, 60, Color.GREEN));
        mDashboardView.setStripeHighlightColorAndRange(highlight2);
        mDashboardView.mButtonCenterStr = "86%";


//        initBingtu();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("仪表盘的里程碑圆圈效果图需要另外重新绘制，这只是一个展示");
//        builder.setPositiveButton("确定", null);
//        builder.setCancelable(true);
//        builder.show();
    }

//    private void initBingtu(){
//        mPieChart = (PieChart) findViewById(R.id.pie_chart);
//
//        // 显示百分比
//        mPieChart.setUsePercentValues(true);
//        // 描述信息
//        mPieChart.setDescription("里程");
//        // 设置偏移量
//        mPieChart.setExtraOffsets(5, 10, 5, 5);
//        // 设置滑动减速摩擦系数
//        mPieChart.setDragDecelerationFrictionCoef(0.95f);
//
////        mPieChart.setCenterText("里程");
//        /*
//            设置饼图中心是否是空心的
//            true 中间是空心的，环形图
//            false 中间是实心的 饼图
//         */
//        mPieChart.setDrawHoleEnabled(false);
//        /*
//            设置中间空心圆孔的颜色是否透明
//            true 透明的
//            false 非透明的
//         */
//        mPieChart.setHoleColorTransparent(true);
//        // 设置环形图和中间空心圆之间的圆环的颜色
//        mPieChart.setTransparentCircleColor(Color.WHITE);
//        // 设置环形图和中间空心圆之间的圆环的透明度
//        mPieChart.setTransparentCircleAlpha(110);
//
//        // 设置圆孔半径
//        mPieChart.setHoleRadius(58f);
//        // 设置空心圆的半径
//        mPieChart.setTransparentCircleRadius(61f);
//        // 设置是否显示中间的文字
//        mPieChart.setDrawCenterText(true);
//
//
//        // 设置旋转角度
//        mPieChart.setRotationAngle(0);
//        // enable rotation of the chart by touch
//        mPieChart.setRotationEnabled(true);
//        mPieChart.setHighlightPerTapEnabled(false);
//
//        // add a selection listener
//        // mPieChart.setOnChartValueSelectedListener(this);
//
//        TreeMap<String, Float> data = new TreeMap<>();
//        data.put("合格率", 0.86f);
//        data.put("data2", 0.14f);
//        setData(data);
//
//        // 设置动画
//        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
//
//        // 设置显示的比例
//        Legend l = mPieChart.getLegend();
//        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(0f);
//    }

//    public void setData(TreeMap<String, Float> data) {
//        ArrayList<String> xVals = new ArrayList<String>();
//        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
//
//        int i = 0;
//        Iterator it = data.entrySet().iterator();
//        while (it.hasNext()) {
//            // entry的输出结果如key0=value0等
//            Map.Entry entry = (Map.Entry) it.next();
//            String key = (String) entry.getKey();
//            float value = (float) entry.getValue();
//            xVals.add(key);
//            yVals1.add(new Entry(value, i++));
//        }
//
//        PieDataSet dataSet = new PieDataSet(yVals1, "Election Results");
//        // 设置饼图区块之间的距离
//        dataSet.setSliceSpace(2f);
//        dataSet.setSelectionShift(5f);
//
//        // 添加颜色
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
//        colors.add(ColorTemplate.getHoloBlue());
//        dataSet.setColors(colors);
//        // dataSet.setSelectionShift(0f);
//
//        PieData data1 = new PieData(xVals, dataSet);
//        data1.setValueFormatter(new PercentFormatter());
//        data1.setValueTextSize(10f);
//        data1.setValueTextColor(Color.BLACK);
//        mPieChart.setData(data1);
//
//        // undo all highlights
//        mPieChart.highlightValues(null);
//
//        mPieChart.invalidate();
//    }

}

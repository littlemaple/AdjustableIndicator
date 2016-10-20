package com.adjustable.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by blue on 2016/10/14.
 * TODO 增加其他形状
 */
public class IndicatorView extends View implements IPageIndicator {
    private final static int DEFAULT_EACH_PADDING = 10;
    private final static int DEFAULT_LINE_WIDTH = 2;
    private final static int DEFAULT_EACH_SIZE = 6;
    private Shape shape = Shape.circle;
    private Mode mode = Mode.progress;
    private int progress = 0;
    private int current = 0;
    private int eachPadding = DEFAULT_EACH_PADDING;
    private int lineWidth = DEFAULT_LINE_WIDTH;
    private int eachSize = DEFAULT_EACH_SIZE;
    private int filledColor = Color.BLACK;
    private int strokeColor = Color.WHITE;
    private final Paint paint;
    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener mListener;
    private float positionOffset;
    private Path path = new Path();
    private float[] point1 = new float[2];
    private float[] point2 = new float[2];
    private float angle = 30;
    private float scale = 0;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndicatorView);
        eachPadding = a.getDimensionPixelSize(R.styleable.IndicatorView_each_padding, DEFAULT_EACH_PADDING);
        eachSize = a.getDimensionPixelSize(R.styleable.IndicatorView_size, DEFAULT_EACH_SIZE);
        lineWidth = a.getDimensionPixelSize(R.styleable.IndicatorView_line_width, DEFAULT_LINE_WIDTH);
        progress = a.getInt(R.styleable.IndicatorView_progress, 0);
        current = a.getInt(R.styleable.IndicatorView_current, 0);
        scale = constraintScale(a.getFloat(R.styleable.IndicatorView_scale, 0));
        angle = constraintAngle(a.getFloat(R.styleable.IndicatorView_angle, 0));
        shape = Shape.mapShape(a.getInt(R.styleable.IndicatorView_shape, 0));
        mode = Mode.mapMode(a.getInt(R.styleable.IndicatorView_mode, 0));
        filledColor = a.getColor(R.styleable.IndicatorView_filled_color, Color.BLACK);
        strokeColor = a.getColor(R.styleable.IndicatorView_stroke_color, Color.WHITE);
        a.recycle();
    }

    /**
     * @param scale should be 0-1
     */
    public void setScale(float scale) {
        this.scale = constraintScale(scale);
        postInvalidate();
    }

    public void setAngle(float angle) {
        this.angle = constraintAngle(angle);
        requestLayout();
    }

    public void setProgress(int progress) {
        if (this.progress == progress)
            return;
        this.progress = constraintProgress(progress);
        requestLayout();
    }

    public void setCurrent(int current) {
        setCurrent(current, 0);
    }

    private void setCurrent(int current, float positionOffset) {
        if (positionOffset == this.positionOffset && current == this.current)
            return;
        this.current = constraintCurrent(current);
        this.positionOffset = constraintPositionOffset(positionOffset);
        postInvalidate();
    }

    private int constraintCurrent(int current) {
        return current < 1 ? 1 : current > progress ? progress : current;
    }

    /**
     * may be not necessary
     *
     * @return constraint position offset
     */
    private float constraintPositionOffset(float positionOffset) {
        return positionOffset < 0 ? 0 : positionOffset > 1 ? 1 : positionOffset;
    }

    private int constraintProgress(int progress) {
        return progress < 0 ? 0 : progress;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (progress <= 0) {
            setMeasuredDimension(0, eachSize);
            return;
        }
        int distance = progress * eachSize + eachPadding * (progress + 1);
        int widthDimension = (int) constraint((float) (Math.sin(Math.toRadians(angle)) * distance), eachSize, distance);
        int heightDimension = (int) constraint((float) Math.abs((Math.cos(Math.toRadians(angle)) * distance)), eachSize, distance);
        setMeasuredDimension(widthDimension, heightDimension);
    }

    private float constraintAngle(float angle) {
        return angle < 0 ? 0 : angle > 180 ? 180 : angle;
    }

    private float constraintScale(float scale) {
        return scale < 0 ? 0 : scale > 1 ? 1 : scale;
    }

    private float constraint(float value, float min, float max) {
        return value < min ? min : value > max ? max : value;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawShape(canvas);
    }

    private void drawBezier(@NonNull Canvas canvas, @NonNull @Size(2) float[] point1, @NonNull @Size(2) float[] point2, float radius, Paint paint) {
        canvas.drawCircle(point1[0], point1[1], radius, paint);
        canvas.drawCircle(point2[0], point2[1], radius, paint);
        double angleRadians = Math.atan2(point2[1] - point1[1], point2[0] - point1[0]);
        float x0 = (float) (point1[0] - radius * Math.sin(angleRadians));
        float y0 = (float) (point1[1] + radius * Math.cos(angleRadians));
        float x1 = (float) (point1[0] + radius * Math.sin(angleRadians));
        float y1 = (float) (point1[1] - radius * Math.cos(angleRadians));
        float x2 = (float) (point2[0] - radius * Math.sin(angleRadians));
        float y2 = (float) (point2[1] + radius * Math.cos(angleRadians));
        float x3 = (float) (point2[0] + radius * Math.sin(angleRadians));
        float y3 = (float) (point2[1] - radius * Math.cos(angleRadians));
        float cenX0 = (x2 + x0) / 2;
        float cenY0 = (y2 + y0) / 2;
        float cenX1 = (x3 + x1) / 2;
        float cenY1 = (y3 + y1) / 2;
        float bezierX0 = (float) (cenX0 + scale * radius * 2 * Math.sin(angleRadians));
        float bezierY0 = (float) (cenY0 - scale * radius * 2 * Math.cos(angleRadians));
        float bezierX1 = (float) (cenX1 - scale * radius * 2 * Math.sin(angleRadians));
        float bezierY1 = (float) (cenY1 + scale * radius * 2 * Math.cos(angleRadians));
        path.reset();
        path.moveTo(x0, y0);
        path.cubicTo(x0, y0, bezierX1, bezierY1, x2, y2);
        path.lineTo(x3, y3);
        path.cubicTo(x3, y3, bezierX0, bezierY0, x1, y1);
        path.lineTo(x0, y0);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawShape(@NonNull Canvas canvas) {

        switch (shape) {
            case circle:
                drawShapeCircle(canvas);
                break;
            case square:
                break;
            case triangle:
                break;
        }
    }


    private void drawSquare(@NonNull Canvas canvas) {

    }

    private void drawTriangle(@NonNull Canvas canvas) {

    }

    private void drawShapeCircle(@NonNull Canvas canvas) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Log.d(getClass().getSimpleName(), "height:" + getHeight() + " angle:" + angle);
        if (angle > 90) {
            for (int i = 0; i < progress; i++) {
                paint.setColor(filledColor);
                float hypotenuse = (i + 1) * (eachSize + eachPadding) - eachSize / 2;
                float x = constraint((float) Math.cos(Math.toRadians(angle - 90)) * hypotenuse, eachSize / 2, getWidth() - eachSize / 2);
                float y = constraint(getHeight() - (float) Math.sin(Math.toRadians(angle - 90)) * hypotenuse, eachSize / 2, getHeight() - eachSize / 2);
                float radius = eachSize / 2;
                canvas.drawCircle(x, y, radius, paint);
                if (i >= current || mode == Mode.select) {
                    paint.setColor(strokeColor);
                    radius = eachSize / 2 - lineWidth;
                    canvas.drawCircle(x, y, radius, paint);
                }
            }
            float offsetPixels = eachPadding * positionOffset;
            paint.setColor(filledColor);
            float offsetHypotenuse;
            float hypotenuse;
            if (positionOffset < 0.5f) {
                offsetHypotenuse = offsetPixels + current * (eachSize + eachPadding) - eachSize / 2;
                hypotenuse = current * (eachSize + eachPadding) - eachSize / 2;

            } else {
                offsetHypotenuse = offsetPixels + current * (eachSize + eachPadding) - eachSize / 2;
                hypotenuse = (current + 1) * (eachSize + eachPadding) - eachSize / 2;
            }
            point1[0] = constraint((float) Math.cos(Math.toRadians(angle - 90)) * hypotenuse, eachSize / 2, getWidth() - eachSize / 2);
            point1[1] = constraint(getHeight() - (float) Math.sin(Math.toRadians(angle - 90)) * hypotenuse, eachSize / 2, getHeight() - eachSize / 2);
            point2[0] = constraint((float) Math.cos(Math.toRadians(angle - 90)) * offsetHypotenuse, eachSize / 2, getWidth() - eachSize / 2);
            point2[1] = constraint(getHeight() - (float) Math.sin(Math.toRadians(angle - 90)) * offsetHypotenuse, eachSize / 2, getHeight() - eachSize / 2);
            paint.setColor(filledColor);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            drawBezier(canvas, point1, point2, eachSize / 2, paint);
        } else {
            for (int i = 0; i < progress; i++) {
                paint.setColor(filledColor);
                float hypotenuse = (i + 1) * (eachSize + eachPadding) - eachSize / 2;
                float x = constraint((float) Math.sin(Math.toRadians(angle)) * hypotenuse, eachSize / 2, getWidth() - eachSize / 2);
                float y = constraint((float) Math.cos(Math.toRadians(angle)) * hypotenuse, eachSize / 2, getHeight() - eachSize / 2);
                float radius = eachSize / 2;
                canvas.drawCircle(x, y, radius, paint);
                if (i >= current || mode == Mode.select) {
                    paint.setColor(strokeColor);
                    radius = eachSize / 2 - lineWidth;
                    canvas.drawCircle(x, y, radius, paint);
                }
            }
            float offsetPixels = eachPadding * positionOffset;
            paint.setColor(filledColor);
            float offsetHypotenuse;
            float hypotenuse;
            if (positionOffset < 0.5f) {
                offsetHypotenuse = offsetPixels + current * (eachSize + eachPadding) - eachSize / 2;
                hypotenuse = current * (eachSize + eachPadding) - eachSize / 2;

            } else {
                offsetHypotenuse = offsetPixels + current * (eachSize + eachPadding) - eachSize / 2;
                hypotenuse = (current + 1) * (eachSize + eachPadding) - eachSize / 2;
            }
            point1[0] = constraint((float) Math.sin(Math.toRadians(angle)) * hypotenuse, eachSize / 2, getWidth() - eachSize / 2);
            point1[1] = constraint((float) Math.cos(Math.toRadians(angle)) * hypotenuse, eachSize / 2, getHeight() - eachSize / 2);
            point2[0] = constraint((float) Math.sin(Math.toRadians(angle)) * offsetHypotenuse, eachSize / 2, getWidth() - eachSize / 2);
            point2[1] = constraint((float) Math.cos(Math.toRadians(angle)) * offsetHypotenuse, eachSize / 2, getHeight() - eachSize / 2);
            paint.setColor(filledColor);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            drawBezier(canvas, point1, point2, eachSize / 2, paint);
        }
    }

    @Override
    public void setViewPager(ViewPager view) {
        this.viewPager = view;
        if (viewPager.getAdapter() == null) {
            throw new NullPointerException("the adapter can not be null");
        }
        setProgress(viewPager.getAdapter().getCount());
        setCurrentItem(viewPager.getCurrentItem());
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);

    }

    @Override
    public void setCurrentItem(int item) {
        if (viewPager == null)
            return;
        setCurrent(item + 1);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {

    }

    @Override
    public void notifyDataSetChanged() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        setCurrent(position + 1, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private enum Shape {
        circle(0), square(1), triangle(2);

        private int index;

        Shape(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static Shape mapShape(int index) {
            if (index == 1)
                return Shape.square;
            if (index == 2)
                return Shape.triangle;
            return Shape.circle;
        }
    }

    private enum Mode {
        select(0), progress(1);

        private int index;

        Mode(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static Mode mapMode(int index) {
            if (index == 1)
                return Mode.select;
            return Mode.progress;
        }
    }
}

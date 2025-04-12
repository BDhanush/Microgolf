package com.cs487.dhanushbommavaram.project4;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

//custom view for hole
public class Hole extends View {

    private Paint paint;
    private int holeColor = Color.BLACK; // Default black

    public Hole(Context context) {
        super(context);
        init(null);
    }

    public Hole(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Hole(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Hole);
            holeColor = typedArray.getColor(R.styleable.Hole_color, holeColor);
            typedArray.recycle();
        }

        paint.setColor(holeColor);
    }

    //draw a circle that fits in the given width and height
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius = Math.min(getWidth(), getHeight()) / 2f;
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        canvas.drawCircle(cx, cy, radius, paint);
    }

    public void setHoleColor(int color) {
        holeColor = color;
        paint.setColor(color);  //set color of the hole to the given color
        invalidate();
    }

    public int getHoleColor() {
        return holeColor;   //get current hole color
    }
}


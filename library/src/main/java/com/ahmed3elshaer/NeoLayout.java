
package com.ahmed3elshaer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ahmed3elshaer.library.R;


public class NeoLayout extends FrameLayout {

    private float mShadowRadius;
    private float mCornerRadius;
    private float mSize;
    private boolean mInvalidateShadowOnSizeChanged = true;
    private boolean mForceInvalidateShadow = false;

    public NeoLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public NeoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public NeoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w > 0 && h > 0 && (getBackground() == null || mInvalidateShadowOnSizeChanged || mForceInvalidateShadow)) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mForceInvalidateShadow) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(right - left, bottom - top);
        }
    }

    public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
        mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
    }

    public void invalidateShadow() {
        mForceInvalidateShadow = true;
        requestLayout();
        invalidate();
    }

    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);

        int xPadding = (int) (mShadowRadius + Math.abs(mSize));
        int yPadding = (int) (mShadowRadius + Math.abs(mSize));
        setPadding(xPadding, yPadding, xPadding, yPadding);
    }

    private void setBackgroundCompat(int w, int h) {
        Bitmap bitmap = createShadowBitmap(w, h, mCornerRadius, mSize);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        setBackground(drawable);
    }


    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray attr = getTypedArray(context, attrs, R.styleable.NeoLayout);

        try {
            mCornerRadius = attr.getDimension(R.styleable.NeoLayout_cornerRadius, getResources().getDimension(R.dimen.default_corner_radius));
            mShadowRadius = getResources().getDimension(R.dimen.default_shadow_radius);
            mSize = attr.getDimension(R.styleable.NeoLayout_size, 0);
            mShadowRadius = 2*mSize;
        } finally {
            attr.recycle();
        }
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float size) {

        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        float shadowRadius = size * 2;
        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                shadowWidth - shadowRadius,
                shadowHeight - shadowRadius);

        if (size > 0) {
            shadowRect.top += size;
            shadowRect.bottom -= size;
        } else if (size < 0) {
            shadowRect.top += Math.abs(size);
            shadowRect.bottom -= Math.abs(size);
        }

        if (size > 0) {
            shadowRect.left += size;
            shadowRect.right -= size;
        } else if (size < 0) {
            shadowRect.left += Math.abs(size);
            shadowRect.right -= Math.abs(size);
        }

        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.TRANSPARENT);
        shadowPaint.setStyle(Paint.Style.FILL);

        Paint shadowPaintInverse = new Paint();
        shadowPaintInverse.setAntiAlias(true);
        shadowPaintInverse.setColor(Color.TRANSPARENT);
        shadowPaintInverse.setStyle(Paint.Style.FILL);

        if (!isInEditMode()) {
            shadowPaint.setShadowLayer(size * 2, size, size, Color.BLACK);
            shadowPaint.setAntiAlias(false);
            shadowPaint.setAlpha(50);

            shadowPaintInverse.setShadowLayer(size * 2, -size, -size, Color.WHITE);
            shadowPaintInverse.setAntiAlias(false);
            shadowPaintInverse.setAlpha(50);
        }

        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);
        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaintInverse);

        return output;
    }

}
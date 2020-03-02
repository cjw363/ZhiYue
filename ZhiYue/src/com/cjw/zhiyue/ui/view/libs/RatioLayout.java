package com.cjw.zhiyue.ui.view.libs;



import com.cjw.zhiyue.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class RatioLayout extends FrameLayout {

	private float ratio;

	public RatioLayout(Context context) {
		super(context);
	}

	public RatioLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// 当自定义属性时, 系统会自动生成属性相关id, 此id通过R.styleable来引用
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.RatioLayout);
		// id = 属性名_具体属性字段名称 (此id系统自动生成)
		ratio = typedArray.getFloat(R.styleable.RatioLayout_ratio, -1);
		typedArray.recycle();// 回收typearray, 提高性能
	}
 
	public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		// 宽度确定, 高度不确定, ratio合法, 才计算高度值
		if (widthMode == MeasureSpec.EXACTLY
				&& heightMode != MeasureSpec.EXACTLY && ratio > 0) {
			// 图片宽度 = 控件宽度 - 左侧内边距 - 右侧内边距
			int imageWidth = width - getPaddingLeft() - getPaddingRight();

			// 图片高度 = 图片宽度/宽高比例
			int imageHeight = (int) (imageWidth / ratio + 0.5f);

			// 控件高度 = 图片高度 + 上侧内边距 + 下侧内边距
			height = imageHeight + getPaddingTop() + getPaddingBottom();
			// 根据最新的高度来重新生成heightMeasureSpec(高度模式是确定模式)
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
					MeasureSpec.EXACTLY);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}

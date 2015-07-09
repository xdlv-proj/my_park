package com.parking;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class TopView extends View {

	Paint paint;
	Rect frame;
	private int maskColor;
	private int frameColor;
	private int laserColor;
	int resultPointColor;
	private int scannerAlpha;
	private final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192, 128, 64 };
	private final long ANIMATION_DELAY = 80L;
	private final int POINT_SIZE = 6;

	public TopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		frameColor = resources.getColor(R.color.viewfinder_frame);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;

		WindowManager manager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);

		int densityDpi = 1;
		frame = rect(densityDpi * dm.widthPixels, densityDpi * dm.heightPixels);
	}
	
	public Rect rect(int width, int height){
		return new Rect(width * 1/16, height * 5/14, width * 15/16,height *9 /14); 
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		paint.setColor(maskColor);
		paint.setStyle(Style.FILL);
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		paint.setColor(frameColor);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(frame,paint);

		paint.setColor(laserColor);
		paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
		scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
		int middle = frame.height() / 2 + frame.top;
		canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
		postInvalidateDelayed(ANIMATION_DELAY, frame.left - POINT_SIZE, frame.top - POINT_SIZE,
				frame.right + POINT_SIZE, frame.bottom + POINT_SIZE);
	}

}

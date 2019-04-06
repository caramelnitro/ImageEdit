package mangrum.mercer.imageedit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

public class DrawActivity extends View {

    //drawing path
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private float brushSize, lastBrushSize;
    private boolean erase = false;

    public DrawActivity(Context context) {
        super(context);
        imageDraw();
    }
    public DrawActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        imageDraw();
    }
    public DrawActivity(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        imageDraw();
    }


    private void imageDraw() {

        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        //setup to draw
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setStrokeWidth(brushSize);
        //starting color
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        //initial stroke sizze
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);

        //The outer edges of a join meet in a circular arc
        drawPaint.setStrokeJoin(Paint.Join.ROUND);

        //stroke projects out as a semicircle, with the center at the end of the path
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        //smooths the stroke
        canvasPaint = new Paint(Paint.DITHER_FLAG);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //position of path drawn by user
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {

            //touch beginning coordinates
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;

                //move with path drawn
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;

                //touch ends coordinates
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);

                //reset to 0, 0 coordinate until touch begins.
                drawPath.reset();
                break;
            default:
                return false;

        }
        Log.i("Test", "FINISHED Draw");
        invalidate();
        return true;
        // return true;
    }

    public void setColor(String newColor){
        // invalidate view
        invalidate();

        //set color
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize){
        //update size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }

    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setErase(boolean isErase){
        //set erase true or false
        erase=isErase;

        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void grabImage(Bitmap b){
        canvasBitmap = getResizedBitmap(b, getWidth(), getHeight());
        invalidate();
        imageDraw();
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}

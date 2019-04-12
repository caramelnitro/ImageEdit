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
    private Path path;
    private Paint paint, canvasPaint;
    //initial color
    private int paintColor = 0xFFFF2020;
    private Canvas canvas;
    //canvas bitmap
    private Bitmap canvasBitmap, filterMap;

    private float brushSize, lastBrushSize;
    private boolean erase = false;

    ConvolutionMatrix cm = new ConvolutionMatrix(3);

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
        path = new Path();
        paint = new Paint();
        paint.setStrokeWidth(brushSize);
        //starting color
        paint.setColor(paintColor);
       paint.setAntiAlias(true);
        //initial stroke size
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);

        //The outer edges of a join meet in a circular arc
        paint.setStrokeJoin(Paint.Join.ROUND);

        //stroke projects out as a semicircle, with the center at the end of the path
       paint.setStrokeCap(Paint.Cap.ROUND);

        //smooths the stroke
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        //disable hardware acceleration to fix the black erase background
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(path, paint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //position of path drawn by user
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {

            //touch beginning coordinates
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX, touchY);
                Log.i("Test", "Down!");
                break;

                //move with path drawn
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX, touchY);
                break;

                //touch ends coordinates
            case MotionEvent.ACTION_UP:
               canvas.drawPath(path, paint);

                //reset to 0, 0 coordinate until touch begins.
                path.reset();
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
        paint.setColor(paintColor);
    }

    public void setBrushSize(float newSize){
        //update size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        paint.setStrokeWidth(brushSize);
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

        if(erase)paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else paint.setXfermode(null);
    }

    public void startNew(){
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void grabImage(Bitmap b){

        canvasBitmap = getResizedBitmap(b, getWidth(), getHeight());
        canvas = new Canvas(canvasBitmap);
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
    public void gray() {
        filterMap = Bitmap.createBitmap(canvasBitmap.getWidth(),canvasBitmap.getHeight(), canvasBitmap.getConfig());
        double red = 0.33;
        double green = 0.59;
        double blue = 0.11;
        int p, r, g, b;

        for (int i = 0; i < canvasBitmap.getWidth(); i++) {
            for (int j = 0; j < canvasBitmap.getHeight(); j++) {
                p = canvasBitmap.getPixel(i, j);
                r = Color.red(p);
                g = Color.green(p);
                b = Color.blue(p);

                r = (int) red * r;
                g = (int) green * g;
                b = (int) blue * b;
                filterMap.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
            }
        }
        grabImage(filterMap);
    }
    public void  bright(){
        filterMap = Bitmap.createBitmap(canvasBitmap.getWidth(),canvasBitmap.getHeight(), canvasBitmap.getConfig());

        int p, r, g, b, alpha;
        for (int i = 0; i < canvasBitmap.getWidth(); i++) {
            for (int j = 0; j < canvasBitmap.getHeight(); j++) {
                p = canvasBitmap.getPixel(i, j);
                r = Color.red(p);
                g = Color.green(p);
                b = Color.blue(p);
                alpha = Color.alpha(p);

                r = r + 50;
                g = g + 50;
                b = b + 50;
                alpha = alpha + 50;
                filterMap.setPixel(i, j, Color.argb(alpha, r, g, b));
            }
        }
        grabImage(filterMap);
    }
    public void  dark(){
        filterMap = Bitmap.createBitmap(canvasBitmap.getWidth(),canvasBitmap.getHeight(), canvasBitmap.getConfig());

        int p, r, g, b, alpha;
        for (int i = 0; i < canvasBitmap.getWidth(); i++) {
            for (int j = 0; j < canvasBitmap.getHeight(); j++) {
                p = canvasBitmap.getPixel(i, j);
                r = Color.red(p);
                g = Color.green(p);
                b = Color.blue(p);
                alpha = Color.alpha(p);

                r = r - 50;
                g = g - 50;
                b = b - 50;
                alpha = alpha - 50;
                filterMap.setPixel(i, j, Color.argb(alpha, r, g, b));
            }
        }
        grabImage(filterMap);
    }
}

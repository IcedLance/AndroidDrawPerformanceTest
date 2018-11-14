package io.github.icedlance.drawperformancetest;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.os.health.SystemHealthManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TimingLogger;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;


public class DrawTestView extends android.support.v7.widget.AppCompatImageView {

    private Bitmap mBitmap = null;
    private Canvas mCanvas = null;
    private Handler mHandler = new Handler();
    private Runnable testRunnable;
    private TimingLogger logger;
    private Random mRand = new Random();
    private int mMult = 1;

    private Path pathA;
    private Bitmap bitmapA;

    private Paint paintFill = new Paint();
    private Paint paintStroke = new Paint();
    private Paint paintShader = new Paint();

    public DrawTestView(Context context) {
        super(context);
        init();
    }

    public DrawTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawRect(this.getLeft()+5, this.getTop()+5, this.getRight()-5, this.getBottom()-5, paintStroke);
        Log.i("DTST", String.format("Drawing with size: [%d, %d]", this.getWidth(), this.getHeight()));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        Log.i("DTST", String.format("Size changed: %d, %d.", this.getWidth(), this.getHeight()));
    }

    private void clearBitmap(){
        Paint transparent = new Paint();
        transparent.setStyle(Paint.Style.FILL);
        transparent.setColor(0x00ffffff);
        mBitmap.eraseColor(0x00000000);
    }

    private void drawPathRandom(){
        int posX = getRandX();
        int posY = getRandY();

        mCanvas.save();
        mCanvas.translate(posX, posY);
        mCanvas.drawPath(pathA, paintFill);
        mCanvas.drawPath(pathA, paintStroke);
        mCanvas.restore();
    }

    private void drawBitmapRandom() {
        int posX = getRandX();
        int posY = getRandY();

        mCanvas.save();
        mCanvas.translate(posX, posY);
        mCanvas.drawBitmap(bitmapA, 0, 0, null);
        mCanvas.restore();
    }

    private void drawBitmapsWithShaderRandom() {
        int posX = getRandX();
        int posY = getRandY();

        mCanvas.save();
        mCanvas.translate(posX, posY);
        //TODO play with paintBitmap(Bitmap.convert)
        mCanvas.drawBitmap(bitmapA.copy(Bitmap.Config.ALPHA_8, true),0,0,paintShader);
        mCanvas.restore();
    }

    private void init() {

        pathA = new Path();
        pathA.moveTo(0, 50);
        pathA.lineTo(25, 0);
        pathA.lineTo(50, 50);
        pathA.lineTo(25, 40);
        pathA.close();

        bitmapA = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        Canvas canv = new Canvas(bitmapA);

        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setColor(0xffffffff);
        canv.drawPath(pathA, paintFill);

        //CHECK why this causes error
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(4);
        paintStroke.setColor(0xffff0000);
        //TODO add antialias [on/off] test
        paintStroke.setAntiAlias(true);
        canv.drawPath(pathA, paintStroke);



        //TODO find why it throws error
        try {

            paintShader.setStyle(Paint.Style.FILL_AND_STROKE);
            Bitmap redBitmap = Bitmap.createBitmap(new int[]{0xffff0000, 0xffff0000, 0xffff0000, 0xff0000ff}, 0, 2, 2, 2, Bitmap.Config.ARGB_8888);
            paintShader.setShader(new BitmapShader(redBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            paintShader.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

            //mCanvas.drawBitmap(redBitmap, new Rect(100, 100, 200, 200), );

            testRunnable = new Runnable() {
                @Override
                public void run() {

                    final int MAX_ITER = 5000;   //CHECK

                    Log.i("DTST", "Starting the tests.");


                    mMult = 1;
                    logger = new TimingLogger("DTST", "Draw test: 5000 objects all fit on screen:");
                    clearBitmap();
                    for (int i = 0; i < MAX_ITER; i++)
                        drawPathRandom();
                    logger.addSplit("Drawing with Path twice (Fill and Stroke)");
                    clearBitmap();
                    for (int i = 0; i < MAX_ITER; i++)
                        drawBitmapRandom();
                    logger.addSplit("Drawing a pre-drawn Bitmap");
                    clearBitmap();
                    for (int i = 0; i < MAX_ITER; i++)
                        drawBitmapsWithShaderRandom();
                    logger.addSplit("Drawing same bitmap with BitmapShader[2px by 2px]");
                    logger.dumpToLog();


                    mMult = 3;
                    logger = new TimingLogger("DTST", "Draw test: 5000 randomly over space of 3x3 screens:");
                    clearBitmap();
                    for (int i = 0; i < MAX_ITER; i++)
                        drawPathRandom();
                    logger.addSplit("Drawing with Path twice (Fill and Stroke)");
                    clearBitmap();
                    for (int i = 0; i < MAX_ITER; i++)
                        drawBitmapRandom();
                    logger.addSplit("Drawing a pre-drawn Bitmap");
                    clearBitmap();
                    for (int i = 0; i < MAX_ITER; i++)
                        drawBitmapsWithShaderRandom();
                    logger.addSplit("Drawing same bitmap with BitmapShader[2px by 2px]");
                    logger.dumpToLog();
                    invalidate();
                }

            };

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("DTST", "Clicked");
                    getHandler().post(testRunnable);
                }
            });

            Log.i("DTST", "Click event created successfully.");
        }
        catch(Exception e){
            System.out.print(e.getMessage());
            Log.i("DTST", "Exception:"+e.getMessage());
        }

    }

    private int getRandX(){
        return mRand.nextInt(mCanvas.getWidth()*mMult);
    }
    private int getRandY(){
        return mRand.nextInt(mCanvas.getHeight()*mMult);
    }

}

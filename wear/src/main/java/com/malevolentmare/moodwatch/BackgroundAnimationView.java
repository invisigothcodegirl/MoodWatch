package com.malevolentmare.moodwatch;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * This is the soothing animation that plays while the person
 * using this app meditates.
 */
public class BackgroundAnimationView extends View {

    //colors to shift between stored in colors.xml
    private int[] moodRingColors;

    //Array to hold the different mood strings
    private String[] moodDescriptors;

    //Change from this color
    private int fromColorIndex = 0;

    //to this color
    private int toColorIndex = 0;

    //The current beats per minute
    private float currentBPM = 60;

    //low threshold
    private final int LOW_BPM = 60;

    //high threshold
    private final int HIGH_BPM = 120;

    //total threshold
    private final int THRESHOLD = HIGH_BPM - LOW_BPM;

    //The current color of the screen
    private int currentColor;

    //The thread handler
    private Handler handler;

    //Regular Digital Font
    private Typeface digitalNumberFont;

    //Italicized Digital Font
    private Typeface italicDigitalNumberFont;

    //The last color the watch was
    private int lastColor;

    //Calculate how far the shift is
    float progressSteps = 0.1f;

    //Constructor
    public BackgroundAnimationView(Context context) {
        //cal to super
        super(context);
        //The init method sets up this view when it is
        //created
        init();
    }

    //Another constructor
    public BackgroundAnimationView(Context context, AttributeSet attrs) {
        //call to super
        super(context, attrs);
        //The init method sets up this view when it is
        //created
        //Load the custom digital font
        digitalNumberFont = Typeface.createFromAsset(
                getContext().getAssets(), "digital_regular.ttf");
        //Load the custom digital italic font in italics
        italicDigitalNumberFont = Typeface.createFromAsset(
                getContext().getAssets(), "digital_italic.ttf");

        init();
    }

    //The init method, gets things setup and also serves
    //as an animation loop
    private void init() {
        Log.d("INIT", "In the init method");

        //Get the moodRingColors out of the colors.xml file
        moodRingColors =
                getContext().getResources().getIntArray(R.array.mood_ring);

        //Get the mood descriptor strings
        moodDescriptors =
                getContext().getResources().getStringArray(R.array.color_meanings);

        //Set default values for and lastColor
        currentColor = moodRingColors[0];
        lastColor = currentColor;

        //Declare and instantiate the handler
        handler = new Handler() {
            Random rand = new Random();
            //When message is sent

            @Override
            public void handleMessage(Message msg) {
                //The following block of code generates
                //A sequence of numbers that simulate
                //A gradually climbing heart rate
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(rand.nextInt()%4==0) {
                    currentBPM -= rand.nextFloat();
                }
                else
                {
                    currentBPM += rand.nextFloat();
                }

                //adjust bpm on scale
                int adjustedBPM = (int)currentBPM - LOW_BPM;

                //make sure it's within bounds this mainly
                //happens when sensor first comes on and returns
                //a bunch of zeros, but could happen if heart rate
                //gets too high or too low for my bounds
                //I've assumed the average human resting heart rate is
                //50 - 120
                if(adjustedBPM < 0) {
                    adjustedBPM = 0;
                }
                else if(adjustedBPM > THRESHOLD - 1) {
                    adjustedBPM = THRESHOLD - 1;
                }

                progressSteps = (float)adjustedBPM/(float)THRESHOLD;

                //Get where the current color should be based on BPM
                toColorIndex = (int)(progressSteps*moodRingColors.length);

                float toColorFloat = progressSteps*moodRingColors.length;

                float currentColorPercent = toColorFloat - toColorIndex;

                Log.d("TOCOLORFLOAT","float: " + toColorFloat + " index " + toColorIndex + " percent " + currentColorPercent);

                //figure out whether moving up or down in the color index arena
                if(toColorIndex > fromColorIndex + 1 && fromColorIndex + 1 < moodRingColors.length) {
                    toColorIndex = fromColorIndex + 1;
                }
                else if(toColorIndex < fromColorIndex - 1 && fromColorIndex - 1 >= 0) {
                    toColorIndex = fromColorIndex - 1;
                }
                int shouldBeColor = getColor(currentColorPercent,lastColor,moodRingColors[fromColorIndex]);
                currentColor = getColor(.07f, lastColor,
                        shouldBeColor);


                postInvalidate();
                fromColorIndex = toColorIndex;
                lastColor = currentColor;

                handler.sendEmptyMessageDelayed(0, 100);
            }
        };
    }

    /** ON DRAW METHOD *************************************************/
    @Override
    protected void onDraw(Canvas canvas) {
        //Call to super
        super.onDraw(canvas);
        //Create an anti alias paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //set the background color of the canvas
        canvas.drawColor(currentColor);

        //find the horizontal center of the screen for the text
        paint.setTextAlign(Paint.Align.LEFT);


        //Set the size of the text to 80
        paint.setTextSize(80);
        //Format the heartrate to two decimal places
        DecimalFormat df = new DecimalFormat("##0.00");
        //Create a formatted version of the heartrate string to measure
        String currentBPMString = df.format(currentBPM);
        //Get the mood text size
        paint.setTextSize(40);
        float moodTextHeight = paint.getTextSize();
        //Get the heart rate text size
        paint.setTextSize(80);












        //Draw the heartrate to the screen

        //Set the font to the custom digital font
        paint.setTypeface(digitalNumberFont);
        //Set the size back to 80
        paint.setTextSize(80);
        //Measure the biggest possible number
        float heartRateWidth = paint.measureText(currentBPMString);
        //Calculate the x coordinate of where to draw the heart rate text
        float heart_text_x = (getWidth() - heartRateWidth)/2.0f;
        //Trying to calculate y coordinate
        float heart_text_y = getHeight()/2.0f + paint.getTextSize()/2.0f - moodTextHeight;
        //Set the color to black
        paint.setColor(Color.BLACK);
        //Draw the shadow of the heartrate text
        canvas.drawText(currentBPMString, heart_text_x + 3, heart_text_y + 3, paint);
        //Set the drawing color to white
        paint.setColor(Color.WHITE);
        //Draw the heart rate text in white over the shadow
        canvas.drawText(currentBPMString, heart_text_x, heart_text_y, paint);

        //Draw the mood descriptor to the screen
        //Set the font to the custom digital italic font
        paint.setTypeface(italicDigitalNumberFont);
        //Set the size back to 40
        paint.setTextSize(40);
        //Measure the biggest possible number
        float moodTextWidth = paint.measureText(moodDescriptors[toColorIndex]);
        //Calculates the x coordinate of where to draw the mood text
        float mood_text_x = (getWidth() - moodTextWidth)/2.0f;
        //Trying to calculate y coordinate
        float mood_text_y = heart_text_y + moodTextHeight;
        //Set the color to black
        paint.setColor(Color.BLACK);
        //Draw the shadow of the mood text
        canvas.drawText(moodDescriptors[toColorIndex], mood_text_x, mood_text_y+3, paint);
        //Set the drawing color to white
        paint.setColor(Color.WHITE);
        //Draw the mood text in white over the shadow
        canvas.drawText(moodDescriptors[toColorIndex], mood_text_x, mood_text_y, paint);



    }

    //Start the handler thread
    public void startAnimating() {
        currentBPM = 60;

        handler.sendEmptyMessage(0);
    }

    //Stop the handler thread
    public void stopAnimating() {
        handler.removeMessages(0);
    }

    //Calculate a value between one color and another, will rewrite this
    //in a less terrifying manner soon
    private int getColor(float fraction, int colorStart, int colorEnd) {
        int startA = (colorStart >> 24) & 0xff;
        int startR = (colorStart >> 16) & 0xff;
        int startG = (colorStart >> 8) & 0xff;
        int startB = colorStart & 0xff;

        int endA = (colorEnd >> 24) & 0xff;
        int endR = (colorEnd >> 16) & 0xff;
        int endG = (colorEnd >> 8) & 0xff;
        int endB = colorEnd & 0xff;

        return (startA + (int)(fraction * (endA - startA))) << 24 |
                (startR + (int)(fraction * (endR - startR))) << 16 |
                (startG + (int)(fraction * (endG - startG))) << 8 |
                ((startB + (int)(fraction * (endB - startB))));
    }

    //A setter for the current BPM coming from the
    //sensors in the MainActivity class
    public void setCurrentBPM(float currentBPM)
    {
        this.currentBPM = currentBPM;
    }
}

package mangrum.mercer.imageedit;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawActivity drawAct;
    private ImageButton currPaint, drawBtn, eraseBtn;

    private float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawAct = (DrawActivity) findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);

        currPaint = (ImageButton)paintLayout.getChildAt(0);

        currPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint_pressed));

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        drawBtn = (ImageButton)findViewById(R.id.draw_btn);

        drawBtn.setOnClickListener(this);

        drawAct.setBrushSize(mediumBrush);

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
    }

    public void paintClicked(View view) {
        drawAct.setErase(false);

        if(view!=currPaint){
        //update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();

            drawAct.setColor(color);


            imgView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint_pressed));

    //        currPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint));

            currPaint=(ImageButton)view;

            drawAct.setBrushSize(drawAct.getLastBrushSize());
      /*          ------------------------------------------------------------------
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
       //THis line gives me errors
         //   currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
   --------------------------------------------------------------------------------------  */

        }

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.draw_btn){

            //draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");

            brushDialog.setContentView(R.layout.brush_choice);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setBrushSize(smallBrush);
                    drawAct.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setBrushSize(mediumBrush);
                    drawAct.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setBrushSize(largeBrush);
                    drawAct.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }
        // Erase

        else if(v.getId()==R.id.erase_btn){
            //switch to erase - choose size
            final Dialog brushDialog = new Dialog(this);

            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_choice);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setErase(true);
                    drawAct.setBrushSize(smallBrush);
                    drawAct.setErase(false);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setErase(true);
                    drawAct.setBrushSize(mediumBrush);

                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setErase(true);
                    drawAct.setBrushSize(largeBrush);
                    drawAct.setErase(false);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();

        }
    }

}

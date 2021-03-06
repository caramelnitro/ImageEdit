package mangrum.mercer.imageedit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION = 0;
    private static final int RESULT_LOAD = 1;
    //instance variable to custom view
    private DrawActivity drawAct;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, uploadBtn, filterBtn, squareBtn;
    private ImageButton colors;
    private float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE
        )!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
            PERMISSION);
        }
        //reference custom view to call methods
        drawAct = findViewById(R.id.drawing);

        LinearLayout paintLayout = findViewById(R.id.paint_colors);

        currPaint = (ImageButton)paintLayout.getChildAt(0);

        currPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint_selected));

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        drawBtn = findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

        drawAct.setBrushSize(mediumBrush);

        eraseBtn = findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        newBtn = findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        uploadBtn = findViewById(R.id.upload_btn);
        uploadBtn.setOnClickListener(this);

        filterBtn = findViewById(R.id.filter_button);
        filterBtn.setOnClickListener(this);

        squareBtn = findViewById(R.id.square_btn);
        squareBtn.setOnClickListener(this);

        for(int i = 0; i < paintLayout.getChildCount(); i++){
            colors = (ImageButton)paintLayout.getChildAt(i);
            colors.setOnClickListener(this);
        }
    }

    public void paintClicked(View view) {
        drawAct.setErase(false);

        if(view!=currPaint){
        //update color
            Log.i("YAY", "Made it");
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();

            drawAct.setColor(color);

            drawAct.setBrushSize(drawAct.getLastBrushSize());

            imgView.setImageDrawable(getDrawable(R.drawable.paint_selected));
           currPaint.setImageDrawable(getDrawable(R.drawable.paint));
            currPaint=imgView;
        }

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.draw_btn){
            drawAct.setErase(false);
            drawAct.setRect(false);
            //draw button clicked allow select brush size
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush:");

            brushDialog.setContentView(R.layout.brushes);

            ImageButton smallBtn = brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setBrushSize(smallBrush);
                    drawAct.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setBrushSize(mediumBrush);
                    drawAct.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton darkerBtn = brushDialog.findViewById(R.id.large_brush);
            darkerBtn.setOnClickListener(new OnClickListener(){
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
            //erase selected - choose size
            final Dialog brushDialog = new Dialog(this);

            brushDialog.setTitle("Eraser:");
            brushDialog.setContentView(R.layout.brushes);

            ImageButton smallBtn = brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setErase(true);
                    drawAct.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setErase(true);
                    drawAct.setBrushSize(mediumBrush);

                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.setErase(true);
                    drawAct.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }
        //NEW
        else if(v.getId()==R.id.new_btn){

            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Would you like to start new drawing?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                public void onClick(DialogInterface dialog, int which){
                    drawAct.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();

        }
        // Save
        else if(v.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    saveBtn.setEnabled(true);
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();

            //enabling drawing
            drawAct.setDrawingCacheEnabled(true);

            //write the image to a file
            String imgSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(), drawAct.getDrawingCache(),
                    UUID.randomUUID().toString()+".png", "drawing");

            if(imgSaved!=null){
                Toast savedToast = Toast.makeText(getApplicationContext(),
                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            }
            else{
                Toast unsavedToast = Toast.makeText(getApplicationContext(),
                        "Unable to save image.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }
            drawAct.destroyDrawingCache();
        }
        else if(v.getId() == R.id.upload_btn){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD);
        }
        else if(v.getId() == R.id.filter_button){
            //erase selected - choose size
            final Dialog brushDialog = new Dialog(this);

            brushDialog.setTitle("Pick Filter:");
            brushDialog.setContentView(R.layout.grayscale);

            ImageButton grayBtn = brushDialog.findViewById(R.id.gray);
            grayBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.gray();
                    brushDialog.dismiss();
                }
            });
            ImageButton lighterBtn = brushDialog.findViewById(R.id.lighter);
            lighterBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.bright();
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = brushDialog.findViewById(R.id.darker);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawAct.dark();
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }
        else if(v.getId() == R.id.square_btn){
            drawAct.setRect(true);
        }
        else {
            paintClicked(v);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case  PERMISSION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        //do nothing
                    }
                }
                else
                {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    //saving the image in internal storage
    private void save(Bitmap bitmap, String fileName) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FILTEREDIMAGES";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case RESULT_LOAD:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String [] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath,
                            null, null, null);
                    c.moveToFirst();
                    int colIndex = c.getColumnIndex(filePath[0]);
                    String picPath = c.getString(colIndex);
                    c.close();
                    drawAct.grabImage(BitmapFactory.decodeFile(picPath));
                }
        }
    }
}

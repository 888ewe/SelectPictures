package com.chenmo.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv;
    private Button button1, button2, button3,button4;
    private int REQUESTCOdE_1 = 100;
    private int REQUESTCOdE_2 = 200;
    private int REQUESTCOdE_3 = 300;
    private String mFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        mFilePath = Environment.getExternalStorageDirectory().getPath();
        mFilePath = mFilePath + "/" + "temp.png";
        Log.e("Tag","mFilePath"+mFilePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCOdE_1) {//相机
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                Log.e("Tag", "相机" + bitmap);
                iv.setImageBitmap(bitmap);
            } else if (requestCode == REQUESTCOdE_2) {//相册
                Log.e("Tag", "相册" + data.getData());
                iv.setImageURI(data.getData());
            } else if (requestCode == REQUESTCOdE_3) {//相机,无压缩
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(mFilePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    iv.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1://相机
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent1, REQUESTCOdE_1);
                break;
            case R.id.button2://相册
                Intent intent2 = new Intent(Intent.ACTION_PICK);
                intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent2, REQUESTCOdE_2);
                break;
            case R.id.button3://相机,无压缩
                Intent intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photouri = Uri.fromFile(new File(mFilePath));
                intent3.putExtra(MediaStore.EXTRA_OUTPUT, photouri);
                startActivityForResult(intent3, REQUESTCOdE_3);
                break;
            case R.id.button4://自定义相机
                Intent intent4 = new Intent(this,CameraActivity.class);
                startActivity(intent4);
                break;
        }
    }
}

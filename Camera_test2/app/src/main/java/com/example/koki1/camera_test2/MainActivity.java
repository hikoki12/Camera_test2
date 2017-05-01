package com.example.koki1.camera_test2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public Uri _imageUri;
    public int btn_flag;//0ならマザー、1ならダビング
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //最初の画面表示

        /*
        if(!OpenCVLoader.initDebug()){
            Log.i("OpenCV", "Failed");
        }else {
            Log.i("OpenCV", "successfully built !");
        }
        */
    }


    //ボタン押したときの動作
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onCameraImageClick(View view) {

        if(view.getId() == R.id.btn_dab) {
            btn_flag = 1;
        }
        else{
            btn_flag = 0;
        }

        //ストレージへのアクセス許可
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 2000);
            return;
        }

        //ここからURIに格納するファイル情報の付与
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");  // （1）
        Date now = new Date(System.currentTimeMillis());  // （1）
        String nowStr = dateFormat.format(now);  // （1）
        String fileName = "UseCameraActivityPhoto_" + nowStr +".jpg";  // （1）

        ContentValues values = new ContentValues();  // （2）
        values.put(MediaStore.Images.Media.TITLE, fileName);  // （3）
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");  // （4）

        ContentResolver resolver = getContentResolver();  // （5）
        _imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  // （6）

        //ここまで

        //ここからカメラ起動
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  // （7）
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);  // （8）
        //System.out.println("imageUri:"+ _imageUri);
        startActivityForResult(intent, 200);  // （9）
    }


    //アクセス許可が下りたら自動でカメラを起動させる
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if(btn_flag == 0) {
                Button btn_mother = (Button) findViewById(R.id.btn_mother);  // （1）
                onCameraImageClick(btn_mother);
            }else{
                Button btn_dab = (Button) findViewById(R.id.btn_dab);  // （1）
                onCameraImageClick(btn_dab);
            }

        }
    }



    //カメラ撮影後の動作、画像解析のActivityに移動
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode == 200 && resultCode == RESULT_OK) {
           Intent intent2 = new Intent();
           intent2.setClassName(getPackageName(), getPackageName()+".present");

           intent2.putExtra("URL", _imageUri);
           //System.out.println("return_imageUri:"+ _imageUri);
           intent2.putExtra("btn_flag",btn_flag);
           startActivity(intent2);
        }
    }


}

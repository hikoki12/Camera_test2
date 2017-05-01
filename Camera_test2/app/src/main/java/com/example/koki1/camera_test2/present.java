package com.example.koki1.camera_test2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;


public class present extends AppCompatActivity {
    Bitmap image; //初期化
    Bitmap dasi_num,uke_num; //出しテープNo.と受けテープNo.を記録
    Mat cvimg;
    private TessBaseAPI mTess;
    String datapath = "";
    public int btn_flag;
    public Uri uke_url,dasi_url;

//    public int flag = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //引き渡した画像のURIを取得
        Bundle bundle =getIntent().getExtras();
        Uri _imageUri = (Uri)bundle.get("URL");
        btn_flag = (int)bundle.get("btn_flag");

        setContentView(R.layout.activity_present);

        //画像を張り付け
        //ImageView ivCamera = (ImageView) findViewById(R.id.ivCamera);  // （2）
        //ivCamera.setImageURI(_imageUri);  // （3）


        //画像をURIをもとにbitmapに貼り付け
        try {
            image = MediaStore.Images.Media.getBitmap(getContentResolver(), _imageUri);
        }catch (IOException e) {
            e.printStackTrace();
        }

        //OCRの下準備
        String language = "eng";
        datapath = getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));

        mTess.init(datapath, language);



        //トリミング画像格納先情報用の時刻情報の取得
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");  // （1）
        Date now = new Date(System.currentTimeMillis());  // （1）
        String nowStr = dateFormat.format(now);  // （1）

        //トリミング画像格納先情報の入力、1つ目のトリミング呼び出し
        String dasi_Name = "dasi_" + nowStr +".jpg";  // （1）
        ContentValues values = new ContentValues();  // （2）
        values.put(MediaStore.Images.Media.TITLE, dasi_Name);  // （3）
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");  // （4）
        ContentResolver resolver = getContentResolver();  // （5）
        dasi_url = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  // （6）

        //intent作成、１つ目のトリミング呼び出し
        Intent crop = new Intent("com.android.camera.action.CROP");
        crop.setData(_imageUri);
        crop.putExtra("return-data", true);
        crop.putExtra(MediaStore.EXTRA_OUTPUT, dasi_url);  // （8）
        System.out.println("dasi:" + dasi_url);
        startActivityForResult(crop, 300);


        //トリミング画像格納先情報の入力、２つ目のトリミング呼び出し
        String uke_Name = "uke_" + nowStr +".jpg";  // （1）
        ContentValues values2 = new ContentValues();  // （2）
        values2.put(MediaStore.Images.Media.TITLE, uke_Name);  // （3）
        values2.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");  // （4）
        ContentResolver resolver2 = getContentResolver();  // （5）
        uke_url = resolver2.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values2);  // （6）

        //intent作成、１つ目のトリミング呼び出し
        Intent crop2 = new Intent("com.android.camera.action.CROP");
        crop2.setData(_imageUri);
        crop2.putExtra("return-data", true);
        crop2.putExtra(MediaStore.EXTRA_OUTPUT, uke_url);  // （8）
        System.out.println("uke:" + uke_url);
        startActivityForResult(crop2, 400);


    }


    //解析ボタンを押した際のOCR処理（未完成）
    public void processImage(View view){
        String OCRresult = null;
        //Mat cv_img = new Mat(image.getHeight(), image.getWidth(),CvType.CV_32F);
        Mat cv_img = new Mat();
        Utils.bitmapToMat(image,cv_img);

//        flag++;

        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setText("Wait a second...");

        System.out.println("matへの変換完了");

        Imgproc.cvtColor(cv_img, cv_img, Imgproc.COLOR_RGB2GRAY);

        System.out.println("グレースケール変換完了");

        /*
        if(flag == 1){
            Imgproc.threshold(cv_img, cv_img, 0.0 , 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        }
        if(flag == 2){
            Imgproc.morphologyEx(cv_img, cv_img, Imgproc.MORPH_ERODE, new Mat(), new Point(-1, -1), 3);
            Imgproc.morphologyEx(cv_img, cv_img, Imgproc.MORPH_DILATE, new Mat(), new Point(-1, -1), 3);
        }
        */



        Imgproc.threshold(cv_img, cv_img, 0.0 , 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        Imgproc.morphologyEx(cv_img, cv_img, Imgproc.MORPH_ERODE, new Mat(), new Point(-1, -1), 3);
        Imgproc.morphologyEx(cv_img, cv_img, Imgproc.MORPH_DILATE, new Mat(), new Point(-1, -1), 3);


        System.out.println("2値化完了");

        Utils.matToBitmap(cv_img,image);

        System.out.println("bitmapへの変換完了");

//テスト
        //ImageView ivCamera = (ImageView) findViewById(R.id.ivCamera);  // （2）
        //ivCamera.setImageBitmap(image);  // （3）

        System.out.println("ImageViewの更新完了");

        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        //TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setText(OCRresult);

        System.out.println("OCR変換結果の表示完了");

        /*
        try {
            Thread.sleep(3000);
        }catch(InterruptedException e){}
        */

        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getPackageName()+".send");
        intent.putExtra("btn_flag",btn_flag);
        intent.putExtra("result",OCRresult);
        startActivity(intent);


    }



    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }


            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //トリミング終了後にトリミング画像をimageviewに貼り付け
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //１つ目のトリミング
            case 300:
                /*
                System.out.println("dasiきました");
                dasi_num = data.getExtras().getParcelable("data");
                ImageView dasi = (ImageView) findViewById(R.id.dasi);  // （2）
                dasi.setImageBitmap(dasi_num);  // （3）
                */
                System.out.println("return_dasi:" + dasi_url);
                ImageView dasi = (ImageView) findViewById(R.id.dasi);  // （2）
                dasi.setImageURI(dasi_url);  // （3）

                // ２つ目のトリミング
            case 400:
                /*
                System.out.println("ukeきました");
                uke_num = data.getExtras().getParcelable("data");
                ImageView uke = (ImageView) findViewById(R.id.uke);  // （2）
                uke.setImageBitmap(uke_num);  // （3）
                */
                System.out.println("return_uke:" + uke_url);
                ImageView uke = (ImageView) findViewById(R.id.uke);  // （2）
                uke.setImageURI(uke_url);  // （3）
        }
    }

}

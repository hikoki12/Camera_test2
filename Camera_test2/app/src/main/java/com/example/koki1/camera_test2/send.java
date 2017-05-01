package com.example.koki1.camera_test2;

import android.content.Context;
import android.content.IntentSender;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.drive.Drive;
//import com.google.android.gms.drive.DriveApi;
//import com.google.android.gms.drive.MetadataChangeSet;

public class send extends AppCompatActivity {

    // キーボード表示を制御するためのオブジェクト
    InputMethodManager inputMethodManager;
    // 背景のレイアウト
    private LinearLayout mainLayout;
    public int btn_flag;
    public String result;
    public String res[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        Bundle bundle =getIntent().getExtras();
        btn_flag = (int)bundle.get("btn_flag");
        result = (String)bundle.get("result");
        res = result.split("\n");
        //System.out.println(result+",btn_flag="+btn_flag);
        //System.out.println(res[0]);

        for(int i = 0 ; i < res.length ; i++) {

            if (i == 0) {
                EditText OCRTextView = (EditText) findViewById(R.id.edit_out);
                OCRTextView.setText(res[0]);
            }
            if (i == 1) {
                EditText OCRTextView2 = (EditText) findViewById(R.id.edit_in);
                OCRTextView2.setText(res[1]);
            }
        }
    }


    // 画面タップ時の処理
    @Override
    public boolean onTouchEvent(MotionEvent event) {

// キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
// 背景にフォーカスを移す
        mainLayout.requestFocus();

        return true;

    }

    public void onSendButtonClick(Button btn) {

        /*
        ResultCallback<DriveApi.DriveContentsResult> newDriveContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult result) {
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setMimeType("text/html").build();
                IntentSender intentSender = Drive.DriveApi.newCreateFileActivityBuilder().setInitialMetadata(metadataChangeSet).setInitialDriveContents(result.getDriveContents()).build(getGoogleApiClient());
                try {
                    startIntentSenderForResult(intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    //Log.w(TAG, "Unable to send intent", e);
                }
            }
        }
        */



    }


}

package com.neotechindia.plugsmart.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by apple on 27/12/16.
 */

public class BarcodeScannerActivity extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
  //  private AlertDialog mDialog;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }
    @Override
    public void onResume() {
        super.onResume();
    //    mDialog =  new AlertDialog.Builder(this).create();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent=new Intent();
        intent.putExtra("SCAN_RESULT",rawResult.getText());
        intent.putExtra("SCAN_RESULT_FORMAT",rawResult.getBarcodeFormat().toString());
       /* if (rawResult != null) {
            mDialog.setTitle("Result");
            mDialog.setMessage(rawResult
                    .getText());
            mDialog.show();
        }*/
     //   Toast.makeText(this,"Result - "+rawResult.getText().toString(),Toast.LENGTH_SHORT).show();
     //   Toast.makeText(this,"Result - "+rawResult,Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK,intent);
        mScannerView.stopCameraPreview();
        finish();

    }

}

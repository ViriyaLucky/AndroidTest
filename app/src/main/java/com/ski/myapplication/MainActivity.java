package com.ski.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Random;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity {
    private static final String ALLOWED_CHARACTERS ="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button gen = (Button) findViewById(R.id.generateBarcode);
        final Button pin = (Button) findViewById(R.id.pin);
        Button scan = (Button) findViewById(R.id.scanner);
        final int min = 1111;
        final int max = 9999;
        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                QRCodeWriter writer = new QRCodeWriter();
                try {
                    //disable generate code
                    gen.setEnabled(false);

                    //generate random number
                    String random  = getRandomString(8);
                    //generate qr code
                    BitMatrix bitMatrix = writer.encode(String.valueOf(random), BarcodeFormat.QR_CODE, 512, 512);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }

//                    //get company logo
//                    Bitmap bMapScale  = BitmapFactory.decodeResource(getResources(), R.drawable.transformx);
//                    Bitmap bitmapToDrawInTheCenter = Bitmap.createScaledBitmap(bMapScale, bMapScale.getWidth()/12, bMapScale.getHeight()/12, true);
//
//                    Bitmap resultBitmap = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), bmp.getConfig());
//
//                    //put company logo in qr code
//                    Canvas canvas = new Canvas(resultBitmap);
//                    canvas.drawBitmap(bmp, new Matrix(), null);
//                    canvas.drawBitmap(bitmapToDrawInTheCenter, (bmp.getWidth() - bitmapToDrawInTheCenter.getWidth()) / 2, (bmp.getHeight() - bitmapToDrawInTheCenter.getHeight()) / 2, new Paint());

                    ((ImageView) findViewById(R.id.img_result_qr)).setImageBitmap(bmp);
                    ((TextView) findViewById(R.id.codegenerated)).setText(String.valueOf(random));
                    gen.setEnabled(true);
                } catch (WriterException e) {
                    e.printStackTrace();
                    gen.setEnabled(true);
                }
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent aa = new Intent(getBaseContext(), Scanner.class);
                startActivity(aa);
            }
        });
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent aa = new Intent(getBaseContext(), pin.class);
                startActivity(aa);
            }
        });

    }
    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}
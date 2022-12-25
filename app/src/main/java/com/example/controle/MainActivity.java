package com.example.controle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1000;
    Button uploadButton;
    Button predictButton;
    TextView textView;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadButton=findViewById(R.id.button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, create an Intent to open the device's image gallery
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

// Next, start the activity for result, passing the intent and a request code
                startActivityForResult(pickImageIntent, REQUEST_CODE_PICK_IMAGE);
            }
        });
        textView=findViewById(R.id.textView3);
        predictButton=findViewById(R.id.predict);
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PredictClass predictClass= null;
                try {
                    predictClass = new PredictClass(getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap newimage=Bitmap.createScaledBitmap(image, 28, 28, false);
//                ByteBuffer byteBuffer = ByteBuffer.allocate(newimage.getByteCount());
//                newimage.copyPixelsToBuffer(byteBuffer);
//                byteBuffer.rewind();
                textView.setText(predictClass.classify(image));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            // The user selected an image. Get the Uri of the selected image
            Uri imageUri = data.getData();

            // You can use the Uri to get the path of the image file on the device
            String picturePath = getPicturePath(imageUri);

            // Now you can use the picturePath to display the image or upload it to a server
            ImageView imageView = findViewById(R.id.image_view);
            image=BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(image);
        }
    }

    private String getPicturePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            return picturePath;
        }
        return null;
    }
}
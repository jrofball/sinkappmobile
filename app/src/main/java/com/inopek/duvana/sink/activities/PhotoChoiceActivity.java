package com.inopek.duvana.sink.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.utils.PathUtils;

import static com.inopek.duvana.sink.constants.SinkConstants.INTENT_EXTRA_FULL_SIZE_FILE_NAME;
import static com.inopek.duvana.sink.constants.SinkConstants.PHOTO_REQUEST_CODE;

public class PhotoChoiceActivity extends AppCompatActivity implements OnClickListener {

    private static final int CAMERA_REQUEST_CODE = 0;
    private static final int SELECT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Dialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_choice);

        Button cameraButton = (Button) findViewById(R.id.cameraButton);
        Button galleryButton = (Button) findViewById(R.id.galeryButton);

        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cameraButton:
                // Launch camera
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                break;

            case R.id.galeryButton:
                // Open gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Intent intent = new Intent();
            String fullSizePath = null;
            if (requestCode == CAMERA_REQUEST_CODE && data.getExtras() != null) {

                // get full size image path
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.DATE_ADDED,
                                MediaStore.Images.ImageColumns.ORIENTATION
                        },
                        MediaStore.Images.Media.DATE_ADDED,
                        null,
                        "date_added DESC");

                if (cursor != null && cursor.moveToFirst()) {
                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    fullSizePath = uri.toString();
                    cursor.close();
                }
            } else if (requestCode == SELECT_REQUEST_CODE) {
                fullSizePath = PathUtils.generatePath(data.getData(), getApplicationContext());
            }
            intent.putExtra(INTENT_EXTRA_FULL_SIZE_FILE_NAME, fullSizePath);
            setResult(PHOTO_REQUEST_CODE, intent);
            finish();
        }
    }
}
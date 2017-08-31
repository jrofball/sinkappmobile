package com.inopek.duvana.sink.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.inopek.duvana.sink.R;
import com.inopek.duvana.sink.utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.inopek.duvana.sink.constants.SinkConstants.INTENT_EXTRA_FULL_SIZE_FILE_NAME;
import static com.inopek.duvana.sink.constants.SinkConstants.PHOTO_REQUEST_CODE;

public class PhotoChoiceActivity extends AppCompatActivity implements OnClickListener {

    private static final int CAMERA_REQUEST_CODE = 0;
    private static final int SELECT_REQUEST_CODE = 1;
    private String mCurrentPhotoPath;

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
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = createImageFile();
                List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.inopek.duvana.sink.provider",
                            photoFile);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        this.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }
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
        String fullSizePath = null;
        if(requestCode == CAMERA_REQUEST_CODE && mCurrentPhotoPath != null) {
            File imgFile = new File(mCurrentPhotoPath);
            if (imgFile.exists()) {
                fullSizePath = imgFile.getAbsolutePath();
            }
        } else if (data != null && requestCode == SELECT_REQUEST_CODE) {
          fullSizePath = PathUtils.generatePath(data.getData(), getApplicationContext());
        }
        if(fullSizePath != null) {
            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_FULL_SIZE_FILE_NAME, fullSizePath);
            setResult(PHOTO_REQUEST_CODE, intent);
        }
        finish();
    }



    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
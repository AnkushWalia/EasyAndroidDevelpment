package com.android.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.R;
import com.android.models.FaceCompare;
import com.yalantis.ucrop.imagepicker.model.Image;
import com.yalantis.ucrop.util.ImageUtils;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class MainActivity extends BaseActivity {

    private ImageView profile;
    private ImageView profile2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        checkSelfPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionCallback() {
//            @Override
//            public void permGranted() {
//                LocationUtil.with(MainActivity.this, new LocationUtil.LocationUpdateListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        Toast.makeText(MainActivity.this, "location : " + location.getLatitude(), Toast.LENGTH_SHORT).show();
//                    }
//                }).doContinuousLocation(true);
//            }
//
//            @Override
//            public void permDenied() {
//
//            }
//        });
        profile = findViewById(R.id.profile);
        profile2 = findViewById(R.id.profile2);
        log("--------------------- MainActivity onCreate");
        //loadProfileWithRxJava();
    }


    public void recogniseTwoFace(final File file1, final File file2) {
        startProgressDialog();
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file1);
        RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"), file2);

        MultipartBody.Part image_file1 = MultipartBody.Part.createFormData("image_file1", file1.getAbsolutePath(), requestFile);
        MultipartBody.Part image_file2 = MultipartBody.Part.createFormData("image_file2", file2.getAbsolutePath(), requestFile2);

        // add another part within the multipart request

        RequestBody api_key =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "R8xjxt1sYwmmQub0XxEOv6HDt2EcydJh");

        RequestBody api_secret =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "yF34x3Qc0v2v5fHnTzB3NRJGk0omAG5T");


        retrofitClient.compareTwoFace(api_key, api_secret, image_file1, image_file2).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FaceCompare>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull FaceCompare soAnswersResponse) {
                        stopProgressDialog();
                        log(soAnswersResponse + "");
                        if (soAnswersResponse.getConfidence() == null)
                            showSnackBar("Photo not recognised please try another one");
                        else if (soAnswersResponse.getConfidence() >= 80)
                            showSnackBar("Face Matched");
                        else
                            showSnackBar("Not Matched");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        handleError(e, new RetryClickListener() {
                            @Override
                            public void onActionClicked() {
                                recogniseTwoFace(file1, file2);
                            }
                        });
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("--------------------- MainActivity Start");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        log("--------------------- MainActivity onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("--------------------- MainActivity onResume");
    }

    public void clickEvent(View view) {

        checkSelfPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionCallback() {

            @Override
            public void permGranted() {


                ImageUtils.with(MainActivity.this, getString(R.string.app_name), new ImageUtils.ImageSelectCallback() {
                    @Override
                    public void onImageSelected(ArrayList<Image> imageData) {
                        if (imageData.size() == 2) {
                            profile.setImageBitmap(imageData.get(0).getBitmap());
                            profile2.setImageBitmap(imageData.get(1).getBitmap());
                            recogniseTwoFace(imageData.get(0).getFile(), imageData.get(1).getFile());
                        } else
                            showSnackBar("Please select two images");

                    }
                }).onlyCamera(false)                               // by default false
                        .cropAspectRatio(600, 400)                 // by default image ratio
                        .doCrop(true)                              // by default true
                        .doImageCompress(true)                     // by default true
                        .onlyGallery(true)                        // by default false
                        .selectedImageSize(2)                      // by default select 1
                        .setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                        .setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.White))
                        .setProgressBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent))
                        .setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark))
                        .setToolbarIconColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                        .setToolbarTextColor(ContextCompat.getColor(MainActivity.this, R.color.White))
                        .show();
            }

            @Override
            public void permDenied() {

            }
        });

    }
}

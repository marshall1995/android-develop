package com.example.marshall.zbieracz_danych;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class AddPersonPhotoActivity extends AppCompatActivity {

    private final static int MY_REQUEST_ID = 1;
    private ImageView imageViewPhoto;
    final static String EXTRA_NAME = "extra_name";
    final static String EXTRA_SURNAME = "extra_surname";
    final static String EXTRA_BIRTHDAY_DATE = "extra_birthday_date";
    final static String EXTRA_PHOTO_ABSOLUTE_PATH = "extra_photo_absolute_path";
    final static String SAVE_PHOTO_ABSOLUTE_PATH = "save_photo_absolute_path";
    private final static String SAVE_CURRENT_LANGUAGE = "saved_current_language";

    private String photoAbsolutePath = "";
    private String oldPhotoAbsolutePath = "";
    private final static String INFO_WHAT_TO_DO = "info_what_to_do";
    private final static String EDIT_ID = "edit_id";
    private final static String EDIT_PHOTO_PATH = "edit_photo_path";
    Intent intentEditAddPersonDetails;

    private String language;
    private Button buttonNext;
    private Button buttonExit;
    private Button buttonTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person_photo);
        imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
        intentEditAddPersonDetails = getIntent();

        if(intentEditAddPersonDetails.getStringExtra(INFO_WHAT_TO_DO).equals("EDIT")){
            photoAbsolutePath = intentEditAddPersonDetails.getStringExtra(EDIT_PHOTO_PATH);
            oldPhotoAbsolutePath = photoAbsolutePath;
            showPicture();
        }
        language = Locale.getDefault().getDisplayLanguage();
        setButtons();
    }


    public void takePhoto(View view){
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photo = null;
        Calendar calendar = Calendar.getInstance();
        String photoName = "photo_" + "_" + calendar.get(Calendar.YEAR) + "_"
                + (calendar.get(Calendar.MONTH)+1) + "_"  + calendar.get(Calendar.DAY_OF_MONTH) + "_"
                + calendar.get(Calendar.HOUR_OF_DAY) + "_"  + calendar.get(Calendar.MINUTE) + "_"
                + calendar.get(Calendar.SECOND);
        String fileExtension = ".jpg";

        File catalogToSavePhoto = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            photo = File.createTempFile(photoName,fileExtension,catalogToSavePhoto);
        }catch (IOException e){
            e.printStackTrace();
        }

        takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        startActivityForResult(takePhoto,MY_REQUEST_ID);
        photoAbsolutePath = photo.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==MY_REQUEST_ID && resultCode==RESULT_OK){
            Log.i("Photo","OK");
            try {
                deletePhotoEditCheck(oldPhotoAbsolutePath);
                showPicture();
            } catch (Exception e) {
                e.printStackTrace();
            }
            oldPhotoAbsolutePath = photoAbsolutePath;
        }
        if(requestCode==MY_REQUEST_ID && resultCode==RESULT_CANCELED) {
            deletePhotoEditCheck(photoAbsolutePath);
            checkIfOldPhotoPathExist();
            Log.i("Photo","canceled");
        }

        if(language.equals("polski")){
            updateLocale("pl");
        }
        if(language.equals("English")){
            updateLocale("en");
        }
        setLanguage();
    }

    private void checkIfOldPhotoPathExist(){
        if(!oldPhotoAbsolutePath.equals("")){
            photoAbsolutePath = oldPhotoAbsolutePath;
        }
    }

    private void deletePhotoFile(String photoPath){
        if(!photoPath.equals("")){
            try {
                File filePhoto = new File(photoPath);
                filePhoto.delete();
                Log.i("Photo","deleted");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deletePhotoEditCheck(String photoPath){
        if(intentEditAddPersonDetails.getStringExtra(INFO_WHAT_TO_DO).equals("EDIT")){
            if(!photoPath.equals(intentEditAddPersonDetails.getStringExtra(EDIT_PHOTO_PATH))){
                deletePhotoFile(photoPath);
                Log.i("Photo","Jest Edit");
            }
        }
        else{
            deletePhotoFile(photoPath);
        }
    }


    private void showPicture(){
        if(!photoAbsolutePath.equals("")){
            Matrix deviceMatrix = new Matrix();

            ExifInterface photoInterface = null;
            try {
                photoInterface = new ExifInterface(photoAbsolutePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int photoOrientation = photoInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,5);

            if(photoOrientation==3){
                deviceMatrix.postRotate(180);
            }
            else if(photoOrientation!=1){
                deviceMatrix.postRotate(90);
            }

            int smallerScreenLength = whatIsSmallerHeightOrWidth();


            Bitmap bitmapPicture = BitmapFactory.decodeFile(photoAbsolutePath);
            deviceMatrix.postScale((float)smallerScreenLength/bitmapPicture.getWidth(),(float)smallerScreenLength/bitmapPicture.getWidth());
            Bitmap processedBitmap = Bitmap.createBitmap(bitmapPicture , 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), deviceMatrix, true);
            imageViewPhoto.setImageBitmap(processedBitmap);
        }
    }


    public void toSummery(View view){
        if(imageViewPhoto.getDrawable()!=null){
            Intent intentSummaryActivity = new Intent(this,SummaryActivity.class);
            intentSummaryActivity.putExtra(INFO_WHAT_TO_DO,intentEditAddPersonDetails.getStringExtra(INFO_WHAT_TO_DO));
            intentSummaryActivity.putExtra(EXTRA_NAME,intentEditAddPersonDetails.getStringExtra(EXTRA_NAME));
            intentSummaryActivity.putExtra(EXTRA_SURNAME,intentEditAddPersonDetails.getStringExtra(EXTRA_SURNAME));
            intentSummaryActivity.putExtra(EXTRA_BIRTHDAY_DATE,intentEditAddPersonDetails.getStringExtra(EXTRA_BIRTHDAY_DATE));
            intentSummaryActivity.putExtra(EXTRA_PHOTO_ABSOLUTE_PATH, photoAbsolutePath);
            if(intentEditAddPersonDetails.getStringExtra(INFO_WHAT_TO_DO).equals("EDIT")){
                intentSummaryActivity.putExtra(EDIT_ID,intentEditAddPersonDetails.getIntExtra(EDIT_ID,0));
                intentSummaryActivity.putExtra(EDIT_PHOTO_PATH,intentEditAddPersonDetails.getStringExtra(EDIT_PHOTO_PATH));
            }
            startActivity(intentSummaryActivity);
            finish();
        }
    }


    public void clickExit(View view){
        deletePhotoEditCheck(photoAbsolutePath);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVE_PHOTO_ABSOLUTE_PATH, photoAbsolutePath);
        outState.putString(SAVE_CURRENT_LANGUAGE,language);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        photoAbsolutePath = savedInstanceState.getString(SAVE_PHOTO_ABSOLUTE_PATH);
        try {
            showPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        language = savedInstanceState.getString(SAVE_CURRENT_LANGUAGE);
        if(language.equals("polski")){
            updateLocale("pl");
        }
        setLanguage();
        super.onRestoreInstanceState(savedInstanceState);
    }

    private int whatIsSmallerHeightOrWidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if(displayMetrics.heightPixels>displayMetrics.widthPixels){
            return  displayMetrics.widthPixels;
        }
        return displayMetrics.heightPixels;
    }

    private void setLanguage(){
        buttonNext.setText(getString(R.string.button_next));
        buttonExit.setText(getString(R.string.button_exit));
        buttonTakePhoto.setText(getString(R.string.button_take_photo));
    }

    private void setButtons(){
        buttonNext = (Button) findViewById(R.id.buttonNextSummary);
        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonTakePhoto = (Button) findViewById(R.id.buttonTakePhoto);
    }

    private void updateLocale(String country)
    {
        Locale locale=new Locale(country);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}

package com.example.mohamedshiyas.parsenoteapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mohamedshiyas on 08/08/17.
 */
public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_NOTE = "NOTE";
    TextView noteTitle, noteDescription;
    ImageView image;
    FloatingActionButton fab;
    Notes notes;
    Bitmap bm, thumbnail;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    ParseFile pFile;

    public static Intent newInstance(Context context, Notes note) {
        Intent intent = new Intent(context, NoteActivity.class);
        if(note != null) {
            intent.putExtra(EXTRA_NOTE, note);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initViews();
        fab.setOnClickListener(this);

        notes = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (notes != null) {
            getSupportActionBar().setTitle("View Notes");
            noteTitle.setText(notes.Title());
            noteDescription.setText(notes.Description());
//            image.setImageResource(Integer.parseInt(notes.Image()));
        }

    }

    private void initViews() {
        noteTitle = (TextView) findViewById(R.id.note_title);
        noteDescription = (TextView) findViewById(R.id.note_description);
        image = (ImageView) findViewById(R.id.imageView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setTitle("Add Notes");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Notes newnotes = new Notes();
                newnotes.setTitle(noteTitle.getText().toString());
                newnotes.setDescription(noteDescription.getText().toString());
//                if (bm != null) {
//                    newnotes.setImage(encodeTobase64(bm));
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    pFile = new ParseFile("DocImage.jpg", stream.toByteArray());
//                    pFile.saveInBackground();
//                }
                if (notes == null) {
                    if (isNetworkAvailable()) {
                        newnotes.saveInBackground();
                    }
                    else {
                        newnotes.pinInBackground();
                    }
                }
                else {
                    ParseQuery<Notes> query = ParseQuery.getQuery("Notes");
                    if (!isNetworkAvailable()) {
                        query.fromLocalDatastore();
                        Log.e("Got Here", "");
                    }
                    query.getInBackground(notes.getObjectId(), new GetCallback<Notes>() {
                        @Override
                        public void done(Notes object, ParseException e) {
                            if (e == null) {
                                object.put("Title", noteTitle.getText().toString());
                                object.put("Description", noteDescription.getText().toString());
//                                object.put("Image", pFile);
                                object.saveInBackground();
                            } else {
                                Log.e("Error occured", e.toString());
                            }
                        }
                    });
                }
                onBackPressed();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Remove Photo",
                "Cancel" };
        final CharSequence[] items1 = { "Take Photo", "Choose from Library",
                "Cancel" };
        CharSequence[] t;

        AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(NoteActivity.this);

                Log.e("result", String.valueOf(result));
                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                }else if (items[item].equals("Remove Photo")){
                    userChoosenTask ="Remove Photo";
                    if (result)
                        bm = null;
//                        image.setImageResource(R.drawable.user);
                }else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        image.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        image.setImageBitmap(bm);
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                selectImage();
                Toast.makeText(NoteActivity.this, "Image Upload", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

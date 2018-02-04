package com.rishabhrawat.fightforhunger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class donerActivity extends AppCompatActivity {

    private int REQUEST_CAMERA = 10, SELECT_FILE = 1;
    private String userChoosenTask;
    private AppCompatSpinner spinner;
    private AppCompatMultiAutoCompleteTextView autoCompleteTextView;
    Bitmap thumbnail;
    Uri FilePathUri;
    String photourl;
    ImageView addimage;
    TextView next;

    String name;
    String email;
    Uri photoUri;
    String uid;
    String phoneno;

    EditText desc;
    TextView confirm;

    EditText entername, mobileno;


    // Folder path for Firebase Storage.
    String Storage_Path = "All_Image_Uploads/";
    ProgressDialog progressDialog;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doner);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        /*link-----*/
        addimage = (ImageView) findViewById(R.id.add_image);
        next = (TextView) findViewById(R.id.btnnext);
        entername = (EditText) findViewById(R.id.entername);
        mobileno = (EditText) findViewById(R.id.mobile);
        desc=(EditText) findViewById(R.id.desc);
        confirm=(TextView)findViewById(R.id.confirm_image);
        progressDialog = new ProgressDialog(donerActivity.this);




          /*--------------------getting user data from firebase auth-------------------*/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            email = user.getEmail();
            photoUri = user.getPhotoUrl();
            uid = user.getUid();
            phoneno = user.getPhoneNumber();
        }
        entername.setText(name);
        mobileno.setText(phoneno);

/*-----------add image on click listener-------------------------------------------------------------*/
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        /*-------------------------confirm on click-------------------------------*/
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageReference = FirebaseStorage.getInstance().getReference();
                if (thumbnail != null && mobileno!=null && desc!=null) {
                    UploadImageFileToFirebaseStorage();
                } else {
                    Toast.makeText(donerActivity.this, "first take a profile photo and compleate all details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*----------------next btn text view onclick listner------------------------------------------------*/
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*update firebase*/

                reference.child("data").child("donor").child(uid).child("description").setValue(desc.getText().toString());
                reference.child("data").child("donor").child(uid).child("image").setValue(photourl);
                reference.child("data").child("donor").child(uid).child("location").setValue("empty");
                reference.child("data").child("donor").child(uid).child("distributer").setValue("empty");
                reference.child("data").child("donor").child(uid).child("mobile").setValue(mobileno.getText().toString());


                /*next activity---*/

                Intent intent =new Intent(donerActivity.this,donerlocActivity.class);
                startActivity(intent);
            }

        });


    }


    /*********************select image method*************************************************************/
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(donerActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                            cameraIntent();
                        } else {
                            String[] permitionRequested = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permitionRequested, REQUEST_CAMERA);
                        }
                    }
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            galleryIntent();
                        } else {
                            String[] permitionRequested = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permitionRequested, SELECT_FILE);
                        }
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: //Request camera code
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo")) {
                        cameraIntent();
                    }

                }
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Choose from Library")) {
                        galleryIntent();
                    }
                    break;
                }
        }

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
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
        FilePathUri = getImageUri(getApplicationContext(), thumbnail);
        addimage.setImageBitmap(thumbnail);
    }

    /*////////////////////////////////geting uri from bitmap///////////////////////////////////////*/
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        thumbnail = null;
        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FilePathUri = data.getData();
        addimage.setImageBitmap(thumbnail);


    }

    /***************************************upload image to firebase storage*******************************************/

    public void UploadImageFileToFirebaseStorage() {
        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
          progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + ".jpg");

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressLint("VisibleForTests")
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            photourl = taskSnapshot.getDownloadUrl().toString();


                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully url=" + photourl, Toast.LENGTH_LONG).show();
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(donerActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });
        } else {

            Toast.makeText(donerActivity.this, "Please Select Image", Toast.LENGTH_LONG).show();

        }
    }

}

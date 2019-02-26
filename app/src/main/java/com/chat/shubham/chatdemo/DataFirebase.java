package com.chat.shubham.chatdemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class DataFirebase extends AppCompatActivity
{
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Button upld,download;
    ImageView imageView;
    StorageReference storageReference;
    StorageReference imageRef;
    private static int Result_load_img = 1;
    String imgDecodeableString,selectedpicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_firebase);
        upld = (Button)findViewById(R.id.upload);
        imageView = (ImageView)findViewById(R.id.imageView);
        download = (Button)findViewById(R.id.downloadBtn);

        storageReference = storage.getReferenceFromUrl("gs://chatdemo-b47c9.appspot.com");
        upld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,Result_load_img);



                storageReference = storage.getReferenceFromUrl("gs://chatdemo-b47c9.appspot.com");
                imageRef = storageReference.child("images");


            }
        });




        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                StorageReference reference = storageReference.child("images").child("IMG-20160701-WA0003.jpg");

                    File folder = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "TollCulator");
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                        Toast.makeText(getApplicationContext(),"was already exists",Toast.LENGTH_SHORT).show();
                    }
                    if (success) {
                        reference.getFile(folder).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                            {
                                Toast.makeText(getApplicationContext(),"data dowloaded success",Toast.LENGTH_SHORT).show();
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast.makeText(getApplicationContext(),"not downlaoded "+e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(),"Folder cannt be created",Toast.LENGTH_SHORT).show();
                    }




            }
        });





    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent  data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Result_load_img && resultCode == RESULT_OK && null != data)
        {
            Uri selectedImg = data.getData();
            String[] filepathcolmn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImg,filepathcolmn,null,null,null);
            cursor.moveToNext();
            int colmnIndex = cursor.getColumnIndex(filepathcolmn[0]);
            imgDecodeableString = cursor.getString(colmnIndex);
            String[] pathArr = imgDecodeableString.split("/");
            selectedpicPath = pathArr[pathArr.length-1];
            Toast.makeText(getApplicationContext(),selectedpicPath,Toast.LENGTH_SHORT).show();
            cursor.close();

            //Bitmap bitmap = null;
            try
            {
                Uri uri = Uri.fromFile(new File(imgDecodeableString));
                StorageReference toUpload = storageReference.child("images/"+uri.getLastPathSegment());
                UploadTask uploadTask = toUpload.putFile(uri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        Uri download = taskSnapshot.getDownloadUrl();
                        imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodeableString));
                        Toast.makeText(getApplicationContext(),"file uploaded",Toast.LENGTH_SHORT).show();
                    }
                });



                //Toast.makeText(getApplicationContext(),"selected selected "+imgDecodeableString,Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"image not selected",Toast.LENGTH_SHORT).show();
        }
    }


}

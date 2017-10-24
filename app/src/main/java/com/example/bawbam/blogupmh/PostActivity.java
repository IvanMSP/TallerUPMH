package com.example.bawbam.blogupmh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private ImageButton mSelectImage;
    private EditText mTitle;
    private EditText mPostDesc;

    private Button mSubmitBtn;

    private Uri mImageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private ProgressDialog mProgress;

    //Variables de Firebase
    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        //Instanciamos el storage y database de Firebase
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");

        //Inicializamos el progress Dialog
        mProgress = new ProgressDialog(this);


        //Inflamos nuestros elementos
        mSelectImage = (ImageButton) findViewById(R.id.imagenSelecciona);

        mTitle =(EditText)findViewById(R.id.titulo);
        mPostDesc = (EditText)findViewById(R.id.descripcion);
        mSubmitBtn = (Button)findViewById(R.id.submitBtn);

        //Evento Onclick para seleccionar una imagen de nuestra galeria
        mSelectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");//aceptamos cualquier tipo de Imagen
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });


        //Evento Onclick para subir nuevo Post
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciopost();
            }
        });
    }

    private void iniciopost() {
        //Mostramos el inicio del progress
        mProgress.setMessage("Uploading Post..");
        mProgress.show();

        //declaramos variables para los campos para utilizar .trim (quitar espacios de los strings)
        final String title_val = mTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();

        //Verfificamos si nuestros campos
        if(!TextUtils.isEmpty(title_val)&& !TextUtils.isEmpty(desc_val)&& mImageUri!=null){
            //Instanciamos la rama del Storage de Firebase
            StorageReference filepath = mStorage.child("Blog_images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = mDatabase.push();
                    newPost.child("titulo").setValue(title_val);
                    newPost.child("descripcion").setValue(desc_val);
                    newPost.child("image").setValue(downloadUri.toString());

                    Toast toast2 =
                            Toast.makeText(getApplicationContext(),
                                    "Post Listo", Toast.LENGTH_SHORT);
                    /*toast2.setGravity(Gravity.CENTER| Gravity.LEFT,0,0);*/

                    toast2.show();
                    mProgress.dismiss();

                    startActivity(new Intent(PostActivity.this, MainActivity.class));

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    mProgress.setMessage("Uploading " + ((int) progress) + "%...");
                    mProgress.show();

                }
            });
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            mImageUri = data.getData();//Obtener imagen
            mSelectImage.setImageURI(mImageUri); //Obtenemos la URI de nuestra imagen
        }
    }
}

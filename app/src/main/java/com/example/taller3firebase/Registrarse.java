package com.example.taller3firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taller3firebase.model.DatabasePaths;
import com.example.taller3firebase.model.User;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registrarse extends AppCompatActivity {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final String TAG = Registrarse.class.getName();
    private Logger logger = Logger.getLogger(TAG);
    EditText nameEdit, lastnameEdit, emailEdit, passEdit, numIDEdit;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    Button buttonRegis, subir, tomar;
    StorageReference mStorage;
    String storage_path;
    Uri file;
    ImageView perfil;
    String currentPhotoPath;
    private final int CAMERA_PERMISSION_ID = 101;
    private final int GALLERY_PERMISSION_ID = 102;
    String cameraPerm = Manifest.permission.CAMERA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        buttonRegis = findViewById(R.id.button9);
        subir = findViewById(R.id.button8);
        tomar = findViewById(R.id.button7);
        mStorage = FirebaseStorage.getInstance().getReference();
        perfil = findViewById(R.id.imageView);

        nameEdit = findViewById(R.id.enombre);
        lastnameEdit = findViewById(R.id.eapellido);
        emailEdit = findViewById(R.id.eemail);
        passEdit = findViewById(R.id.epassw);
        numIDEdit = findViewById(R.id.eident);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        logger.info("Se va a solicitar el permiso");
        requestPermission(Registrarse.this, cameraPerm, "Permiso para utiliza la camara", CAMERA_PERMISSION_ID);
        initView();

        buttonRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdit.getText().toString();
                String pass = passEdit.getText().toString();

                if(validateForm()){
                    if(!isEmailValid(email)){
                        Toast.makeText(Registrarse.this, "Email is not a valid format",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }createAccount(email, pass);

                    storage_path = "fotos/" + numIDEdit.getText().toString();
                    StorageReference imageRef = mStorage.child(storage_path);
                    imageRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    Log.i("FBApp", "Succesfully upload image");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            });
                    Intent intent = new Intent(Registrarse.this, Opciones.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void requestPermission(Activity context, String permission, String justification, int id) {
        // Se verifica si no hay permisos
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            // ¿Deberiamos mostrar una explicación?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, cameraPerm)) {
                Toast.makeText(context, justification, Toast.LENGTH_SHORT).show();
            }
            // Solicitar el permiso
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
    }

    private void initView() {
        if (ContextCompat.checkSelfPermission(this, cameraPerm) != PackageManager.PERMISSION_GRANTED) {
            logger.warning("Failed to getting the permission :(");
        } else {
            logger.info("Success getting the permission :)");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_ID) {
            initView();
        }
    }

    public void startCamera(View view) {
        if (ContextCompat.checkSelfPermission(this, cameraPerm) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            logger.warning("Failed to getting the permission :(");
        }
    }

    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Asegurarse de que hay una actividad de camara para manejar el intent
        if (takePictureIntent != null) {
            //Crear el archivo donde debería ir la foto
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                logger.warning(ex.getMessage());
            }
            //Continua solo el archivo ha sido exitosamente creado
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "TallerFirebase.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_PERMISSION_ID);
            }
        }
    }

    private File createImageFile() throws IOException {
        //Crear un nombre dde archivo de imagen
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("IMG",".jpg", storageDir);

        // Guardar un archivo: Ruta para usar con ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        logger.info("Ruta: "+currentPhotoPath);
        return image;
    }

    public void startGallery(View view){
        Intent pickGalleryImage = new Intent(Intent.ACTION_PICK);
        pickGalleryImage.setType("image/*");
        startActivityForResult(pickGalleryImage, GALLERY_PERMISSION_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int rresultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, rresultCode, data);
        if (rresultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case CAMERA_PERMISSION_ID:
                    perfil.setImageURI(Uri.parse(currentPhotoPath));
                    logger.info("Image capture successfully.");
                    break;
                case GALLERY_PERMISSION_ID:
                    Uri imageUri = data.getData();
                    perfil.setImageURI(imageUri);
                    file = data.getData();
                    logger.info("Image loaded successfully");
                    break;
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser){
        if(currentUser!=null){
            Intent intent = new Intent(getBaseContext(), Opciones.class);
            //intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            emailEdit.setText("");
            lastnameEdit.setText("");//hola
            emailEdit.setText("");
            passEdit.setText("");
            numIDEdit.setText("");
        }
    }
    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user!=null){
                                // uploadImageToFirebase(user.getUid());
                                User p = new User();
                                p.setName(nameEdit.getText().toString());
                                p.setLastName(lastnameEdit.getText().toString());
                                p.setNumID(numIDEdit.getText().toString());
                                p.setAvailable(false);

                                myRef=FirebaseDatabase.getInstance().getReference(DatabasePaths.USER + user.getUid());
                                myRef.setValue(p);
                                updateUI(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Registrarse.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }
    public static boolean isEmailValid(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    private boolean validateForm() {
        boolean valid = true;
        String nameC = nameEdit.getText().toString();
        if (TextUtils.isEmpty(nameC)) {
            nameEdit.setError("Required");
            valid = false;
        } else {
            nameEdit.setError(null);
        }
        String lastn = lastnameEdit.getText().toString();
        if (TextUtils.isEmpty(lastn)) {
            lastnameEdit.setError("Required");
            valid = false;
        } else {
            lastnameEdit.setError(null);
        }
        String password = passEdit.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passEdit.setError("Required");
            valid = false;
        } else {
            passEdit.setError(null);
        }
        String email = emailEdit.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Required");
            valid = false;
        } else {
            emailEdit.setError(null);
        }
        String noid = numIDEdit.getText().toString();
        if (TextUtils.isEmpty(noid)) {
            numIDEdit.setError("Required");
            valid = false;
        } else {
            numIDEdit.setError(null);
        }

        return valid;
    }

}
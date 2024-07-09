package com.example.archat.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;


import com.example.archat.R;
import com.example.archat.databinding.ActivitySignUpBinding;
import com.example.archat.databinding.ActivitySigninBinding;
import com.example.archat.utilites.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Pattern;



@RequiresApi(api = Build.VERSION_CODES.O)
public class SignUpActivity extends AppCompatActivity {



    //PREFERENCE MANAGER
    public class PreferenceManager {
        private SharedPreferences sharedPreferences;

        public PreferenceManager (Context context){
            sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,context.MODE_PRIVATE);

        }



        public void putBoolean(String key , Boolean value) {
            SharedPreferences.Editor editor  = sharedPreferences.edit();
            editor.putBoolean(key,value);
            editor.apply();
        }
        public Boolean getBoolean(String key){
            return sharedPreferences.getBoolean(key,false);

        }

        public void putString(String key , String value) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
        public String getString(String key){
            return sharedPreferences.getString(key,null);

        }
        public void clear(){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

    }
    //PREFERENCE MANAGER


    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();

    }

    private void setListeners() {
        binding.alreadyhaveacc.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v -> {
            if(isValidSignUpDetails()){
                SignUp();
            }
        });
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }


    private void SignUp(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.cName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.cEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.cPassword.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        database.collection(Constants.KEY_COLLECTION_Users)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME,binding.cName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());

                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encodedImage(Bitmap bitmap){
        int previewWidth =150;
        int previewheight = bitmap.getHeight() * previewWidth/ bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewheight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);


    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.inputprifilepic.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodedImage(bitmap);

                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

   private boolean isValidSignUpDetails() {
        if (encodedImage == null){
            showToast("select profile pic");
            return false;
        } else if (binding.cName.getText().toString().trim().isEmpty()) {
            showToast("enter name");
            return false;
        } else if (binding.cEmail.getText().toString().trim().isEmpty()) {
            showToast("enter email");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.cEmail.getText().toString()).matches()) {
                showToast("enter valid email");
                return false;
        } else if (binding.cPassword.getText().toString().isEmpty()) {
            showToast("enter password");
            return false;
        }  else if (binding.concPassword.getText().toString().isEmpty()) {
            showToast("Confirm your password");
            return false;
        } else if(!binding.cPassword.getText().toString().equals(binding.concPassword.getText().toString())) {
            showToast("passwords doesn't match");
            return false;
        }
        else {
            return true;
        }

   }

   private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {

            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);

        }
   }
}
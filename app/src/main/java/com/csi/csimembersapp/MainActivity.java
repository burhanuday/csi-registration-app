package com.csi.csimembersapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.securepreferences.SecurePreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText et_email, et_password;
    Button bt_login;
    FirebaseFirestore db;
    List<String> admins = new ArrayList<>();
    SharedPreferences sharedPreferences;
    LinearLayout login;
    ProgressDialog progDailog;
    boolean dial = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_login);
        login = findViewById(R.id.ll_login);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        sharedPreferences = new SecurePreferences(MainActivity.this);
        db.collection("admin_access")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (dial){
                                progDailog.dismiss();
                            }
                            login.setVisibility(View.VISIBLE);
                            for (QueryDocumentSnapshot document : task.getResult()){
                                admins.add(document.getString("email"));
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Internet connection is needed to login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(et_email) || isEmpty(et_password)){
                    Toast.makeText(MainActivity.this, "Enter valid details", Toast.LENGTH_SHORT).show();
                }else {
                    loginFirebase(et_email.getText().toString(), et_password.getText().toString());
                }
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser==null){
            Toast.makeText(this, "Login to continue", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isEmpty(EditText editText){
        if (editText.getText().toString().trim().length() > 0){
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser!=null){
            updateUI(currentUser);
        }else {
            dial = true;
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
        }
    }

    public void updateUI(FirebaseUser firebaseUser){
        Toast.makeText(this, "Welcome back " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, Home.class));
        finish();
    }


   public void loginFirebase(String email, String password){
       Toast.makeText(this, "Wait while you are being logged in...", Toast.LENGTH_SHORT).show();
       FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                           // Sign in success, update UI with the signed-in user's information
                           FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                           sharedPreferences.edit().putBoolean("admin_access", false).apply();
                           for (String string : admins){
                               if (string.matches(user.getEmail())){
                                   sharedPreferences.edit().putBoolean("admin_access", true).apply();
                                   Toast.makeText(MainActivity.this, "Admin access granted", Toast.LENGTH_SHORT).show();
                               }
                           }
                           updateUI(user);
                       } else {
                           // If sign in fails, display a message to the user.
                           Toast.makeText(MainActivity.this, "Authentication failed.",
                                   Toast.LENGTH_SHORT).show();
                       }
                   }
               });
   }

    public void signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    public void getUserData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
             String uid = user.getUid();
        }
    }
}

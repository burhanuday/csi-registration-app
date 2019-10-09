package com.csi.csimembersapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.securepreferences.SecurePreferences;

public class Home extends AppCompatActivity {
    CardView register, scan, viewRegistrations, logout;
    SharedPreferences sharedPreferences;
    boolean admin = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = new SecurePreferences(Home.this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        admin = sharedPreferences.getBoolean("admin_access",false);
        if (currentUser==null){
            //tell user to login
            Toast.makeText(this, "Login to continue", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Home.this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        register = findViewById(R.id.card_register);
        scan = findViewById(R.id.card_scan);
        viewRegistrations = findViewById(R.id.card_view);
        logout = findViewById(R.id.card_logout);

        viewRegistrations.setVisibility(View.GONE);
        if (admin){
            viewRegistrations.setVisibility(View.VISIBLE);
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Register.class));
            }
        });

        viewRegistrations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, ViewRegistered.class));
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(Home.this, new String[] {Manifest.permission.CAMERA}, 100);
                }else {
                    startActivity(new Intent(Home.this, Scanner.class));
                }
            }
        });

        //updateMarkChecked();
    }

    public void signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        finish();
                    }
                });

    }

    private void updateMarkChecked(){

        db.collection("registration_handle")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()){
                                RegData regData = document.toObject(RegData.class);
                                regData.setId(document.getId());
                                pushMark(regData);
                            }
                        } else {
                            Toast.makeText(Home.this, "Internet connection is needed to login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void pushMark(RegData regData){
        DocumentReference docRef = db.collection("registration_handle").document(regData.getId());
        docRef.update("mark_used", false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }
}

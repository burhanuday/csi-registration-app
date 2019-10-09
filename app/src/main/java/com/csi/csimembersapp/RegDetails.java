package com.csi.csimembersapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.securepreferences.SecurePreferences;

public class RegDetails extends AppCompatActivity {

    FirebaseFirestore db;
    String id;
    public static String TAG = "firebaseDoc";
    RegData details;
    TextView name, email, college, event, apid, apending, registeredby, registeredat, phone;
    DocumentReference docRef;
    float event_price;
    SharedPreferences sharedPreferences;
    private boolean admin = false;
    Button delete, used, edit;
    int pendingAmount = 1;
    RegData regData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_details);

        sharedPreferences = new SecurePreferences(RegDetails.this);
        admin = sharedPreferences.getBoolean("admin_access", false);

        init();
        db = FirebaseFirestore.getInstance();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            id = extras.getString("id");
        }

        Log.i(TAG, id);
        //get the document
        details = getDocument(id);

        if (admin){
            delete.setVisibility(View.VISIBLE);
        }
    }

    public void markCheck(){
        used.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (regData.getAmount_pending()>0){
                    Toast.makeText(RegDetails.this, "Pay the full amount to enter!", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog alertDialog = new AlertDialog.Builder(RegDetails.this)
                        .setTitle("MARK AS USED?")
                        .setMessage("This can only be done once!")
                        .setPositiveButton("MARK USED", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                docRef.update("mark_used", true)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                used.setVisibility(View.GONE);
                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                Toast.makeText(RegDetails.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                                Toast.makeText(RegDetails.this, "There was an error while performing this action", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("CANCEL", null).create();
                alertDialog.show();
            }
        });
    }

    public void init(){
        name = findViewById(R.id.tv_name);
        email = findViewById(R.id.tv_email);
        college = findViewById(R.id.tv_college);
        event = findViewById(R.id.tv_event);
        apid = findViewById(R.id.tv_apaid);
        apending = findViewById(R.id.tv_apending);
        registeredby = findViewById(R.id.tv_regby);
        registeredat = findViewById(R.id.tv_regat);
        delete = findViewById(R.id.bt_delete);
        phone = findViewById(R.id.tv_phone);
        used = findViewById(R.id.bt_mark_used);
        edit = findViewById(R.id.bt_edit);
    }

    public void settingText(RegData details){
        if (!(details == null)) {
            name.setText("Name: " + details.getName());
            event.setText("Event: " + details.getEvent_name());
            college.setText("College: " + details.getCollege());
            email.setText("Email: " + details.getEmail());
            //apid.setText(details.getAmount_paid());
            apid.setText("Amount Paid: " + Float.toString(details.getAmount_paid()));
            //apending.setText(details.getAmount_pending());
            apending.setText("Amount Pending: " + Float.toString(details.getAmount_pending()));
            pendingAmount = (int) details.getAmount_pending();
            registeredby.setText("Registered by: " + details.getRegisteredby());
            registeredat.setText("Registration time: " + details.getDate_registered());
            event_price = (float)details.getEvent_price();
            phone.setText("Phone: " + details.getPhone());
            if (details.getUsed()){
                used.setVisibility(View.GONE);
                Toast.makeText(this, "Already used", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Not used", Toast.LENGTH_SHORT).show();
            }
            if (details.getAmount_pending()==0){
                edit.setVisibility(View.GONE);
            }
        }else {
            Log.i(TAG, "details is null");
        }
    }

    public RegData getDocument(String id){
        // get document and return object
        docRef = db.collection("registration_handle").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "DocumentSnapshot data: " + document.getData());
                        regData = document.toObject(RegData.class);
                        regData.setUsed((boolean)document.get("mark_used"));
                        settingText(regData);
                        markCheck();
                    } else {
                        Log.i(TAG, "No such document");
                    }
                } else {
                    Log.i(TAG, "get failed with ", task.getException());
                }
            }
        });
        return regData;
    }

    public void editReg(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(RegDetails.this);
        final View dialog = layoutInflater.inflate(R.layout.edit_regdata_dialog, null);

        final EditText amountPaid = dialog.findViewById(R.id.dialog_et_amt_paid);

        AlertDialog alertDialog = new AlertDialog.Builder(RegDetails.this)
                .setTitle("Edit Data")
                .setView(dialog)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateValues(Float.parseFloat(amountPaid.getText().toString()), "amount_paid");
                    }
                })
                .setNegativeButton("CANCEL", null).create();
        alertDialog.show();
    }

    public void delReg(View view) {
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

        finish();
    }

    public void updateValues(float amount_paid, String field){
        regData.setAmount_paid((int)amount_paid);
        regData.setAmount_pending((int)(event_price - amount_paid));
        if (regData.getAmount_pending() == 0){
            edit.setVisibility(View.GONE);
        }
        docRef.update(field, amount_paid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(RegDetails.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(RegDetails.this, "There was an error while performing this action", Toast.LENGTH_SHORT).show();
                    }
                });

        docRef.update("amount_pending", event_price - amount_paid)
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

        apid.setText("Amount Paid: " + Float.toString(amount_paid));
        apending.setText("Amount Pending: " + Float.toString(event_price - amount_paid));
    }
}
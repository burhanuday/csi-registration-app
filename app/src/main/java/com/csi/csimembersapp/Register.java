package com.csi.csimembersapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {
    Spinner event;
    TextView amount;
    EditText name, college, email, amount_paid, amount_pending, phone;
    int event_prices[] = {30, 250, 50, 250, 50, 80, 60 , 120, 30, 100, 200, 250, 50};
    String selected_event = null;
    Button register;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    AlertDialog alertDialog = null;

    //todo send event name, name, email, college, amount paid, amount left, date, registered by

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialise();
        listenSpinnerChanges();
        selected_event = getEvents().get(0);
    }

    public void clearData(){
        name.setText("");
        college.setText("");
        email.setText("");
        amount_paid.setText("");
        amount_pending.setText("");
        phone.setText("");
    }

    public void initialise(){
        event = findViewById(R.id.spinner_events);
        amount = findViewById(R.id.tv_event_amount);
        name = findViewById(R.id.et_name);
        college = findViewById(R.id.et_college);
        email = findViewById(R.id.et_email);
        amount_paid = findViewById(R.id.et_amount_paid);
        amount_pending = findViewById(R.id.et_amount_remaining);
        register = findViewById(R.id.btn_register);
        phone = findViewById(R.id.et_phone);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getEvents());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        event.setAdapter(arrayAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
        db.setFirestoreSettings(settings);

        if (currentUser == null){
            Toast.makeText(this, "Login to continue", Toast.LENGTH_SHORT).show();
            finish();
        }

        amount_paid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                //apply changes here
                if (amount_paid.getText().toString().trim().length()>0) {
                    int left = Integer.parseInt(amount.getText().toString()) - Integer.parseInt(amount_paid.getText().toString());
                    amount_pending.setText(Integer.toString(left));
                }
            }
        });
    }

    public void listenSpinnerChanges(){
        event.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_event = adapterView.getItemAtPosition(i).toString();
                amount.setText(Integer.toString(event_prices[i]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(Register.this, "Select an event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<String> getEvents(){
        List<String> events = new ArrayList<>();
        events.add("Bull Run");
        events.add("CS 1.6");
        events.add("Code in 'X'");
        events.add("DOTA 2");
        events.add("FIFA 18");
        events.add("Maze Runner");
        events.add("PUBG - 2 PLAYERS");
        events.add("PUBG - 4 PLAYERS");
        events.add("Pixelate");
        events.add("Quiz");
        events.add("Technical Treasure Hunt - 4 Players");
        events.add("Technical Treasure Hunt - 5 Players");
        events.add("Web Masters");
        return events;
    }

    public boolean checkBeforeSending(){
        if (isEmpty(name) || isEmpty(college) || isEmpty(amount_paid) || isEmpty(amount_pending) || isEmpty(email) || isEmpty(phone)){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selected_event == null){
            Toast.makeText(this, "Select an event from the dropdown", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (currentUser == null){
            Toast.makeText(this, "Sign out and sign in again", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isEmpty(EditText etText) {
        //returns true if empty
        //returns false if not empty
        if (etText.getText().toString().trim().length()>0){
            return false;
        }
        return true;
    }

    public String getDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return df.format(c);
    }

    public String getUsername(){
        return currentUser.getEmail();
    }

    public void sendDataToDatabase(View view) {
        if (!checkBeforeSending()){
            return;
        }
        if (!isValidEmail(email.getText().toString())){
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, Object> registration = new HashMap<>();
                registration.put("amount_paid", Integer.parseInt(amount_paid.getText().toString()));
                registration.put("amount_pending", Integer.parseInt(amount_pending.getText().toString()));
                registration.put("event_price", Integer.parseInt(amount.getText().toString()));
                registration.put("name", name.getText().toString());
                registration.put("college", college.getText().toString());
                registration.put("email", email.getText().toString());
                registration.put("event_name", selected_event);
                registration.put("date_registered", getDate());
                registration.put("registeredby", getUsername());
                registration.put("phone", phone.getText().toString());
                registration.put("mark_used", false);

                db.collection("registration_handle")
                        .add(registration)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(Register.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                clearData();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }
}

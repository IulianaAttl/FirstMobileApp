package com.example.notesapp;

import static com.example.notesapp.Welcome.MESSAGE_KEY;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//register activity
public class Register extends AppCompatActivity {
    //declare variables
    private FirebaseAuth mAuth;
    EditText nametext;
    EditText phonetext;
    String userID;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    //main method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //get the intent message key - userID
        Intent intent = getIntent();
        userID = intent.getStringExtra(MESSAGE_KEY);

        //get the continue button
        Button continuebutton = (Button) findViewById(R.id.continuebutton);

        //add listener to the continue button
        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the values inserted by the user
                nametext = findViewById(R.id.nametext);
                phonetext = findViewById(R.id.phonetext);
                String name = nametext.getText().toString();
                String phone = phonetext.getText().toString();
                mAuth = FirebaseAuth.getInstance();

                //check if all fields have been completed
                if (name.equals("")) {
                    nametext.setError("Name not completed");
                } else if (phone.equals("")) {
                    phonetext.setError("Phone number not completed");
                }
                else{
                    //if all the fields were completed add the info to the database
                    rootNode = FirebaseDatabase.getInstance();
                    reference = rootNode.getReference();
                    User user = new User(name, phone);
                    reference.child("Users").child(userID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //if the information was added successfully display message
                            Toast.makeText(Register.this, "Write was successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //display error message
                                    Toast.makeText(Register.this, "Write failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                    //start the next activity
                    Intent intent = new Intent(getApplicationContext(), Notes.class);
                    intent.putExtra(MESSAGE_KEY, userID);
                    startActivity(intent);
                }
            }
        });
    }
}
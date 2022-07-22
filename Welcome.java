package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//welcome activity
public class Welcome extends AppCompatActivity {
    //declare variables
    private FirebaseAuth mAuth;
    private EditText emailtext;
    private EditText passwordtext;
    //pass the user id to another activity
    public final static String MESSAGE_KEY = "USERID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //get the buttons
        Button login = (Button) findViewById(R.id.loginbutton);
        Button register = (Button) findViewById(R.id.registerbutton);

        //listener for the login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the values inserted by the user
                emailtext = findViewById(R.id.emailtext);
                passwordtext = findViewById(R.id.passwordtext);
                String email = emailtext.getText().toString();
                String password = passwordtext.getText().toString();
                mAuth = FirebaseAuth.getInstance();

                //check if email and password have been entered correctly
                if (password.length() < 6){
                    passwordtext.setError("Password must contain 6 characters or more");
                }
                else if(email.equals("")){
                    emailtext.setError("Email cannot be empty");
                }
                else {
                    //sign in method using firebase database
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Welcome.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //display message if sign in is successful
                                Toast.makeText(Welcome.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                                //get the user id
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                //start the next activity and pass the user id
                                Intent intent = new Intent(getApplicationContext(), Notes.class);
                                intent.putExtra(MESSAGE_KEY, userID);
                                startActivity(intent);
                            } else {
                                //if sign in fails, display error message
                                Toast.makeText(Welcome.this, "Email or password incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //listener for register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the values inserted by the user
                emailtext = findViewById(R.id.emailtext);
                passwordtext = findViewById(R.id.passwordtext);
                String email = emailtext.getText().toString();
                String password = passwordtext.getText().toString();
                mAuth = FirebaseAuth.getInstance();

                //check if email and password were entered correctly
                if (password.length() < 6){
                    passwordtext.setError("Password must contain 6 characters or more");
                }
                else if(email.equals("")){
                    emailtext.setError("Email cannot be empty");
                }
                else {
                    //create a user in the firebase database
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Welcome.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //display message
                                Toast.makeText(Welcome.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                                //get the user id
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                //start the next activity and pass the user id
                                Intent intent = new Intent(getApplicationContext(), Register.class);
                                intent.putExtra(MESSAGE_KEY, userID);
                                startActivity(intent);
                            } else {
                                //display error message
                                Toast.makeText(Welcome.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
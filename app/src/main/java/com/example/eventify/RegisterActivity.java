package com.example.eventify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText signupUsername, signupEmail, signupContact, signupPassword, confirmPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseAuth firebaseAuth;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        signupUsername = findViewById(R.id.signup_username);
        signupEmail = findViewById(R.id.signup_email);
        signupContact = findViewById(R.id.signup_contact);
        signupPassword = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.confirm_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = signupUsername.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String contact = signupContact.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String confirm_password = confirmPassword.getText().toString().trim();

                if (username.isEmpty() || contact.isEmpty() || email.isEmpty() || password.isEmpty() || confirm_password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(confirm_password)) {
                        // Register the user with Firebase
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            if (firebaseAuth.getCurrentUser() != null) {
                                                // Check if the user is not null before getting UID
                                                String userId = firebaseAuth.getCurrentUser().getUid();
                                                saveUserDataToDatabase(userId, email, username, contact);
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "User is null", Toast.LENGTH_SHORT).show();
                                            }
                                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else
                        Toast.makeText(RegisterActivity.this, "The password is not matched", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveUserDataToDatabase(String userId, String email, String username, String contact) {
        // Create a new child node for the user using their user ID
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference("userinfo");
        DatabaseReference userReference = usersReference.child(userId);

        userReference.child("email").setValue(email);
        userReference.child("username").setValue(username);
        userReference.child("contact").setValue(contact);
    }

}

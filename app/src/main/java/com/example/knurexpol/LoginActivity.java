package com.example.knurexpol;
/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements
            View.OnClickListener {

        private static final String TAG = "LoginActivity";

        private TextView mStatusTextView;
        private TextView mDetailTextView;
        private EditText mEmailField;
        private EditText mPasswordField;


        private FirebaseFunctions mFunctions;
        private FirebaseAuth mAuth;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // Views
            mStatusTextView = findViewById(R.id.status);
            mDetailTextView = findViewById(R.id.detail);
            mEmailField = findViewById(R.id.fieldEmail);
            mPasswordField = findViewById(R.id.fieldPassword);

            // Buttons
            findViewById(R.id.emailSignInButton).setOnClickListener(this);
            findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
            findViewById(R.id.signOutButton).setOnClickListener(this);
            findViewById(R.id.verifyEmailButton).setOnClickListener(this);
            findViewById(R.id.startAppButton).setOnClickListener(this);


            mAuth = FirebaseAuth.getInstance();
            mFunctions = FirebaseFunctions.getInstance();
        }


        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }

        private void createAccount(String email, String password) {
            Log.d(TAG, "createAccount:" + email);
            if (!validateForm()) {
                return;
            }

            String domain = email.substring(email .indexOf("@") + 1);
            if(!domain.equals("student.wat.edu.pl") && !domain.equals("wat.edu.pl")){
                Toast.makeText(LoginActivity.this, "Wrong domain. Your email must be from student.wat.edu.pl or wat.edu.pl"+domain,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // [START create_user_with_email]
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                addUserToFirebase();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                        }
                    });
            // [END create_user_with_email]
        }

    private Task<HashMap> addUserToListFunction() {
        // Create the arguments to the callable function.

        return mFunctions
                .getHttpsCallable("addUserToListFunction")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                    @Override
                    public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        HashMap result = (HashMap) task.getResult().getData();
                        return result;
                    }
                });
    }

        public void addUserToFirebase(){
            addUserToListFunction().addOnCompleteListener(this, new OnCompleteListener<HashMap>() {
                @Override
                public void onComplete(@NonNull Task<HashMap> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "addUserToListFunction:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "addUserToListFunction:failure", task.getException());
                        updateUI(null);
                    }
                }
            });
        }

    private Task<HashMap> isUserOnListFunction() {
        // Create the arguments to the callable function.

        return mFunctions
                .getHttpsCallable("isUserOnListFunction")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                    @Override
                    public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        HashMap result = (HashMap) task.getResult().getData();
                        return result;
                    }
                });
    }

    public void isUserOnListToFirebase(){
        isUserOnListFunction().addOnCompleteListener(this, new OnCompleteListener<HashMap>() {
            @Override
            public void onComplete(@NonNull Task<HashMap> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "isUserOnListFunction:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "isUserOnListFunction:failure", task.getException());
                    updateUI(null);
                }
            }
        });
    }

        private void signIn(String email, String password) {
            Log.d(TAG, "signIn:" + email);
            if (!validateForm()) {
                return;
            }

            // [START sign_in_with_email]
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                isUserOnListToFirebase();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // [START_EXCLUDE]
                            if (!task.isSuccessful()) {
                                mStatusTextView.setText(R.string.auth_failed);
                            }
                            // [END_EXCLUDE]
                        }
                    });
            // [END sign_in_with_email]
        }

        private void signOut() {
            mAuth.signOut();
            updateUI(null);
        }

        private void sendEmailVerification() {
            // Disable button
            findViewById(R.id.verifyEmailButton).setEnabled(false);

            // Send verification email
            // [START send_email_verification]
            final FirebaseUser user = mAuth.getCurrentUser();
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // [START_EXCLUDE]
                            // Re-enable button
                            findViewById(R.id.verifyEmailButton).setEnabled(true);

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(LoginActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // [END_EXCLUDE]
                        }
                    });
            // [END send_email_verification]
        }

        private boolean validateForm() {
            boolean valid = true;

            String email = mEmailField.getText().toString();
            if (TextUtils.isEmpty(email)) {
                mEmailField.setError("Required.");
                valid = false;
            } else {
                mEmailField.setError(null);
            }

            String password = mPasswordField.getText().toString();
            if (TextUtils.isEmpty(password)) {
                mPasswordField.setError("Required.");
                valid = false;
            } else {
                mPasswordField.setError(null);
            }

            return valid;
        }

        private void updateUI(FirebaseUser user) {
            if (user != null) {
                mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                        user.getEmail(), user.isEmailVerified()));
                mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

                findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
                findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
                findViewById(R.id.startAppButton).setVisibility(View.VISIBLE);
                findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);

                findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());

                findViewById(R.id.startAppButton).setEnabled(user.isEmailVerified());



            } else {
                mStatusTextView.setText(R.string.signed_out);
                mDetailTextView.setText(null);

                findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
                findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
                findViewById(R.id.startAppButton).setVisibility(View.GONE);
                findViewById(R.id.signedInButtons).setVisibility(View.GONE);
                findViewById(R.id.startAppButton).setEnabled(false);
            }
        }

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.emailCreateAccountButton) {
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            } else if (i == R.id.emailSignInButton) {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            } else if (i == R.id.signOutButton) {
                signOut();
            } else if (i == R.id.verifyEmailButton) {
                sendEmailVerification();
            } else if (i == R.id.startAppButton) {
                Intent myIntent = new Intent(this, MenuActivity.class);
                startActivity(myIntent);
            }
        }
    }
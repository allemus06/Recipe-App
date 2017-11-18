package cecs453.android.csulb.edu.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created By Alejandro Lemus and related XML
 */
public class Activity_Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button loginButton;
    private Button createAccountButton;
    private Button emailClearButton;
    private Button passwordClearButton;
    private Button conPassClearButton;
    private EditText emailET;
    private EditText passwordET;
    private EditText conPassET;

    //0 = Login State
    //1 = Create Account State
    private int CREATE_ACCOUNT_STATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        initializeViews();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.i("User ID", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.i("User ID", "onAuthStateChanged:signed_out");
                }
            }
        };

        //TODO: Remove this (I got tired of logging in every time
        Button skipButton = (Button) findViewById(R.id.skipButton);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailET.setText("a@test.com");
                passwordET.setText("123456");
                signIn("a@test.com", "123456");
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CREATE_ACCOUNT_STATE == 1) {
                    createAccount(emailET.getText().toString(), passwordET.getText().toString());
                } else {
                    signIn(emailET.getText().toString(), passwordET.getText().toString());
                }
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CREATE_ACCOUNT_STATE == 1) {
                    createAccountButton.setText("CREATE ACCOUNT");
                    loginButton.setText("LOGIN");

                    conPassET.setVisibility(View.INVISIBLE);
                    conPassClearButton.setVisibility(View.INVISIBLE);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(getApplicationContext(), R.layout.activity_login);
                    constraintSet.connect(R.id.passwordET, ConstraintSet.BOTTOM, R.id.loginLinearLayout, ConstraintSet.TOP);
                    ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.loginLayout);
                    constraintSet.applyTo(constraintLayout);

                    conPassET.setVisibility(View.INVISIBLE);
                    conPassClearButton.setVisibility(View.INVISIBLE);

                    CREATE_ACCOUNT_STATE = 0;
                } else {
                    createAccountButton.setText("BACK");
                    loginButton.setText("CREATE ACCOUNT");
                    conPassET.setText("");

                    conPassET.setVisibility(View.VISIBLE);
                    conPassClearButton.setVisibility(View.VISIBLE);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(getApplicationContext(), R.layout.activity_login);
                    constraintSet.connect(R.id.passwordET, ConstraintSet.BOTTOM, R.id.conPassET, ConstraintSet.TOP);
                    ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.loginLayout);
                    constraintSet.applyTo(constraintLayout);

                    CREATE_ACCOUNT_STATE = 1;
                }
                //createAccount(emailET.getText().toString(), passwordET.getText().toString());
            }
        });

        emailClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailET.setText("");
                emailET.requestFocus();
            }
        });
        passwordClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordET.setText("");
                passwordET.requestFocus();
            }
        });
        conPassClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conPassET.setText("");
                conPassET.requestFocus();
            }
        });

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailET.setBackgroundColor(getResources().getColor(R.color.colorBlackTransparent60));
                emailClearButton.setBackgroundColor(getResources().getColor(R.color.colorBlackTransparent60));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordET.setBackgroundColor(getResources().getColor(R.color.colorBlackTransparent60));
                passwordClearButton.setBackgroundColor(getResources().getColor(R.color.colorBlackTransparent60));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        conPassET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                conPassET.setBackgroundColor(getResources().getColor(R.color.colorBlackTransparent60));
                conPassClearButton.setBackgroundColor(getResources().getColor(R.color.colorBlackTransparent60));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void initializeViews () {
        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        conPassET = (EditText) findViewById(R.id.conPassET);
        loginButton = (Button) findViewById(R.id.loginButton);
        emailClearButton = (Button) findViewById(R.id.emailClearButton);
        passwordClearButton = (Button) findViewById(R.id.passwordClearButton);
        conPassClearButton = (Button) findViewById(R.id.conPassClearButton);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);

        conPassET.setVisibility(View.INVISIBLE);
        conPassClearButton.setVisibility(View.INVISIBLE);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(getApplicationContext(), R.layout.activity_login);
        constraintSet.connect(R.id.passwordET, ConstraintSet.BOTTOM, R.id.loginLinearLayout, ConstraintSet.TOP);
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.loginLayout);
        constraintSet.applyTo(constraintLayout);

        conPassET.setVisibility(View.INVISIBLE);
        conPassClearButton.setVisibility(View.INVISIBLE);

        CREATE_ACCOUNT_STATE = 0;
    }

    private void createAccount(final String email, final String password) {
        Log.i("Account", "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("Account", "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()) {
                                        Toast.makeText(Activity_Login.this, "Sent verification email to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Activity_Login.this, "Verification email failed to send", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                            Toast.makeText(Activity_Login.this, "Account Created",
                                    Toast.LENGTH_SHORT).show();

                            signIn(email, password);
                        } else {
                            Log.i("Account", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Activity_Login.this, "Create Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        //hideProgressDialog();
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.i("Account", "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("Account", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Activity_Login.this, "Login Successful for " + user.getEmail().toString(), Toast.LENGTH_SHORT).show();

                            Intent mainActivityIntent = new Intent(Activity_Login.this, MainActivity.class);
                            startActivity(mainActivityIntent);
                        } else {
                            Log.i("Account", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Activity_Login.this, "Login Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailET.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailET.setError("Required");
            emailET.setBackgroundColor(getResources().getColor(R.color.colorRedTransparent55));
            emailClearButton.setBackgroundColor(getResources().getColor(R.color.colorRedTransparent55));
            valid = false;
        } else {
            emailET.setError(null);
        }

        String password = passwordET.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordET.setError("Required");
            passwordET.setBackgroundColor(getResources().getColor(R.color.colorRedTransparent55));
            passwordClearButton.setBackgroundColor(getResources().getColor(R.color.colorRedTransparent55));;
            valid = false;
        } else {
            passwordET.setError(null);
        }

        String conPassword = conPassET.getText().toString();
        if (CREATE_ACCOUNT_STATE == 1) {
            if (TextUtils.isEmpty(conPassword)) {
                conPassET.setError("Required");
                conPassET.setBackgroundColor(getResources().getColor(R.color.colorRedTransparent55));
                conPassClearButton.setBackgroundColor(getResources().getColor(R.color.colorRedTransparent55));
                valid = false;
            } else {
                if (password.equals(conPassword)) {
                    conPassET.setError(null);
                } else {
                    conPassET.setError("Passwords don't match");
                    conPassET.setBackgroundColor(getResources().getColor(R.color.colorRedTransparent55));
                    conPassClearButton.setBackgroundColor(getResources().getColor(R.color.colorRedTransparent55));
                    valid = false;
                }
            }
        } else {
            conPassET.setError(null);
        }

        return valid;
    }
}

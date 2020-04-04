package com.example.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText EmailET, PwdET;
    private Button Login, Signup;
    private final static int RC_SIGN_IN = 123;
    private LoginButton btn2;
    private ProgressBar PB;
    private Button btn1;
    private FirebaseAuth auth;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private FragmentManager.BackStackEntry acct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        connectXml();
        createRequest();

    }

    @SuppressLint("WrongViewCast")
    private void connectXml() {
        Login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Login();

            }

        });
        PB=findViewById(R.id.PB);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

finish();
            }
        });
        Signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //SignUp();
                UserExist();
            }
        });
btn1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
signIn();
    }
});
        mCallbackManager = CallbackManager.Factory.create();
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    PB.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "facebook:onSuccess:" + loginResult, Toast.LENGTH_SHORT).show();
                    handleFacebookAccessToken(loginResult.getAccessToken());

                }

                @Override
                public void onCancel() {
                    Toast.makeText(MainActivity.this, "facebook:onCancel:", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(MainActivity.this, "facebook:onError:", Toast.LENGTH_SHORT).show();
                }
            });
            }


        });


        EmailET = findViewById(R.id.EmailET);
        PwdET = findViewById(R.id.PwdET);
        Login=findViewById(R.id.login);
        Signup=findViewById(R.id.Signup);
        btn2= findViewById(R.id.btn2);
    }
    private void Login() {
        try {
            PB.setVisibility(View.VISIBLE);
            if (!EmailET.getText().toString().isEmpty() && !PwdET.getText().toString().isEmpty()) {

                if (auth.getCurrentUser() != null) {

                    PB.setVisibility(View.INVISIBLE);

                    auth.signOut();
                    Login.setEnabled(false);

                    Toast.makeText(this, "User Logged Out Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(EmailET.getText().toString(),
                            PwdET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                     PB.setVisibility(View.INVISIBLE);
                                     Login.setEnabled(true);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Login.setEnabled(true);
                            EmailET.requestFocus();

                            PB.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            } else if (EmailET.getText().toString().isEmpty()) {
                Login.setEnabled(true);
                //bar.setVisibility(View.INVISIBLE);

                EmailET.requestFocus();
                Toast.makeText(this, "Please Enter The Email", Toast.LENGTH_SHORT).show();
            } else if (PwdET.getText().toString().isEmpty()) {
                Login.setEnabled(true);
                // bar.setVisibility(View.INVISIBLE);

                PwdET.requestFocus();
                Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {

            Login.setEnabled(true);
            EmailET.requestFocus();

            //bar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Logging In Error" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void SignUp() {
        try {
            if (!EmailET.getText().toString().isEmpty() &&
                    !PwdET.getText().toString().isEmpty()) {
                if (auth != null) {
                    Signup.setEnabled(false);
                    auth.createUserWithEmailAndPassword(EmailET.getText().toString(),
                            PwdET.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(MainActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                            if (authResult.getUser() != null) {
                                PB.setVisibility(View.VISIBLE);
                                auth.signOut();
                                EmailET.setText("");
                                PwdET.setText("");
                                EmailET.requestFocus();
                                Signup.setEnabled(true);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Login.setEnabled(true);
                            EmailET.requestFocus();


                            Toast.makeText(MainActivity.this, "Failed To Create User" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (EmailET.getText().toString().isEmpty()) {
                    Login.setEnabled(true);

                    EmailET.requestFocus();
                    Toast.makeText(this, "Please Enter The Email", Toast.LENGTH_SHORT).show();
                } else if (PwdET.getText().toString().isEmpty()) {
                    Login.setEnabled(true);
                    PB.setVisibility(View.VISIBLE);

                    PwdET.requestFocus();
                    Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e) {
            Toast.makeText(MainActivity.this, "LoginUser" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void UserExist() {
        try {
            if (!EmailET.getText().toString().isEmpty()) {
                if (auth != null) {
                    Signup.setEnabled(false);
                    auth.fetchSignInMethodsForEmail(EmailET.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean check = task.getResult().getSignInMethods().isEmpty();
                                    if (!check) {
                                        Signup.setEnabled(true);


                                        Toast.makeText(MainActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();

                                    } else if (check) {

                                        Signup.setEnabled(true);

                                        SignUp();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Signup.setEnabled(true);
                            Toast.makeText(MainActivity.this, "Fails To Check If User Exists or not" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                EmailET.requestFocus();
                Signup.setEnabled(true);
                Toast.makeText(this, "Fields are Empty", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Signup.setEnabled(true);
            Toast.makeText(this, "Check User Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken" + token);
        Toast.makeText(this, "handleFacebookAccessToken" + token, Toast.LENGTH_SHORT).show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();
                           
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed. signInWithCredential:failure" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createRequest() {
        //Creating a send request to open a Pop-up so that user can Log-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn() {
        //Intent in which you can select your Google account
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       PB.setVisibility(View.VISIBLE);

        try {
           // bar.setVisibility(View.INVISIBLE);
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception ex) {
            PB.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "On Activity: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
               PB.setVisibility(View.INVISIBLE);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                PB.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Login Failed: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(String.valueOf(acct.getId()), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }
}


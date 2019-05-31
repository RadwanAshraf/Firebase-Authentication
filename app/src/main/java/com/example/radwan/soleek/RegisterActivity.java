package com.example.radwan.soleek;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity {

    private SignInButton gmailLogin;
    private Button facbookLogin;
    private Button backTologin;
    private Button confirmRegister;
    private EditText rMail;
    private EditText rPassword;
    private EditText rConfirmPassword;
    private FirebaseAuth mAuth;

    private GoogleApiClient googleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN=101;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager mCallbackManager;
    private FirebaseUser firebaseUser;
    private LoginButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        gmailLogin=findViewById(R.id.login_With_gmail);
        facbookLogin=findViewById(R.id.login_With_facebook);
        backTologin=findViewById(R.id.back_to_login_button);
        confirmRegister=findViewById(R.id.register_button);
        rMail=findViewById(R.id.mail_reg_field);
        rPassword=findViewById(R.id.password_reg_field);
        rConfirmPassword=findViewById(R.id.password_reg_field_confirm);


        //Register Button Clicked


        confirmRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email;
                String pass;
                String cofirmPass;
                email=rMail.getText().toString();
                pass=rPassword.getText().toString();
                cofirmPass=rConfirmPassword.getText().toString();

                if(isUserDateValid(email,pass,cofirmPass))
                {

                    mAuth.createUserWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this,"Registration Done",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

        backTologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(RegisterActivity.this,HomeActivity.class));
                finish();
            }
        });
       //google Sign-in

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient=GoogleSignIn.getClient(this,gso);

        gmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                Toast.makeText(RegisterActivity.this,"Done",Toast.LENGTH_SHORT).show();
            }
        };
        googleApiClient =new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(
                this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(RegisterActivity.this,"Connection Failed",Toast.LENGTH_SHORT).show();
                    }
                }
        ).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        //Facebook Sign-in

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_With_facebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                        Toast.makeText(RegisterActivity.this,"facebook:onCancel",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(RegisterActivity.this,"facebook:onError",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private boolean isUserDateValid(String email, String pass, String cofirmPass) {

        if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(RegisterActivity.this,"Password Field is Empty !!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisterActivity.this,"Email Field is Empty !!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(cofirmPass))
        {
            Toast.makeText(RegisterActivity.this,"Confirmed Password Field is Empty !!",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(pass.length()<6)
        {
            Toast.makeText(RegisterActivity.this,"Password Too Short !!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!pass.equals(cofirmPass))
        {
            Toast.makeText(RegisterActivity.this,"In correct Password !!",Toast.LENGTH_SHORT).show();
            return false;
        }

        return  true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
    }


    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(RegisterActivity.this,"Success",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            rMail.setText(mAuth.getCurrentUser().getEmail());
                            rPassword.setText(mAuth.getCurrentUser().getPhoneNumber());

                        } else {
                            // If sign in fails, display a message to the user.
                             Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                Toast.makeText(RegisterActivity.this,"ERROR",Toast.LENGTH_SHORT).show();

            }
        }else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

            Toast.makeText(RegisterActivity.this,"Request CODE : "+requestCode,Toast.LENGTH_SHORT).show();
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this,"Successful",Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(RegisterActivity.this,RegisterActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,"Google Authentication failed Successfully",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}

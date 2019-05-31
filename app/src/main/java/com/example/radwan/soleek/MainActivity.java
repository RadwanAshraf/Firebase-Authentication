package com.example.radwan.soleek;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {


    private EditText mMailInput;
    private EditText mPassInput;

    public FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener authStateListener;

    Boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flag=false;
        mMailInput = findViewById(R.id.mail_login_field);
        mPassInput = findViewById(R.id.password_login_field);

        Button mLoginButt = findViewById(R.id.login_button);
        TextView mRegister=findViewById(R.id.register_textView);
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


                    if(firebaseAuth.getCurrentUser()!=null)
                    {
                        flag=true;
                        startActivity( new Intent(MainActivity.this,HomeActivity.class));
                        Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    }

            }
        };
        getHashKey();
        mAuth = FirebaseAuth.getInstance();
        mLoginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLogin();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        authStateListener.onAuthStateChanged(mAuth);
    }

    private void startLogin() {

        String Email = mMailInput.getText().toString();
        String Pass = mPassInput.getText().toString();
        if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Pass))
        {  Toast.makeText(MainActivity.this, "Mail OR Pass Empty", Toast.LENGTH_SHORT).show();
    }else {
            mAuth.signInWithEmailAndPassword(Email, Pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                                startActivity( new Intent(MainActivity.this,HomeActivity.class));

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }
    //Hash Key For Facebook sign-in
    private void getHashKey()
    {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.example.radwan.soleek", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                //String something = new String(Base64.encode(md.digest(), 0));
                String something = new String(Base64.encode(md.digest(),0));
          //      mMailInput.setText(something);
                Log.e("**** Hash Key", something);
            }
        }
        catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        }

        catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        }
        catch (Exception e){
            Log.e("exception", e.toString());
        }

    }
}

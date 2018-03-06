package ca.bcit.facebooklogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    TextView textView;

    CallbackManager callbackManager;

    private FirebaseAuth mAuth;

    private final String TAG = "FACEBOOK";

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            System.out.println("User already signed in");

            //Go to HomeActivity
        }

        else {
            System.out.println("User not signed in Yet");
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        loginButton = (LoginButton) findViewById(R.id.button);
        textView  = (TextView) findViewById(R.id.fbTxt);

        callbackManager = CallbackManager.Factory.create();

        mAuth = FirebaseAuth.getInstance();

        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                textView.setText("USER LOG IN COMPLETE: \n"
                        + loginResult.getAccessToken().getUserId() + "\n" +
                loginResult.getAccessToken().getToken());


                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                textView.setText("USER CANCELED IT");

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode,resultCode,data);


    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Login SUCCESS", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Login FAILED", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
}

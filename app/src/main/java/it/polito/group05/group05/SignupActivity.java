package it.polito.group05.group05;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import de.hdodenhof.circleimageview.CircleImageView;
import com.mvc.imagepicker.ImagePicker;


import org.greenrobot.eventbus.EventBus;

import it.polito.group05.group05.Utility.DB_Manager;
import it.polito.group05.group05.Utility.EventClasses.CurrentUserChangedEvent;
import it.polito.group05.group05.Utility.ImageUtils;

public class SignupActivity extends AppCompatActivity {

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private static final String TAG = "SignupActivity";

    CircleImageView _cvUserImage;
    EditText _nameText;
    EditText _surnameText;
    EditText _emailText;
    EditText _pnumber;
    EditText _passwordText;
    Button _signupButton;
    TextView _loginLink;
    TextView _verificationLink;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseUser mCurrentUser;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        if (bitmap != null) {
            _cvUserImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        _cvUserImage = (CircleImageView)findViewById(R.id.iv_user_image);
        _nameText = (EditText) findViewById(R.id.input_name);
        _surnameText = (EditText) findViewById(R.id.input_surname);
        _emailText = (EditText) findViewById(R.id.input_email);
        _pnumber = (EditText)findViewById(R.id.input_phone);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);
        _verificationLink = (TextView) findViewById(R.id.link_verification);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    String UserId = user.getUid();
                    mCurrentUser = user;
                    Toast.makeText(SignupActivity.this, "USER ID\n"+UserId,Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    Toast.makeText(SignupActivity.this, "no id got", Toast.LENGTH_SHORT).show();
                }
            }

        };

        _cvUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.pickImage(SignupActivity.this, "Select Image:");
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                onBackPressed();
            }
        });

        _verificationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser user = mAuth.getCurrentUser();
                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this,
                                            "Verification email sent to " + user.getEmail(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e(TAG, "sendEmailVerification", task.getException());
                                    Toast.makeText(SignupActivity.this,
                                            "Failed to send verification email.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                // [END_EXCLUDE]
                            }
                        });
                // [END send_email_verification]
            }
        });
    }


    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }
    // [END on_stop_remove_listener]


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        final String emailfordb = new String(email);
        final String namefordb = new String(name);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                onSignupFailed();
                            }
                        });
                        if(task.isSuccessful())
                            onSignupSuccess();

                        // [START_EXCLUDE]
                        progressDialog.dismiss();
                        // [END_EXCLUDE]
                    }


                });
        // [END create_user_with_email]

    }


    public void onSignupSuccess() {
        if(mCurrentUser != null) {
            DB_Manager.setDbContext(getApplicationContext());
            DB_Manager.getDatabase();
            DB_Manager.getInstance().pushNewUser(_emailText.getText().toString(), _nameText.getText().toString(),_pnumber.getText().toString(), ((BitmapDrawable)_cvUserImage.getDrawable()).getBitmap(), mCurrentUser.getUid());
            _signupButton.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Registered as " + mCurrentUser.getUid(), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, null);
            Intent i = new Intent(this, HomeScreen.class);
            startActivity(i);
            finish();
        } else
            onSignupFailed();
     }

    public void onSignupFailed() {
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

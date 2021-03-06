package com.silpe.vire.slip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silpe.vire.slip.dtos.User;
import com.silpe.vire.slip.models.SessionModel;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterManually extends AppCompatActivity {

    // UI references.
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mOccupationView;
    private EditText mCompanyView;
    private View mProgressView;
    private View mLoginFormView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mFirstNameView = (EditText) findViewById(R.id.firstName);
        mLastNameView = (EditText) findViewById(R.id.lastName);
        mOccupationView = (EditText) findViewById(R.id.occupation);
        mCompanyView = (EditText) findViewById(R.id.company);

        mCompanyView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
    }

    private void attemptRegister() {
        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mOccupationView.setError(null);
        mCompanyView.setError(null);

        // Store values at the time of the login attempt.
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String occupation = mOccupationView.getText().toString();
        String company = mCompanyView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(company)) {
            mCompanyView.setError(getString(R.string.error_field_required));
            focusView = mCompanyView;
            cancel = true;
        }
        if (TextUtils.isEmpty(occupation)) {
            mOccupationView.setError(getString(R.string.error_field_required));
            focusView = mOccupationView;
            cancel = true;
        }
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            doRegister(firstName, lastName, occupation, company);
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        /*
         * TODO
         * -- Make the register form look nicer
         * -- Overlay the loading indicator instead of hiding everything
         * -- Add load timeout
         */
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }



    private void doRegister(String firstName, String lastName, String occupation, String company) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this,
                    "Fuck you, fucking retard for crashing the app. " +
                    "Now you are happy eh? How sad your little pathetic life got " +
                    "nothing better to do than crasahing ppl's code. " +
                    "Go home and get a life, sucker",Toast.LENGTH_LONG).show();
            return;
        }
        User slipUser = new User(user.getUid(), user.getEmail(), "", firstName, lastName, occupation, company);
        SessionModel.get().setUser(slipUser, this);
        ref.child(getString(R.string.database_users)).child(slipUser.getUid()).setValue(slipUser);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}


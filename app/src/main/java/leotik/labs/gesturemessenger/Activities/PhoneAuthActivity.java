package leotik.labs.gesturemessenger.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.firebase.ui.auth.ui.phone.SpacedEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.Logging;
import leotik.labs.gesturemessenger.Util.PhoneNumberUtils;
import leotik.labs.gesturemessenger.Util.RealtimeDB;
import leotik.labs.gesturemessenger.Util.User;
import leotik.labs.gesturemessenger.Views.CountrySpinner;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


public class PhoneAuthActivity extends AppCompatActivity implements View.OnClickListener {
    public EditText Phone_et;
    private TextView registerAs, smsTos, editphone;
    private SpacedEditText Code;
    private Button Send_Code_btn;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ConstraintLayout Send_Container, Verify_Container;
    private String mverificationID;
    private PhoneAuthCredential phoneAuthCredential;
    private MaterialProgressBar progressBar;
    private CountrySpinner mSpinner;


    //todo save variable as user might exit current activity
    //todo show message of not accepting users after message sending failed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        if (isPhoneVerified()) {
            launchMainActivity();

        } else {
            editphone = findViewById(R.id.edit_phone_number);
            editphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            mSpinner = findViewById(R.id.country_list);
            mSpinner.init(new Bundle());
            Send_Container = findViewById(R.id.send_code_container);
            Verify_Container = findViewById(R.id.verify_code_container);
            Send_Code_btn = findViewById(R.id.send_code);
            Send_Code_btn.setOnClickListener(this);
            Phone_et = findViewById(R.id.phone_number);
            Code = findViewById(R.id.confirmation_code);
            registerAs = findViewById(R.id.registeras);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            registerAs.setText(getString(R.string.RegisterAs, user.getDisplayName(), user.getEmail()));
            registerAs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(PhoneAuthActivity.this, SplashScreen.class));
                    finish();
                }
            });
            smsTos = findViewById(R.id.send_sms_tos);
            smsTos.setText(getString(R.string.sms_terms_of_service, Send_Code_btn.getText().toString()));
            setupConfirmationCodeEditText();
            progressBar = findViewById(R.id.top_progress_bar);
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    progressBar.setShowProgressBackground(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    Code.setAnimation(shakeError(4));
                    Code.setEnabled(true);
                    Code.setText("------");
                    Code.setAnimation(shakeError(4));
                }

                @Override
                public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    Log.d("Ritik", "onCodeSent: " + verificationID);
                    Send_Container.setVisibility(View.GONE);
                    Verify_Container.setVisibility(View.VISIBLE);
                    mverificationID = verificationID;
                    progressBar.setShowProgressBackground(false);
                    progressBar.setVisibility(View.INVISIBLE);

                    super.onCodeSent(verificationID, forceResendingToken);

                }
            };

        }
    }


    public Boolean isPhoneVerified() {
        String userPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        Log.d("Ritik", ",User Phone is " + userPhone);
        return userPhone != null && !userPhone.equals("null") && !userPhone.equals("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Code.requestFocus();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(Code, 0);
    }

    private void setupConfirmationCodeEditText() {
        Code.setText("------");
        Code.addTextChangedListener(new BucketedTextChangeListener2(
                Code, 6, "-",
                new BucketedTextChangeListener2.ContentChangeCallback() {
                    @Override
                    public void whileComplete() {
                        progressBar.setShowProgressBackground(true);
                        progressBar.setVisibility(View.VISIBLE);
                        Code.setEnabled(false);
                        submitCode();
                    }

                    @Override
                    public void whileIncomplete() {
                        Code.setEnabled(true);
                    }
                }));


    }

    private void submitCode() {
        phoneAuthCredential = PhoneAuthProvider.getCredential(mverificationID, Code.getUnspacedText().toString());
        signInWithPhoneAuthCredential(phoneAuthCredential);

    }

    @Override
    public void onBackPressed() {
        if (Send_Container.getVisibility() == View.VISIBLE)
            super.onBackPressed();
        else {
            Code.setText("------");
            Send_Code_btn.setEnabled(true);
            Verify_Container.setVisibility(View.GONE);
            Send_Container.setVisibility(View.VISIBLE);

        }
    }


    @Nullable
    private String getPseudoValidPhoneNumber() {
        String everythingElse = Phone_et.getText().toString();

        if (TextUtils.isEmpty(everythingElse)) {
            return null;
        }

        return PhoneNumberUtils.format(everythingElse, mSpinner.getSelectedCountryInfo());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_code:
                progressBar.setShowProgressBackground(true);
                progressBar.setVisibility(View.VISIBLE);
                // Send_Code_btn.setClickable(false);
                Send_Code_btn.setEnabled(false);
                String phoneNumber = getPseudoValidPhoneNumber();
                if (phoneNumber.length() > 4) {
                    editphone.setText(phoneNumber);
                    Logging.logDebug(PhoneAuthActivity.class, "Sending Verification code to " + phoneNumber);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            this,               // Activity (for callback binding)
                            mCallbacks);
                } else
                    Phone_et.setError("Invalid Phone number");
                break;

//            case R.id.verify_code:
//                progressBar.setShowProgressBackground(true);
//                Verify_Code_btn.setEnabled(false);
//                String code = Code_et.getText().toString();
//                if (code.length()==6) {
//                    phoneAuthCredential = PhoneAuthProvider.getCredential(mverificationID, code);
//                    signInWithPhoneAuthCredential(phoneAuthCredential);
//                } else
//                    Code_et.setError("Invalid OTP");
//                break;


        }


    }

    private void launchMainActivity() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(PhoneAuthActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String newToken = instanceIdResult.getToken();
                Log.d("Ritik", "onSuccess: " + newToken);
                RealtimeDB.getInstance(PhoneAuthActivity.this).updatetokenonServer(newToken);

            }
        });
        startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class));
        finish();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User user = User.getInstance();
                        user.initUser(PhoneAuthActivity.this, null);
                        launchMainActivity();
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                progressBar.setShowProgressBackground(false);
                                progressBar.setVisibility(View.INVISIBLE);
                                Code.startAnimation(shakeError(4));
                                Code.setEnabled(true);
                                Code.setText("------");
                                Code.startAnimation(shakeError(4));
                            }
                        }
                    }
                });
    }

    public TranslateAnimation shakeError(int times) {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(times));
        return shake;
    }

}

final class BucketedTextChangeListener2 implements TextWatcher {

    private final EditText mEditText;
    private final BucketedTextChangeListener2.ContentChangeCallback mCallback;
    private final String[] mPostFixes;
    private final String mPlaceHolder;
    private final int mExpectedContentLength;

    public BucketedTextChangeListener2(EditText editText, int expectedContentLength, String
            placeHolder, BucketedTextChangeListener2.ContentChangeCallback callback) {
        mEditText = editText;
        mExpectedContentLength = expectedContentLength;
        mPostFixes = generatePostfixArray(placeHolder, expectedContentLength);
        mCallback = callback;
        mPlaceHolder = placeHolder;
    }

    /**
     * For example, passing in ("-", 6) would return the following result:
     * {"", "-", "--", "---", "----", "-----", "------"}
     *
     * @param repeatableChar the char to repeat to the specified length
     * @param length         the maximum length of repeated chars
     * @return an increasing sequence array of chars up the specified length
     */
    private static String[] generatePostfixArray(CharSequence repeatableChar, int length) {
        final String[] ret = new String[length + 1];

        for (int i = 0; i <= length; i++) {
            ret[i] = TextUtils.join("", Collections.nCopies(i, repeatableChar));
        }

        return ret;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTextChanged(
            CharSequence s, int ignoredParam1, int ignoredParam2, int ignoredParam3) {
        // The listener is expected to be used in conjunction with the SpacedEditText.

        // Approach
        // 1) Strip all spaces and hyphens introduced by the SET for aesthetics
        final String numericContents = s.toString()
                .replaceAll(" ", "")
                .replaceAll(mPlaceHolder, "");

        // 2) Trim the content to acceptable length.
        final int enteredContentLength = Math.min(numericContents.length(), mExpectedContentLength);
        final String enteredContent = numericContents.substring(0, enteredContentLength);

        // 3) Reset the text to be the content + required hyphens. The SET automatically inserts
        // spaces requires for aesthetics. This requires removing and resetting the listener to
        // avoid recursion.
        mEditText.removeTextChangedListener(this);
        mEditText.setText(enteredContent + mPostFixes[mExpectedContentLength - enteredContentLength]);
        mEditText.setSelection(enteredContentLength);
        mEditText.addTextChangedListener(this);

        // 4) Callback listeners waiting on content to be of expected length
        if (enteredContentLength == mExpectedContentLength && mCallback != null) {
            mCallback.whileComplete();
        } else if (mCallback != null) {
            mCallback.whileIncomplete();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public interface ContentChangeCallback {
        /**
         * Idempotent function invoked by the listener when the edit text changes and is of expected
         * length
         */
        void whileComplete();

        /**
         * Idempotent function invoked by the listener when the edit text changes and is not of
         * expected length
         */
        void whileIncomplete();
    }
}





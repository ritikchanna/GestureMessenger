package leotik.labs.gesturemessenger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.TimeUnit;

import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.RealtimeDB;
import leotik.labs.gesturemessenger.Util.User;

public class PhoneAuthActivity extends AppCompatActivity implements View.OnClickListener {
    public EditText Phone_et, Code_et;
    private Button Send_Code_btn, Verify_Code_btn;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ConstraintLayout Send_Container, Verify_Container;
    private String mverificationID;
    private PhoneAuthCredential phoneAuthCredential;


    //todo save variable as user might exit current activity
    //todo show message of not accepting users after message sending failed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        if (isPhoneVerified()) {
            launchMainActivity();

        } else {
            Send_Container = findViewById(R.id.send_code_container);
            Verify_Container = findViewById(R.id.verify_code_container);
            Send_Code_btn = findViewById(R.id.send_code);
            Send_Code_btn.setOnClickListener(this);
            Verify_Code_btn = findViewById(R.id.verify_code);
            Verify_Code_btn.setOnClickListener(this);
            Phone_et = findViewById(R.id.phone_number);
            Code_et = findViewById(R.id.code_et);
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    //todo do something here
                    Log.e("Ritik", "onVerificationFailed: " + e.getMessage());
                }

                @Override
                public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    Log.d("Ritik", "onCodeSent: " + verificationID);
                    Send_Container.setVisibility(View.GONE);
                    Verify_Container.setVisibility(View.VISIBLE);
                    mverificationID = verificationID;
                    super.onCodeSent(verificationID, forceResendingToken);
                }
            };

        }
    }


    public Boolean isPhoneVerified() {
        Log.d("Ritik", "isPhoneVerified: " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        return (!(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() == null));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_code:
                String phoneNumber = Phone_et.getText().toString();
                if (phoneNumber != null) {
                    Log.d("Ritik", "onClick: sendcode");
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            this,               // Activity (for callback binding)
                            mCallbacks);
                } else
                    Phone_et.setError("Invalid Phone number");
                break;

            case R.id.verify_code:
                String code = Code_et.getText().toString();
                if (code != null) {
                    Log.d("Ritik", "onClick: verifycode");
                    phoneAuthCredential = PhoneAuthProvider.getCredential(mverificationID, code);
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                } else
                    Code_et.setError("Invalid OTP");
                break;


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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Ritik", "signInWithCredential:success" + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                            User user = User.getInstance();
                            user.initUser(PhoneAuthActivity.this, null);
                            launchMainActivity();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("Ritik", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Code_et.setError("Invalid code.");
                            }
                        }
                    }
                });
    }
}

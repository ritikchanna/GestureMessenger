package leotik.labs.gesturemessenger.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;

import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.RealtimeDB;
import leotik.labs.gesturemessenger.Util.User;

public class SplashScreen extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(SplashScreen.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1024);
        } else
            login();
    }

    public void login() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.AppTheme)
                            .setLogo(R.drawable.ic_launcher_foreground)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
//                                    new AuthUI.IdpConfig.FacebookBuilder().build(),
//                                    new AuthUI.IdpConfig.TwitterBuilder().build(),
//                                    new AuthUI.IdpConfig.GitHubBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build()
                                    //new AuthUI.IdpConfig.PhoneBuilder().build()
                            ))
                            .build(),
                    RC_SIGN_IN);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                login();
            } else {
                //todo show a dialog, try again or maybe switch to legacy mode (Without Overlay)
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        }
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        else if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //startActivity(SignedInActivity.createIntent(this, response));
                User user = User.getInstance();
                user.initUser(SplashScreen.this, null);
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreen.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();
                        RealtimeDB.getInstance(SplashScreen.this).updatetokenonServer(newToken);

                    }
                });
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Snackbar.make(findViewById(R.id.splash_container), "Sign in Cancellled", Snackbar.LENGTH_SHORT);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(findViewById(R.id.splash_container), "No Internet Connection", Snackbar.LENGTH_SHORT);
                    return;
                }

                Snackbar.make(findViewById(R.id.splash_container), "Unknown Error", Snackbar.LENGTH_SHORT);
                Log.e("Ritik", "Sign-in error: ", response.getError());
            }
        }
    }
}

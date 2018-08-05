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
import leotik.labs.gesturemessenger.Util.Constants;
import leotik.labs.gesturemessenger.Util.RealtimeDB;
import leotik.labs.gesturemessenger.Util.User;

public class SplashScreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(SplashScreen.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, Constants.REQUEST_OVERLAY_PERMISSION);
        } else
            login();
    }

    public void login() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            User user = User.getInstance();
            user.initUser(SplashScreen.this, null, null);
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreen.this, new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {

                    String newToken = instanceIdResult.getToken();
                    Log.d("Ritik", "onSuccess: " + newToken);
                    RealtimeDB.getInstance(SplashScreen.this).updatetokenonServer(newToken);

                }
            });
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.AppTheme)
                            .setLogo(R.drawable.ic_launcher_foreground)
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
//                                    new AuthUI.IdpConfig.FacebookBuilder().build(),
//                                    new AuthUI.IdpConfig.TwitterBuilder().build(),
//                                    new AuthUI.IdpConfig.GitHubBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build()
                                    //new AuthUI.IdpConfig.PhoneBuilder().build()
                            ))
                            .build(),
                    Constants.REQUEST_SIGN_IN);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_OVERLAY_PERMISSION) {


            if (resultCode == RESULT_OK) {
                login();
            } else {
                //todo show a dialog, try again or maybe switch to legacy mode (Without Overlay)
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else if (requestCode == Constants.REQUEST_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //startActivity(SignedInActivity.createIntent(this, response));
                login();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Snackbar.make(findViewById(R.id.splash_container), getString(R.string.sign_in_cancelled), Snackbar.LENGTH_SHORT);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(findViewById(R.id.splash_container), getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
                    return;
                }

                Snackbar.make(findViewById(R.id.splash_container), getString(R.string.unknown_error), Snackbar.LENGTH_SHORT);

            }
        }
    }
}

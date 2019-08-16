package leotik.labs.gesturemessenger.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.Constants;
import leotik.labs.gesturemessenger.Util.Logging;

public class SplashScreen extends AppCompatActivity {


    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return Settings.canDrawOverlays(context);
        } else {
            if (Settings.canDrawOverlays(context)) return true;
            try {
                WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (mgr == null) return false; //getSystemService might return null
                View viewToAdd = new View(context);
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
                viewToAdd.setLayoutParams(params);
                mgr.addView(viewToAdd, params);
                mgr.removeView(viewToAdd);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logging.logDebug(SplashScreen.class, "Created");
        setContentView(R.layout.activity_splash_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        } else
            login();
    }

    public void login() {
        Logging.logDebug(SplashScreen.class, "Logging in");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            startActivity(new Intent(SplashScreen.this, PhoneAuthActivity.class));
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
                                    new AuthUI.IdpConfig.FacebookBuilder().build(),
//                                    new AuthUI.IdpConfig.TwitterBuilder().build(),
//                                    new AuthUI.IdpConfig.GitHubBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build()
//                                    new AuthUI.IdpConfig.PhoneBuilder().build()
                            ))
                            .build(),
                    Constants.REQUEST_SIGN_IN);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        if (canDrawOverlays(SplashScreen.this)) {
            login();

        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, Constants.REQUEST_OVERLAY_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_OVERLAY_PERMISSION) {


            if (resultCode == RESULT_OK) {
                login();
            } else {
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
                FirebaseUiException exception = response.getError();
                if (exception != null) {
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Snackbar.make(findViewById(R.id.splash_container), getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
                        return;
                    }

                }

                Snackbar.make(findViewById(R.id.splash_container), getString(R.string.unknown_error), Snackbar.LENGTH_SHORT);

            }
        }
    }
}

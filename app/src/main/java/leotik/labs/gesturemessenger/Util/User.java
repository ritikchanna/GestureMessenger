package leotik.labs.gesturemessenger.Util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private static User mUser;
    private FirebaseUser firebaseUser;


    private User() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public static synchronized User getInstance() {
        if (mUser == null) {
            mUser = new User();
        }
        return mUser;
    }

    public FirebaseUser getFirebaseUser() {
        if (firebaseUser == null)
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser;
    }

    public void destroy() {
        mUser = null;
    }

    public String getEmail() {
        if (firebaseUser == null)
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser.getEmail();
    }

    public String getName() {
        if (firebaseUser == null)
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("Ritik", "getName: " + firebaseUser + "  ");
        return firebaseUser.getDisplayName();
    }

    public void initUser(Context context, @Nullable String phoneNo, @Nullable String photo) {
        RealtimeDB.getInstance(context).initUser(getName(), getEmail(), photo, phoneNo);

    }


}

package leotik.labs.gesturemessenger.Util;

import android.content.Context;
import android.support.annotation.Nullable;

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

    public void destroy() {
        mUser = null;
    }

    public String getEmail() {
        return firebaseUser.getEmail();
    }

    public String getName() {
        return firebaseUser.getDisplayName();
    }

    public void initUser(Context context, @Nullable String phoneNo) {
        RealtimeDB.getInstance(context).initUser(getName(), "", getEmail(), phoneNo);

    }


}

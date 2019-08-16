package leotik.labs.gesturemessenger.Util;

import android.content.Context;

import androidx.annotation.Nullable;

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
        return firebaseUser.getDisplayName();
    }

    public String getPhoneNo() {
        if (firebaseUser == null)
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            return firebaseUser.getPhoneNumber();
        } catch (Exception e) {
            return null;
        }
    }

    public String getPhoto() {
        if (firebaseUser == null)
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser.getPhotoUrl().toString();
    }

    public void initUser(Context context, @Nullable String photo) {
        if (photo == null) {
            RealtimeDB.getInstance(context).initUser(getName(), getEmail(), getPhoto(), getPhoneNo());
        } else
            RealtimeDB.getInstance(context).initUser(getName(), getEmail(), photo, getPhoneNo());
    }


}

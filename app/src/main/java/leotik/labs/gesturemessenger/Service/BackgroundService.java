package leotik.labs.gesturemessenger.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class BackgroundService extends Service {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ChildEventListener childEventListener;
    private FirebaseFirestore firebaseFirestore;

    //todo dont do anything when user is not logged in

    @Override
    public void onCreate() {
        Log.d("Ritik", "Background Service onCreate: ");
        //Realtime Database related inits
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("Ritik", "onChildAdded: " + dataSnapshot.getKey());
                Log.d("Ritik", "onChildAdded: " + dataSnapshot.getValue());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        //Firebase Firestore related inits
        firebaseFirestore = FirebaseFirestore.getInstance();


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        if (!(phoneNumber == null))
            databaseReference.child("a").child(phoneNumber).addChildEventListener(childEventListener);
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        Log.d("Ritik", "BackgroundService onDestroy: ");
        Intent intent = new Intent("leotik.labs.gesturemessenger.startservice");
        //intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }


}

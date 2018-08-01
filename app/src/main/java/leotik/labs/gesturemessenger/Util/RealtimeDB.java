package leotik.labs.gesturemessenger.Util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

import leotik.labs.gesturemessenger.Service.OverlayService;

public class RealtimeDB {
    private static RealtimeDB mrealtimeDB;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseFirestoreSettings firebaseFirestoreSettings;

    private RealtimeDB(Context context) {
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(firebaseFirestoreSettings);
        databaseReference = database.getReference();
        this.context = context;

    }

    public static synchronized RealtimeDB getInstance(Context context) {

        if (mrealtimeDB == null) {
            mrealtimeDB = new RealtimeDB(context);
        }
        return mrealtimeDB;
    }

    public void initUser(@Nullable String FirstName, @Nullable String SecondName, String Email, @Nullable String Phoneno, @Nullable String token) {
        //todo return if succesful by adding listners
        Map<String, String> user = new HashMap<>();
        user.put("f", FirstName);
        user.put("s", SecondName);
        user.put("p", Phoneno);
        user.put("t", token);
        Boolean success;
// Add a new document with a generated ID
        firebaseFirestore.collection("u").document(Email)
                .set(user);
    }

    public void addFriend() {
        //todo maintain list of friends for each user on a separate node

    }

    public void updatetokenonServer(String token, String Email) {
        //todo return if succesful by adding listners
        firebaseFirestore.collection("u").document(Email)
                .update("t", token);
    }


    public void sendMessage(String Sender, String Receiver, String Message) {
        //todo break message if exceeds firebase limit
        //todo add oncomplete listener to show loading to sender
        Sender = System.currentTimeMillis() + Sender.replace('.', '*');
        databaseReference.child("m").child(Sender).setValue(Message);
        databaseReference.child("a").child(Receiver.replace('.', '*')).child(Sender).setValue("u");
    }


    public void setValueEventListner(String Receiver) {
        databaseReference.child("a").child(Receiver.replace('.', '*')).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("Ritik", s + "  onChildAdded: " + dataSnapshot.getKey().toString());
                databaseReference.child("m").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e("Ritik", "onDataChange: " + dataSnapshot.getValue());
                        Intent intent = new Intent(context, OverlayService.class);
                        intent.putExtra("gesture", dataSnapshot.getValue().toString());
                        context.startService(intent);
                        ((AppCompatActivity) context).finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
        });
    }

}

package leotik.labs.gesturemessenger.Util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealtimeDB {
    private static RealtimeDB mrealtimeDB;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private RealtimeDB() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        database.setPersistenceEnabled(true);
    }

    public static synchronized RealtimeDB getInstance() {

        if (mrealtimeDB == null) {
            mrealtimeDB = new RealtimeDB();
        }
        return mrealtimeDB;
    }


    public void sendMessage(String Sender, String Receiver, String Message) {
        //todo break message if exceeds firebase limit
        //todo add oncomplete listener to show loading to sender
        databaseReference.child("m").child(System.currentTimeMillis() + Sender.replace('.', '*')).setValue(Message);
        databaseReference.child("a").child(Receiver.replace('.', '*')).child(System.currentTimeMillis() + Sender.replace('.', '*')).setValue("u");
    }


    public void setValueEventListner(String Receiver) {
        databaseReference.child("a").child(Receiver.replace('.', '*')).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("Ritik", s + "  onChildAdded: " + dataSnapshot.getKey().toString());


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

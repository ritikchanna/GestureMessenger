package leotik.labs.gesturemessenger.Util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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

    public void initUser(@Nullable String FirstName, @Nullable String SecondName, String Email, @Nullable String Phoneno) {
        //todo return if succesful by adding listners
        Map<String, String> user = new HashMap<>();
        user.put("f", FirstName);
        user.put("s", SecondName);
        user.put("p", Phoneno);
        Boolean success;
// Add a new document with a generated ID
        firebaseFirestore.collection("u").document(Email.replace('.', '*'))
                .set(user);
    }

    public void addFriend() {
        //todo maintain list of friends for each user on a separate node

    }

    public void updatetokenonServer(String token, String Email) {
        //todo return if succesful by adding listners
        //databaseReference.child("t").child(Email.replace('.','*')).setValue(token);
        firebaseFirestore.collection("t").document(Email.replace('.', '*')).update("v", token);
    }


    public void sendMessage(String Sender, String Receiver, String Gesture) {
        //todo break message if exceeds firebase limit
        //todo add oncomplete listener to show loading to sender
        Sender = Sender.replace('.', '*');
        Receiver = Receiver.replace('.', '*');
        Map<String, String> msg = new HashMap<>();
        msg.put("s", Sender);
        msg.put("r", Receiver);
        msg.put("t", System.currentTimeMillis() + "");
        msg.put("m", Gesture);
        databaseReference.child("m").child(databaseReference.push().getKey()).setValue(msg);
    }

    public void displayGesture(String MessageID) {
        databaseReference.child("m").child(MessageID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Ritik", "onDataChange: " + dataSnapshot.getChildrenCount());
                Log.e("Ritik", "onDataChange: " + dataSnapshot.toString());


                String gesture = "gesture_value";
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.getKey().equals("m"))
                        gesture = userSnapshot.getValue().toString();

                }

                Log.e("Ritik", "onDataChange: " + gesture);
                Intent intent = new Intent(context, OverlayService.class);
                intent.putExtra("gesture", gesture);
                context.startService(intent);


                //((AppCompatActivity) context).finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


//    public void setValueEventListner(String Receiver) {
//        databaseReference.child("a").child(Receiver.replace('.', '*')).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Log.d("Ritik", s + "  onChildAdded: " + dataSnapshot.getKey().toString());
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

}

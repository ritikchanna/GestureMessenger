package leotik.labs.gesturemessenger.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import leotik.labs.gesturemessenger.Interface.DownloadListner;
import leotik.labs.gesturemessenger.POJO.UserPOJO;
import leotik.labs.gesturemessenger.Service.OverlayService;

public class RealtimeDB {
    private static RealtimeDB mrealtimeDB;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseFirestoreSettings firebaseFirestoreSettings;
    private User mUser;
    private DatabaseHelper databaseHelper;
    private ChatsDatabaseHelper chatsDatabaseHelper;

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
        mUser = User.getInstance();
        databaseHelper = new DatabaseHelper(context);
        chatsDatabaseHelper = new ChatsDatabaseHelper(context);
    }

    public static synchronized RealtimeDB getInstance(Context context) {

        if (mrealtimeDB == null) {
            mrealtimeDB = new RealtimeDB(context);
        }
        return mrealtimeDB;
    }

    public void initUser(@Nullable String Name, String Email, @Nullable String photoUrl, @Nullable String Phoneno) {
        //todo return if succesful by adding listners
        Map<String, String> user = new HashMap<>();
        user.put("n", Name);
        user.put("u", photoUrl);
        user.put("p", Phoneno);
        Boolean success;
// Add a new document with a generated ID
        firebaseFirestore.collection("u").document(sanitizeEmail(Email))
                .set(user);
    }

    public void addFriend(String EmailofFriend, DownloadListner downloadListner) {
        Map<String, Object> newFriend = new HashMap<>();
        newFriend.put(sanitizeEmail(EmailofFriend), "s");
        firebaseFirestore.collection("f").document(sanitizeEmail(mUser.getEmail())).set(newFriend, SetOptions.merge());
        downloadListner.OnDownloadResult(Constants.REFRESH_CONTACTS, "");
    }

    public void attachFriendListListner(final DownloadListner downloadListner) {
        firebaseFirestore.collection("f").document(sanitizeEmail(mUser.getEmail())).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Ritik", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("Ritik", "Current data: " + documentSnapshot.getData());

                    Map<String, Object> newUsers = documentSnapshot.getData();
                    newUsers.keySet().removeAll(databaseHelper.getallEmails());
                    updateFriendList(newUsers, downloadListner);
                    Log.d("Ritik", "Added Friends: " + newUsers);
                } else {
                    Log.d("Ritik", "Current data: null");
                }
            }
        });
    }

    public void updateFriendList(Map<String, Object> userstoadd, final DownloadListner downloadListner) {


        for (final Map.Entry<String, Object> entry : userstoadd.entrySet()) {
            firebaseFirestore.collection("u").document(entry.getKey().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getData() != null) {
                            Log.e("Ritik", "onComplete: " + task.getResult().getData());
                            Map<String, Object> user = task.getResult().getData();
                            UserPOJO userPOJO = new UserPOJO(entry.getKey(), user.get("n") + "", user.get("u") + "", user.get("p") + "");
                            databaseHelper.insertUser(userPOJO);
                        }


                    } else Log.e("Ritik", "onComplete: task failed " + task.getException());
                }
            });
            // user.put("s",entry.getValue());


        }
        if (downloadListner != null) {
            downloadListner.OnDownloadResult(Constants.REFRESH_CONTACTS, "");
        }

    }

    public void updateUser(String Email) {
        Map<String, Object> user = firebaseFirestore.collection("u").document(sanitizeEmail(Email)).get().getResult().getData();
        databaseHelper.insertUser(Email, user.get("n").toString(), user.get("u").toString(), user.get("p").toString());

    }

    public void updatetokenonServer(String token) {
        //todo return if succesful by adding listners
        //databaseReference.child("t").child(Email.replace('.','*')).setValue(token);
        if (mUser.getFirebaseUser() == null) {
            Log.d("Ritik", "updatetokenonServer: FIrebaseuser null");
            User.getInstance().destroy();
            mUser = User.getInstance();
        }
        if (mUser == null) {
            Log.e("Ritik", "updatetokenonServer: firebaseuser still null");
            return;
        }
        HashMap<String, Object> tokenvalue = new HashMap<>();
        tokenvalue.put("v", token);

        firebaseFirestore.collection("t").document(sanitizeEmail(mUser.getEmail())).set(tokenvalue);
    }


    public void sendMessage(String Receiver, String Gesture, final DownloadListner downloadListner) {
        //todo break message if exceeds firebase limit
        //todo add oncomplete listener to show loading to sender
        String Sender = sanitizeEmail(mUser.getEmail());
        Receiver = sanitizeEmail(Receiver);
        Map<String, String> msg = new HashMap<>();
        msg.put("s", Sender);
        msg.put("r", Receiver);
        msg.put("m", Gesture);
        databaseReference.child("m").child(databaseReference.push().getKey()).setValue(msg).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                downloadListner.OnDownloadResult(Constants.SEND_MESSAGE, "");

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        downloadListner.OnErrorDownloadResult(Constants.SEND_MESSAGE);
                    }
                });
        databaseReference.child("a").child(Receiver).child(System.currentTimeMillis() + Sender).setValue("u");
    }

    public void displayGesture(String MessageID) {
        databaseReference.child("m").child(MessageID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Ritik", "onDataChange: " + dataSnapshot.getChildrenCount());
                Log.e("Ritik", "onDataChange: " + dataSnapshot.toString());


                String gesture = "gesture_value";
                String sender = "sender value";
                String time = "time_value";

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.getKey().equals("m"))
                        gesture = userSnapshot.getValue().toString();
                    else if (userSnapshot.getKey().equals("s"))
                        sender = userSnapshot.getValue().toString();
                    else if (userSnapshot.getKey().equals("t"))
                        time = userSnapshot.getValue().toString();


                }

                Log.e("Ritik", "onDataChange: " + gesture);
                Intent intent = new Intent(context, OverlayService.class);
                intent.putExtra("gesture", gesture);
                //todo check if should be drawn otherwise drop notification
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                } else {
                    context.startService(intent);
                }
                chatsDatabaseHelper.insertChat(sender, gesture, time, "s");
                //todo update seen on server and database


                //((AppCompatActivity) context).finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void attachMessageListner() {
        //todo this can help with delay, but has to keep running use service for it....
        databaseReference.child("a").child(sanitizeEmail(mUser.getEmail())).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("Ritik", "onChildAdded: " + dataSnapshot.getKey());
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

    public String sanitizeEmail(String Email) {
        return Email.replace('.', '_');
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

package leotik.labs.gesturemessenger.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                .setPersistenceEnabled(true)
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

    public void getUser(final DownloadListner downloadListner, final int ResposeCode, final String Phone, @Nullable final String Status) {
        final UserPOJO userPOJO = new UserPOJO();
        firebaseFirestore.collection(Phone).document("i").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userPOJO.setP(Phone);
                userPOJO.setU(documentSnapshot.get("u") + "");
                userPOJO.setN(documentSnapshot.get("n") + "");
                userPOJO.setE(documentSnapshot.get("e") + "");
                if (Status != null)
                    userPOJO.setS(Status);
                downloadListner.OnDownloadResult(ResposeCode, userPOJO);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                downloadListner.OnErrorDownloadResult(ResposeCode);
            }
        });
    }


    public void getFriends(final DownloadListner downloadListner, final int ResponseCode) {
        final List<UserPOJO> friends = new ArrayList<>();
        firebaseFirestore.collection(mUser.getPhoneNo()).document("f").collection("f").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots.getDocuments().size() < 1) {
                    downloadListner.OnDownloadResult(ResponseCode, null);
                    return;
                }

                final List<DocumentSnapshot> Friendlist = queryDocumentSnapshots.getDocuments();


                for (final DocumentSnapshot entry : Friendlist) {
                    final String Phone = entry.getId();
                    final String Status = entry.get("s") + "";

                    Log.d("Ritik", "working: " + Phone);
                    Log.d("Ritik", "Friend Status: " + Status);
                    databaseHelper.deleteAll();
                    final Task<DocumentSnapshot> documentSnapshotTask = firebaseFirestore.collection(Phone).document("i").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserPOJO userPOJO = new UserPOJO();
                            if (documentSnapshot.getData() != null) {
                                userPOJO.setP(Phone);
                                userPOJO.setE(documentSnapshot.get("e") + "");
                                userPOJO.setN(documentSnapshot.get("n") + "");
                                userPOJO.setU(documentSnapshot.get("u") + "");
                                userPOJO.setS(Status);
                            } else {
                                Crashlytics.log("Blank user " + Phone + "  for " + mUser.getPhoneNo());
                                userPOJO.setN(Phone);
                                userPOJO.setP(Phone);
                                userPOJO.setS(Status);
                            }
                            Log.d("Ritik", "onSuccess: added " + userPOJO.getE() + userPOJO.getU() + userPOJO.getN() + userPOJO.getP());
                            databaseHelper.insertUser(userPOJO, null);
                            if (databaseHelper.getUsersCount() == Friendlist.size())
                                downloadListner.OnDownloadResult(ResponseCode, null);
                        }
                    });


                }

            }
        });
    }


    public void UpdateContacts(final DownloadListner downloadListner) {
        Log.d("Ritik", "UpdateContacts: user" + mUser.getPhoneNo() + " after " + databaseHelper.getlastUserupdate());
        Query newContacts = firebaseFirestore.collection(mUser.getPhoneNo()).document("f").collection("f").whereGreaterThan("t", databaseHelper.getlastUserupdate());
        newContacts.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                Log.d("Ritik", "list of friends: " + documents.toString());
                if (documents.size() == 0) {
                    downloadListner.OnDownloadResult(Constants.REFRESH_CONTACTS, null);
                }
                for (int i = 0; i < documents.size(); i++) {
                    final int j = i + 1;
                    final DocumentSnapshot friendemail = documents.get(i);
                    Log.d("Ritik", "working: " + friendemail.getId());
                    Log.d("Ritik", "details: " + friendemail.getData().toString());
                    final Task<DocumentSnapshot> documentSnapshotTask = firebaseFirestore.collection(friendemail.getId()).document("i").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d("Ritik", "Friend details: " + documentSnapshot.getData().toString());
                            UserPOJO userPOJO = new UserPOJO();
                            userPOJO.setP(friendemail.getId());
                            userPOJO.setE(documentSnapshot.get("e") + "");
                            userPOJO.setN(documentSnapshot.get("n") + "");
                            userPOJO.setU(documentSnapshot.get("u") + "");
                            Log.d("Ritik", "onSuccess: added " + userPOJO.getE() + userPOJO.getU() + userPOJO.getN() + userPOJO.getP());
                            databaseHelper.insertUser(userPOJO, friendemail.get("t").toString());
                            if (j == documents.size())
                                downloadListner.OnDownloadResult(Constants.REFRESH_CONTACTS, null);
                        }
                    });


                }

            }
        });
    }


    public void initUser(String Name, String Email, @Nullable String photoUrl, String Phoneno) {
        Log.d("Ritik", "initUser: " + Name + Email + Phoneno);
        Map<String, String> user = new HashMap<>();
        user.put("n", Name);
        user.put("u", photoUrl);
        user.put("e", Email);
        firebaseFirestore.collection(Phoneno).document("i")
                .set(user);
        Crashlytics.setUserIdentifier(Phoneno);
    }

    public void addFriend(final String Phone, final DownloadListner downloadListner, final int RequestCode) {
        final Map<String, Object> request = new HashMap<>();
        request.put("s", "r");
        firebaseFirestore.collection(Phone).document("f").collection("f").document(mUser.getPhoneNo()).set(request, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final Map<String, Object> sent = new HashMap<>();
                sent.put("s", "s");
                firebaseFirestore.collection(mUser.getPhoneNo()).document("f").collection("f").document(Phone).set(sent, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        downloadListner.OnDownloadResult(RequestCode, Phone);
                    }
                });

            }
        });
    }

    public void acceptFriend(final UserPOJO user, @Nullable final DownloadListner downloadListner, final int RequestCode) {
        final Map<String, Object> request = new HashMap<>();
        request.put("s", "f");
        firebaseFirestore.collection(user.getP()).document("f").collection("f").document(mUser.getPhoneNo()).set(request, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final Map<String, Object> sent = new HashMap<>();
                sent.put("s", "f");
                firebaseFirestore.collection(mUser.getPhoneNo()).document("f").collection("f").document(user.getP()).set(sent, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (downloadListner != null)
                            downloadListner.OnDownloadResult(RequestCode, user);
                        user.setS("f");
                        databaseHelper.updateUser(user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (downloadListner != null)
                            downloadListner.OnErrorDownloadResult(RequestCode);
                    }
                });

            }
        });
    }

    public void deleteFriend(final UserPOJO user, @Nullable final DownloadListner downloadListner, final int RequestCode) {
        firebaseFirestore.collection(user.getP()).document("f").collection("f").document(mUser.getPhoneNo()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseFirestore.collection(mUser.getPhoneNo()).document("f").collection("f").document(user.getP()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (downloadListner != null)
                            downloadListner.OnDownloadResult(RequestCode, user);
                        databaseHelper.deleteUser(user.getP());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (downloadListner != null)
                            downloadListner.OnErrorDownloadResult(RequestCode);
                    }
                });

            }
        });
    }

//    public void attachFriendListListner(final DownloadListner downloadListner) {
//        firebaseFirestore.collection("f").document(sanitizeEmail(mUser.getEmail())).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("Ritik", "Listen failed.", e);
//                    return;
//                }
//
//                if (documentSnapshot != null && documentSnapshot.exists()) {
//                    Log.d("Ritik", "Current data: " + documentSnapshot.getData());
//
//                    Map<String, Object> newUsers = documentSnapshot.getData();
//                    newUsers.keySet().removeAll(databaseHelper.getallEmails());
//                    updateFriendList(newUsers, downloadListner);
//                    Log.d("Ritik", "Added Friends: " + newUsers);
//                } else {
//                    Log.d("Ritik", "Current data: null");
//                }
//            }
//        });
//    }

    public void updateFriendList(Map<String, Object> userstoadd, final DownloadListner downloadListner) {


        for (final Map.Entry<String, Object> entry : userstoadd.entrySet()) {
            firebaseFirestore.collection("u").document(entry.getKey().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getData() != null) {
                            Log.e("Ritik", "onComplete: " + task.getResult().getData());
                            Map<String, Object> user = task.getResult().getData();
                            UserPOJO userPOJO = new UserPOJO(entry.getKey(), user.get("n") + "", user.get("u") + "", user.get("p") + "", null);
                            //todo replace null from actual timestamp on server
                            databaseHelper.insertUser(userPOJO, null);
                        }


                    } else Log.e("Ritik", "onComplete: task failed " + task.getException());

                    downloadListner.OnDownloadResult(Constants.REFRESH_CONTACTS, "");
                }

            });
            // user.put("s",entry.getValue());


        }
        if (downloadListner != null) {

        }

    }

//    public void updateUser(String Email) {
//        Map<String, Object> user = firebaseFirestore.collection("u").document(sanitizeEmail(Email)).get().getResult().getData();
//        //todo update null values
//        databaseHelper.insertUser(Email, user.get("n").toString(), user.get("u").toString(), user.get("p").toString(), null, null);
//
//    }

    public void updatetokenonServer(String token) {
        if (mUser.getFirebaseUser() == null) {
            User.getInstance().destroy();
            mUser = User.getInstance();
        }
        if (mUser == null) {
            Log.e("Ritik", "updatetokenonServer: firebaseuser still null");
            return;
        }
        HashMap<String, Object> tokenvalue = new HashMap<>();
        tokenvalue.put("v", token);
        String userPhone = mUser.getPhoneNo();
        Logging.logDebug(RealtimeDB.class, "Setting FCM Token for " + userPhone);
        if (userPhone != null)
            firebaseFirestore.collection(mUser.getPhoneNo()).document("t").set(tokenvalue);
    }


    public void sendMessage(String Receiver, String Gesture, final DownloadListner downloadListner) {
        //todo break message if exceeds firebase limit
        //todo add oncomplete listener to show loading to sender
        Log.d("Ritik", "sendMessage: " + Receiver);
        String Sender = mUser.getPhoneNo();
        Map<String, String> msg = new HashMap<>();
        msg.put("s", Sender);
        msg.put("r", Receiver);
        msg.put("m", Gesture);
        msg.put("t", (System.currentTimeMillis()) + "");
        databaseReference.child("m").child(databaseReference.push().getKey()).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Ritik", "onComplete: ");
            }
        })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Ritik", "onsuccess: ");
                        downloadListner.OnDownloadResult(Constants.SEND_MESSAGE, "");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Ritik", "onfailiure: ");
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
                UserPOJO user = databaseHelper.getUser(sender);
                if (user == null) {
                    intent.putExtra("sender_name", sender);
                    intent.putExtra("sender_picture", "");
                } else {
                    intent.putExtra("sender_name", user.getN());
                    intent.putExtra("sender_picture", user.getU());
                }
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

'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
admin.firestore().settings({
    timestampsInSnapshots: true
});
var token = 'token_value';
var sender = 'sender_value';
var reciever = 'reciever_value';
var message = 'message_value';
var payload ='payload_value';
 


exports.sendFollowerNotification = functions.database.ref('/m/{messageid}')
    .onCreate((snapshot, context) => {
        console.log('v16');
         message = context.params.messageid;
        console.log('Message Id:', message);

         reciever = snapshot.val().r;
        console.log('Message reciever: ', reciever);
         sender = snapshot.val().s;
        console.log('Message sender: ', sender);


        var db = admin.firestore();
        var tokenRef = db.collection('t').doc(reciever);
        var getDoc = tokenRef.get()
            .then(doc => {
                if (!doc.exists) {
                    console.log('Token doesnt exist ');
                } else {
                    token = doc.data().v;
                    console.log('Token data:', token);
                    payload = {
                        data: {
                            id: `${message}`,
                            sender: `${sender}`
                        }
               
                    };




                }
                console.log('End Then');
                return admin.messaging().sendToDevice(token,payload);

            })
            .catch(err => {
                console.log('Error getting token', err);
            }).then((response) => {
                // Response is a message ID string.
                return console.log('Successfully sent message:', response);
            })
            .catch((error) => {
                console.log('Error sending message:', error);
            });



    })

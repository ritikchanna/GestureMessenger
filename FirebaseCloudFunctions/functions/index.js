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
var db = admin.firestore();
 


exports.sendFollowerNotification = functions.database.ref('/m/{messageid}')
    .onCreate((snapshot, context) => {
        console.log('v19');
         message = context.params.messageid;
        console.log('Message Id:', message);

         reciever = snapshot.val().r;
        console.log('Message reciever: ', reciever);
         sender = snapshot.val().s;
        console.log('Message sender: ', sender);
                    payload = {
                        data: {
                            id: `${message}`,
                            sender: `${sender}`
                        }
               
                    };
        console.log('Payload Created');


        
        var tokenRef = db.collection(reciever).doc('t');
        console.log('Fetching Token');
        tokenRef.get()
            .then(doc => {
                console.log('Fetching Token started'); 
                if (!doc.exists) {
                    console.log('Token doesnt exist ');
                } else {
                    token = doc.data().v;
                    console.log('Token data:', token);

                }
                console.log('End Then');
                return token;

            })
            .catch(err => {
                console.log('Error getting token', err);
            }).then((token) => {
                console.log('Sending FCM now');
                admin.messaging().sendToDevice(token,payload);
                return console.log('Successfully sent message:');
            })
            .catch((error) => {
                console.log('Error sending message:', error);
            });



    })

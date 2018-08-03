'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
admin.firestore().settings({
    timestampsInSnapshots: true
});




exports.sendFollowerNotification = functions.database.ref('/m/{messageid}')
    .onCreate((snapshot, context) => {
        console.log('v12');
        const message = context.params.messageid;
        console.log('Message Id:', message);

        const reciever = snapshot.val().r;
        console.log('Message reciever: ', reciever);
        const sender = snapshot.val().s;
        console.log('Message sender: ', sender);


        var db = admin.firestore();
        var tokenRef = db.collection('t').doc(reciever);
        var getDoc = tokenRef.get()
            .then(doc => {
                if (!doc.exists) {
                    console.log('Token doesnt exist ');
                } else {
                    const token = doc.data().v;
                    console.log('Token data:', token);
                    var message = {
                        data: {
                            id: message,
                            sender: sender
                        },
                        token: token
                    };




                }
                console.log('End Then');
                return admin.messaging().send(message);

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

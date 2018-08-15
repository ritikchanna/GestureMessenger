const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
admin.firestore().settings({
  timestampsInSnapshots: true
});
var db = admin.firestore();

function debugLogger(msg) {
  // comment out to disable logging
  console.log(msg);
}

function errLogger(err) {
  console.log(err);
}

exports.sendFollowerNotification =
  functions.database.ref('/m/{messageid}')
    .onCreate((snapshot, context) => {
      debugLogger('v23_stackoverflow');
      const message = context.params.messageid;
      debugLogger(`Message Id: ${message}`);

      const reciever = snapshot.val().r;
      debugLogger(`Message reciever: ${reciever}`);
      const sender = snapshot.val().s;
      debugLogger(`Message sender: ${sender}`);
      const payload = {
        data: {
          id: `${message}`,
          sender: `${sender}`
        }
      };
      debugLogger('Payload Created');

      let tokenRef = db.collection(reciever).doc('t');
      debugLogger('Fetching Token');
      return tokenRef.get()
        .then(doc => {
          debugLogger('Fetching Token started');
          if (!doc.exists)
            throw new Error("Token doesnt exist");

          let token = doc.data().v;
          debugLogger(`Token data: ${token}`);
          return token;
        })
        .then(token => {
          debugLogger('Sending FCM now');
          return admin.messaging().sendToDevice(token, payload);
        })
        .then(() => {
          debugLogger('Successfully sent message!');
          return null;
        })
        .catch(err => {
          errLogger(err);
        })
    })

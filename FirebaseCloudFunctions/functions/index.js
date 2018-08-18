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

exports.sendMessageNotification =
  functions.database.ref('/m/{messageid}')
    .onCreate((snapshot, context) => {
      debugLogger('sendMessageNotification v23');
      const message = context.params.messageid;
      debugLogger(`Message Id: ${message}`);

      const reciever = snapshot.val().r;
      debugLogger(`Message reciever: ${reciever}`);
      const sender = snapshot.val().s;
      debugLogger(`Message sender: ${sender}`);
      const payload = {
        data: {
          type: `message`,
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





exports.sendFriendNotification =
  functions.firestore.document('{user}/f/{status}/{user2}')
    .onWrite((change, context) => {
      debugLogger('sendFriendNotification v03');
      const user = context.params.user;
      debugLogger(`Primary User Id: ${user}`);
      const status = context.params.status;
      debugLogger(`Status of Request: ${status}`);
      const user2 = context.params.user2;
      debugLogger(`Secondary Used Id: ${user2}`);
      const newValue = change.after.exists ? change.after.data() : null;
     // const newValue = change.after.data();
      var updatetime = 0;
      if(newValue !== null)
            updatetime = newValue.t;
       else
            updatetime = '-1';
    debugLogger(`Update time value: ${updatetime}`);


      const payload = {
        data: {
          type: `friend`,
          user: `${user}`,
          user2: `${user2}`,
          status: `${status}`,
          time: `${updatetime}`
        }
      };
      debugLogger('Payload Created');

      let tokenRef = db.collection(user).doc('t');
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

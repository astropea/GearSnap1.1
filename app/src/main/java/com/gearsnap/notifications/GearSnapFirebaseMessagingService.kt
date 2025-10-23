package com.gearsnap.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class GearSnapFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        // TODO: envoyer le token au backend
    }
    override fun onMessageReceived(message: RemoteMessage) {
        // TODO: afficher une notification locale
    }
}
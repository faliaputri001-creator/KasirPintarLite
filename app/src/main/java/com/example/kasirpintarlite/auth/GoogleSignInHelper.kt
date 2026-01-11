package com.example.kasirpintarlite.auth

import android.app.Activity
import android.content.Intent
import com.example.kasirpintarlite.R
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleSignInHelper(private val activity: Activity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(activity, gso)
    }

    fun signIn() {
        val intent = googleSignInClient.signInIntent
        activity.startActivityForResult(intent, RC_SIGN_IN)
    }

    fun handleResult(data: Intent?, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it.message ?: "Login gagal") }

        } catch (e: Exception) {
            onFailure(e.message ?: "Google Sign-In gagal")
        }
    }

    companion object {
        const val RC_SIGN_IN = 1001
    }
}

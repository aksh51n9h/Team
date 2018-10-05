package com.asq.dev.team

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {
    private val TAG = "SignInActivity"
    private val RC_SIGN_IN = 9001
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        auth = FirebaseAuth.getInstance()

        databaseReference = FirebaseDatabase.getInstance().reference

        sign_in.setOnClickListener {
            signIn()
        }
    }

    private fun createNewuser(account: GoogleSignInAccount) {

    }

    private fun hasAccount(account: GoogleSignInAccount): Boolean {
        var hasAccount = false
        databaseReference.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                //No users
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentUserId = account.id ?: "100654"
                val userIds = dataSnapshot.children

                for (userId in userIds) {
                    if (currentUserId == userId.key) {
                        hasAccount = !hasAccount
                        break
                    }
                }
            }
        })

        return hasAccount
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign-In was successful, authenticate with Firebase
                val account = result.signInAccount
                if (account != null) {
//                    val hasAccount = hasAccount(account)
//                    if (hasAccount)
                    firebaseAuthWithGoogle(account)
//                    else
//                        createNewuser(account)
                }
            } else {
                // Google Sign-In failed
//                Log.e(TAG, "Google Sign-In failed.")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    //                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
                    if (!task.isSuccessful) {
//                        Log.w(TAG, "signInWithCredential", task.exception)
                        Toast.makeText(this@SignInActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    } else {
                        val user = User(acct.displayName ?: "displayName", acct.photoUrl.toString())

                        databaseReference.child("users").child(acct.id ?: "0").setValue(user)
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                    }
                }
    }
}

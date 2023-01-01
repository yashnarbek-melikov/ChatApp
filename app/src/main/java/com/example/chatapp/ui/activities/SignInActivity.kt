package com.example.chatapp.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivitySignInBinding
import com.example.chatapp.models.Account
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import java.util.*

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    private val TAG = "SignInActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener {
            signIn()
        }

        binding.signOut.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                }
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val account = Account(
                        user?.displayName,
                        user?.email,
                        user?.uid,
                        user?.photoUrl.toString(),
                        "online",
                        Date()
                    )
                    reference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var isHave = false
                            val children = snapshot.children
                            for (child in children) {
                                val value = child.getValue(Account::class.java)
                                if (value?.uid == user?.uid) {
                                    isHave = true
                                    break
                                }
                            }
                            if (isHave) {
                                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                reference.child(user?.uid ?: "").setValue(account)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            val intent = Intent(
                                                this@SignInActivity,
                                                MainActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if(GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
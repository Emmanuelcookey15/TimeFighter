package com.cookey.sandra.foodplug.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.cookey.sandra.foodplug.R
import com.cookey.sandra.foodplug.utils.loadState
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val TAG = "SignUpActivity"

    private lateinit var signUpButton: SignInButton
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 1

    var name: String? = null
    var email:String? = null
    var idToken: String? = null


    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.title = HtmlCompat.fromHtml("<font color='#000000'>Sign up</font>",
            HtmlCompat.FROM_HTML_MODE_LEGACY)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_chevron_left_black_24dp)
        supportActionBar?.setHomeAsUpIndicator(backArrow)

        auth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener {
            val user = auth.currentUser

            if (user != null) {
                // User is signed in
                // you could place other firebase code
                //logic to save the user details to Firebase
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid);
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id)) //you can also use R.string.default_web_client_id
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signUpButton = findViewById(R.id.sign_up_button)
        signUpButton.setOnClickListener{
            when (it.id) {
                R.id.sign_up_button -> signIn()
            }
        }


        register_sign_up_btn.setOnClickListener {


            when{
                TextUtils.isEmpty(email_edit_text_sign_up.text) -> {
                    email_edit_text_sign_up.error = "Please enter your Email"
                }
                TextUtils.isEmpty(password_edit_text_sign_up.text) -> {
                    password_edit_text_sign_up.error = "Please enter password"
                }
                TextUtils.isEmpty(name_edit_text_sign_up.text) -> {
                    name_edit_text_sign_up.error = "Please enter your full name"
                }
                (password_edit_text_sign_up.text.toString().length < 6) ->{
                    password_edit_text_sign_up.error = "Password cannot bre less than 6 characters"
                }
                else -> {

                    register_sign_up_btn.loadState(0, "Please wait...")

                    val email = email_edit_text_sign_up.text.toString()

                    val password = password_edit_text_sign_up.text.toString()

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()
                            register_sign_up_btn.loadState(1, getString(R.string.register))
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                        }else {
                            Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
                            register_sign_up_btn.loadState(1, getString(R.string.register))
                        }
                    })
                }
            }


        }

    }


    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result  = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: Task<GoogleSignInAccount>?) {

        try {
            val account = result?.getResult(ApiException::class.java)
            idToken = account!!.idToken
            name = account.displayName
            email = account.email
            // you can store user data to SharedPreference
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuthWithGoogle(credential)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.e(TAG, "Login Unsuccessful. $result")
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
        }

    }

    private fun firebaseAuthWithGoogle(credential: AuthCredential) {

        auth.signInWithCredential(credential).addOnCompleteListener(this, OnCompleteListener { task ->

            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
            if(task.isSuccessful){
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                gotoProfile()
            }else{
                Log.w(TAG, "signInWithCredential" + task.exception?.message);
                task.exception?.printStackTrace()
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun gotoProfile() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }



    override fun onStart() {
        super.onStart()
        if (authStateListener != null){
            FirebaseAuth.getInstance().signOut()
        }
        auth.addAuthStateListener(authStateListener)

    }

    override fun onStop() {
        super.onStop()
        if (authStateListener != null){
            auth.removeAuthStateListener(authStateListener)
        }

    }


}

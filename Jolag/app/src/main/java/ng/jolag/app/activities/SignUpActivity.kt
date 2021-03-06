package ng.jolag.app.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
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
import ng.jolag.app.MainActivity
import ng.jolag.app.R
import ng.jolag.app.utils.loadState

class SignUpActivity : AppCompatActivity() {

    private val TAG = "SignUpActivity"


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

                    val username = name_edit_text_sign_up

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




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }




}

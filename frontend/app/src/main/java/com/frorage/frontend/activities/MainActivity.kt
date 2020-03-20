package com.frorage.frontend.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.frorage.frontend.R
import com.frorage.frontend.storage.SharedPrefMenager
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Class which manage buttons and implements logic connected with start window activity.
 *
 * @property AppCompatActivity extends LoginActivity with methods which helps to add functionality to the GUI
 */

class MainActivity : AppCompatActivity() {


/**
     * Override function onStart which at the very beginning has to check if user is logged in and if yes it put user to MyItemsActivity.
     */

    override fun onStart() {
        super.onStart()

        if (SharedPrefMenager.getInstance(this).isLoggedIn) {
            val intent = Intent(applicationContext, GeneralActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    /**
     * Override function onCreate - it initialize all views, listeners and objects when the activity started.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLoginButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        mainRegisterButton.setOnClickListener{
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

}

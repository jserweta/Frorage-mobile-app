package com.frorage.frontend.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.frorage.frontend.R

class MyItemsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_items)



    }



   /* override fun onStart() {
        super.onStart()

        if(!SharedPrefMenager.getInstance(this).isLoggedIn){
            val intent = Intent(this@MyItemsActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }*/
}

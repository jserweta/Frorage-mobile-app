package com.frorage.frontend.fragments


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner

import com.frorage.frontend.R
import com.frorage.frontend.activities.GeneralActivity
import com.frorage.frontend.activities.KitchenCreatorActivity
import com.frorage.frontend.activities.LoginActivity
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * A simple [Fragment] subclass.
 */
class MenuFragment (private val applicationContext: Context): Fragment(),View.OnClickListener {

    private var btnKitchenManager: TextView? = null
    private var btnUserManager: TextView? = null
    private var btnLogout: TextView? = null
    private var btnBack: ImageButton? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_menu, container, false)

        initViews(rootView)

        initListeners()

        return rootView
    }

    /**
     *  Override function onClick which perform action when user clicked certain button.
     */
    override fun onClick(v: View) {
        when (v.id){
            R.id.btn_kitchen_manager -> {
                replaceFragment(KitchenManagerFragment(applicationContext))
            }
            R.id.btn_user_manager ->{
                replaceFragment(UserManagerFragment(applicationContext))
            }
            R.id.btn_logout -> {
                SharedPrefMenager.getInstance(applicationContext).clearUserId()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_back ->{
                val intent = Intent(applicationContext, GeneralActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    /**
     * Function which initialize all variables which are needed to add functionality to all GUI elements.
     */
    private fun initViews(rootView: View) {

        btnKitchenManager = rootView.findViewById<View>(R.id.btn_kitchen_manager) as TextView
        btnUserManager = rootView.findViewById<View>(R.id.btn_user_manager) as TextView
        btnLogout = rootView.findViewById<View>(R.id.btn_logout) as TextView

        btnBack = rootView.findViewById<View>(R.id.btn_back) as ImageButton
    }

    /**
     * Function which initialize button listeners for resending e-mail with confirmation code and for confirming your account.
     */
    private fun initListeners() {
        btnLogout!!.setOnClickListener(this)
        btnUserManager!!.setOnClickListener(this)
        btnBack!!.setOnClickListener(this)
        btnKitchenManager!!.setOnClickListener(this)
    }

}

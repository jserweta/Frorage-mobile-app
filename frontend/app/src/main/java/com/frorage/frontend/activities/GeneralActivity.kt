package com.frorage.frontend.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.frorage.frontend.R
import com.frorage.frontend.fragments.*
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_general.*


class GeneralActivity : AppCompatActivity(), View.OnClickListener{

    private var kitchenNameTextView: TextView? = null
    private var btnMenu: ImageButton? = null
    private var drawerLayout: DrawerLayout? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId){
            R.id.my_items -> {
                println("my items pressed")
                replaceFragment(ProductListFragment(applicationContext))
                return@OnNavigationItemSelectedListener true
            }
            R.id.recipes -> {
                println("recipes pressed")
                replaceFragment(RecipesFragment(applicationContext))
                return@OnNavigationItemSelectedListener true
            }
            R.id.shopping_list -> {
                println("shopping list pressed")
                replaceFragment(ShoppingListFragment(applicationContext))
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

     override fun onStart() {
        super.onStart()

        if(!SharedPrefMenager.getInstance(this).isLoggedIn){
            val intent = Intent(this@GeneralActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    /*override fun onDestroy() {
        super.onDestroy()
        SharedPrefMenager.getInstance(applicationContext).clearUserId()
    }*/


    /**
     * Override function onCreate - it initialize all views, listeners and objects when the activity started.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general)

        bottom_nav.selectedItemId = R.id.my_items
        bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        replaceFragment(ProductListFragment(applicationContext))


        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()


        val sharedPreferences = SharedPrefMenager.getInstance(applicationContext)
        val kitchenData = sharedPreferences.activatedKitchenData
        val kitchenName = kitchenData.kitchenName
        kitchenNameTextView!!.text = "$kitchenName Kitchen"
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
    /**
     *  Override function onClick which perform action when user clicked certain button.
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_menu -> {
                replaceFragment(MenuFragment(applicationContext))
            }
        }
    }

    /**
     * Function which initialize all variables which are needed to add functionality to all GUI elements.
     */
    private fun initViews() {
        btnMenu = findViewById<View>(R.id.btn_menu) as ImageButton
        kitchenNameTextView = findViewById<View>(R.id.kitchen_name_text) as TextView
        drawerLayout = findViewById(R.id.drawer_general_layout)
    }

    /**
     * Function which initialize main and bottom menu button listeners.
     */
    private fun initListeners() {
        btnMenu!!.setOnClickListener(this)
    }

    /**
     * Function which initialize object of InputValidation class.
     */
    private fun initObjects() {

    }
}

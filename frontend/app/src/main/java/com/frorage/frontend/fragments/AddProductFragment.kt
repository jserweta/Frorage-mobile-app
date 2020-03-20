package com.frorage.frontend.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.frorage.frontend.R
import com.frorage.frontend.adapters.ProductListAdapter
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_add_product_to_buy.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Double.parseDouble

class AddProductFragment(
    private val mCtx: Context,
    private val adapter: ProductListAdapter
) : Fragment()  {


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_add_product_to_buy, container, false)

        val btnBack = rootView.findViewById<View>(R.id.btnBack) as Button
        val btnAddProduct = rootView.findViewById<View>(R.id.btnAddProduct) as Button

        val productName = rootView.findViewById<View>(R.id.productNameIn) as TextInputEditText
        val amount = rootView.findViewById<View>(R.id.amountIn) as TextInputEditText
        val unit = rootView.findViewById<View>(R.id.unitIn) as TextInputEditText

        val header = SharedPrefMenager.getInstance(mCtx).token
        val kitchenId = SharedPrefMenager.getInstance(mCtx).activatedKitchenData.kitchenId

        btnBack.setOnClickListener {
            val fragment = ProductListFragment(mCtx)
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)

            fragmentTransaction.commit()
        }

        btnAddProduct.setOnClickListener{
            val productNameText = productName.text.toString().trim()

            if(productNameText.isNotEmpty()) {
                var numeric = true

                val amountTextString = amount.text.toString().trim()

                numeric = amountTextString.matches("-?\\d+(\\.\\d+)?".toRegex())
                var amountText = 1f

                if (numeric && amountTextString.isNotEmpty())
                    amountText = amountTextString.toFloat()

                val unitText = unit.text.toString().trim()

                addProduct(header, Model.FullProduct(kitchenId, productNameText, amountText, unitText, toBuy = false, favourite = false, runningOut = false, expirationDate = null))
            }
        }

        return rootView
    }

    private fun addProduct(header: String, addFullProductRequest: Model.FullProduct) {
        val call = RetrofitClient.userApi.addProduct(
            header,
            addFullProductRequest
        )


        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val code = response.code()

                when (code) {
                    201 -> {
                        adapter.notifyDataSetChanged()
                        Toast.makeText(context, "Product added!", Toast.LENGTH_LONG).show()
                    }

                    else -> {
                        Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }

                // clear
                productNameIn.text?.clear()
                amountIn.text?.clear()
                unitIn.text?.clear()
            }

        })

        return
    }
}
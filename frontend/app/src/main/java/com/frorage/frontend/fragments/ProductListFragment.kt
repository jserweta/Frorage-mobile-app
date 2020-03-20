package com.frorage.frontend.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.frorage.frontend.R
import com.frorage.frontend.adapters.ProductListAdapter
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList


class ProductListFragment constructor(private val mCtx: Context) : Fragment()   {

    private lateinit var listView: ListView
    private var addButton: Button? = null
    private var azButton: Button? = null
    private var favouriteButton: Button? = null
    private var runningOutButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_my_items, container, false)

        addButton = rootView.findViewById<View>(R.id.addButton) as Button
        azButton = rootView.findViewById<View>(R.id.az) as Button
        favouriteButton = rootView.findViewById<View>(R.id.favourite) as Button
        runningOutButton = rootView.findViewById<View>(R.id.runningOut) as Button

        val header = SharedPrefMenager.getInstance(mCtx).token
        val kitchenId = SharedPrefMenager.getInstance(mCtx).activatedKitchenData.kitchenId


        val productList : ArrayList<Model.FullProductWithId> = ArrayList()


        val adapter = ProductListAdapter(this.activity!!, productList)
        listView = rootView.findViewById(R.id.productList)
        listView.adapter = adapter

        getProductList(header, kitchenId, productList,adapter) // show data


        listView.setOnItemClickListener { parent, view, position, id ->
            productList[position].runningOut = !productList[position].runningOut

            val item = productList[position]
            val product: Model.FullProductWithId = Model.FullProductWithId(item.productId,item.kitchenId,item.productName,item.amount,item.unit,item.toBuy,item.favourite,item.runningOut,item.expirationDate)

            updateProduct(header,product)
            adapter.notifyDataSetChanged()
        }

        addButton!!.setOnClickListener {
            val fragment = AddProductFragment(mCtx, adapter)
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        runningOutButton!!.setOnClickListener{
            adapter.sortRunningOut()
        }

        favouriteButton!!.setOnClickListener{
            adapter.sortFavourite()
        }

        azButton!!.setOnClickListener{
            adapter.sortAZ()
        }


        return rootView
    }

    private fun getProductList(header: String, kitchenId: Int, productList: ArrayList<Model.FullProductWithId> , adapter: ProductListAdapter): ArrayList<Model.FullProductWithId>? {
        val call = RetrofitClient.userApi.getProductList(
            header,
            kitchenId
        )

        var result : ArrayList<Model.FullProductWithId>? = null

        call.enqueue(object : Callback<ArrayList<Model.FullProductWithId>> {
            override fun onFailure(
                call: Call<ArrayList<Model.FullProductWithId>>,
                t: Throwable
            ) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Model.FullProductWithId>>,
                response: Response<ArrayList<Model.FullProductWithId>>
            ) {
                val code = response.code()
                val body = response.body()

                when (code) {
                    200 -> {
                        result = body
                        productList.addAll(result!!)
                        adapter.notifyDataSetChanged()
                    }
                    else -> {
                        Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })

        return result
    }

    private fun updateProduct(header: String, product: Model.FullProductWithId){
        val call = RetrofitClient.userApi.updateProduct(
            header,
            product
        )

        call.enqueue(object : Callback<Unit> {
            override fun onFailure(
                call: Call<Unit>,
                t: Throwable
            ) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>
            ) {
                val code = response.code()

                when (code) {
                    201 -> {

                    }
                    else -> {
                        Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
package com.frorage.frontend.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frorage.frontend.R
import com.frorage.frontend.activities.GeneralActivity
import com.frorage.frontend.adapters.ShoppingListAdapter
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShoppingListFragment  constructor(private val mCtx: Context) : Fragment()   {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var dataset: ArrayList<Model.ShoppingListProduct>

    private var addButton: Button? = null
    private var buyButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_shopping_list, container, false)

        val header = SharedPrefMenager.getInstance(mCtx).token
        val kitchenId = SharedPrefMenager.getInstance(mCtx).activatedKitchenData.kitchenId



        recyclerView = rootView.findViewById(R.id.recyclerView)

        layoutManager = LinearLayoutManager(activity)

        setRecyclerViewLayoutManager()

        // Get Data
        getShoppingList(header,kitchenId)

        addButton = rootView.findViewById<View>(R.id.addButton) as Button
        buyButton = rootView.findViewById<View>(R.id.buyButton) as Button


        addButton!!.setOnClickListener {
            val fragment = AddProductToBuyFragment(mCtx, recyclerView.adapter)
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        buyButton!!.setOnClickListener{

            val list : ArrayList<Int> = ArrayList()
            dataset.filter{x -> x.inCart }.map { x -> x.productId }.toCollection(list)

            buyProducts(header,list)
        }

        return rootView
    }

    private fun setRecyclerViewLayoutManager() {
        var scrollPosition = 0

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.layoutManager != null) {
            scrollPosition = (recyclerView.layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition()
        }

        layoutManager = LinearLayoutManager(activity)

        with(recyclerView) {
            layoutManager = this@ShoppingListFragment.layoutManager
            scrollToPosition(scrollPosition)
        }
    }

    private fun getShoppingList(header: String, kitchenId: Int): ArrayList<Model.ShoppingListResponse>? {
        val call = RetrofitClient.userApi.getShoppingList(
            header,
            kitchenId
        )

        var result : ArrayList<Model.ShoppingListResponse>? = null

        call.enqueue(object : Callback<ArrayList<Model.ShoppingListResponse>> {
            override fun onFailure(
                call: Call<ArrayList<Model.ShoppingListResponse>>,
                t: Throwable
            ) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Model.ShoppingListResponse>>,
                response: Response<ArrayList<Model.ShoppingListResponse>>
            ) {
                val code = response.code()
                val body = response.body()

                when (code) {
                    200 -> {
                        result = body
                    }
                    else -> {
                        Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }

                dataset = ArrayList()

                for (i in result!!) { dataset.add(i.toShoppingListProduct()) }

                val array = toArray<Model.ShoppingListProduct>(dataset)

                // Set ShoppingListAdapter as the adapter for RecyclerView.
                recyclerView.adapter = ShoppingListAdapter(array)
            }

        })

        return result
    }

    private fun buyProducts(header: String, listOfProductId: ArrayList<Int>) {
        val call = RetrofitClient.userApi.setToBuy(
            header,
            false,
            listOfProductId
        )


        call.enqueue(object : Callback<Model.GeneralResponse> {
            override fun onFailure(call: Call<Model.GeneralResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<Model.GeneralResponse>,
                response: Response<Model.GeneralResponse>
            ) {
                val code = response.code()

                when (code) {
                    200 -> {
                        replaceFragment()
                        Toast.makeText(context, "products bought!", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    inline fun <reified T> toArray(list: List<*>): Array<T> {
        return (list as List<T>).toTypedArray()
    }

    private fun replaceFragment(){
        val fragment = ProductListFragment(mCtx)
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    fun Model.ShoppingListResponse.toShoppingListProduct() = Model.ShoppingListProduct(
        productId = productId,
        productName = productName,
        quantity = amount,
        unit = unit,
        inCart = false
    )
}

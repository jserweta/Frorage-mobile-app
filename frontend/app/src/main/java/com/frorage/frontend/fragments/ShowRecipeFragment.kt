package com.frorage.frontend.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.frorage.frontend.R
import com.frorage.frontend.adapters.ProductListAdapter
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.absoluteValue

class ShowRecipeFragment(private val mCtx: Context, val recipe: Model.Recipe) : Fragment() {

    var ingredientsList = ArrayList<Model.Ingredient>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_show_recipe, container, false)

        // views
        val btnBack = rootView.findViewById<View>(R.id.btnBack) as Button
        val btnAddProduct = rootView.findViewById<View>(R.id.btnAddProduct) as TextView
        val recipeName = rootView.findViewById<View>(R.id.recipeName) as TextView
        val ingredients = rootView.findViewById<View>(R.id.ingredients) as TextView
        val description = rootView.findViewById<View>(R.id.description) as TextView

        // parameters
        val header = SharedPrefMenager.getInstance(mCtx).token
        val kitchenId = SharedPrefMenager.getInstance(mCtx).activatedKitchenData.kitchenId

        // setting
        recipeName.text = recipe.recipeName
        description.text = recipe.recipeDescription

        getIngredients(header, recipe.recipeId, ingredients)

        // listeners
        btnBack.setOnClickListener {
            val fragment = RecipesFragment(mCtx)
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.commit()
        }

        btnAddProduct.setOnClickListener {
            // finalize recipe - remove proper amount of products from our products
            finalize(header,kitchenId)

            // move to previous fragment
            val fragment = RecipesFragment(mCtx)
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.commit()
        }

        return rootView
    }

    private fun getIngredients(header: String, recipeId: Int, ingredients: TextView){
        val call = RetrofitClient.userApi.getIngredients(
            header,
            recipeId
        )

        call.enqueue(object : Callback<ArrayList<Model.Ingredient>> {
            override fun onFailure(
                call: Call<ArrayList<Model.Ingredient>>,
                t: Throwable
            ) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Model.Ingredient>>,
                response: Response<ArrayList<Model.Ingredient>>
            ) {
                val code = response.code()
                val body = response.body()

                when (code) {
                    200 -> {
                        ingredientsList.addAll(body!!)
                        var ingr = ""
                        ingredientsList.forEach{x -> ingr += (""+ if(x.amount.absoluteValue % 1.0 >= 0.005 ){ x.amount }else{ x.amount.toInt()} + x.unit + " " + x.ingredientName +"\n")}
                        ingredients.text = ingr
                    }
                    else -> {
                        Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun finalize(header: String, kitchenId: Int) {
        val call = RetrofitClient.userApi.getProductList(
            header,
            kitchenId
        )


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
                        ingredientsList.forEach{x ->
                            var tmpProduct = body?.find{ y -> y.productId == x.productId }

                            val resAmount = tmpProduct?.amount?.minus(x.amount)
                            tmpProduct?.amount = resAmount!!
                            if (tmpProduct != null) {
                                finalizeRecipe(header,tmpProduct)
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })
    }

    private fun finalizeRecipe(header: String, product: Model.FullProductWithId){
        val call = RetrofitClient.userApi.littleProductUpdate(
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
                println(code)

                when (code) {
                    200 -> {

                    }
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
package com.frorage.frontend.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.frorage.frontend.R
import com.frorage.frontend.adapters.IngredientListAdapter
import com.frorage.frontend.adapters.ProductListAdapter
import com.frorage.frontend.adapters.RecipeListAdapter
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_add_product_to_buy.*
import kotlinx.android.synthetic.main.fragment_add_product_to_buy.amountIn
import kotlinx.android.synthetic.main.fragment_add_product_to_buy.productNameIn
import kotlinx.android.synthetic.main.fragment_add_product_to_buy.unitIn
import kotlinx.android.synthetic.main.fragment_add_recipe.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRecipeFragment(
    private val mCtx: Context,
    private val adapter: RecipeListAdapter
) : Fragment()  {

    private lateinit var listView: ListView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        val btnBack = rootView.findViewById<View>(R.id.btnBack) as Button
        val btnAddRecipe = rootView.findViewById<View>(R.id.btnAddRecipe) as Button

        // recipe
        val recipeName = rootView.findViewById<View>(R.id.recipeNameIn) as TextInputEditText
        val description = rootView.findViewById<View>(R.id.descriptionIn) as EditText

        // ingredient
        val addButton = rootView.findViewById<View>(R.id.addButton) as Button
        val productNameIn = rootView.findViewById<View>(R.id.productNameIn) as TextInputEditText
        val amountIn = rootView.findViewById<View>(R.id.amountIn) as TextInputEditText
        val unitIn = rootView.findViewById<View>(R.id.unitIn) as TextInputEditText

        val ingredientList : ArrayList<Model.IngredientRequest> = ArrayList()
        val adapter1 = IngredientListAdapter(this.activity!!, ingredientList)

        listView = rootView.findViewById(R.id.ingredientList)
        listView.adapter = adapter1


        val header = SharedPrefMenager.getInstance(mCtx).token
        val kitchenId = SharedPrefMenager.getInstance(mCtx).activatedKitchenData.kitchenId

        btnBack.setOnClickListener {
            val fragment = RecipesFragment(mCtx)
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)

            fragmentTransaction.commit()
        }

        addButton.setOnClickListener{
            var numeric = true

            val amountTextString = amountIn.text.toString().trim()

            numeric = amountTextString.matches("-?\\d+(\\.\\d+)?".toRegex())
            var amountText = 1f

            if (numeric && amountTextString.isNotEmpty())
                amountText = amountTextString.toFloat()

            ingredientList.add(Model.IngredientRequest(amountText, productNameIn.text.toString(), 0 ,unitIn.text.toString()))
            adapter1.notifyDataSetChanged()

            amountIn.text?.clear()
            unitIn.text?.clear()
            productNameIn.text?.clear()
        }

        btnAddRecipe.setOnClickListener{

            // add recipe
            val recipeAdd = Model.RecipeAdd(kitchenId, description.text.toString(), recipeName.text.toString())

            addRecipe(kitchenId, header, adapter, recipeAdd, ingredientList)
        }

        return rootView
    }

    private fun addRecipe(kitchenId: Int, header: String, adapter: RecipeListAdapter, recipeAdd: Model.RecipeAdd, ingredientList: ArrayList<Model.IngredientRequest>) {
        val call = RetrofitClient.userApi.addRecipe(
            header,
            recipeAdd
        )


        call.enqueue(object : Callback<Model.Recipe> {
            override fun onFailure(call: Call<Model.Recipe>, t: Throwable) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Model.Recipe>, response: Response<Model.Recipe>) {
                val code = response.code()
                val body = response.body()

                val recipeId = body?.recipeId

                when (code) {
                    201 -> {
                        adapter.items.add(Model.Recipe(kitchenId, recipeAdd.recipeDescription, recipeId!!, recipeAdd.recipeName ))
                        adapter.notifyDataSetChanged()

                        ingredientList.forEach{x -> x.recipeId=recipeId!!} // change ingredients recipeId

                        ingredientList.forEach{x -> addIngredient(header, kitchenId, x)} // add ingredients to database

                        Toast.makeText(context, "Recipe added!", Toast.LENGTH_LONG).show()
                    }

                    else -> {
                        Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }

                // clear
                recipeNameIn.text?.clear()
                descriptionIn.text?.clear()
                amountIn.text?.clear()
                unitIn.text?.clear()
                productNameIn.text?.clear()
                ingredientList.clear()
            }
        })
    }

    private fun addIngredient(header: String, kitchen_id: Int, ingredient: Model.IngredientRequest) {
        val call = RetrofitClient.userApi.addIngredient(
            header,
            kitchen_id,
            ingredient
        )


        call.enqueue(object : Callback<Model.GeneralResponse> {
            override fun onFailure(call: Call<Model.GeneralResponse>, t: Throwable) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Model.GeneralResponse>, response: Response<Model.GeneralResponse>) {
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
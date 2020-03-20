package com.frorage.frontend.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.frorage.frontend.R
import com.frorage.frontend.adapters.RecipeListAdapter
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList


class RecipesFragment constructor(private val mCtx: Context) : Fragment()   {

    private lateinit var listView: ListView
    private var addButton: Button? = null
    private var azButton: Button? = null
    private var possibleButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_recipes, container, false)

        addButton = rootView.findViewById<View>(R.id.addButton) as Button
        azButton = rootView.findViewById<View>(R.id.az) as Button
        possibleButton = rootView.findViewById<View>(R.id.possible) as Button

        val header = SharedPrefMenager.getInstance(mCtx).token
        val kitchenId = SharedPrefMenager.getInstance(mCtx).activatedKitchenData.kitchenId

        val recipeList : ArrayList<Model.Recipe> = ArrayList()

        val adapter = RecipeListAdapter(this.activity!!, recipeList)
        listView = rootView.findViewById(R.id.recipeList)
        listView.adapter = adapter

        getRecipeList(header, kitchenId, recipeList, adapter) // show data


        listView.setOnItemClickListener { parent, view, position, id ->
            val fragment = ShowRecipeFragment(mCtx,recipeList[position])
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        addButton!!.setOnClickListener {
            val fragment = AddRecipeFragment(mCtx, adapter)
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        possibleButton!!.setOnClickListener{
            adapter.sortPossible()
        }

        azButton!!.setOnClickListener{
            adapter.sortAZ()
        }


        return rootView
    }

    private fun getRecipeList(header: String, kitchenId: Int, recipeList: ArrayList<Model.Recipe> , adapter: RecipeListAdapter) {
        val call = RetrofitClient.userApi.getRecipeList(
            header,
            kitchenId
        )

        call.enqueue(object : Callback<ArrayList<Model.Recipe>> {
            override fun onFailure(
                call: Call<ArrayList<Model.Recipe>>,
                t: Throwable
            ) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Model.Recipe>>,
                response: Response<ArrayList<Model.Recipe>>
            ) {
                val code = response.code()
                val body = response.body()


                when (code) {
                    200 -> {
                        body?.forEach { x -> setState(header, x, recipeList, adapter) }

                    }
                    else -> {
                        Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun setState(header: String, recipe: Model.Recipe, recipeList: ArrayList<Model.Recipe>,adapter: RecipeListAdapter){
        val call = RetrofitClient.userApi.getState(
            header,
            recipe.recipeId
        )

        call.enqueue(object : Callback<Model.IngredientStatus> {
            override fun onFailure(
                call: Call<Model.IngredientStatus>,
                t: Throwable
            ) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<Model.IngredientStatus>,
                response: Response<Model.IngredientStatus>
            ) {
                val code = response.code()
                val body = response.body()
                val requiredAmount = body?.requiredAmount.toString()
                val availableAmount = body?.availableAmount.toString()

                var percentage = 0f

                if(requiredAmount.toInt() != 0){
                    percentage = availableAmount.toFloat()/requiredAmount.toFloat()*100f
                }
                recipe.recipeName += "   $availableAmount/$requiredAmount  -  ${percentage.toInt()}%"

                when (code) {
                    200 -> {
                        recipeList.add(recipe)
                        adapter.notifyDataSetChanged()
                    }
                    else -> {
                        Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}

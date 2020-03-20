package com.frorage.frontend.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.frorage.frontend.R
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class RecipeListAdapter(var activity: FragmentActivity,var items: ArrayList<Model.Recipe>) : BaseAdapter() {

    private class ViewHolder(row: View) {
        val recipeName: TextView
        val state: TextView
        val delete: Button

        init {
            this.recipeName = row.findViewById(R.id.recipeName)
            this.state = row.findViewById(R.id.state)
            this.delete = row.findViewById(R.id.deleteRecipe)
        }
    }




    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        val header = SharedPrefMenager.getInstance(activity.applicationContext).token

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.text_row_recipe, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // views
        val recipeId = items[position].recipeId
        //setState(header, recipeId, viewHolder.state)
        viewHolder.recipeName.text = items[position].recipeName




        //listeners
        viewHolder.delete.setOnClickListener{
            deleteProduct(header, items[position].recipeId, position)
        }

        return view
    }

    override fun getItem(i: Int): Any {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    private fun deleteProduct(header: String, recipeId: Int , position: Int){
        val call = RetrofitClient.userApi.deleteRecipe(
            header,
            recipeId
        )

        call.enqueue(object : Callback<Model.GeneralResponse> {
            override fun onFailure(
                call: Call<Model.GeneralResponse>,
                t: Throwable
            ) {
                Toast.makeText(activity.applicationContext, "Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<Model.GeneralResponse>,
                response: Response<Model.GeneralResponse>
            ) {
                val code = response.code()

                when (code) {
                    200 -> {
                        items.removeAt(position)
                        notifyDataSetChanged()
                    }
                    else -> {
                        Toast.makeText(activity.applicationContext, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun setState(header: String, recipeId: Int, state: TextView){
        val call = RetrofitClient.userApi.getState(
            header,
            recipeId
        )

        call.enqueue(object : Callback<Model.IngredientStatus> {
            override fun onFailure(
                call: Call<Model.IngredientStatus>,
                t: Throwable
            ) {
                Toast.makeText(activity.applicationContext, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<Model.IngredientStatus>,
                response: Response<Model.IngredientStatus>
            ) {
                val code = response.code()
                val body = response.body()
                val requiredAmount = body?.requiredAmount.toString()
                val availableAmount = body?.availableAmount.toString()

                when (code) {
                    200 -> {
                        state.text = "$availableAmount/$requiredAmount"
                        notifyDataSetChanged()
                    }
                    else -> {
                        Toast.makeText(activity.applicationContext, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }


    fun sortPossible(){
        val copy = ArrayList<Model.Recipe>()
        copy.addAll(items)

        items.sortByDescending{it.recipeName.substring(it.recipeName.length-4,it.recipeName.length-1)}


        //TODO sortowanie trudne 1/5 wziac procent

        notifyDataSetChanged()
    }



    fun sortAZ(){
        items.sortBy{it.recipeName.toLowerCase()}
        notifyDataSetChanged()
    }
}
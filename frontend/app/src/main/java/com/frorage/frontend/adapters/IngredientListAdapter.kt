package com.frorage.frontend.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.frorage.frontend.R
import com.frorage.frontend.api.RetrofitClient
import com.frorage.frontend.model.Model
import com.frorage.frontend.storage.SharedPrefMenager
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class IngredientListAdapter(var activity: FragmentActivity,var items: ArrayList<Model.IngredientRequest>) : BaseAdapter() {

    private class ViewHolder(row: View) {
        val ingredientName: TextView
        val quantity: TextView
        val deleteIngredient: Button

        init {
            this.ingredientName = row.findViewById(R.id.ingredientName)
            this.quantity = row.findViewById(R.id.quantity)
            this.deleteIngredient = row.findViewById(R.id.deleteIngredient)
        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.text_row_ingredient, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // views
        viewHolder.ingredientName.text = items[position].ingredientName
        quantityUpdate(viewHolder,position)

        //listeners
        viewHolder.deleteIngredient.setOnClickListener{
            items.removeAt(position)
            notifyDataSetChanged()
        }

        return view
    }

    private fun quantityUpdate(viewHolder: ViewHolder, position: Int){
        val amount: Number = if(items[position].amount.absoluteValue % 1.0 >= 0.005 ){
            items[position].amount
        } else {
            items[position].amount.toInt()
        }

        val quantityAndUnit = "$amount ${items[position].unit}"
        viewHolder.quantity.text = quantityAndUnit
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
}
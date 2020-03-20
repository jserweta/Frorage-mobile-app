package com.frorage.frontend.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.frorage.frontend.R
import com.frorage.frontend.model.Model
import kotlin.math.absoluteValue

/**
 * Provide views to RecyclerView with data from dataSet.
 *
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
class ShoppingListAdapter(private val dataSet: Array<Model.ShoppingListProduct>) :
    RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val productName: TextView
        val quantity: TextView
        val cart: CheckBox


        init {
            // Define click listener for the ViewHolder's View.
            productName = v.findViewById(R.id.productNameIn)
            quantity = v.findViewById(R.id.quantity)
            cart = v.findViewById(R.id.cart)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.productName.text = dataSet[position].productName

        val amount: Number = if(dataSet[position].quantity.absoluteValue % 1.0 >= 0.005 ){
            dataSet[position].quantity
        } else {
            dataSet[position].quantity.toInt()
        }

        val quantityAndUnit = "$amount ${dataSet[position].unit}"
        viewHolder.quantity.text = quantityAndUnit

        viewHolder.cart.setOnCheckedChangeListener { buttonView, isChecked ->
            dataSet[position].inCart = isChecked
            viewHolder.cart.isChecked = isChecked
        }

        viewHolder.cart.isChecked = dataSet[position].inCart

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size



}
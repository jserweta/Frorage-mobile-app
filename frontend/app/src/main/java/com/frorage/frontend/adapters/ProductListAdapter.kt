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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class ProductListAdapter(var activity: FragmentActivity,var items: ArrayList<Model.FullProductWithId>) : BaseAdapter() {

    private class ViewHolder(row: View) {
        val productName: TextView
        val quantity: TextView
        val plus: Button
        val minus: Button
        val favourite: CheckBox
        val delete: Button

        init {
            this.productName = row.findViewById(R.id.productName)
            this.quantity = row.findViewById(R.id.quantity)
            this.plus = row.findViewById(R.id.plus)
            this.minus = row.findViewById(R.id.minus)
            this.favourite = row.findViewById(R.id.favourite)
            this.delete = row.findViewById(R.id.delete)
        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        val header = SharedPrefMenager.getInstance(activity.applicationContext).token

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.text_row_product, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // views
        viewHolder.favourite.isChecked = items[position].favourite

        quantityUpdate(viewHolder,position)

        if(items[position].runningOut){
            viewHolder.productName.setTextColor(Color.RED)
        }else{
            viewHolder.productName.setTextColor(Color.BLACK)
        }


        // listeners
        viewHolder.favourite.setOnClickListener{
            val isChecked = viewHolder.favourite.isChecked
            val item = items[position]
            items[position].favourite = isChecked
            val product: Model.FullProductWithId = Model.FullProductWithId(item.productId,item.kitchenId,item.productName,item.amount,item.unit,item.toBuy,isChecked,item.runningOut,item.expirationDate)
            updateProduct(header, product)
        }

        viewHolder.plus.setOnClickListener{
            val item = items[position]
            val product: Model.FullProductWithId = Model.FullProductWithId(item.productId,item.kitchenId,item.productName,item.amount+1,item.unit,item.toBuy,item.favourite,item.runningOut,item.expirationDate)
            updateProduct(header, product)

            items[position].amount += 1f
            quantityUpdate(viewHolder,position)
        }

        viewHolder.minus.setOnClickListener{
            items[position].amount -= 1f
            quantityUpdate(viewHolder,position)

            if(items[position].amount <= 0){
                deleteProduct(header, items[position].productId, position)
            }else{
                val item = items[position]
                val product: Model.FullProductWithId = Model.FullProductWithId(item.productId,item.kitchenId,item.productName,item.amount,item.unit,item.toBuy,item.favourite,item.runningOut,item.expirationDate)
                updateProduct(header, product)
            }
        }

        viewHolder.delete.setOnClickListener{
            deleteProduct(header, items[position].productId, position)
        }

        return view
    }

    private fun quantityUpdate(viewHolder: ViewHolder, position: Int){
        val product = items[position]
        viewHolder.productName.text = product.productName

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

    private fun deleteProduct(header: String, productId: Int , position: Int){
        val call = RetrofitClient.userApi.deleteProduct(
            header,
            productId
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
                Toast.makeText(activity.applicationContext, "Error", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(activity.applicationContext, "Wrong operation!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    fun sortRunningOut(){
        val copy = ArrayList<Model.FullProductWithId>()
        copy.addAll(items)

        items.clear()

        copy.forEach{ x ->
            run {
                if(x.runningOut){
                    items.add(x)
                }
            }
        }

        copy.forEach{ x ->
            run {
                if(!x.runningOut){
                    items.add(x)
                }
            }
        }

        notifyDataSetChanged()
    }

    fun sortFavourite(){
        val copy = ArrayList<Model.FullProductWithId>()
        copy.addAll(items)

        items.clear()

        copy.forEach{ x ->
            run {
                if(x.favourite){
                    items.add(x)
                }
            }
        }

        copy.forEach{ x ->
            run {
                if(!x.favourite){
                    items.add(x)
                }
            }
        }

        notifyDataSetChanged()
    }

    fun sortAZ(){
        items.sortBy {it.productName}
        notifyDataSetChanged()
    }
}
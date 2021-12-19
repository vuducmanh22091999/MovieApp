package com.example.movieapp.ui.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.model.product.StatusCartModel
import kotlinx.android.synthetic.main.item_status_cart.view.*
import java.util.concurrent.Executors

class StatusCartAdapter(private val onClick: (Int, String) -> Unit) :
    ListAdapter<StatusCartModel, StatusCartAdapter.DataViewHolder>(
        AsyncDifferConfig.Builder<StatusCartModel>(BaseDiffAdapter())
            .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()).build()
    ) {
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDataViewHolder(statusCartModel: StatusCartModel) {
            val adminCartAdapter = AdminCartAdapter()
            adminCartAdapter.submitList(statusCartModel.listProduct)
            val linearLayoutManager =
                LinearLayoutManager(
                    itemView.context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            itemView.itemStatusCart_rcvCart.setHasFixedSize(true)
            itemView.itemStatusCart_rcvCart.layoutManager = linearLayoutManager
            itemView.itemStatusCart_rcvCart.adapter = adminCartAdapter
            itemView.itemStatusCart_tvUserNameOrder.text = statusCartModel.userName
            itemView.itemStatusCart_tvStatusOrder.text = statusCartModel.valueStatus

            itemView.setOnClickListener {
                onClick(absoluteAdapterPosition, statusCartModel.valueStatus)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatusCartAdapter.DataViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_status_cart, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bindDataViewHolder(currentList[position])
    }
}
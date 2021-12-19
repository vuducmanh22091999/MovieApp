package com.example.movieapp.ui.order_history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.model.product.StatusCartModel
import com.example.movieapp.ui.cart.adapter.BaseDiffAdapter
import kotlinx.android.synthetic.main.item_status_cart.view.*
import java.util.concurrent.Executors

class CartHistoryAdapter(private val onClick: (Int, String) -> Unit) :
    ListAdapter<StatusCartModel, CartHistoryAdapter.DataViewHolder>(
        AsyncDifferConfig.Builder<StatusCartModel>(BaseDiffAdapter())
            .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()).build()
    ) {
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDataViewHolder(statusCartModel: StatusCartModel) {
            val orderHistoryAdapter = OrderHistoryAdapter()
            orderHistoryAdapter.submitList(statusCartModel.listProduct)
            val linearLayoutManager =
                LinearLayoutManager(
                    itemView.context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            itemView.itemStatusCart_rcvCart.setHasFixedSize(true)
            itemView.itemStatusCart_rcvCart.layoutManager = linearLayoutManager
            itemView.itemStatusCart_rcvCart.adapter = orderHistoryAdapter
            itemView.itemStatusCart_tvUserNameOrder.text = ""
            itemView.itemStatusCart_tvStatusOrder.text = statusCartModel.valueStatus

            itemView.setOnClickListener {
                onClick(absoluteAdapterPosition, statusCartModel.valueStatus)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartHistoryAdapter.DataViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_status_cart, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bindDataViewHolder(currentList[position])
    }
}
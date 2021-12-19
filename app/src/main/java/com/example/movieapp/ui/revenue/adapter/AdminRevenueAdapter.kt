package com.example.movieapp.ui.revenue.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.data.model.product.StatusCartModel
import com.example.movieapp.ui.cart.adapter.BaseDiffAdapter
import com.example.movieapp.ui.cart.adapter.StatusCartAdapter
import com.example.movieapp.utils.formatStringLong
import kotlinx.android.synthetic.main.item_admin_cart_product.view.*
import kotlinx.android.synthetic.main.item_admin_cart_product.view.itemCartProductAdmin_tvAmountUserOrder
import kotlinx.android.synthetic.main.item_admin_cart_product.view.itemCartProductAdmin_tvOrderUserName
import kotlinx.android.synthetic.main.item_admin_cart_product.view.itemCartProductAdmin_tvPrice
import kotlinx.android.synthetic.main.item_admin_cart_product.view.itemCartProductAdmin_tvSize
import kotlinx.android.synthetic.main.item_admin_cart_product.view.itemCartProductAdmin_tvTitleNameProduct
import kotlinx.android.synthetic.main.item_admin_revenue.view.*
import java.util.concurrent.Executors

class AdminRevenueAdapter : ListAdapter<StatusCartModel, AdminRevenueAdapter.DataViewHolder>(
    AsyncDifferConfig.Builder<StatusCartModel>(BaseDiffAdapter())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()).build()
) {
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindDataViewHolder(statusCartModel: StatusCartModel) {
            itemView.context?.let {
                Glide.with(itemView.context).load(statusCartModel.listProduct[0].productModel?.urlAvatar)
                    .placeholder(R.drawable.img_placeholder)
                    .into(itemView.itemAdminRevenue_imgAvatar)
            }
            statusCartModel.listProduct[0].amountUserOrder
            itemView.itemAdminRevenue_tvAmountUserOrder.text =
                "Amount user order: ${statusCartModel.listProduct[0].amountUserOrder}"
            itemView.itemAdminRevenue_tvPrice.text =
                "Total: ${formatStringLong(statusCartModel.listProduct[0].totalPrice)}$"
            itemView.itemAdminRevenue_tvSize.text =
                "Size: ${statusCartModel.listProduct[0].productModel?.listSize?.get(0)?.size.toString()}"
            itemView.itemAdminRevenue_tvTitleNameProduct.text =
                statusCartModel.listProduct[0].productModel?.name
            itemView.itemAdminRevenue_tvOrderUserName.text = statusCartModel.userName
            itemView.itemAdminRevenue_tvOrderDateCompleted.text = statusCartModel.completedDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRevenueAdapter.DataViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_admin_revenue, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bindDataViewHolder(currentList[position])
    }
}
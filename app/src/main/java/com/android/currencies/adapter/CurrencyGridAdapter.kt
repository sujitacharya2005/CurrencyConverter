package com.android.currencies.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.currencies.R
import com.android.currencies.data.local.model.CurrencyData


class CurrencyGridAdapter(private val clickListener: OnClickListener) :
    ListAdapter<CurrencyData, CurrencyGridAdapter.CurrencyViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.currency_list_item, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position), position)
    }


    class CurrencyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val symbolText: TextView = view.findViewById(R.id.symbol)

        fun bind(clickListener: OnClickListener, currencyData: CurrencyData, position: Int) {
            view.setOnClickListener {
                view.setOnClickListener {
                    val position = adapterPosition

                }
            }
            view.setOnClickListener {
                clickListener.onItemClick(currencyData)
            }
            symbolText.text = currencyData.symbol
        }
    }

    class DiffCallBack : ItemCallback<CurrencyData>() {
        override fun areItemsTheSame(oldItem: CurrencyData, newItem: CurrencyData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CurrencyData, newItem: CurrencyData): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.symbol == newItem.symbol
                    && oldItem.value == newItem.value
        }
    }

    interface OnClickListener {
        fun onItemClick(currencyData: CurrencyData)
    }
}
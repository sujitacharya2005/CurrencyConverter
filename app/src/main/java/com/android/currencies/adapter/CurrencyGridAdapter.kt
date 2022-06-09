package com.android.currencies.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.databinding.CurrencyListItemBinding
import com.android.currencies.databinding.OnItemActionListener


class CurrencyGridAdapter(private val clickListener: OnItemActionListener) :
    ListAdapter<CurrencyData, CurrencyGridAdapter.CurrencyViewHolder>(DiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding =
            CurrencyListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }


    class CurrencyViewHolder(private val binding: CurrencyListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: OnItemActionListener, currencyData: CurrencyData) {
            binding.currencyData = currencyData
            binding.itemClickListener = clickListener
        }

    }

}
package com.android.currencies.adapter

import androidx.recyclerview.widget.DiffUtil
import com.android.currencies.data.local.model.CurrencyData

/**
 * This class responsible to check two list is same or not
 */
internal class DiffUtilCallBack : DiffUtil.ItemCallback<CurrencyData>() {
    override fun areItemsTheSame(oldItem: CurrencyData, newItem: CurrencyData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CurrencyData, newItem: CurrencyData): Boolean {
        return oldItem.id == newItem.id
                && oldItem.symbol == newItem.symbol
                && oldItem.value == newItem.value
    }
}
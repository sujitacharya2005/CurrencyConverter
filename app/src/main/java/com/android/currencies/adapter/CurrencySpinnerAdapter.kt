package com.android.currencies.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.android.currencies.data.local.model.CurrencyData


class CurrencySpinnerAdapter(
    context: Context, textViewResourceId: Int,
    private val list: List<CurrencyData>,
) : ArrayAdapter<CurrencyData>(context, textViewResourceId, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label:TextView = if(convertView == null) {
            super.getDropDownView(position, convertView, parent) as TextView
        } else {
            convertView as TextView
        }
        label.setTextColor(Color.BLACK)
        label.text = list[position].symbol

        return label
    }

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup?,
    ): View {
        val label:TextView = if(convertView == null) {
             super.getDropDownView(position, convertView, parent) as TextView
        } else {
             convertView as TextView
        }
        label.setTextColor(Color.BLACK)
        label.text = list[position].symbol
        return label
    }
}
package com.android.currencies.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import com.android.currencies.R
import com.android.currencies.adapter.CurrencyGridAdapter
import com.android.currencies.adapter.CurrencySpinnerAdapter
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.util.convertToCurrency
import com.android.currencies.viewmodel.CurrencyConverterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyConverterFragment : Fragment(), CurrencyGridAdapter.OnClickListener {

    private lateinit var currencyRatesAdapter: CurrencyGridAdapter
    private lateinit var currencySpinnerAdapter: CurrencySpinnerAdapter
    private val currencyConverterViewModel: CurrencyConverterViewModel by viewModels()
    private var fromCurrencyData: CurrencyData? = null
    private var toCurrencyData: CurrencyData? = null
    private lateinit var enteredValueEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.currency_converter_fragment, container, false)
        initRecyclerView(view)
        observeWorkerLiveData(view)
        editTextDta(view)
        observeProgressBarLiveData(view)
        return view
    }

    private fun editTextDta(view: View) {
        enteredValueEditText = view.findViewById(R.id.amount)
    }

    private fun observeProgressBarLiveData(view: View) {
        currencyConverterViewModel.progressBarLiveData.observe(viewLifecycleOwner, {
            if(it == false) {
                view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            }
            else {
                view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            }

        })
    }

    private fun observeWorkerLiveData(view: View) {
        currencyConverterViewModel.updatesFromWorkerLiveData.observe(viewLifecycleOwner, {
            val workInfo = it[0]

            if (workInfo.state == WorkInfo.State.ENQUEUED) {
                initSpinnerData(view)
            }
        })
    }

    private fun initRecyclerView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        currencyRatesAdapter = CurrencyGridAdapter(this)
        recyclerView.adapter = currencyRatesAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    private fun initSpinnerData(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.selectedCurrency)
        if (spinner != null) {
            currencyConverterViewModel.getCurrencies()
            currencyConverterViewModel.symbolsLiveData.observe(viewLifecycleOwner, { currencies ->
                currencyRatesAdapter.submitList(currencies)
                context?.let {
                    currencySpinnerAdapter = CurrencySpinnerAdapter(it,
                        android.R.layout.simple_spinner_dropdown_item,
                        currencies)
                    spinner.adapter = currencySpinnerAdapter
                }
            })

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    fromCurrencyData = currencySpinnerAdapter.getItem(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }
    }

    override fun onItemClick(currencyData: CurrencyData) {
        toCurrencyData = currencyData
        val enteredValue = if (enteredValueEditText.text.toString().isNotEmpty())
            enteredValueEditText.text.toString().toDouble()
        else
            0.0
        val convertedValue = convertToCurrency(fromCurrencyData, toCurrencyData, enteredValue)
        enteredValueEditText.setText(convertedValue.toString())
    }
}


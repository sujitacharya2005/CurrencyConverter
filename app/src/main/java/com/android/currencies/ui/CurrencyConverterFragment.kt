package com.android.currencies.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.WorkInfo
import com.android.currencies.adapter.CurrencyGridAdapter
import com.android.currencies.adapter.CurrencySpinnerAdapter
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.databinding.CurrencyConverterFragmentBinding
import com.android.currencies.databinding.OnItemActionListener
import com.android.currencies.util.convertToCurrency
import com.android.currencies.viewmodel.CurrencyConverterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyConverterFragment : Fragment(), OnItemActionListener {

    private lateinit var currencyRatesAdapter: CurrencyGridAdapter
    private lateinit var currencySpinnerAdapter: CurrencySpinnerAdapter
    private val currencyConverterViewModel: CurrencyConverterViewModel by viewModels()
    private var fromCurrencyData: CurrencyData? = null
    private var toCurrencyData: CurrencyData? = null

    private lateinit var binding: CurrencyConverterFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = CurrencyConverterFragmentBinding.inflate(inflater, container, false)
        initRecyclerView()
        observeWorkerLiveData()
        observeProgressBarLiveData(binding.root)
        return binding.root
    }


    private fun observeProgressBarLiveData(view: View) {
        currencyConverterViewModel.progressBarLiveData.observe(viewLifecycleOwner, {
            binding.progressBarVisibility = it
        })
    }

    private fun observeWorkerLiveData() {
        currencyConverterViewModel.updatesFromWorkerLiveData.observe(viewLifecycleOwner, {
            val workInfo = it[0]

            if (workInfo.state == WorkInfo.State.ENQUEUED) {
                initSpinnerData()
            }
        })
    }

    private fun initRecyclerView() {
        currencyRatesAdapter = CurrencyGridAdapter(this)
        binding.gridAdapter = currencyRatesAdapter
    }

    private fun initSpinnerData() {
        val spinner = binding.selectedCurrency
        if (spinner != null) {
            currencyConverterViewModel.getCurrencies()
            currencyConverterViewModel.symbolsLiveData.observe(viewLifecycleOwner, { currencies ->
                currencyRatesAdapter.submitList(currencies)
                context?.let {
                    currencySpinnerAdapter = CurrencySpinnerAdapter(it,
                        android.R.layout.simple_spinner_dropdown_item, currencies)
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


    override fun onItemClicked(data: Any) {
        toCurrencyData = data as CurrencyData
        val enteredValue = if (binding.amount.text.toString().isNotEmpty())
            binding.amount.text.toString().toDouble()
        else
            0.0
        val convertedValue = convertToCurrency(fromCurrencyData, toCurrencyData, enteredValue)
        binding.amount.setText(convertedValue.toString())
    }
}


package com.android.currencies.ui

import android.R
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.currencies.adapter.CurrencyGridAdapter
import com.android.currencies.adapter.CurrencySpinnerAdapter
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.databinding.CurrencyConverterFragmentBinding
import com.android.currencies.databinding.OnItemActionListener
import com.android.currencies.prefs.CurrencyConverterPrefs
import com.android.currencies.viewmodel.CurrencyConverterViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.snackbar.Snackbar


private const val TAG = "Currency:Fragment"

const val SCHEDULE_TIME = 30 * 60 * 1000L // 30 min

@AndroidEntryPoint
class CurrencyConverterFragment : Fragment(), OnItemActionListener {

    private lateinit var currencyRatesAdapter: CurrencyGridAdapter
    private lateinit var currencySpinnerAdapter: CurrencySpinnerAdapter
    private val currencyConverterViewModel: CurrencyConverterViewModel by viewModels()
    private var fromCurrencyData: CurrencyData? = null
    private var toCurrencyData: CurrencyData? = null

    private lateinit var binding: CurrencyConverterFragmentBinding
    var enteredValue = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = CurrencyConverterFragmentBinding.inflate(inflater, container, false)
        initRecyclerView()
        getEditTextData()
        observeProgressBarLiveData()
        observerCurrencyLiveData()
        observeCurrencyConvertedLiveData()
        showSnackBar()
        return binding.root
    }

    private fun getEditTextData() {
        binding.amount.doOnTextChanged { text, start, count, after ->
            if (binding.amount.hasFocus()) {
                enteredValue = if (!text?.toString().isNullOrEmpty())
                    text.toString().toDouble()
                else
                    0.0
            }
        }
    }

    private fun observerCurrencyLiveData() {
        currencyConverterViewModel.getCurrencies()
        currencyConverterViewModel.symbolsLiveData.observe(viewLifecycleOwner, { currencies ->
            currencyRatesAdapter.submitList(currencies)
            initSpinnerData(currencies)
        })
    }

    override fun onResume() {
        super.onResume()
        paused = false
        setScheduleRemoteApi()
    }

    private var handler: Handler? = null

    private fun setScheduleRemoteApi() {
        try {
            handler?.removeCallbacks(pauseVideoCheckRunnable)
        } catch (e: Exception) {

        }
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed(pauseVideoCheckRunnable, 0)

        currencyConverterViewModel.convertedRatesSuccessLiveData.observe(this, {
            if (it == true) {
                val sharePref = CurrencyConverterPrefs(requireContext())
                sharePref.timeStamp = System.currentTimeMillis()
                handler?.postDelayed(pauseVideoCheckRunnable, SCHEDULE_TIME)
            }
        })
    }

    var paused = false
    override fun onPause() {
        super.onPause()
        paused = true
        pausePlaying()
    }

    private fun pausePlaying() {
        if (pauseVideoCheckRunnable != null) {
            try {
                handler?.removeCallbacks(pauseVideoCheckRunnable)
                handler?.removeCallbacksAndMessages(null)
            } catch (e: Exception) {
            }
        }
    }

    private val pauseVideoCheckRunnable: Runnable
        get() {
            return Runnable {
                runRemoteApi()
            }
        }

    private fun runRemoteApi() {
        val sharePref = CurrencyConverterPrefs(requireContext())
        var diff = System.currentTimeMillis() - sharePref.timeStamp
        //for 1s time case diff will be bigger value it should reset
        if (diff > SCHEDULE_TIME) {
            diff = SCHEDULE_TIME
        }
        val time = if (SCHEDULE_TIME - diff <= 0) 1 else diff
        if (time == 1L) {
            currencyConverterViewModel.getConversionRates()
        } else
            handler?.postDelayed(pauseVideoCheckRunnable, time)
    }


    private fun observeProgressBarLiveData() {
        currencyConverterViewModel.progressBarLiveData.observe(viewLifecycleOwner, {
            binding.progressBarVisibility = it
        })
    }


    private fun initRecyclerView() {
        currencyRatesAdapter = CurrencyGridAdapter(this)
        binding.gridAdapter = currencyRatesAdapter
    }

    private fun initSpinnerData(currencies: List<CurrencyData>) {
        val spinner = binding.selectedCurrency
        currencySpinnerAdapter = CurrencySpinnerAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, currencies)
        spinner.adapter = currencySpinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                fromCurrencyData = currencySpinnerAdapter.getItem(position)
                try {
                    if (!binding.amount?.toString().isNullOrEmpty())
                        enteredValue = binding.amount.toString().toDouble()
                } catch (e: NumberFormatException) {

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }


    override fun onItemClicked(data: Any) {
        toCurrencyData = data as CurrencyData
        binding.amount.clearFocus()
        currencyConverterViewModel.getConvertedValue(fromCurrencyData, toCurrencyData, enteredValue)
    }

    private fun observeCurrencyConvertedLiveData() {
        currencyConverterViewModel.convertedValueLiveData.observe(
            viewLifecycleOwner, { convertedValue ->
                binding.amount.setText(convertedValue.toString())
            })
    }

    private fun showSnackBar() {
        currencyConverterViewModel.showSnackBarLiveData.observe(this, {
            if (!it.isNullOrEmpty()) {
                Snackbar.make(view!!, it, Snackbar.LENGTH_INDEFINITE)
                    .setAction("retry") {
                        currencyConverterViewModel.getConversionRates()
                    }
                    .setActionTextColor(resources.getColor(R.color.holo_red_light))
                    .show()
                currencyConverterViewModel.showSnackBarLiveData.value = ""
            }
        })
    }
}


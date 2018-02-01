package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.PetrolType
import kotlinx.android.synthetic.main.cell_price.view.*
import kotlinx.android.synthetic.main.fragment_petrol_price_history.*
import java.text.SimpleDateFormat

/**
 * Created by ApptivityLab on 31/01/2018.
 */

class PetrolPriceHistoryFragment : Fragment() {

    companion object {
        const val PETROL_TYPE_EXTRA = "petrol_type_object"

        fun newInstance(petrolType: PetrolType): PetrolPriceHistoryFragment {
            val fragment = PetrolPriceHistoryFragment()

            val args: Bundle = Bundle()
            args.putParcelable(PETROL_TYPE_EXTRA, petrolType)

            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var petrolType: PetrolType

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_petrol_price_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.petrolType = it.getParcelable(PETROL_TYPE_EXTRA)
        }

        this.setCurrentPriceViews()
        this.generatePreviousPriceViews()
    }

    private fun setCurrentPriceViews() {
        this.petrolTypeTextView.text = this.petrolType.petrolName
        this.currentPriceTextView.text = getString(R.string.price_value, this.petrolType.currentPrice)

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        this.currentPriceSinceTextView.text = getString(R.string.since_value, simpleDateFormat.format(this.petrolType.priceChangeDates[0]))
    }

    private fun generatePreviousPriceViews() {
        for (count in 1 until this.petrolType.previousPrices.size) {
            val layoutInflater: LayoutInflater = LayoutInflater.from(this.context)
            val previousPriceCell = layoutInflater.inflate(R.layout.cell_price, this.priceHistoryLinearLayout, false)

            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

            previousPriceCell.priceTextView.text = getString(R.string.price_value, petrolType.previousPrices[count])
            previousPriceCell.dateTextView.text = getString(R.string.changed_on, simpleDateFormat.format(this.petrolType.priceChangeDates[count]))

            this.priceHistoryLinearLayout.addView(previousPriceCell)
        }
    }
}
package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        if (this.petrolType.previousPrices.size > 1) {
            this.generatePriceDifferenceTextView(this.currentPriceDifferenceTextView, 0)
        }

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        this.currentPriceSinceTextView.text = getString(R.string.since_value, simpleDateFormat.format(this.petrolType.priceChangeDates[0]))
    }

    private fun generatePreviousPriceViews() {
        (1 until this.petrolType.previousPrices.size).forEach { count ->
            val layoutInflater: LayoutInflater = LayoutInflater.from(this.context)
            val previousPriceCell = layoutInflater.inflate(R.layout.cell_price, this.priceHistoryLinearLayout, false)

            if (count < this.petrolType.previousPrices.size - 1) {
                this.generatePriceDifferenceTextView(previousPriceCell.priceDifferenceTextView, count)
            }

            val dayMonthFormat = SimpleDateFormat("dd MMM")
            val yearFormat = SimpleDateFormat("yyyy")

            previousPriceCell.dayMonthTextView.text = dayMonthFormat.format(this.petrolType.priceChangeDates[count])
            previousPriceCell.yearTextView.text = yearFormat.format(this.petrolType.priceChangeDates[count])
            previousPriceCell.priceTextView.text = getString(R.string.price_value, petrolType.previousPrices[count])

            this.priceHistoryLinearLayout.addView(previousPriceCell)
        }
    }

    private fun generatePriceDifferenceTextView(priceDifferenceTextView: TextView, currentCount: Int) {
        var priceDifference = this.petrolType.previousPrices[currentCount] - this.petrolType.previousPrices[currentCount + 1]
        val priceDifferenceText: String
        val priceDifferenceTextColor: Int

        if (priceDifference > 0) {
            priceDifferenceText = getString(R.string.positive_price_value, priceDifference)
            priceDifferenceTextColor = ResourcesCompat.getColor(resources, android.R.color.holo_red_light, null)
        } else {
            priceDifference = Math.abs(priceDifference)
            priceDifferenceText = getString(R.string.negative_price_value, priceDifference)
            priceDifferenceTextColor = ResourcesCompat.getColor(resources, R.color.colorGreen, null)
        }

        priceDifferenceTextView.text = priceDifferenceText
        priceDifferenceTextView.setTextColor(priceDifferenceTextColor)
    }
}
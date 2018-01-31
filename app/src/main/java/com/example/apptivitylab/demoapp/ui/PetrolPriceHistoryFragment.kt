package com.example.apptivitylab.demoapp.ui

import android.app.ActionBar
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.PetrolType
import kotlinx.android.synthetic.main.fragment_petrol_price_history.*
import java.text.SimpleDateFormat

/**
 * Created by ApptivityLab on 31/01/2018.
 */

class PetrolPriceHistoryFragment : Fragment() {

    companion object {
        const val PETROLTYPE_EXTRA = "petrolType_object"

        fun newInstance(petrolType: PetrolType): PetrolPriceHistoryFragment {
            val fragment = PetrolPriceHistoryFragment()

            val args: Bundle = Bundle()
            args.putParcelable(PETROLTYPE_EXTRA, petrolType)

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
            this.petrolType = it.getParcelable(PETROLTYPE_EXTRA)
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
            val linearLayout = LinearLayout(this.context!!)
            linearLayout.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.colorAccent))
            linearLayout.orientation = LinearLayout.VERTICAL

            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(0, 32, 0, 16)

            linearLayout.layoutParams = layoutParams
            linearLayout.setPadding(32, 32, 32, 32)

            layoutParams.setMargins(0, 8, 0, 8)

            val priceTextView = TextView(this.context!!)
            priceTextView.layoutParams = layoutParams
            priceTextView.textSize = 24f
            priceTextView.typeface = Typeface.DEFAULT_BOLD
            priceTextView.text = getString(R.string.price_value, petrolType.previousPrices[count])

            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            val dateTextView = TextView(this.context!!)
            dateTextView.layoutParams = layoutParams
            dateTextView.textSize = 16f
            dateTextView.text = getString(R.string.changed_on, simpleDateFormat.format(this.petrolType.priceChangeDates[count]))

            linearLayout.addView(priceTextView)
            linearLayout.addView(dateTextView)
            this.priceHistoryLinearLayout.addView(linearLayout)
        }
    }
}
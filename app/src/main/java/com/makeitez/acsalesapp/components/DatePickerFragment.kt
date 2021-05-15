package com.makeitez.acsalesapp.components

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import com.makeitez.acsalesapp.utils.extensions.toCalendar
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    interface Listener {
        fun onDateSelected(requestCode: Int? = null, day: Int, month: Int, year: Int)
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()

    private val currentDate: Date? by lazy {
        arguments?.getLong(SELECTED_DATE_KEY)?.let { Date(it) }
    }

    private val requestCode: Int? by lazy {
        arguments?.getInt(REQUEST_CODE_KEY)
    }

    private val minDate: Date? by lazy {
        arguments?.getLong(MIN_DATE_KEY)?.let { Date(it) }
    }

    fun withData(currentDate: Date?, requestCode: Int? = null, minDate: Date? = null): DatePickerFragment {
        val args = arguments ?: Bundle()
        currentDate?.let {
            args.putLong(SELECTED_DATE_KEY, it.time)
        }
        requestCode?.let {
            args.putInt(REQUEST_CODE_KEY, it)
        }
        minDate?.let {
            args.putLong(MIN_DATE_KEY, it.time)
        }
        this.arguments = args
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = currentDate?.toCalendar() ?: Calendar.getInstance()
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH)
        val day = date.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireActivity(), this, year, month, day).apply {
            minDate?.let {
                datePicker.minDate = it.time
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        listener?.onDateSelected(requestCode, day, month, year)
    }

    companion object {
        private const val SELECTED_DATE_KEY = "selectedDateKey"
        private const val REQUEST_CODE_KEY = "requestCodeKey"
        private const val MIN_DATE_KEY = "minDateKey"
    }
}
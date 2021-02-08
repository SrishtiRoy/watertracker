package com.shiva.hydra

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.shiva.hydra.utils.AppUtils
import kotlinx.android.synthetic.main.activity_init_user_info.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

import com.shiva.hydra.utils.NumberPickerDialogFragment
import android.content.DialogInterface
import android.graphics.PorterDuff
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.OnColorSelectedListener
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class InitUserInfoActivity : AppCompatActivity(), NumberPicker.OnValueChangeListener {

    private var weight: String = ""
    private var workTime: String = ""
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    private lateinit var sharedPref: SharedPreferences
    private var doubleBackToExitPressedOnce = false
    private var isFocusable = false
    private var selectedColorFinal :Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_init_user_info)

        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        wakeupTime = sharedPref.getLong(AppUtils.WAKEUP_TIME, 1558323000000)
        sleepingTime = sharedPref.getLong(AppUtils.SLEEPING_TIME_KEY, 1558369800000)

        etWakeUpTime.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = wakeupTime

            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)
                    wakeupTime = time.timeInMillis

                    etWakeUpTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
            )
            mTimePicker.setTitle("Select Wakeup Time")
            mTimePicker.show()
        }

        etSleepTime.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = sleepingTime

            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)
                    sleepingTime = time.timeInMillis

                    etSleepTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
            )
            mTimePicker.setTitle("Select Sleeping Time")
            mTimePicker.show()
        }


        etWeight.editText!!.setOnClickListener {

            showNumberPicker(etWeight)
            isFocusable = true;


        }
        colorpicker.editText!!.setOnClickListener {
            showColorDialog()

        }

        etWorkTime.editText!!.setOnClickListener {

            showNumberPicker(etWorkTime)


        }

        btnContinue.setOnClickListener {

            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(init_user_info_parent_layout.windowToken, 0)

            weight = etWeight.editText!!.text.toString()
            workTime = etWorkTime.editText!!.text.toString()

            when {
                TextUtils.isEmpty(weight) -> Snackbar.make(
                    it,
                    "Please input your weight",
                    Snackbar.LENGTH_SHORT
                ).show()
                weight.toInt() > 200 || weight.toInt() < 20 -> Snackbar.make(
                    it,
                    "Please input a valid weight",
                    Snackbar.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(workTime) -> Snackbar.make(
                    it,
                    "Please input your workout time",
                    Snackbar.LENGTH_SHORT
                ).show()
                workTime.toInt() > 500 || workTime.toInt() < 0 -> Snackbar.make(
                    it,
                    "Please input a valid workout time",
                    Snackbar.LENGTH_SHORT
                ).show()
                else -> {

                    val editor = sharedPref.edit()
                    editor.putInt(AppUtils.WEIGHT_KEY, weight.toInt())
                    editor.putInt(AppUtils.WORK_TIME_KEY, workTime.toInt())
                    editor.putLong(AppUtils.WAKEUP_TIME, wakeupTime)
                    editor.putLong(AppUtils.SLEEPING_TIME_KEY, sleepingTime)
                    editor.putBoolean(AppUtils.FIRST_RUN_KEY, false)


                    val totalIntake = AppUtils.calculateIntake(weight.toInt(), workTime.toInt())
                    val df = DecimalFormat("#")
                    df.roundingMode = RoundingMode.CEILING
                    editor.putInt(AppUtils.TOTAL_INTAKE, df.format(totalIntake).toInt())
                    editor.putInt(AppUtils.COLORCODE, selectedColorFinal)

                    editor.apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

                }
            }
        }

    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Snackbar.make(
            this.window.decorView.findViewById(android.R.id.content),
            "Please click BACK again to exit",
            Snackbar.LENGTH_SHORT
        ).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1000)
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        weight = picker?.value.toString()
        if (isFocusable) {
            isFocusable = false
            etWeight.editText!!.setText(weight)
            etWeight.editText!!.setSelection(etWeight.editText!!.text.toString().length)

        } else {
            etWorkTime.editText!!.setText(
                picker?.value.toString()
            )
            etWorkTime.editText!!.setSelection(etWeight.editText!!.text.toString().length)

        }
    }



    fun showNumberPicker(view: View) {
        val newFragment = NumberPickerDialogFragment()
        newFragment.setValueChangeListener(this)
        newFragment.show(supportFragmentManager, "time picker")
    }

    fun showColorDialog() {
        ColorPickerDialogBuilder
            .with(this)
            .setTitle("Choose color")
            .initialColor(R.color.colorSkyBlue)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setOnColorSelectedListener { selectedColor ->
                colorpicker.editText!!.setText("Color is selected")
                selectedColorFinal=selectedColor
                colorpicker.editText!!.compoundDrawables[0].setTint(selectedColor)



            }
            .setPositiveButton(
                "ok"
            ) { dialog, selectedColor, allColors ->
                //val editor = sharedPref.edit()

               // editor.putInt(AppUtils.COLORCODE, selectedColorFinal)
               // editor.apply()


            }
            .setNegativeButton(
                "cancel"
            ) { dialog, which -> }
            .build()
            .show()

    }



}

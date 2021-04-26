package com.shiva.hydra

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.shiva.hydra.OutlinesFragments.OutlineActivity
import com.shiva.hydra.R
import com.shiva.hydra.fragments.BottomSheetFragment
import com.shiva.hydra.helpers.AlarmHelper
import com.shiva.hydra.utils.AppUtils
import io.github.z3r0c00l_2k.aquadroid.helpers.SqliteHelper

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var totalIntake: Int = 0
    private var inTook: Int = 0
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var dateNow: String
    private var notificStatus: Boolean = false
    private var selectedOption: Int? = null
    private var snackbar: Snackbar? = null
    private var doubleBackToExitPressedOnce = false
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }
    private var progressInfo: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        sqliteHelper = SqliteHelper(this)

        totalIntake = sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0)

       /* if (sharedPref.getBoolean(AppUtils.FIRST_RUN_KEY, true)) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }*/

        dateNow = AppUtils.getCurrentDate()!!

    }

    fun updateValues() {
        totalIntake = sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0)

        inTook = sqliteHelper.getIntook(dateNow)

        setWaterLevel(inTook, totalIntake)
    }

    override fun onStart() {
        super.onStart()

        val outValue = TypedValue()
        applicationContext.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )

        notificStatus = sharedPref.getBoolean(AppUtils.NOTIFICATION_STATUS_KEY, true)
        val alarm = AlarmHelper()
        if (!alarm.checkAlarm(this) && notificStatus) {
            btnMenu.setImageDrawable(getDrawable(R.drawable.ic_bell))
            alarm.setAlarm(
                this,
                sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
            )
        }

        if (notificStatus) {
            btnMenu.setImageDrawable(getDrawable(R.drawable.ic_bell))
        } else {
            btnMenu.setImageDrawable(getDrawable(R.drawable.ic_bell_disabled))
        }

        sqliteHelper.addAll(dateNow, 0, totalIntake)

        updateValues()
        milk.getDrawable().setTint(sharedPref.getInt(AppUtils.COLORCODE, R.color.colorSecondaryDark))
        tea.getDrawable().setTint(sharedPref.getInt(AppUtils.COLORCODE, R.color.colorSecondaryDark))
        water.getDrawable().setTint(sharedPref.getInt(AppUtils.COLORCODE, R.color.colorSecondaryDark))
        coffee.getDrawable().setTint(sharedPref.getInt(AppUtils.COLORCODE, R.color.colorSecondaryDark))
        cola.getDrawable().setTint(sharedPref.getInt(AppUtils.COLORCODE, R.color.colorSecondaryDark))
        juice.getDrawable().setTint(sharedPref.getInt(AppUtils.COLORCODE, R.color.colorSecondaryDark))


        btnMenu.setOnClickListener {
            notificStatus = !notificStatus
            sharedPref.edit().putBoolean(AppUtils.NOTIFICATION_STATUS_KEY, notificStatus).apply()
            if (notificStatus) {
                btnMenu.setImageDrawable(getDrawable(R.drawable.ic_bell))
                Snackbar.make(it, "Notification Enabled..", Snackbar.LENGTH_SHORT).show()
                alarm.setAlarm(
                    this,
                    sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
                )
            } else {
                btnMenu.setImageDrawable(getDrawable(R.drawable.ic_bell_disabled))
                Snackbar.make(it, "Notification Disabled..", Snackbar.LENGTH_SHORT).show()
                alarm.cancelAlarm(this)
            }
        }
        menu.setOnItemSelectedListener { id ->
            val option = when (id) {
                R.id.home -> {
                    R.color.home to "Home"
                    startActivity(Intent(this, StatsActivity::class.java))

                }
                R.id.activity -> {
                    R.color.activity to "Activity"
                    val bottomSheetFragment = BottomSheetFragment(this)
                    bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                }

                R.id.favorites -> {
                    R.color.favorites to "Favorites"
                    startActivity(Intent(this, OutlineActivity::class.java))
                }
                else -> R.color.colorSkyBlue to ""
            }
            }



        op50ml.setOnClickListener {

            selectedOption = 50
            op50ml.background = getDrawable(R.drawable.option_select_bg)
            op100ml.background = getDrawable(outValue.resourceId)
            op150ml.background = getDrawable(outValue.resourceId)
            op200ml.background = getDrawable(outValue.resourceId)
            op250ml.background = getDrawable(outValue.resourceId)
            opCustom.background = getDrawable(outValue.resourceId)
            createAlertDialog(outValue)

        }

        op100ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 100
            op50ml.background = getDrawable(outValue.resourceId)
            op100ml.background = getDrawable(R.drawable.option_select_bg)
            op150ml.background = getDrawable(outValue.resourceId)
            op200ml.background = getDrawable(outValue.resourceId)
            op250ml.background = getDrawable(outValue.resourceId)
            opCustom.background = getDrawable(outValue.resourceId)
            createAlertDialog(outValue)

        }

        op150ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 150
            op50ml.background = getDrawable(outValue.resourceId)
            op100ml.background = getDrawable(outValue.resourceId)
            op150ml.background = getDrawable(R.drawable.option_select_bg)
            op200ml.background = getDrawable(outValue.resourceId)
            op250ml.background = getDrawable(outValue.resourceId)
            opCustom.background = getDrawable(outValue.resourceId)
            createAlertDialog(outValue)

        }

        op200ml.setOnClickListener {

            selectedOption = 200
            op50ml.background = getDrawable(outValue.resourceId)
            op100ml.background = getDrawable(outValue.resourceId)
            op150ml.background = getDrawable(outValue.resourceId)
            op200ml.background = getDrawable(R.drawable.option_select_bg)
            op250ml.background = getDrawable(outValue.resourceId)
            opCustom.background = getDrawable(outValue.resourceId)
            createAlertDialog(outValue)


        }

        op250ml.setOnClickListener {

            selectedOption = 250
            op50ml.background = getDrawable(outValue.resourceId)
            op100ml.background = getDrawable(outValue.resourceId)
            op150ml.background = getDrawable(outValue.resourceId)
            op200ml.background = getDrawable(outValue.resourceId)
            op250ml.background = getDrawable(R.drawable.option_select_bg)
            opCustom.background = getDrawable(outValue.resourceId)
            createAlertDialog(outValue)

        }

        opCustom.setOnClickListener {

            val li = LayoutInflater.from(this)
            val promptsView = li.inflate(R.layout.custom_input_dialog, null)

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(promptsView)

            val userInput = promptsView
                .findViewById(R.id.etCustomInput) as TextInputLayout

            alertDialogBuilder.setPositiveButton("OK") { dialog, id ->
                val inputText = userInput.editText!!.text.toString()
                if (!TextUtils.isEmpty(inputText)) {
                    tvCustom.text = "${inputText} ml"
                    selectedOption = inputText.toInt()

                    if ((inTook * 100 / totalIntake) <= 140) {
                        if (sqliteHelper.addIntook(dateNow, selectedOption!!) > 0) {
                            inTook += selectedOption!!
                            setWaterLevel(inTook, totalIntake)

                            Snackbar.make(promptsView, "Your water intake was saved...!!", Snackbar.LENGTH_SHORT)
                                .show()

                        }
                    }
                }
            }.setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            op50ml.background = getDrawable(outValue.resourceId)
            op100ml.background = getDrawable(outValue.resourceId)
            op150ml.background = getDrawable(outValue.resourceId)
            op200ml.background = getDrawable(outValue.resourceId)
            op250ml.background = getDrawable(outValue.resourceId)
            opCustom.background = getDrawable(R.drawable.option_select_bg)

        }

    }


    private fun createAlertDialog( outValue:TypedValue)
    {
        val li = LayoutInflater.from(this)
        val promptsView = li.inflate(R.layout.dialog_layout, null)

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(promptsView)



        alertDialogBuilder.setPositiveButton("OK") { dialog, id ->

            if (selectedOption != null) {
                if(progressInfo<=100){

                    if ((inTook * 100 / totalIntake) <= 140) {
                        if (sqliteHelper.addIntook(dateNow, selectedOption!!) > 0) {
                            inTook += selectedOption!!
                            setWaterLevel(inTook, totalIntake)

                            Snackbar.make(
                                promptsView,
                                "Your water intake was saved...!!",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()

                        }
                    }
                } else {
                    Log.e("value","jj")

                    Snackbar.make(promptsView, "You already achieved the goal", Snackbar.LENGTH_SHORT).show()
                }
                selectedOption = null
                tvCustom.text = "Custom"
                op50ml.background = getDrawable(outValue.resourceId)
                op100ml.background = getDrawable(outValue.resourceId)
                op150ml.background = getDrawable(outValue.resourceId)
                op200ml.background = getDrawable(outValue.resourceId)
                op250ml.background = getDrawable(outValue.resourceId)
                opCustom.background = getDrawable(outValue.resourceId)
            }
        }.setNegativeButton("Cancel") { dialog, id ->
            dialog.cancel()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }


    private fun setWaterLevel(inTook: Int, totalIntake: Int) {

        YoYo.with(Techniques.SlideInDown)
            .duration(500)
            .playOn(tvIntook)
        progressInfo = ((inTook / totalIntake.toFloat()) * 100).toInt()
        if(progressInfo<=100){

        tvIntook.text = "$inTook"
        tvTotalIntake.text = "/$totalIntake ml"
        YoYo.with(Techniques.Pulse)
            .duration(500)
            .playOn(intakeProgress)
        intakeProgress.progress = progressInfo
            if(inTook>0){
                var  value=inTook/totalIntake
                value=value*100;
                Log.e("value",""+value)
                Log.e("progresss",""+progressInfo)

                //if ((inTook * 100 / totalIntake) > 140) {

        }
        }
        else
            Snackbar.make(main_activity_parent, "You achieved the goal", Snackbar.LENGTH_SHORT)
                .show()
    }


}

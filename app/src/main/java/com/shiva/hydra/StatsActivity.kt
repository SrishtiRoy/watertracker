package com.shiva.hydra

import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.shiva.hydra.R
import com.shiva.hydra.utils.AppUtils
import com.shiva.hydra.utils.ChartXValueFormatter
import com.shiva.hydra.utils.TopSpacingItemDecoration
import io.github.z3r0c00l_2k.aquadroid.helpers.SqliteHelper
import kotlinx.android.synthetic.main.activity_stats.*


class StatsActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: SqliteHelper
    private var totalPercentage: Float = 0f
    private var totalGlasses: Float = 0f
    private var totalCount: Float = 0f
    val dataList = ArrayList<Model>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        sqliteHelper = SqliteHelper(this)
        initRecyclerView()

        btnBack.setOnClickListener {
            finish()
        }

        val entries = ArrayList<Entry>()
        val dateArray = ArrayList<String>()

        val cursor: Cursor = sqliteHelper.getAllStats()

        if (cursor.moveToFirst()) {

            for (i in 0 until cursor.count) {
                dateArray.add(cursor.getString(1))
                totalCount+=cursor.getInt(2);
                val percent = cursor.getInt(2) / cursor.getInt(3).toFloat() * 100
                totalPercentage += percent
                totalGlasses += cursor.getInt(2)
                entries.add(Entry(i.toFloat(), percent))
                dataList.add(Model(""+cursor.getString(1), totalCount))

                cursor.moveToNext()

            }

        } else {
            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
        }




        if (!entries.isEmpty()) {

             chart.description.isEnabled = false
             chart.animateY(1000, Easing.Linear)
             chart.viewPortHandler.setMaximumScaleX(1.5f)
             chart.xAxis.setDrawGridLines(false)
             chart.xAxis.position = XAxis.XAxisPosition.TOP
             chart.xAxis.isGranularityEnabled = true
             chart.legend.isEnabled = false
             chart.fitScreen()
             chart.isAutoScaleMinMaxEnabled = true
             chart.scaleX = 1f
             chart.setPinchZoom(true)
             chart.isScaleXEnabled = true
             chart.isScaleYEnabled = false
             chart.axisLeft.textColor = Color.BLACK
             chart.xAxis.textColor = Color.BLACK
             chart.axisLeft.setDrawAxisLine(false)
             chart.xAxis.setDrawAxisLine(false)
             chart.setDrawMarkers(false)
             chart.xAxis.labelCount = 5
             val rightAxix = chart.axisRight
             rightAxix.setDrawGridLines(false)
             rightAxix.setDrawZeroLine(false)
             rightAxix.setDrawAxisLine(false)
             rightAxix.setDrawLabels(false)

             val dataSet = LineDataSet(entries, "Label")
             dataSet.setDrawCircles(false)
             dataSet.lineWidth = 2.5f
             //dataSet.color = ContextCompat.getColor(this, R.color.colorSecondaryDark)
             dataSet.setDrawFilled(true)
             //dataSet.fillDrawable = getDrawable(R.drawable.graph_fill_gradiant)
             dataSet.setDrawValues(false)

            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER)
            dataSet.setCubicIntensity(0.2f)
            dataSet.setDrawFilled(true)
            dataSet.setDrawCircles(false)
            dataSet.setLineWidth(1.8f)
            dataSet.setCircleRadius(4f)
            dataSet.setCircleColor(Color.GREEN)
            dataSet.setHighLightColor(Color.rgb(244, 117, 117))
            dataSet.setColor(getColor(R.color.colorSkyBlue))
            dataSet.setFillColor(getColor(R.color.colorSecondaryLighter))
            dataSet.setFillAlpha(100)
            dataSet.setDrawHorizontalHighlightIndicator(false)
            dataSet.setFillFormatter(IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum })

             val lineData = LineData(dataSet)
             chart.xAxis.valueFormatter = (ChartXValueFormatter(dateArray))
             chart.data = lineData
             chart.invalidate()

             val remaining = sharedPref.getInt(
                 AppUtils.TOTAL_INTAKE,
                 0
             ) - sqliteHelper.getIntook(AppUtils.getCurrentDate()!!)

           /*  if (remaining > 0) {
                 remainingIntake.text = "$remaining ml"
             } else {
                 remainingIntake.text = "0 ml"
             }

             targetIntake.text = "${sharedPref.getInt(
                 AppUtils.TOTAL_INTAKE,
                 0
             )
             } ml"*/

             val percentage = sqliteHelper.getIntook(AppUtils.getCurrentDate()!!) * 100 / sharedPref.getInt(
                 AppUtils.TOTAL_INTAKE,
                 0
             )

         }
    }

    private fun initRecyclerView(){

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@StatsActivity)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecorator)

          val  blogAdapter = RvAdapter(dataList)
            adapter = blogAdapter
        }
    }
}

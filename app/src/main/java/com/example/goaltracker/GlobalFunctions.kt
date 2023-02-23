package com.example.goaltracker

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt

fun roundDouble(value: Double, multiplier: Int): Double{
    return (floor(value * multiplier)) / multiplier
}

fun setCalendarToDayStart(calendar: Calendar): Calendar {
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar
}

fun setCalendarToDayEnd(calendar: Calendar): Calendar{
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar
}

fun getTimeDifferenceInDays(start: Calendar, end: Calendar): Int{
    val timeInMillis = end.timeInMillis - start.timeInMillis
    return floor(timeInMillis/ MILLIS_IN_DAY.toDouble()).toInt()
}

fun getTimeInMonths(start: Long, end: Long): Int{

    var monthDifference = 0
    val firstDateCalendar = Calendar.getInstance().apply { timeInMillis = start }
    val lastDateCalendar = Calendar.getInstance().apply { timeInMillis = end }

    val yearDifference = lastDateCalendar.get(Calendar.YEAR) - firstDateCalendar.get(Calendar.YEAR)
    if(yearDifference == 0){
        monthDifference = lastDateCalendar.get(Calendar.MONTH) - firstDateCalendar.get(Calendar.MONTH) + 1
    }
    else{
        monthDifference = MONTHS_IN_YEAR * (yearDifference - 1)
        monthDifference += MONTHS_IN_YEAR - (firstDateCalendar.get(Calendar.MONTH) - 1)
        monthDifference += lastDateCalendar.get(Calendar.MONTH)
    }
    return monthDifference
}

fun getTimeInYears(start: Long, end: Long): Int{
    val startCal = setCalendarToDayStart(Calendar.getInstance().apply{ timeInMillis = start})
    val endCal = setCalendarToDayStart(Calendar.getInstance().apply{ timeInMillis = end})
    if(endCal.get(Calendar.YEAR) == startCal.get(Calendar.YEAR)){
        return 1
    }
    return endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)
}

fun doubleHoursToHoursAndMinutes(duration: Double): Pair<Int, Int>{
    val hours = floor(duration).toInt()
    val minutes = ((duration-hours)*60).roundToInt()
    return Pair(hours, minutes)
}

fun hoursAndMinutesToDoubleHours(hours: Int, minutes: Int): Double{
    return hours + minutes/60.0
}

fun makeLineChart(chart: LineChart, data: List<Pair<Calendar, Double>>, context: Context, chartLength: DurationPeriod){
    val entries = data.map{ Entry(it.first.timeInMillis.toFloat(), it.second.toFloat()) }

    val lineDataSet = LineDataSet(entries, "Daily time")
    lineDataSet.colors = mutableListOf(Color.WHITE)
    lineDataSet.setDrawValues(false)
    lineDataSet.setDrawCircles(false)
    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    lineDataSet.lineWidth = 2f
    val dataSets = ArrayList<ILineDataSet>()
    dataSets.add(lineDataSet)

    val lineData = LineData(dataSets)
    chart.data = lineData
    chart.setTouchEnabled(false)
    chart.setPinchZoom(false)
    chart.setDrawGridBackground(false)

    val formatter = SimpleDateFormat("MMM dd", Locale.US)
    chart.xAxis.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return formatter.format(Date(value.toLong()))
        }
    }
    chart.axisLeft.valueFormatter = object: ValueFormatter(){
        override fun getFormattedValue(value: Float): String {
            val hourMinutePair = doubleHoursToHoursAndMinutes(value.toDouble())
            return String.format(
                context.getString(R.string.hours_and_minutes_placeholder),
                hourMinutePair.first,
                hourMinutePair.second
            )
        }
    }

    chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    chart.xAxis.setDrawGridLines(false)
    chart.xAxis.setDrawAxisLine(true)
    chart.xAxis.textColor = Color.WHITE

    chart.axisLeft.setDrawGridLines(false)
    chart.axisLeft.setDrawAxisLine(true)
    chart.axisLeft.textColor = Color.WHITE

    chart.axisRight.setDrawGridLines(false)
    chart.axisRight.setDrawAxisLine(false)

    chart.legend.isEnabled = false

    lineChartMargin(chart, chartLength)
}

fun makeBarChart(chart: BarChart, data: List<Pair<Calendar, Double>>, context: Context, chartLength: DurationPeriod){
    val entries = data.map{ BarEntry(it.first.timeInMillis.toFloat(), it.second.toFloat()) }

    val barDataSet = BarDataSet(entries, "Daily time")
    barDataSet.colors = mutableListOf(Color.WHITE)
    barDataSet.setDrawValues(false)
    barDataSet.barBorderWidth = if(chartLength == DurationPeriod.ThisMonth || chartLength == DurationPeriod.Month) 10f else 2f
    barDataSet.barBorderColor = Color.WHITE
    barDataSet.highLightAlpha = 0
    barDataSet.valueTextColor = Color.WHITE

    val dataSets = ArrayList<IBarDataSet>()
    dataSets.add(barDataSet)

    val barData = BarData(dataSets)
    chart.data = barData
    chart.setTouchEnabled(true)
    chart.setPinchZoom(false)
    chart.setDrawGridBackground(false)
    chart.description.isEnabled = false

    chart.setOnChartValueSelectedListener(object: OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            Toast.makeText(context, e?.y.toString(), Toast.LENGTH_SHORT).show()
        }

        override fun onNothingSelected() {
        }
    })

    val formatter = SimpleDateFormat("MMM dd", Locale.US)
    chart.xAxis.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return formatter.format(Date(value.toLong()))
        }
    }
    chart.axisLeft.valueFormatter = object: ValueFormatter(){
        override fun getFormattedValue(value: Float): String {
            val hourMinutePair = doubleHoursToHoursAndMinutes(value.toDouble())
            return String.format(
                context.getString(R.string.hours_and_minutes_placeholder),
                hourMinutePair.first,
                hourMinutePair.second
            )
        }
    }

    chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    chart.xAxis.setDrawGridLines(false)
    chart.xAxis.setDrawAxisLine(true)
    chart.xAxis.textColor = Color.WHITE

    chart.axisLeft.setDrawGridLines(false)
    chart.axisLeft.setDrawAxisLine(true)
    chart.axisLeft.textColor = Color.WHITE

    chart.axisRight.setDrawGridLines(false)
    chart.axisRight.setDrawAxisLine(false)

    chart.legend.isEnabled = false

    //add a margin of one day for the right and left side of the chart
    barChartMargin(chart, chartLength)
}

fun barChartMargin(chart: BarChart, durationPeriod: DurationPeriod){
    if(durationPeriod == DurationPeriod.Month){
        val startCalendar = setCalendarToDayStart(Calendar.getInstance())
        startCalendar.add(Calendar.DAY_OF_YEAR, -30)
        val endCalendar = setCalendarToDayStart(Calendar.getInstance())
        endCalendar.add(Calendar.DAY_OF_YEAR, 1)

        chart.xAxis.axisMaximum = endCalendar.timeInMillis.toFloat()
        chart.xAxis.axisMinimum = startCalendar.timeInMillis.toFloat()
    }
    else if(durationPeriod == DurationPeriod.Year){
        val startCalendar = setCalendarToDayStart(Calendar.getInstance())
        startCalendar.add(Calendar.DAY_OF_YEAR, -365)
        val endCalendar = setCalendarToDayStart(Calendar.getInstance())
        endCalendar.add(Calendar.DAY_OF_YEAR, 1)

        chart.xAxis.axisMaximum = endCalendar.timeInMillis.toFloat()
        chart.xAxis.axisMinimum = startCalendar.timeInMillis.toFloat()
    }
    else if(durationPeriod == DurationPeriod.ThisMonth){
        val startCalendar = setCalendarToDayStart(Calendar.getInstance())
        startCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endCalendar = setCalendarToDayStart(Calendar.getInstance())
        startCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMinimum(Calendar.DAY_OF_MONTH) + 1)

        chart.xAxis.axisMaximum = endCalendar.timeInMillis.toFloat()
        chart.xAxis.axisMinimum = startCalendar.timeInMillis.toFloat()
    }
    else{
        val startCalendar = setCalendarToDayStart(Calendar.getInstance())
        startCalendar.set(Calendar.DAY_OF_YEAR, startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR))
        val endCalendar = setCalendarToDayStart(Calendar.getInstance())
        startCalendar.set(Calendar.DAY_OF_YEAR, endCalendar.getActualMinimum(Calendar.DAY_OF_YEAR) + 1)

        chart.xAxis.axisMaximum = endCalendar.timeInMillis.toFloat()
        chart.xAxis.axisMinimum = startCalendar.timeInMillis.toFloat()
    }
}

fun lineChartMargin(chart: LineChart, durationPeriod: DurationPeriod){
    if(durationPeriod == DurationPeriod.Month){
        val startCalendar = setCalendarToDayStart(Calendar.getInstance())
        startCalendar.add(Calendar.DAY_OF_YEAR, -30)
        val endCalendar = setCalendarToDayStart(Calendar.getInstance())
        endCalendar.add(Calendar.DAY_OF_YEAR, 1)

        chart.xAxis.axisMaximum = endCalendar.timeInMillis.toFloat()
        chart.xAxis.axisMinimum = startCalendar.timeInMillis.toFloat()
    }
    else if(durationPeriod == DurationPeriod.Year){
        val startCalendar = setCalendarToDayStart(Calendar.getInstance())
        startCalendar.add(Calendar.DAY_OF_YEAR, -365)
        val endCalendar = setCalendarToDayStart(Calendar.getInstance())
        endCalendar.add(Calendar.DAY_OF_YEAR, 1)

        chart.xAxis.axisMaximum = endCalendar.timeInMillis.toFloat()
        chart.xAxis.axisMinimum = startCalendar.timeInMillis.toFloat()
        chart.axisLeft.axisMinimum = 0f
    }
    else if(durationPeriod == DurationPeriod.ThisMonth){
        val startCalendar = setCalendarToDayStart(Calendar.getInstance())
        startCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        val endCalendar = setCalendarToDayStart(Calendar.getInstance())
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1)

        chart.xAxis.axisMaximum = endCalendar.timeInMillis.toFloat()
        chart.xAxis.axisMinimum = startCalendar.timeInMillis.toFloat()
    }
    else{
        val startCalendar = setCalendarToDayStart(Calendar.getInstance())
        startCalendar.set(Calendar.DAY_OF_YEAR, startCalendar.getActualMinimum(Calendar.DAY_OF_YEAR))
        val endCalendar = setCalendarToDayStart(Calendar.getInstance())
        endCalendar.set(Calendar.DAY_OF_YEAR, endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) + 1)

        chart.xAxis.axisMaximum = endCalendar.timeInMillis.toFloat()
        chart.xAxis.axisMinimum = startCalendar.timeInMillis.toFloat()
        chart.axisLeft.axisMinimum = 0f
    }
}

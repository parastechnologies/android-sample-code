package com.in2bliss.ui.activity.home.fragment.meditationTracker.dashboard

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.in2bliss.R
import com.in2bliss.base.BaseFragment
import com.in2bliss.data.model.MeditationTrackerStreakResponse
import com.in2bliss.data.networkRequest.ApiConstant
import com.in2bliss.data.networkRequest.apiResponseHandler.handleResponse
import com.in2bliss.databinding.FragmentDashboardBinding
import com.in2bliss.ui.activity.ProgressDialog
import com.in2bliss.ui.activity.alertDialogBox
import com.in2bliss.ui.activity.home.fragment.meditationTracker.bottomSheet.SelectMeditationTypeBottomSheet
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.getCurrentDate
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(
    layoutInflater = FragmentDashboardBinding::inflate
) {

    private val viewModel: DashboardVM by viewModels()

    private lateinit var barData: BarData

    private lateinit var barDataSet: BarDataSet

    private var barEntriesList: ArrayList<BarEntry> = ArrayList()

    private var total: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.data = viewModel
        recyclerView()
        observer()
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.setCalendarData()
        }
        viewModel.retryApiRequest(
            apiName = ApiConstant.MEDITATION_STREAK
        )
        onClick()
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.meditationStreakReasonsResponse.collectLatest {
                handleResponse(
                    response = it,
                    context = requireActivity(),
                    success = { response ->
                        viewModel.totalMeditationTime.set(
                            "${
                                String.format(
                                    "%02d", secondsToMinutes(
                                        response.data?.totalMeditationTime ?: 0
                                    ).toInt()
                                )
                            } min"
                        )
                        viewModel.currentStreak.set(
                            String.format("%02d", response.data?.weekStreak?.toInt() ?: 0)
                        )
                        viewModel.totalEntries.set(
                            String.format("%02d", response.data?.totalEntries ?: 0)
                        )

                        CoroutineScope(Dispatchers.Main).launch {
                            setGraphData(response.graphData)
                        }
                    },
                    error = { message, apiName ->
                        requireActivity().alertDialogBox(
                            message = message,
                            retry = {
                                viewModel.retryApiRequest(
                                    apiName = apiName
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    private fun onClick() {
        binding.btnStartMeditation.setOnClickListener {
            val dialog = SelectMeditationTypeBottomSheet()
            dialog.show(childFragmentManager, "")
        }
    }

    private fun recyclerView() {
        binding.rvHorizontalCalendar.itemAnimator = null
        binding.rvHorizontalCalendar.adapter = viewModel.horizontalCalendar
        viewModel.selectedPositionListener = { scrollPosition ->
            binding.rvHorizontalCalendar.scrollToPosition(scrollPosition)
            binding.rvHorizontalCalendar.suppressLayout(true)
        }
    }

    private fun setGraphData(graphData: ArrayList<MeditationTrackerStreakResponse.GraphData>) {
        getAverage(graphData)
        getBarChartData(graphData)
        binding.idBarChart.legend.isEnabled = false
        binding.idBarChart.description.isEnabled = false
        barDataSet = BarDataSet(barEntriesList, "")
        val barChartRender = CustomBarChartRender(
            binding.idBarChart,
            binding.idBarChart.animator,
            binding.idBarChart.viewPortHandler
        )
        barChartRender.setRadius(20)
        binding.idBarChart.renderer = barChartRender
        binding.idBarChart.extraBottomOffset = 20f
        binding.idBarChart.xAxis.isEnabled = true
        binding.idBarChart.xAxis.setDrawGridLines(false)
        binding.idBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barData = BarData(barDataSet)
        barData.barWidth = 0.4f
        barDataSet.color = ContextCompat.getColor(requireContext(), R.color.blue_1c92f1)
        barData.isHighlightEnabled = false
        binding.idBarChart.data = barData
        barDataSet.valueTextColor = Color.BLACK

        barDataSet.valueTextSize = 4f
        binding.idBarChart.description.isEnabled = false
        barDataSet.colors = colorArrays
        binding.idBarChart.invalidate()
        binding.idBarChart.animate()
        binding.idBarChart.setScaleEnabled(false)
        binding.idBarChart.animateX(3000)
        binding.idBarChart.axisLeft.isEnabled = false
        binding.idBarChart.axisRight.gridColor =
            ContextCompat.getColor(requireContext(), R.color.color_DBD9E9)
        binding.idBarChart.xAxis.textColor =
            ContextCompat.getColor(requireContext(), R.color.purple_5a52ed)
        binding.idBarChart.xAxis.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)

    }

    private fun getAverage(graphData: ArrayList<MeditationTrackerStreakResponse.GraphData>) {
        total = 0
        for (i in 0 until graphData.size) {
            total += graphData[i].minutes!!
        }
        if (total == 0) {
            binding.mcvCard.gone()
            binding.tvNoDatFound.visible()
        } else {
//            val averageTime = "${String.format("%.02f", secondsToMinutes(total / 7))} min"
            val averageTime = secondsToMinutes(60/7).toString().take(4).plus("min")
            binding.tvAverageTime.text = averageTime
        }

    }

    private fun secondsToMinutes(seconds: Int): Double {
        return seconds.toDouble() / 60.0
    }

    private val colorArrays = mutableListOf<Int>()

    private fun getBarChartData(graphData: ArrayList<MeditationTrackerStreakResponse.GraphData>) {
        binding.idBarChart.setXAxisRenderer(
            CustomXAxisRenderer(
                binding.idBarChart.viewPortHandler,
                binding.idBarChart.xAxis,
                binding.idBarChart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )
        val currentDate = getCurrentDate()
        for (i in 0 until graphData.size) {
            graphData[i].minutes?.let {
                BarEntry(i.toFloat(), secondsToMinutes(it).toFloat()).let { barEntriesList.add(it) }
            }
            if (currentDate == graphData[i].date) {
                colorArrays.add(Color.parseColor("#5a52ed"))
            } else {
                colorArrays.add(Color.parseColor("#DBD9E8"))
            }
        }
        binding.idBarChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val data = graphData[value.roundToInt()]
                return "${data.day}\n${formatDate(data.date!!, "yyyy-MM-dd", "dd-MM")}"

            }
        }
        binding.idBarChart.invalidate()
        binding.idBarChart.animate()
    }

}

class CustomXAxisRenderer(viewPortHandler: ViewPortHandler?, xAxis: XAxis?, trans: Transformer?) :
    XAxisRenderer(viewPortHandler, xAxis, trans) {
    override fun drawLabel(
        c: Canvas,
        formattedLabel: String,
        x: Float,
        y: Float,
        anchor: MPPointF,
        angleDegrees: Float
    ) {
        val line = formattedLabel.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees)
//        Utils.drawXAxisValue(
//            c,
//            line[1],
//            x + mAxisLabelPaint.textSize,
//            y + mAxisLabelPaint.textSize,
//            mAxisLabelPaint,
//            anchor,
//            angleDegrees
//        )
        Utils.drawXAxisValue(
            c,
            line[1],
            x,
            y + mAxisLabelPaint.textSize,
            mAxisLabelPaint,
            anchor,
            angleDegrees
        )
    }
}

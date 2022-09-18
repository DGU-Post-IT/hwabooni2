package com.postit.hwabooni2.presentation.detail

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.postit.hwabooni2.R
import com.postit.hwabooni2.databinding.ActivityDetailBinding
import com.postit.hwabooni2.model.Emotion
import com.postit.hwabooni2.model.FriendData
import com.postit.hwabooni2.model.PlantRecord
import com.postit.hwabooni2.presentation.detail.DetailActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val id = intent.getStringExtra(EXTRA_ID_KEY)
        if (id == null) finish()

        /*
        유저 정보 로드
        이름, 현재 감정, 식물 사진
         */
        db.collection("User").document(id!!).get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    val data = task.result.toObject(FriendData::class.java)
                    binding.friendName.text = data!!.name
                    val drawable = AppCompatResources.getDrawable(
                        binding.root.context, Emotion.values()[data.emotion.toInt()].icon
                    )
                    binding.friendEmotionView.setImageDrawable(drawable)
                    binding.plantImageView.visibility = View.VISIBLE
                    Glide.with(binding.root)
                        .load(data.plantImage)
                        .transform(CenterCrop(), RoundedCorners(12))
                        .into(binding.plantImageView)
                }
            }

        /*
        유저의 이벤트 리스트 로드
        이벤트 리스트에서 물주기, 예쁜말 한 날짜 체크 후 달력에 그리는 작업 호출
         */
        val today = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        val firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
        val date = Date.from(firstDayOfMonth.toInstant(ZoneOffset.of("+9")))
        val check = Array(32) { arrayOf(false, false) }
        val count = AtomicInteger()
        db.collection("User").document(id)
            .collection("event")
            .whereGreaterThanOrEqualTo("timestamp", date)
            .get().addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                for (doc in it.result.documents) {
                    val temp = LocalDateTime.ofInstant(
                        (doc["timestamp"] as Timestamp?)!!.toDate().toInstant(),
                        ZoneId.of("Asia/Seoul")
                    )
                    check[temp.dayOfMonth][0] =
                        check[temp.dayOfMonth][0] or (doc["type"] as Long == 1L)
                    check[temp.dayOfMonth][1] =
                        check[temp.dayOfMonth][1] or (doc["type"] as Long == 2L)
                    Log.d(
                        TAG,
                        "onCreate: data is here $temp ${check[temp.dayOfMonth][0]} ${check[temp.dayOfMonth][1]}"
                    )
                }
                updateDecorator(check)
            }

        /*
        친구의 식물 데이터 가져오기
         */
        val path = db.collection("User").document(id).collection("plant")

        path.limit(1).get().addOnCompleteListener {
            if (it.isSuccessful.not()) return@addOnCompleteListener
            if (it.result.isEmpty) {
                //식물 없을 때 처리
            } else {
                path.document(it.result.documents.first().id)
                    .collection("record")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1).get().addOnCompleteListener {
                        if (it.isSuccessful && !it.result.isEmpty) {
                            it.result.documents.first().toObject<PlantRecord>()?.let {
                                showPlantInfo(it)
                            }
                        }
                    }
            }
        }

//        db.collection("dummyFriend").document(id)
//            .collection("watering").whereGreaterThanOrEqualTo("datetime", date)
//            .get().addOnCompleteListener { task: Task<QuerySnapshot> ->
//                if (task.isSuccessful) {
//                    for (doc in task.result.documents) {
//                        val temp = LocalDateTime.ofInstant(
//                            (doc["datetime"] as Timestamp?)!!.toDate().toInstant(),
//                            ZoneId.of("Asia/Seoul")
//                        )
//                        check[temp.dayOfMonth] += 1
//                    }
//                    if (count.incrementAndGet() == 2) updateDecorator(check)
//                }
//            }
//        db.collection("dummyFriend").document(id)
//            .collection("prettyWord").whereGreaterThanOrEqualTo("datetime", date)
//            .get().addOnCompleteListener { task: Task<QuerySnapshot> ->
//                if (task.isSuccessful) {
//                    for (doc in task.result.documents) {
//                        val temp = LocalDateTime.ofInstant(
//                            (doc["datetime"] as Timestamp?)!!.toDate().toInstant(),
//                            ZoneId.of("Asia/Seoul")
//                        )
//                        check[temp.dayOfMonth] += 2
//                    }
//                    if (count.incrementAndGet() == 2) updateDecorator(check)
//                }
//            }

        /*
        앱 사용 기록 바 그래프 (랜덤 데이터)
         */
        initBarChart()
        val arr = ArrayList<BarEntry>()
        val random = Random()
        for (i in 0..29) {
            arr.add(BarEntry(i.toFloat(), random.nextFloat()))
        }
        val bds = BarDataSet(arr, "")
        bds.setDrawValues(false)
        bds.color = android.graphics.Color.YELLOW
        binding.dailyLogBarChart.data = BarData(bds)
        binding.dailyLogBarChart.invalidate()
        binding.root.viewTreeObserver.addOnScrollChangedListener {
            val scrollBounds = Rect()
            binding.root.getHitRect(scrollBounds)
            binding.monthlyLogBarChart.animate =
                binding.monthlyLogBarChart.getLocalVisibleRect(scrollBounds)
        }

        /*
        감정기록 불러오기
        월별 감정 분포 그래프 그리기
         */
        db.collection("User").document(id).collection("emotion").get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    if (task.result.isEmpty) return@addOnCompleteListener
                    val emotionMap = Emotion.values().associateBy({ it.type }, { 0 }).toSortedMap()
                    val entries = task.result.documents.map { it.get("emotion").toString().toInt() }
                        .groupingBy { it }.eachCount()
                    val amountOfEachEmotionMap = emotionMap + entries
                    val colorList =
                        Emotion.values().map { Color(applicationContext.getColor(it.color)) }

                    binding.monthlyLogBarChart.apply {
                        data = amountOfEachEmotionMap.values.toList()
                        colors = colorList
                        strokeWidth = 160f
                        modifier = Modifier.fillMaxSize()
                    }


                }
            }
    }

    /*
    로그 바 그래프 초기화
     */
    private fun initBarChart() {
        binding.dailyLogBarChart.apply {
            defaultFocusHighlightEnabled = false
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false
            isClickable = false
            isDoubleTapToZoomEnabled = false
            setDrawBorders(false)
            setDrawGridBackground(false)
            description.isEnabled = false
            legend.isEnabled = false
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawLabels(false)
            axisLeft.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.setDrawLabels(false)
            xAxis.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            axisRight.setDrawLabels(false)
            axisRight.setDrawAxisLine(false)
        }
    }

    fun updateDecorator(chk: Array<Array<Boolean>>) {
        binding.calendarView.removeDecorators()
        binding.calendarView.addDecorators(
            PrettyWordDecorator(chk),
            WateringDecorator(chk),
            BothDecorator(chk)
        )
        binding.calendarView.invalidate()
    }

    internal inner class PrettyWordDecorator(private val chk: Array<Array<Boolean>>) :
        DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return chk[day.day][1] && !chk[day.day][0]
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    applicationContext,
                    R.drawable.ic_heart
                )!!
            )
        }
    }

    internal inner class WateringDecorator(private val chk: Array<Array<Boolean>>) :
        DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return chk[day.day][0] && !chk[day.day][1]
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    applicationContext,
                    R.drawable.ic_line_new
                )!!
            )
        }
    }

    internal inner class BothDecorator(private val chk: Array<Array<Boolean>>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return chk[day.day][0] && chk[day.day][1]
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    applicationContext,
                    R.drawable.ic_heart_line
                )!!
            )
        }
    }

    fun showPlantInfo(record: PlantRecord) {
        try {
            setHumid(record.getHumid())
            setTemp(record.getTemp())
        } catch (e: Exception) {
            setHumid(-99999.0)
            setTemp(-99999.0)
        }
    }

    fun setHumid(value: Double) {
        if (value == -99999.0) {
            binding.humidNo.visibility = View.VISIBLE
            binding.humidIndicator.visibility = View.GONE
            return
        } else {
            binding.humidNo.visibility = View.GONE
            binding.humidIndicator.visibility = View.VISIBLE
            if (value <= 700) {
                binding.humidIndicator.value = 0.999
            } else if (value >= 4000) {
                binding.humidIndicator.value = 0.001
            } else {
                binding.humidIndicator.value = 1 - (value - 700) / 3300
            }
        }
    }

    fun setTemp(value: Double) {
        if (value == -99999.0) {
            binding.tempNo.visibility = View.VISIBLE
            binding.tempIndicator.visibility = View.GONE
            return
        } else {
            binding.tempNo.visibility = View.GONE
            binding.tempIndicator.visibility = View.VISIBLE
            if (value <= 20) {
                binding.tempIndicator.value = 0.001
            } else if (value >= 30) {
                binding.tempIndicator.value = 0.999
            } else {
                binding.tempIndicator.value = (value - 20) / 10
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val rect = Rect()
            currentFocus!!.getGlobalVisibleRect(rect)
            if (!rect.contains(ev.x.toInt(), ev.y.toInt())) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                currentFocus!!.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        private const val TAG = "DetailActivity"
        fun getIntent(context: Context?, id: String?): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_ID_KEY, id)
            return intent
        }

        const val EXTRA_ID_KEY = "EXTRA_ID_KEY"
    }
}
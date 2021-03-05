package com.example.boxtypetemplate.exercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.boxtypetemplate.R
import kotlinx.android.synthetic.main.activity_exercise.*
import java.text.SimpleDateFormat
import java.util.*

class ExerciseActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var todayWeekOfMonth = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH)
    var todayWeekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    var selectedTab = todayWeekday
    val sampleDate = arrayListOf<WeekdayData>()
    val sampleDateStringList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)
        setSupportActionBar(findViewById(R.id.toolbar_exercise))

        //초기화
        //샘플: 4주 전부터 이번주까지의 데이터
        //나중에 데이터 불러와서 저장된 데이터가 있는 날짜만 집어넣도록 바꿔야함.
        //------------- spinner에 데이터 집어넣기--------------------- //
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        for(i in 3 downTo 0){
            val startOfWeek = Calendar.getInstance().run {
                set(Calendar.WEEK_OF_MONTH, get(Calendar.WEEK_OF_MONTH) - i)
                set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                dateFormat.format(time)
            }
            val endOfWeek = Calendar.getInstance().run {
                set(Calendar.WEEK_OF_MONTH, get(Calendar.WEEK_OF_MONTH) - i)
                set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                dateFormat.format(time)
            }
            val weekOfMonth = Calendar.getInstance().run {
                set(Calendar.WEEK_OF_MONTH, get(Calendar.WEEK_OF_MONTH) - i)
                get(Calendar.WEEK_OF_MONTH).toString()
            }
            val weekdayData = WeekdayData(startOfWeek, endOfWeek, weekOfMonth)
            sampleDate.add(weekdayData)
            sampleDateStringList.add(weekdayData.formedString)
            Log.d("exercise", "${weekdayData.formedString}")
        }


        //spinner 어댑터, 리스너, 기본값 설정
        spinner_exercise_calender.adapter =
            ArrayAdapter(this, R.layout.spinneritem_exercise, sampleDateStringList)
        spinner_exercise_calender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) { }
                override fun onItemSelected( parent: AdapterView<*>?,  view: View?,  position: Int,  id: Long) {
                    //미래 날짜의 요일 비활성화
                    disableFutureWeekdayBtn()
                    //일요일이 선택된 상태로 변경
                    setTabSelected(Calendar.SUNDAY)
                }
            }
        spinner_exercise_calender.apply{ setSelection(adapter.count-1) }

        //탭 초기 설정, 최초 실행시 오늘 선택하도록 설정, 오늘 이후의 요일 비활성화
        disableFutureWeekdayBtn()
        setTabSelected(todayWeekday)
        //탭 동작 구현
        initAllTabListener()

        btn_exercise_toggle_content.setOnClickListener {
            when(fl_exercise_content.visibility){
                View.VISIBLE -> {
                    fl_exercise_content.isClickable = false
                    fl_exercise_content.visibility = View.INVISIBLE
                    fl_exercise_content_details.isClickable = true
                    fl_exercise_content_details.visibility = View.VISIBLE
                    btn_exercise_toggle_content.text = "< Back"
                }
                View.INVISIBLE ->{
                    fl_exercise_content.isClickable = true
                    fl_exercise_content.visibility = View.VISIBLE
                    fl_exercise_content_details.isClickable = false
                    fl_exercise_content_details.visibility = View.INVISIBLE
                    btn_exercise_toggle_content.text = "details >"
                }
            }

        }
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }

    //선택된 주차가 이번주일 때만 동작하며, 미래의 요일을 비활성화.
    private fun disableFutureWeekdayBtn(){
        setAllTabEnabled(true)
        //이번주차의 formed string을 생성해 비교, 같을 때만 동작
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val startOfWeek = Calendar.getInstance().run {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            dateFormat.format(time)
        }
        val endOfWeek = Calendar.getInstance().run {
            set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            dateFormat.format(time)
        }
        val weekOfMonth = Calendar.getInstance().run {
            get(Calendar.WEEK_OF_MONTH).toString()
        }
        val weekdayData = WeekdayData(startOfWeek, endOfWeek, weekOfMonth)
        //선택된 드롭다운 아이템이 이번주일 때,
        if(spinner_exercise_calender.selectedItem.toString() == weekdayData.formedString){
            when(todayWeekday){
                Calendar.SUNDAY -> {
                    btn_exercise_mon.isEnabled = false
                    btn_exercise_tue.isEnabled = false
                    btn_exercise_wed.isEnabled = false
                    btn_exercise_thu.isEnabled = false
                    btn_exercise_fri.isEnabled = false
                    btn_exercise_sat.isEnabled = false
                }
                Calendar.MONDAY -> {
                    btn_exercise_tue.isEnabled = false
                    btn_exercise_wed.isEnabled = false
                    btn_exercise_thu.isEnabled = false
                    btn_exercise_fri.isEnabled = false
                    btn_exercise_sat.isEnabled = false
                }
                Calendar.TUESDAY -> {
                    btn_exercise_wed.isEnabled = false
                    btn_exercise_thu.isEnabled = false
                    btn_exercise_fri.isEnabled = false
                    btn_exercise_sat.isEnabled = false
                }
                Calendar.WEDNESDAY -> {
                    btn_exercise_thu.isEnabled = false
                    btn_exercise_fri.isEnabled = false
                    btn_exercise_sat.isEnabled = false
                }
                Calendar.THURSDAY -> {
                    btn_exercise_fri.isEnabled = false
                    btn_exercise_sat.isEnabled = false
                }
                Calendar.FRIDAY -> {
                    btn_exercise_sat.isEnabled = false
                }
            }

        }
    }
    private fun setTabSelected(weekday : Int){
        setAllTabDisslected()
        when(weekday){
            Calendar.SUNDAY -> { btn_exercise_sun.isSelected = true }
            Calendar.MONDAY -> { btn_exercise_mon.isSelected = true }
            Calendar.TUESDAY -> { btn_exercise_tue.isSelected = true }
            Calendar.WEDNESDAY -> { btn_exercise_wed.isSelected = true }
            Calendar.THURSDAY -> { btn_exercise_thu.isSelected = true }
            Calendar.FRIDAY -> { btn_exercise_fri.isSelected = true }
            Calendar.SATURDAY -> { btn_exercise_sat.isSelected = true }
        }
    }
    private fun setAllTabDisslected(){
        btn_exercise_sun.isSelected = false
        btn_exercise_mon.isSelected = false
        btn_exercise_tue.isSelected = false
        btn_exercise_wed.isSelected = false
        btn_exercise_thu.isSelected = false
        btn_exercise_fri.isSelected = false
        btn_exercise_sat.isSelected = false
    }
    private fun initAllTabListener(){
        btn_exercise_sun.setOnClickListener{ Calendar.SUNDAY.let{ selectedTab = it; setTabSelected(it)} }
        btn_exercise_mon.setOnClickListener{ Calendar.MONDAY.let{ selectedTab = it; setTabSelected(it)} }
        btn_exercise_tue.setOnClickListener{ Calendar.TUESDAY.let{ selectedTab = it; setTabSelected(it)} }
        btn_exercise_wed.setOnClickListener{ Calendar.WEDNESDAY.let{ selectedTab = it; setTabSelected(it)} }
        btn_exercise_thu.setOnClickListener{ Calendar.THURSDAY.let{ selectedTab = it; setTabSelected(it)} }
        btn_exercise_fri.setOnClickListener{ Calendar.FRIDAY.let{ selectedTab = it; setTabSelected(it)} }
        btn_exercise_sat.setOnClickListener{ Calendar.SATURDAY.let{ selectedTab = it; setTabSelected(it)} }
    }
    private fun setAllTabEnabled(b : Boolean){
        btn_exercise_sun.isEnabled = b
        btn_exercise_mon.isEnabled = b
        btn_exercise_tue.isEnabled = b
        btn_exercise_wed.isEnabled = b
        btn_exercise_thu.isEnabled = b
        btn_exercise_fri.isEnabled = b
        btn_exercise_sat.isEnabled = b
    }
}

data class WeekdayData(val startOfWeek : String, val endOfWeek : String, val weekOfMonth : String) {
    val formedString = "$startOfWeek ~ $endOfWeek(Week $weekOfMonth)"
}

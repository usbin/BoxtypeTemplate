package com.example.boxtypetemplate.risk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.FrameMetrics
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.boxtypetemplate.R
import com.example.boxtypetemplate.SettingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_risk.*

class RiskActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk)
        setSupportActionBar(findViewById(R.id.toolbar_risk))

        risk_pager.adapter = PagerAdapter(supportFragmentManager, lifecycle)
        risk_pager.registerOnPageChangeCallback(PageChangeCallback())
        risk_bottom_navigation.setOnNavigationItemSelectedListener{ navigationSelected(it) }



    }
    private inner class PagerAdapter(fm: FragmentManager, lc: Lifecycle): FragmentStateAdapter(fm,lc){
        override fun getItemCount(): Int = 4
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    RiskTabFragment().apply {
                        arguments = Bundle().apply {
                            putString("tabTitle", "risk")
                        }
                    }
                }
                1 -> {
                    RiskTabFragment().apply {
                        arguments = Bundle().apply {
                            putString("tabTitle", "goal")
                        }
                    }
                }
                2 -> {
                    RiskTabFragment().apply {
                        arguments = Bundle().apply {
                            putString("tabTitle", "step")
                        }
                    }
                }
                3 -> {
                    RiskTabFragment().apply {
                        arguments = Bundle().apply {
                            putString("tabTitle", "speed")
                        }
                    }
                }
                else -> error("no such position: ${position}")
            }
        }
    }
    //네비게이션 버튼 클릭을 페이지 이동과 연동
    private fun navigationSelected(item : MenuItem): Boolean{
        val checked = item.setChecked(true)
        when(checked.itemId){
            R.id.risk_bottom_tab_risk -> {
                risk_pager.currentItem = 0
                return true
            }
            R.id.risk_bottom_tab_goal -> {
                risk_pager.currentItem = 1
                return true
            }
            R.id.risk_bottom_tab_step -> {
                risk_pager.currentItem = 2
                return true
            }
            R.id.risk_bottom_tab_speed -> {
                risk_pager.currentItem = 3
                return true
            }
        }
        return false
    }
    //페이지 이동을 네비게이션과 연동
    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            risk_bottom_navigation.selectedItemId = when (position) {
                0 -> R.id.risk_bottom_tab_risk
                1 -> R.id.risk_bottom_tab_goal
                2 -> R.id.risk_bottom_tab_step
                3 -> R.id.risk_bottom_tab_speed
                else -> error("no such position: $position")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.risk_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.risk_menu_setting -> {
                val intent = Intent(this, RiskSettingActivity::class.java)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
package com.example.boxtypetemplate.management

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.boxtypetemplate.R
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_management.*
import kotlinx.android.synthetic.main.dialog_device_list.*
import kotlinx.android.synthetic.main.fragment_management_fitvideo.*

class ManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_management)

        pager_management.adapter = ManagementPageAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(tab_management, pager_management) { tab, position ->
            when(position){
                0 -> tab.text = "Fit video"
                1 -> tab.text = "Vital video"
            }
        }.attach()

    }

    private inner class ManagementPageAdapter(fm: FragmentManager, lc: Lifecycle): FragmentStateAdapter(fm,lc){
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FitVideoFragment()
                1 -> VitalVideoFragment()
                else -> error("no such position: ${position}")
            }
        }
    }
}

class FitVideoFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_management_fitvideo, container, false)
    }

    override fun onResume() {
        super.onResume()
        rv_management_fitvideo.adapter = ManagementVideoAdapter()
        rv_management_fitvideo.layoutManager = LinearLayoutManager(view?.context, LinearLayoutManager.VERTICAL, false)
    }

}

class VitalVideoFragment:Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_management_vitalvideo, container, false)
    }
}
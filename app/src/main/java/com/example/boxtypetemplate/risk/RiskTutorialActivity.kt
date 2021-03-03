package com.example.boxtypetemplate.risk

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.boxtypetemplate.R
import kotlinx.android.synthetic.main.activity_risk.*
import kotlinx.android.synthetic.main.activity_risk.risk_bottom_navigation
import kotlinx.android.synthetic.main.activity_risk_tutorial.*
import kotlinx.android.synthetic.main.fragment_risk_tutorial.*

class RiskTutorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_tutorial)
        setSupportActionBar(findViewById(R.id.toolbar_risk_tutorial))
        risk_tutorial_pager.adapter = PagerAdapter(supportFragmentManager, lifecycle)

    }

    override fun onResume() {
        super.onResume()
    }

    private inner class PagerAdapter(fm: FragmentManager, lc: Lifecycle): FragmentStateAdapter(fm,lc){
        override fun getItemCount(): Int = 4
        override fun createFragment(position: Int): Fragment {

            var fragment : Fragment? = null
            return when (position) {
                0 -> {
                    fragment = RiskTutorialFragment()
                    val bundle = Bundle(1)
                    bundle.putString("description", "Tutorial - first page description.")
                    fragment.arguments = bundle
                    fragment
                }
                1 -> {
                    fragment = RiskTutorialFragment()
                    val bundle = Bundle(1)
                    bundle.putString("description", "Tutorial - second page description.")
                    fragment.arguments = bundle
                    fragment
                }
                2 -> {
                    fragment = RiskTutorialFragment()
                    val bundle = Bundle(1)
                    bundle.putString("description", "Tutorial - third page description.")
                    fragment.arguments = bundle
                    fragment
                }
                3 -> {
                    fragment = RiskTutorialFragment()
                    val bundle = Bundle(1)
                    bundle.putString("description", "Tutorial - fourth page description.")
                    fragment.arguments = bundle
                    fragment
                }
                else -> error("no such position: ${position}")
            }
        }
    }
}

class RiskTutorialFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var description : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            description = it.getString("description")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_risk_tutorial, container, false)
    }

    override fun onResume() {
        super.onResume()
        tv_risk_tutorial_description.text = description
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RiskTutorialFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RiskTutorialFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
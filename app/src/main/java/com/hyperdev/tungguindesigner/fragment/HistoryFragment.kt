package com.hyperdev.tungguindesigner.fragment

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.adapter.PagerAdapter
import com.hyperdev.tungguindesigner.utils.UtilsContant.Companion.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT

class HistoryFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_hisotri, container, false)

        tabLayout = view.findViewById(R.id.tabs) as TabLayout
        viewPager = view.findViewById(R.id.container) as ViewPager

        tabLayout.addTab(tabLayout.newTab().setText("Order"))
        tabLayout.addTab(tabLayout.newTab().setText("Wallet Transaction"))
        tabLayout.addTab(tabLayout.newTab().setText("Withdraw"))

        //Memanggil dan Memasukan Value pada Class PagerAdapter(FragmentManager dan JumlahTab)
        val pageAdapter = PagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabLayout.tabCount)

        //Memasang Adapter pada ViewPager
        viewPager.adapter = pageAdapter
        /*
         Menambahkan Listener yang akan dipanggil kapan pun halaman berubah atau
         bergulir secara bertahap, sehingga posisi tab tetap singkron
         */
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        //Callback Interface dipanggil saat status pilihan tab berubah.
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //Dipanggil ketika tab memasuki state/keadaan yang dipilih.
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return view
    }
}
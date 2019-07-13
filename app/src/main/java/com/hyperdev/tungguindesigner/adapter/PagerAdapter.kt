package com.hyperdev.tungguindesigner.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hyperdev.tungguindesigner.fragment.OrderHistoriFragment
import com.hyperdev.tungguindesigner.fragment.TransactionHistoriFragment
import com.hyperdev.tungguindesigner.fragment.WithdrawHistoriFragment

class PagerAdapter(fm: FragmentManager, behavior: Int, private val numberTabs: Int) :
    FragmentPagerAdapter(fm, behavior) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> OrderHistoriFragment()
            1 -> TransactionHistoriFragment()
            2 -> WithdrawHistoriFragment()
            else -> null
        }!!
    }

    override fun getCount(): Int {
        return numberTabs
    }
}
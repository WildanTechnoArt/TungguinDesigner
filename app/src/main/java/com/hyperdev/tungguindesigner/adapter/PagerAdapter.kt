package com.hyperdev.tungguindesigner.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hyperdev.tungguindesigner.fragment.OrderHistoryFragment
import com.hyperdev.tungguindesigner.fragment.TransactionHistoryFragment
import com.hyperdev.tungguindesigner.fragment.WithdrawHistoriFragment

class PagerAdapter(fm: FragmentManager, behavior: Int, private val numberTabs: Int) :
    FragmentStatePagerAdapter(fm, behavior) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> OrderHistoryFragment()
            1 -> TransactionHistoryFragment()
            2 -> WithdrawHistoriFragment()
            else -> null
        }!!
    }

    override fun getCount(): Int {
        return numberTabs
    }
}
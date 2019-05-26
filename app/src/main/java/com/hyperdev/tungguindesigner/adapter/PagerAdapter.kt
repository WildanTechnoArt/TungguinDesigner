package com.hyperdev.tungguindesigner.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.hyperdev.tungguindesigner.fragment.OrderHistoriFragment
import com.hyperdev.tungguindesigner.fragment.TransactionHistoriFragment
import com.hyperdev.tungguindesigner.fragment.WithdrawHistoriFragment

class PagerAdapter(fm: FragmentManager, private val numberTabs: Int) : FragmentStatePagerAdapter(fm) {

    //Mengembalikan Fragment yang terkait dengan posisi tertentu
    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> OrderHistoriFragment()
            1 -> TransactionHistoriFragment()
            2 -> WithdrawHistoriFragment()
            else -> null
        }
    }

    //Mengembalikan jumlah tampilan yang tersedia.
    override fun getCount(): Int {
        return numberTabs
    }
}
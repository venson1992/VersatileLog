package com.venson.versatile.app

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.venson.versatile.app.databinding.ItemMainBinding
import com.venson.versatile.log.database.entity.LogEntity

class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private var mData: List<LogEntity>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun notifyData(data: List<LogEntity>? = null) {
        mData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMainBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    inner class ViewHolder(binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root) {
        val mBinding = binding
    }
}
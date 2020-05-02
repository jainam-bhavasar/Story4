package com.jainam.story2.player.playerSettings.playerSettingsNavigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.database.Thumbnail
import com.jainam.story2.databinding.PlayerSettingsNavigationFragmentItemBinding

class PlayerSettingsNavigationAdapter(private val clickListener: SettingClickListener) : RecyclerView.Adapter<PlayerSettingsNavigationAdapter.ViewHolder>(){
    class ViewHolder(val binding:PlayerSettingsNavigationFragmentItemBinding):RecyclerView.ViewHolder(binding.root)


    val data :List<Setting> = Setting.values().toList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PlayerSettingsNavigationFragmentItemBinding.inflate(layoutInflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setting = data[position]
        holder.binding.clickListener = clickListener
        holder.binding.executePendingBindings()
    }

    class SettingClickListener(val clickListener: (setting: Setting) ->Unit){
        fun onClick(setting: Setting) = clickListener(setting)
    }

    override fun getItemCount(): Int  = data.size
}
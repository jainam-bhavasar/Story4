package com.jainam.story2.player.voiceSeclection

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.speech.tts.Voice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.R
import com.pixplicity.sharp.Sharp
import kotlinx.android.synthetic.main.country_chooser_recycler_view_item.view.*
import java.lang.Exception
import java.util.*

class CountryChooseAdapter(private val countryList: List<String>,private val context: Context,private val adapterOnClick: (String) -> Unit  ) : RecyclerView.Adapter<CountryChooseAdapter.ViewHolder>() {

    inner class ViewHolder( itemView:View):RecyclerView.ViewHolder(itemView){
        val countryImageView: ImageView = itemView.countryIcon
        val countryBackground:ImageView =itemView.countryBackground
        fun setItem(country:String,position: Int){
            countryImageView.setOnClickListener {
                selectedCountryPosition.postValue(position)
                adapterOnClick(country)
            }
        }
    }

    private val selectedCountryPosition = MutableLiveData<Int>(0).also {
        it.observeForever {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view  = layoutInflater.inflate(R.layout.country_chooser_recycler_view_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int  = countryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.countryImageView.text = countryList[position]
        try {
            Sharp.loadAsset(context.assets,"flags/${countryList[position].toUpperCase(Locale.ROOT)}.svg")
                .into(holder.countryImageView)
        }catch (e:Exception){
            e.printStackTrace()
        }


        //setting the content description of the country
        if (countryList.isNotEmpty()){
            val locale:Locale? = Locale("",countryList[position])
            if (locale != null) {
                holder.countryImageView.contentDescription =locale.displayCountry
            }else{
                holder.countryImageView.contentDescription =countryList[position]

            }
        }

        //setting onclick listener
        holder.setItem(countryList[position],position = position)



        //if the position of text is the selected country position, then change the color
        if (position == selectedCountryPosition.value){
            holder.countryBackground.visibility = View.VISIBLE
        }
        else holder.countryBackground.visibility = View.INVISIBLE



    }





}
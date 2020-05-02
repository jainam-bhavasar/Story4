package com.jainam.story2.player.playerSettings.playerSettingsNavigation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.jainam.story2.databinding.PlayerSettingsNavigationFragmentBinding
import com.jainam.story2.player.PlayerFragmentDirections
import com.jainam.story2.player.playerSettings.playerSettingsNavigation.navigateBy.NavigateByViewModel
import kotlinx.android.synthetic.main.naviigate_by_settings_layout.view.*
import kotlinx.android.synthetic.main.player_fragment.*
import kotlinx.android.synthetic.main.player_settings_navigation_fragment.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [PlayerSettingsNavigationFragment.new Instance] factory method to
 * create an instance of this fragment.
 */
class PlayerSettingsNavigationFragment : Fragment() {
    private lateinit var callback: OnBackPressedCallback
    private val navigateByViewModel:NavigateByViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val playerSettingsBinding =PlayerSettingsNavigationFragmentBinding.inflate(inflater,container,false)
        playerSettingsBinding.lifecycleOwner = this


        //navigation settings adapter
        val navigateByAdapter = NavigateByAdapter(navigateByViewModel.selectedNavigateBy.value!!)
        playerSettingsBinding.navigateBySettingsLayout.navigateBySelectionRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
            adapter = navigateByAdapter
        }
        //set the navigation type (sentence,para or page..) to the item chosen
        navigateByAdapter.selectedNavBy.observe(viewLifecycleOwner, Observer {
            navigateByViewModel.selectedNavigateBy.postValue(it)
        })





        //click on the setting and it will show that fragment
        val settingsAdapter = PlayerSettingsNavigationAdapter(PlayerSettingsNavigationAdapter.SettingClickListener {
              if(it==Setting.VOICE_SETTING)findNavController().navigate(PlayerSettingsNavigationFragmentDirections.actionPlayerSettingsNavigationFragmentToVoiceSelectionMenuFragment())
        })

        //player settings initialisation
        playerSettingsBinding.playerSettingsNavigationFragmentRv.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = settingsAdapter
        }






        playerSettingsBinding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }


        return playerSettingsBinding.root
    }


}

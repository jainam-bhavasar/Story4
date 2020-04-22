package com.jainam.story2.player

import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.adapters.SeekBarBindingAdapter.setOnSeekBarChangeListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.databinding.FragmentMyPlayerBinding
import com.jainam.story2.player.textCardViewPager.DepthPageTransformer
import com.jainam.story2.player.textCardViewPager.TextCardViewPagerAdapter
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class MyPlayerFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //setting binding
        val binding = FragmentMyPlayerBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this


        val scope = CoroutineScope(Dispatchers.Unconfined)
        val fragment = this

        //getting the application variable
        val application = requireNotNull(this.activity).application
            //getting data from home fragment
            val args = MyPlayerFragmentArgs.fromBundle(requireArguments())
            val uri = Uri.parse(args.uriString)



            val dataSource = BookDatabase.getInstance(requireContext()).thumbnailDatabaseDao
            val viewModelFactory = PlayerViewModelFactory(context = requireContext(),
                dataSource = dataSource, application = application, uri = uri
            )
            val viewModel = ViewModelProviders.of(fragment, viewModelFactory).get(PlayerViewModel::class.java)




        CoroutineScope(Dispatchers.Default).launch {
            binding.viewModel = viewModel
            binding.totalPages = viewModel.bookLength-1

        }
            //setting up page change seekbar

            binding.pageNumberSeekBar.apply {
                setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        viewModel.currentPageNum.postValue(progress+1)
                        viewModel.synthesizeAndSpeak()
                    }

                })
            }

            val textCardAdapter = TextCardViewPagerAdapter()
            val list = listOf<String>("Please wait").let { textCardAdapter.submitList(it) }
            binding.textCardViewPager.adapter = textCardAdapter
            binding.textCardViewPager.setPageTransformer(DepthPageTransformer())
            //    setting up play pause button

            viewModel.cardTextList.observe(viewLifecycleOwner, Observer {
                //setting card view
                    it?.let {
                        textCardAdapter.submitList(it)
                    }

            })








        // Inflate the layout for this fragment


        return binding.root


    }




}

package com.jainam.story2.home

import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jainam.story2.R
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.databinding.HomeFragmentBinding
import com.jainam.story2.player.MyPlayerFragment
import com.jainam.story2.utils.GetLang
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


const val PICK_PDF_REQUEST = 1 // The request code.
const val OPEN_PDF = 2
@Suppress("NAME_SHADOWING")
class HomeFragment : Fragment(),GetLang{

    private lateinit var viewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //setting binding
        val binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val fragment = this

        //setting view model




        lifecycleScope.launch {
            val dataSource = BookDatabase.getInstance(application).thumbnailDatabaseDao
            val viewModelFactory = HomeViewModelFactory(dataSource, application)
            viewModel = ViewModelProviders.of(fragment, viewModelFactory).get(HomeViewModel::class.java)
            //  setting the recycler view
            val thumbnailViewAdapter :ThumbnailViewAdapter= ThumbnailViewAdapter( ThumbnailClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToMyPlayerFragment(it))
            })
            binding.thumbnailsRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.thumbnailsRecyclerView.adapter = thumbnailViewAdapter

            viewModel.thumbnails.observe(viewLifecycleOwner, Observer {

                it?.let {
                    thumbnailViewAdapter.submitList(it)
                }

            })



            // viewModel.deleteAll()
            //setting fab for testing
            binding.addButton.setOnClickListener {
                //  viewModel.score.postValue(viewModel.score.value+1)
                // viewModel.insert(Book1(uriAsString = " "))
                //navigating to player fragment


                val intent = viewModel.pickPdfIntent()
                startActivityForResult(intent, PICK_PDF_REQUEST)

                //  findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToVoiceSelectionMenuFragment())

            }

            binding.searchButton.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToSearchFragment()) }


        }

        return binding.root
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==RESULT_OK){
            if(requestCode== PICK_PDF_REQUEST){

                val uri:Uri? = data?.data

                //initiating view model

                val uriAsString = uri.toString()
                try {

                    viewModel.insert(uri!!)
                }catch (e:Exception){

                }


            }
        }
    }



}

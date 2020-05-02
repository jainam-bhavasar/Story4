package com.jainam.story2.home

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.database.Thumbnail
import com.jainam.story2.databinding.HomeFragmentBinding
import com.jainam.story2.utils.GetLang
import com.pixplicity.sharp.Sharp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
                viewModel = ViewModelProvider(fragment, viewModelFactory).get(HomeViewModel::class.java)
                //  setting the recycler view
                val thumbnailViewAdapter :ThumbnailViewAdapter= ThumbnailViewAdapter( ThumbnailClickListener {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToMyPlayerFragment(it))
                })

                binding.thumbnailsRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                binding.thumbnailsRecyclerView.adapter = thumbnailViewAdapter

              thumbnailViewAdapter.setOnItemClickListener(object :ThumbnailViewAdapter.OnClickListener{

                  override fun onItemLongClick(position: Int, v: View?) {
                      val alertDialog: AlertDialog? = activity?.let {
                          val builder = AlertDialog.Builder(it)
                          builder.setTitle("Delete?")
                          builder.apply {
                              setPositiveButton("Ok",
                                  DialogInterface.OnClickListener { dialog, id ->
                                     viewModel.delete(thumbnail = thumbnailViewAdapter.currentList[position])

                                  })
                              setNegativeButton("Cancel",
                                  DialogInterface.OnClickListener { dialog, id ->

                                  })
                          }

                          // Create the AlertDialog
                          builder.create()
                      }
                      alertDialog!!.show()

                  }

              })

                viewModel.thumbnails.observe(viewLifecycleOwner, Observer {

                    it?.let {

                        thumbnailViewAdapter.submitList(it)
                    }

                })




                binding.addButton.setOnClickListener {

                    val intent = viewModel.pickPdfIntent()
                    startActivityForResult(intent, PICK_PDF_REQUEST)

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

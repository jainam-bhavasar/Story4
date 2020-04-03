package com.jainam.story2.Home

import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.databinding.HomeFragmentBinding
import kotlinx.coroutines.*


const val PICK_PDF_REQUEST = 1 // The request code.

@Suppress("NAME_SHADOWING")
class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //setting binding
        val binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application


        //setting view model
        val dataSource = BookDatabase.getInstance(application).thumbnailDatabaseDao
        val viewModelFactory = HomeViewModelFactory(dataSource, application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)


        //  setting the recycler view
        val thumbnailViewAdapter = ThumbnailViewAdapter(viewModel.thumbnails)
        binding.thumbnailsRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.thumbnailsRecyclerView.adapter = thumbnailViewAdapter

        viewModel.thumbnails.observe(viewLifecycleOwner, Observer {
            binding.thumbnailsRecyclerView.adapter = thumbnailViewAdapter
        })

       // viewModel.deleteAll()
        //setting fab for testing
        binding.addButton.setOnClickListener {
          //  viewModel.score.postValue(viewModel.score.value+1)
           // viewModel.insert(Book1(uriAsString = " "))
            //navigating to player fragment


            val intent = viewModel.pickPdfIntent()
            startActivityForResult(intent, PICK_PDF_REQUEST)

        }


        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==RESULT_OK){
            if(requestCode== PICK_PDF_REQUEST){

                val uri:Uri? = data?.data

                //initiating view model
                val dataSource = BookDatabase.getInstance(context!!).thumbnailDatabaseDao
                val viewModelFactory = HomeViewModelFactory(dataSource, Application())
                val viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)
                val uriAsString = uri.toString()
                try {
                    viewModel.insert(uri!!)
                }catch (e:Exception){

                }



                //creating firebase image object for proccessing images


//               val image = FirebaseVisionImage.fromFilePath(context!!, uri!!)
//
//
//                //initiate the onDeviceTextRecogniser
//                val detector = FirebaseVision.getInstance()
//                    .onDeviceTextRecognizer
//
//                //get the result
//                val result = detector.processImage(image)
//                    .addOnSuccessListener { firebaseVisionText ->
//                        Log.d("text",firebaseVisionText.text)
//                        val text = firebaseVisionText.text
//                        // Task completed successfully
//                        // ...
//                    }
//                    .addOnFailureListener { e ->
//                        // Task failed with an exception
//                        // ...
//                    }



//                data?.data?.let {
//                    var bitmap = BitmapFactory.decodeStream(context!!.contentResolver.openInputStream(it))
//                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//                    Log.d("bitmap",viewModel.extract(bitmap))
//                }
               findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToMyPlayerFragment(uriAsString))
               // viewModel.readMsWord(dataUri!!)
               // viewModel.addBook(dataUri)
            }
        }
    }



}

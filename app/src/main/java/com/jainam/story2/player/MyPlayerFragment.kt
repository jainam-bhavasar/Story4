package com.jainam.story2.player

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.jainam.story2.database.BookDatabase
import com.jainam.story2.databinding.FragmentMyPlayerBinding



/**
 * A simple [Fragment] subclass.
 * Use the [MyPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyPlayerFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    companion object {
        fun newInstance() = MyPlayerFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //getting data from home fragment
        val args = MyPlayerFragmentArgs.fromBundle(arguments!!)
        val uri = Uri.parse(args.uriString)

        //setting binding
        val binding = FragmentMyPlayerBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this

        //getting the application variable
        val application = requireNotNull(this.activity).application

        //viewmodel
        val dataSource = BookDatabase.getInstance(context!!).thumbnailDatabaseDao
        val viewModelFactory = PlayerViewModelFactory(dataSource, application,uri)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(PlayerViewModel::class.java)
         //  setting binding's view's model to viewmodel
        //binding.viewModel = viewModel
      //  setting txt
        viewModel.currentPageText.observe(viewLifecycleOwner, Observer {
            binding.textView.text = viewModel.currentPageText.value
        })

        //on click listener
        binding.button.setOnClickListener {
           viewModel.increaseCurrentPageNumTillEnd()
        }

        // Inflate the layout for this fragment
        return binding.root
    }


}

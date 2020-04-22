package com.jainam.story2.bookSearch

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jainam.story2.databinding.SearchFragmentBinding
import com.jainam.story2.home.HomeFragmentDirections
import com.jainam.story2.home.ThumbnailClickListener
import com.jainam.story2.home.ThumbnailViewAdapter

const val TAG_SEARCH = "Search"
class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //setting binding
        val binding = SearchFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        val thumbnailViewAdapter : ThumbnailViewAdapter = ThumbnailViewAdapter( ThumbnailClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToMyPlayerFragment(it))
        })
        binding.searchFilterRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.searchFilterRecyclerView.adapter = thumbnailViewAdapter

        viewModel.allThumbnails.observe(viewLifecycleOwner, Observer { list ->

            viewModel.bookName.observe(viewLifecycleOwner, Observer {charSequence->

              val  list1  = list.filter { it.bookName.contains(charSequence,true)}
                list1.let {
                    thumbnailViewAdapter.submitList(list1)
                }
            })
        })


        binding.searchEditText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG_SEARCH,"before change")

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("taf",s.toString())
                viewModel.bookName.postValue(s.toString())
            }

        })
        binding.searchEditText.apply {
            ( requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }




        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)





    }

}

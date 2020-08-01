package com.jainam.story2.bookSearch

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jainam.story2.databinding.SearchFragmentBinding
import com.jainam.story2.home.ThumbnailClickListener
import com.jainam.story2.home.ThumbnailViewAdapter


class SearchFragment : Fragment() {



    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //setting binding
        val binding = SearchFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        val thumbnailViewAdapter  = ThumbnailViewAdapter( ThumbnailClickListener {
            view?.rootView?.let { it1 -> context?.let { it2 -> hideKeyboardFrom(it2, it1) } }
           findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToPlayerGraph(it.uriAsString,it.lastPage,it.lastPosition,it.language,it.bookLength))
        })
        binding.searchFilterRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.searchFilterRecyclerView.adapter = thumbnailViewAdapter

        viewModel.allThumbnails.observe(viewLifecycleOwner, Observer { list ->

            viewModel.bookName.observe(viewLifecycleOwner, Observer {charSequence->

              val  list1  = list.filter { it.thumbnailName.contains(charSequence,true)}
                list1.let {
                    thumbnailViewAdapter.submitList(list1)
                }
            })
        })


        binding.searchEditText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.bookName.postValue(s.toString())
            }

        })
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                context?.let { view?.rootView?.let { it1 -> hideKeyboardFrom(it, it1) } }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        binding.searchEditText.apply {
            ( requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        binding.btnSearch.setOnClickListener {
            context?.let { it1 -> view?.rootView?.let { it2 -> hideKeyboardFrom(it1, it2) } }
        }



        return binding.root
    }
    private fun hideKeyboardFrom(
        context: Context,
        view: View
    ) {
        val imm =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}

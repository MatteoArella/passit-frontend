package com.github.passit.ui.screens.list

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.github.passit.databinding.FragmentInsertionsListBinding
import com.github.passit.ui.contracts.insertion.CreateInsertionContract
import com.github.passit.ui.models.insertions.GetInsertionsViewModel
import com.github.passit.ui.view.Alert
import com.github.passit.core.extension.hideKeyboard
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import com.github.passit.R
import com.github.passit.ui.contracts.insertion.ShowInsertionContract
import com.github.passit.ui.models.insertions.InsertionSearchView
import com.github.passit.ui.view.ErrorAlert

@AndroidEntryPoint
class InsertionsListFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentInsertionsListBinding? = null
    private val binding get() = _binding!!
    private val showInsertion = registerForActivityResult(ShowInsertionContract()) { }
    private val createInsertion = registerForActivityResult(CreateInsertionContract()) {
        // Launch fragment/activity to show insertion
        launch {
            it?.let { insertion ->
                showInsertion.launch(insertion.id)
            }
        }
    }

    private val getInsertionsModel: GetInsertionsViewModel by activityViewModels()
    private lateinit var insertionsAdapter: InsertionsAdapter
    private var searchJob: Job? = null
    private var lastSearch: InsertionSearchView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentInsertionsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emptyLayout.emptyResultMessage.text = getString(R.string.empty_insertions_result)

        getInsertionsModel.searchView?.let { search ->
            lastSearch = search
            binding.searchSubjectTextLayout.editText?.text = SpannableStringBuilder(search.subject)
            binding.searchCityTextLayout.editText?.text = SpannableStringBuilder(search.city)
            binding.searchStateTextLayout.editText?.text = SpannableStringBuilder(search.state)
            binding.searchCountryTextLayout.editText?.text = SpannableStringBuilder(search.country)
        }

        insertionsAdapter = InsertionsAdapter { insertionView -> showInsertion.launch(insertionView.id) }
        binding.insertionsRecyclerView.adapter = insertionsAdapter

        binding.createInsertionExtendedFab.setOnClickListener {
            createInsertion.launch(null)
        }

        binding.refreshLayout.setOnRefreshListener {
            insertionsAdapter.refresh()
            binding.refreshLayout.isRefreshing = false
        }

        search(lastSearch)

        binding.searchInsertionsBtn.setOnClickListener {
            requireActivity().hideKeyboard(it)
            val subject = binding.searchSubjectTextLayout.editText?.text.toString()
            val city = binding.searchCityTextLayout.editText?.text.toString()
            val state = binding.searchStateTextLayout.editText?.text.toString()
            val country = binding.searchCountryTextLayout.editText?.text.toString()
            lastSearch = InsertionSearchView(subject = subject, city = city, state = state, country = country)
            search(lastSearch)
        }

        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.createInsertionExtendedFab.shrink()
            } else {
                binding.createInsertionExtendedFab.extend()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            insertionsAdapter.loadStateFlow
                .collectLatest { loadState ->
                    binding.progressIndicator.isVisible = loadState.source.refresh is LoadState.Loading
                    binding.emptyLayout.emptyLayout.isVisible = (loadState.refresh is LoadState.NotLoading &&
                            insertionsAdapter.itemCount == 0)
                    binding.insertionsRecyclerView.isVisible = !binding.emptyLayout.emptyLayout.isVisible
                }
        }
    }

    private fun search(searchView: InsertionSearchView?) {
        searchView?.let { search ->
            Log.i("insertions-frag", "start new search $search")
            // Cancel the previous job before creating a new one
            searchJob?.cancel()
            searchJob = launch {
                getInsertionsModel.getInsertions(search.subject, search.city, search.state, search.country).catch { error ->
                        ErrorAlert(requireContext()).setTitle(getString(R.string.search_insertions_error)).setMessage(error.localizedMessage).show()
                    }.collectLatest {
                        insertionsAdapter.submitData(it)
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.github.passit.ui.screens.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import com.github.passit.databinding.FragmentInsertionsListBinding
import com.github.passit.ui.contracts.insertion.CreateInsertionContract
import com.github.passit.ui.view.Alert
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InsertionsListFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentInsertionsListBinding? = null
    private val binding get() = _binding!!
    private val createInsertion = registerForActivityResult(CreateInsertionContract()) {
        // TODO: handle created insertion
        launch {
            it?.let { insertion ->
                Alert(this@InsertionsListFragment.requireContext()).setTitle(insertion.title).setMessage(Gson().toJson(insertion)).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentInsertionsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createInsertionFab.setOnClickListener {
            createInsertion.launch(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
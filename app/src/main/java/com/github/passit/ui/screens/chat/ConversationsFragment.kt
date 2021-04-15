package com.github.passit.ui.screens.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.passit.R
import com.github.passit.databinding.FragmentConversationsBinding
import com.github.passit.ui.contracts.conversation.ChatContract
import com.github.passit.ui.models.conversations.GetConversationsViewModel
import com.github.passit.ui.view.ErrorAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ConversationsFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentConversationsBinding? = null
    private val binding get() = _binding!!

    private val getConversationsViewModel: GetConversationsViewModel by activityViewModels()
    private lateinit var conversationsAdapter: ConversationsAdapter
    private val showConversation = registerForActivityResult(ChatContract()) {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentConversationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conversationsAdapter = ConversationsAdapter { conversationView -> showConversation.launch(conversationView) }
        binding.conversationsRecyclerView.adapter = conversationsAdapter

        binding.refreshLayout.setOnRefreshListener {
            conversationsAdapter.refresh()
            binding.refreshLayout.isRefreshing = false
        }

        lifecycleScope.launchWhenResumed {
            getConversationsViewModel.getConversations().catch { error ->
                ErrorAlert(requireContext()).setTitle(getString(R.string.get_conversations_error)).setMessage(error.localizedMessage).show()
            }.collectLatest {
                conversationsAdapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
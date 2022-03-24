package com.example.firebase_chat_demo.ui.message

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.base.basefragment.BaseFragment
import com.example.firebase_chat_demo.data.model.LoadDataStatus
import com.example.firebase_chat_demo.data.model.message.Message
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.databinding.FragmentMessageBinding
import com.example.firebase_chat_demo.ui.message.adapter.MessageAdapter

class MessageFragment : BaseFragment<FragmentMessageBinding>() {

    private lateinit var viewModel: MessageViewModel
    private val args: MessageFragmentArgs by navArgs()
    private lateinit var mMessageAdapter: MessageAdapter

    override fun getLayoutID() = R.layout.fragment_message

    override fun initView() {
        (activity as AppCompatActivity).setSupportActionBar(binding!!.mToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.getFriend()
        viewModel.getMessage()
        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        setHasOptionsMenu(true)

        val factory = MessageViewModel.Factory(requireActivity().application, args.userId)
        viewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]

        viewModel.mMessagesLiveData.observe(this) {
            if (it.loadDataStatus == LoadDataStatus.SUCCESS) {
                val result = (it as DataResponse.DataSuccessResponse).body
                initRecycler(result)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecycler(messages: MutableList<Message>) {
        mMessageAdapter = MessageAdapter(requireContext())
        mMessageAdapter.setMessageList(messages)
        binding!!.rcMessage.adapter = mMessageAdapter
    }
}
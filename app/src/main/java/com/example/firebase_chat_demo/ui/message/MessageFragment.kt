package com.example.firebase_chat_demo.ui.message

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.base.basefragment.BasePermissionRequestFragment
import com.example.firebase_chat_demo.data.model.LoadDataStatus
import com.example.firebase_chat_demo.data.model.message.Chat
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.databinding.FragmentMessageBinding
import com.example.firebase_chat_demo.ui.message.adapter.MessageAdapter
import com.example.firebase_chat_demo.utils.ExoPlayerUtils
import com.example.firebase_chat_demo.utils.MediaPlayerUtils
import com.example.firebase_chat_demo.utils.Utils
import com.google.android.exoplayer2.ui.PlayerView

class MessageFragment : BasePermissionRequestFragment<FragmentMessageBinding>(),
    View.OnClickListener {

    private lateinit var viewModel: MessageViewModel
    private val args: MessageFragmentArgs by navArgs()
    private lateinit var mMessageAdapter: MessageAdapter
    private val mExoPlayerUtils: ExoPlayerUtils by lazy {
        ExoPlayerUtils()
    }

    override fun getLayoutID() = R.layout.fragment_message

    override fun initView() {
        (activity as AppCompatActivity).setSupportActionBar(binding!!.mToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding!!.btnUpload.setOnClickListener(this)

        initRecycler()
        viewModel.getFriend()
        viewModel.getMessage()
        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        setHasOptionsMenu(true)

        val factory = MessageViewModel.Factory(requireActivity().application, args.userId)
        viewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]
        viewModel.getSeenMessage()
        viewModel.mMessagesLiveData.observe(this) {
            if (it.loadDataStatus == LoadDataStatus.SUCCESS) {
                val result = (it as DataResponse.DataSuccessResponse).body
                mMessageAdapter.setMessageList(result)
            }
        }
        viewModel.mMessageChangesLiveData.observe(this) {
            if (it.loadDataStatus == LoadDataStatus.SUCCESS) {
                val result = (it as DataResponse.DataSuccessResponse).body
                mMessageAdapter.setMessageChanges(result)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.disableValueEventListener()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(p0: View?) {
        if (p0 == binding!!.btnUpload) {
            if (Utils.storagePermissionGrant(requireContext())) {
                val intent = Intent()
                intent.type = "*/*"
                intent.action = Intent.ACTION_GET_CONTENT
                getMedia.launch(intent)
            } else {
                requestPermission()
            }
        }
    }

    private fun initRecycler() {
        mMessageAdapter = MessageAdapter(requireContext())
        binding!!.rcMessage.adapter = mMessageAdapter
        mMessageAdapter.setFileMessageClickListener(object :
            MessageAdapter.OnFileMessageClickListener {
            override fun onFileMessageClicked(playerView: PlayerView, chat: Chat) {
                mExoPlayerUtils.initPlayer(requireContext(), playerView, chat.message!!)
            }
        })
    }

    override fun setupWhenPermissionGranted() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getMedia.launch(intent)
    }

    private var getMedia =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    viewModel.onSendMediaFile(intent.data!!)
                }
            }
        }
}
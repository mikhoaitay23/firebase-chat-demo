package com.example.firebase_chat_demo.ui.home

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.base.basefragment.BaseFragment
import com.example.firebase_chat_demo.data.model.LoadDataStatus
import com.example.firebase_chat_demo.data.model.user.User
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.databinding.FragmentHomeBinding
import com.example.firebase_chat_demo.ui.home.adapter.ChatsAdapter
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var mChatsAdapter: ChatsAdapter

    override fun getLayoutID() = R.layout.fragment_home

    override fun initView() {
        (activity as AppCompatActivity).setSupportActionBar(binding!!.mToolbar)
        (activity as AppCompatActivity).title = null

        viewModel.getCurrentUser()
        viewModel.getChatList()
        viewModel.getMessage()
        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        setHasOptionsMenu(true)
        val factory = HomeViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        viewModel.usersLiveData.observe(this) {
            if (it.loadDataStatus == LoadDataStatus.SUCCESS) {
                val result = (it as DataResponse.DataSuccessResponse).body
                initRecycler(result)
                viewModel.unreadMessages.observe(this) { count ->
                    mChatsAdapter.setUnreadMessages(count)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnLogout -> {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigateUp()
                return true
            }
            R.id.btnUsers -> {
                findNavController().navigate(R.id.action_global_usersFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecycler(users: MutableList<User>) {
        mChatsAdapter = ChatsAdapter()
        mChatsAdapter.setListUser(users)
        binding!!.rcChats.apply {
            adapter = mChatsAdapter
            setHasFixedSize(true)
        }
        mChatsAdapter.setListener(object : ChatsAdapter.OnClickListener {
            override fun onItemClickedListener(user: User) {
                val action = HomeFragmentDirections.actionGlobalMessageFragment().setUserId(user.id)
                findNavController().navigate(action)
            }

        })
    }
}
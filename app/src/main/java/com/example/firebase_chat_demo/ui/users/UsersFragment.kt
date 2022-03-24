package com.example.firebase_chat_demo.ui.users

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.base.basefragment.BaseFragment
import com.example.firebase_chat_demo.data.model.LoadDataStatus
import com.example.firebase_chat_demo.data.model.user.User
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.databinding.FragmentUsersBinding
import com.example.firebase_chat_demo.ui.users.adapter.UsersAdapter

class UsersFragment : BaseFragment<FragmentUsersBinding>() {

    private lateinit var mUsersAdapter: UsersAdapter
    private lateinit var viewModel: UsersViewModel

    override fun getLayoutID() = R.layout.fragment_users

    override fun initView() {
        (activity as AppCompatActivity).setSupportActionBar(binding!!.mToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.getUsers()
    }

    override fun initViewModel() {
        setHasOptionsMenu(true)
        val factory = UsersViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[UsersViewModel::class.java]

        viewModel.usersLiveData.observe(this) {
            if (it.loadDataStatus == LoadDataStatus.SUCCESS) {
                val result = (it as DataResponse.DataSuccessResponse).body
                if (result.isNotEmpty()) {
                    initRecycler(result)
                }
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

    private fun initRecycler(users: MutableList<User>) {
        mUsersAdapter = UsersAdapter()
        mUsersAdapter.setUsers(users)
        binding!!.rcUsers.apply {
            adapter = mUsersAdapter
        }
        mUsersAdapter.setListener(object : UsersAdapter.OnClickListener {
            override fun onItemClicked(user: User) {
                val action =
                    UsersFragmentDirections.actionGlobalMessageFragment().setUserId(user.id)
                findNavController().navigate(action)
            }

        })
    }
}
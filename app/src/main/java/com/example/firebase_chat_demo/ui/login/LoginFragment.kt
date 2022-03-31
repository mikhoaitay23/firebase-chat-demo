package com.example.firebase_chat_demo.ui.login

import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.base.basefragment.BaseFragment
import com.example.firebase_chat_demo.data.model.LoadDataStatus
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.databinding.FragmentLoginBinding
import com.example.firebase_chat_demo.utils.Constants

class LoginFragment : BaseFragment<FragmentLoginBinding>(), View.OnClickListener {

    private lateinit var viewModel: LoginViewModel

    override fun getLayoutID() = R.layout.fragment_login

    override fun initView() {
        binding!!.tvSignUp.setOnClickListener(this)
        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        val factory = LoginViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        viewModel.validateLiveData.observe(this){
            if (it.loadDataStatus == LoadDataStatus.SUCCESS) {
                when ((it as DataResponse.DataSuccessResponse).body) {
                    Constants.ValidateType.ValidateDone -> {
                        viewModel.onLoginLiveData.observe(this) { it1 ->
                            if (it1.loadDataStatus == LoadDataStatus.SUCCESS) {
                                val result = (it1 as DataResponse.DataSuccessResponse).body
                                if (result) {
                                    findNavController().navigate(R.id.action_global_homeFragment)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.login_failed),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.empty_input),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        if (view == binding!!.tvSignUp) {
            findNavController().navigate(R.id.action_global_signupFragment)
        }
    }
}
package com.example.firebase_chat_demo.ui.signup

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.base.basefragment.BaseFragment
import com.example.firebase_chat_demo.data.model.LoadDataStatus
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.databinding.FragmentSignupBinding
import com.example.firebase_chat_demo.utils.Constants

class SignupFragment : BaseFragment<FragmentSignupBinding>() {

    private lateinit var viewModel: SignupViewModel

    override fun getLayoutID(): Int{
        return R.layout.fragment_signup
    }

    override fun initView() {
        binding!!.viewModel = viewModel
    }

    override fun initViewModel() {
        val factory = SignupViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[SignupViewModel::class.java]

        viewModel.saveDoneLiveData.observe(this) {
            if (it.loadDataStatus == LoadDataStatus.SUCCESS) {
                when ((it as DataResponse.DataSuccessResponse).body) {
                    Constants.ValidateType.ValidateDone -> {
                        viewModel.signUpLiveData.observe(this) { it1 ->
                            if (it1.loadDataStatus == LoadDataStatus.SUCCESS) {
                                val result = (it1 as DataResponse.DataSuccessResponse).body
                                if (result) {
                                    findNavController().navigateUp()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.sign_up_failed),
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
}
package com.example.firebase_chat_demo.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.firebase_chat_demo.R
import com.example.firebase_chat_demo.base.basefragment.BaseFragment
import com.example.firebase_chat_demo.data.response.DataResponse
import com.example.firebase_chat_demo.databinding.FragmentSplashBinding
import com.example.firebase_chat_demo.ui.login.LoginViewModel

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private lateinit var viewModel: SplashViewModel

    override fun getLayoutID() = R.layout.fragment_splash

    override fun initView() {
        viewModel.getCurrentUser()
    }

    override fun initViewModel() {
        val factory = SplashViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[SplashViewModel::class.java]

        viewModel.isCurrentUser.observe(this) {
            if ((it as DataResponse.DataSuccessResponse).body) {
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_global_homeFragment)
                }, SPLASH_DELAY)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_global_loginFragment)
                }, SPLASH_DELAY)
            }
        }
    }

    companion object {
        const val SPLASH_DELAY = 2000L
    }


}
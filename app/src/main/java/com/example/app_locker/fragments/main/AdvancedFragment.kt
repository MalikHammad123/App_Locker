package com.example.app_locker.fragments.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_locker.R
import com.example.app_locker.activities.intruder.IntruderActivity
import com.example.app_locker.activities.vault.Vault
import com.example.app_locker.adapters.AdvancedButtonsCallback
import com.example.app_locker.adapters.AdvancedRecyclerviewAdapter
import com.example.app_locker.data.AdvancedData
import com.example.app_locker.directory.utils.BiometricHelper
import com.example.app_locker.directory.utils.BiometricHelper.checkDeviceCanAuthenticateWithBiometrics

class AdvancedFragment : Fragment(), AdvancedButtonsCallback, SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: AdvancedRecyclerviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_advanced, container, false)
        sharedPreferences = requireContext().getSharedPreferences("fingerprint", Context.MODE_PRIVATE)

        // Register this fragment as a listener for SharedPreferences changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        val dataList = listOf(
            AdvancedData("Photo Vault", R.drawable.vault_group, false),
            AdvancedData("Intruder Detection", R.drawable.intruder_group, false),
            AdvancedData("Unlock with \nFingerprint", R.drawable.fingerprint_group, true)
        )
        val recyclerView = view.findViewById<RecyclerView>(R.id.advanced_rv)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        adapter = AdvancedRecyclerviewAdapter(dataList, this)
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BiometricHelper.init(requireContext(),
            requireActivity() as AppCompatActivity,
            Vault::class.java
        ) { intent, requestCode ->
            requireActivity().startActivityForResult(
                intent, requestCode
            )
        }
        checkDeviceCanAuthenticateWithBiometrics()
    }

    override fun onButtonClick(position: Int) {
        // Ensure the position is valid
        if (position != RecyclerView.NO_POSITION) {
            // Determine which activity to start based on the clicked item position
            when (position) {
                0 -> {
                    val intent = Intent(requireContext(), Vault::class.java)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(requireContext(), IntruderActivity::class.java)
                    startActivity(intent)
                }
                2 -> {

                }

            }
        }
    }

    companion object {
        const val RC_BIOMETRICS_ENROLL = 10
        const val RC_DEVICE_CREDENTIAL_ENROLL = 18
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key == "SwitchState"){
            adapter.notifyDataSetChanged()
        }
    }

}
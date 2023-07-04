package com.example.app_locker.fragments.main

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_locker.R
import com.example.app_locker.adapters.adapter.ApplicationsAdapter

class AllAppFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ApplicationsAdapter

    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_app, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val applications = getInstalledApplications()
        //adapter = ApplicationsAdapter(applications)
        adapter = ApplicationsAdapter(requireContext(), applications)

        recyclerView.adapter = adapter

        searchView = view.findViewById(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        return view
    }


    private fun getInstalledApplications(): List<ResolveInfo> {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList: List<ResolveInfo> =
            requireContext().packageManager.queryIntentActivities(mainIntent, 0)
        return pkgAppsList
    }


}


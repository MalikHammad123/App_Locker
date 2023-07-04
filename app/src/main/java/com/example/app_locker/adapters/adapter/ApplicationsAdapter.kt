package com.example.app_locker.adapters.adapter

import android.content.Context
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.app_locker.R
import java.util.*


class ApplicationsAdapter(
    private val context: Context,
    private val applications: List<ResolveInfo>
) :
    RecyclerView.Adapter<ApplicationsAdapter.ViewHolder>(), Filterable {

    private var filteredApplications: List<ResolveInfo> = applications
    private val selectedApps: MutableSet<String> =
        mutableSetOf() // List to store selected package names

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_app_recycler_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val application = filteredApplications[position]
        holder.bind(application, selectedApps)
    }

    override fun getItemCount(): Int {
        return filteredApplications.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val query = constraint.toString().toLowerCase(Locale.getDefault())
                filteredApplications = if (query.isEmpty()) {
                    applications
                } else {
                    applications.filter { resolveInfo ->
                        resolveInfo.loadLabel(context.packageManager)
                            .toString().toLowerCase(Locale.getDefault())
                            .contains(query)
                    }
                }
                val results = FilterResults()
                results.values = filteredApplications
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredApplications = results.values as List<ResolveInfo>
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appNameTextView: TextView = itemView.findViewById(R.id.appName_tv)
        private val appIconImageView: ImageView = itemView.findViewById(R.id.appIcon_iv)
        private val lockSwitch: Switch = itemView.findViewById(R.id.switchv)

        fun bind(application: ResolveInfo, selectedApps: MutableSet<String>) {
            val context = itemView.context
            val appName = application.loadLabel(context.packageManager)
            val appIcon = application.loadIcon(context.packageManager)
            val appPackageName = application.activityInfo.packageName

            appNameTextView.text = appName
            appIconImageView.setImageDrawable(appIcon)

            // Set the initial state of the switch
            lockSwitch.isChecked = selectedApps.contains(appPackageName)

            // Set the switch listener
            lockSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedApps.add(appPackageName)
                } else {
                    selectedApps.remove(appPackageName)
                }
                saveSelectedApps(selectedApps)
            }
        }

        private fun saveSelectedApps(selectedApps: MutableSet<String>) {
            val sharedPref = context.getSharedPreferences("AppSwitchPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putStringSet("selectedApps", selectedApps)
                apply()
            }
        }

        private fun loadSelectedApps() {
            val sharedPref = context.getSharedPreferences("AppSwitchPrefs", Context.MODE_PRIVATE)
            selectedApps.addAll(sharedPref.getStringSet("selectedApps", emptySet())!!)
        }

        init {
            loadSelectedApps()
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val toastText = "Clicked on item at position ${position + 1}"
                    Toast.makeText(itemView.context, toastText, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

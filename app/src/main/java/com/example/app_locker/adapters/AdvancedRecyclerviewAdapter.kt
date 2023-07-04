package com.example.app_locker.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_locker.R
import com.example.app_locker.data.AdvancedData
import com.example.app_locker.directory.utils.SwitchHandler


interface AdvancedButtonsCallback {
    fun onButtonClick(position: Int)
}

class AdvancedRecyclerviewAdapter(
    private val dataList: List<AdvancedData>, val listener: AdvancedButtonsCallback
) : RecyclerView.Adapter<AdvancedRecyclerviewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            // Get the clicked item position
            listener.onButtonClick(adapterPosition)
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            if (adapterPosition == 2) {
                SwitchHandler.saveSwitchState(buttonView?.context!!, isChecked)
            }
        }

        val imageView: ImageView = view.findViewById(R.id.vault_iv)
        val textView: TextView = view.findViewById(R.id.vault_tv)
        val switch: Switch = view.findViewById(R.id.extraswitch)


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.advanced_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.imageView.setImageResource(item.imageResId)
        holder.textView.text = item.text
        holder.switch

        holder.switch.visibility = if (item.switch) View.VISIBLE else View.INVISIBLE
        if (position == 2) {
            val switchState = SwitchHandler.getSwitchState(holder.itemView.context)
            holder.switch.isChecked = switchState
        }
        if (item.switch) {
            holder.switch.setOnCheckedChangeListener(holder)
        } else {
            holder.switch.setOnCheckedChangeListener(null)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

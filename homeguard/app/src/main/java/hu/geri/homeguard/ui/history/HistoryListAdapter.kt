package hu.geri.homeguard.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.geri.homeguard.R
import hu.geri.homeguard.domain.history.model.HistoryItem
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryListAdapter() :
    ListAdapter<HistoryItem, HistoryListAdapter.HistoryViewHolder>(HistoryComparator) {

    val listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)

        holder.item = item

        holder.nameText.text = item.name
        holder.captureDateText.text = item.captureDate
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.tvHistoryName
        val captureDateText: TextView = itemView.tvHistoryCaptureDate

        var item: HistoryItem? = null

        init {
            itemView.setOnClickListener {
                item?.let { item -> listener?.onItemSelected(item.id) }
            }
        }
    }

    object HistoryComparator : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun onItemSelected(historyId: Long)
    }
}

package hu.geri.homeguard.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.geri.homeguard.R
import hu.geri.homeguard.domain.history.model.HistoryItem
import kotlinx.android.synthetic.main.item_facegallery.view.*
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryListAdapter(
    private val onClick: HistoryOnClickListener
) :
    ListAdapter<HistoryItem, HistoryListAdapter.HistoryViewHolder>(HistoryComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)

        holder.item = item

        holder.nameText.text = item.name
        holder.captureDateText.text = item.captureDate
        holder.imgFace.setImageBitmap(item.bitmap)
    }

    inner class HistoryViewHolder(
        itemView: View,
        private val onClick: HistoryOnClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.tvHistoryName
        val captureDateText: TextView = itemView.tvHistoryCaptureDate
        val imgFace: ImageView = itemView.imgHistory

        var item: HistoryItem? = null

        init {
            itemView.setOnClickListener {
                item?.let { item -> onClick.onClick(item.id) }
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

    interface HistoryOnClickListener {
        fun onClick(historyId: Long)
    }
}

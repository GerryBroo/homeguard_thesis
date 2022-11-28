package hu.geribruu.homeguardbeta.ui.faceGalleryScreen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import hu.geribruu.homeguardbeta.R
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.RecognizedFace
import kotlinx.android.synthetic.main.row_facegallery_item.view.btnDelete
import kotlinx.android.synthetic.main.row_facegallery_item.view.imgGalleryItem
import kotlinx.android.synthetic.main.row_facegallery_item.view.tvGalleryCaptureDate
import kotlinx.android.synthetic.main.row_facegallery_item.view.tvGalleryName
import java.io.File

class FaceGalleryAdapter(private val onClick: FaceClickListener) :
    ListAdapter<RecognizedFace, FaceGalleryAdapter.GalleryViewHolder>(FACES_COMPARATOR) {

    private lateinit var current: RecognizedFace

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        current = getItem(position)
        holder.bind(current, position)
    }

    class GalleryViewHolder(itemView: View, private val onClick: FaceClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private val tvFaceName: TextView = itemView.tvGalleryName
        private val tvFaceCaptureDate: TextView = itemView.tvGalleryCaptureDate
        private val imgFace: CircleImageView = itemView.imgGalleryItem

        fun bind(face: RecognizedFace, position: Int) {
            tvFaceName.text = face.name
            tvFaceCaptureDate.text = face.captureDate

            val imgFile = File(face.faceUrl)
            Picasso.get().load(imgFile).into(imgFace)

            itemView.setOnClickListener {
                onClick.onClick(face.id)
            }

            itemView.btnDelete.setOnClickListener {
                onClick.onDelete(face)
            }
        }

        companion object {
            fun create(parent: ViewGroup, onClick: FaceClickListener): GalleryViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_facegallery_item, parent, false)
                return GalleryViewHolder(view, onClick)
            }
        }
    }

    companion object {
        private val FACES_COMPARATOR = object : DiffUtil.ItemCallback<RecognizedFace>() {
            override fun areItemsTheSame(
                oldItem: RecognizedFace,
                newItem: RecognizedFace,
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: RecognizedFace,
                newItem: RecognizedFace,
            ): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    interface FaceClickListener {
        fun onClick(id: Long)
        fun onDelete(face: RecognizedFace)
    }
}

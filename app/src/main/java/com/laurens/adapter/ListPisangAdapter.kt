package com.laurens.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.laurens.bananaripe.R
import com.laurens.data.Banana

class ListPisangAdapter(private val listBanana: ArrayList<Banana>) : RecyclerView.Adapter<ListPisangAdapter.ListViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Banana)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
        val tvDetail: TextView = itemView.findViewById(R.id.tv_item_description)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photo, Detail) = listBanana[position]
        holder.imgPhoto.setImageResource(photo)
        holder.tvName.text = name
        holder.tvDescription.text = description
        holder.tvDetail.text = Detail
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(listBanana[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listBanana.size

}
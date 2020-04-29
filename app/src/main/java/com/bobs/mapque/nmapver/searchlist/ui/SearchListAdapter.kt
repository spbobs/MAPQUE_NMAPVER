package com.bobs.mapque.nmapver.searchlist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bobs.mapque.nmapver.R
import com.bobs.mapque.nmapver.databinding.SearchlistItemBinding
import com.bobs.mapque.nmapver.searchlist.data.model.SearchItem
import com.bobs.mapque.nmapver.util.listener.MapListener
import com.bobs.mapque.nmapver.util.listener.SearchListListener

class SearchListAdapter(
    private val mapListener: MapListener<SearchItem>,
    private val searchListListener: SearchListListener<SearchItem>
) : RecyclerView.Adapter<SearchListAdapter.SearchListHolder>() {

    var items: List<SearchItem>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.searchlist_item, parent, false)
        return SearchListHolder(item)
    }

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: SearchListHolder, position: Int) {
        val item = items?.get(position)

        holder.run{
            binding?.run {
                this.item = item

                popupBtn.setOnClickListener {
                    PopupMenu(holder.itemView.context, it).apply {
                        inflate(R.menu.searchitem_menu)
                        setOnMenuItemClickListener {menuItem ->
                            when(menuItem.itemId){
                                R.id.moveMap -> {
                                    binding.item?.let { mapListener.moveMap(it) }
                                    true
                                }

                                R.id.deleteItem ->{
                                    binding.item?.let { searchListListener.deleteItem(it) }
                                    true
                                }

                                else -> false
                            }
                        }

                        show()
                    }
                }
            }
        }
    }

    inner class SearchListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: SearchlistItemBinding? = DataBindingUtil.bind(itemView)
    }
}
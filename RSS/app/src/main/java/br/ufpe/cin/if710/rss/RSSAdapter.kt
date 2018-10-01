package br.ufpe.cin.if710.rss

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.itemlista.view.*

class RSSAdapter (val rsss: List<ItemRSS>) : RecyclerView.Adapter<RSSAdapter.ViewHolder>() {

    class ViewHolder (row: View) : RecyclerView.ViewHolder(row) {
        val title = row.item_titulo
        val date = row.item_data

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.itemlista, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        if (rsss != null)
            return rsss.size
        else
            return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rss = rsss[position]

        holder?.title.text = rss.title
        holder?.date.text = rss.pubDate
    }

}
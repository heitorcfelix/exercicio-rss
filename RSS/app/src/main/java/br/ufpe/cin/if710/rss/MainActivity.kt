package br.ufpe.cin.if710.rss

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.itemlista.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {

    //ao fazer envio da resolucao, use este link no seu codigo!
    //private val RSS_FEED = "http://leopoldomt.com/if1001/g1brasil.xml"

    //OUTROS LINKS PARA TESTAR...
    //http://rss.cnn.com/rss/edition.rss
    //http://pox.globo.com/rss/g1/brasil/
    //http://pox.globo.com/rss/g1/ciencia-e-saude/
    //http://pox.globo.com/rss/g1/tecnologia/

    //use ListView ao invés de TextView - deixe o atributo com o mesmo nome
    internal var conteudoRSS: RecyclerView? = null
    private var rssAdapter: RSSAdapter? = null
    internal var listaRSS: List<ItemRSS>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        conteudoRSS = findViewById(R.id.conteudoRSS)

        listaRSS = emptyList()
        rssAdapter = RSSAdapter(listaRSS)

        conteudoRSS = RecyclerView(this).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = rssAdapter
        }
        setContentView(conteudoRSS)

    }

    //Funçao de chamada de rede dentro do DoAsync para ser executada em outra thread
    override fun onStart() {
        super.onStart()
        doAsync {
            try {
                val feedXML = getRssFeed(getString(R.string.rssfeed))
                uiThread {
                    val readXML =  ParserRSS.parse(feedXML)
                    rssAdapter = RSSAdapter(readXML)

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun getRssFeed(feed: String): String {
        var `in`: InputStream? = null
        var rssFeed = ""
        try {
            val url = URL(feed)
            val conn = url.openConnection() as HttpURLConnection
            `in` = conn.inputStream
            val response = `in`.readBytes()
            rssFeed = String(response, charset("UTF-8"))
        } finally {
            `in`?.close()
        }
        return rssFeed
    }

    //adicionar Adapter
    private inner class RSSAdapter(private val rsss: List<ItemRSS>?) : RecyclerView.Adapter<CardChangeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardChangeHolder {
            val v = layoutInflater.inflate(R.layout.itemlista, parent, false)
            return CardChangeHolder(v)
        }

        override fun onBindViewHolder(holder: CardChangeHolder, position: Int) {
            val item = listaRSS?.get(position)
            holder.title?.text = item?.title
            holder.date?.text = item?.pubDate
        }

        override fun getItemCount(): Int {
            if (rsss!=null)
                return rsss.size
            else
                return 0
        }
    }

    //adicionar Holder
    internal class CardChangeHolder
    (row: View) : RecyclerView.ViewHolder(row), View.OnClickListener {
        var title: TextView? = null
        var date: TextView? = null

        init {

            this.title = row.item_titulo
            this.date = row.item_data

        }

        fun bindModel(p: ItemRSS?) {
            title?.text = p?.title
            date?.text = p?.pubDate
        }

        override fun onClick(v: View) {
        }
    }

}
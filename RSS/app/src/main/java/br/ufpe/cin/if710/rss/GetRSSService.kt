package br.ufpe.cin.if710.rss

import android.app.IntentService
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class GetRSSService: IntentService("GetRSSService") {

    public override fun onHandleIntent(i: Intent?) {
        Log.e("teste", "SERVICE INICIADO")
        val dbHelper =  SQLiteRSSHelper.getInstance(applicationContext)


        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val rss_url = prefs.getString(MainActivity.RSS, "nada escolhido...")
        doAsync {
            try {
                val feedXML = getRssFeed(rss_url)
                val rss = ParserRSS.parse(feedXML)

                rss.forEach { dbHelper.insertItem(it) }
                Log.e("teste", "cadastro finalizado")


            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("teste", "ERRRORRR")

            } finally {
                //sendBroadcast(Intent("br.ufpe.cin.if710.broadcasts.dinamico"))
            }
            uiThread {  }
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


}
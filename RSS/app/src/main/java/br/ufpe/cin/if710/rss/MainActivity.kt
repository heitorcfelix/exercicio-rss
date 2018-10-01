package br.ufpe.cin.if710.rss

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    private val intentFilter = IntentFilter("br.ufpe.cin.if710.broadcasts.dinamico")
    private val receiver = DynamicReceiver()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainToolbar)

        conteudoRSS.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        val getRSSService = Intent(applicationContext, GetRSSService::class.java)

        startService(getRSSService)

        registerReceiver(receiver, intentFilter)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }


    companion object {
        val RSS = "rssfeed"
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(
                    this@MainActivity,
                    PrefsMenuActivity::class.java))
            true
        }
        else -> {

            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    inner class DynamicReceiver: BroadcastReceiver() {
        private val TAG = "DynamicReceiver"

        override fun onReceive(context: Context, intent: Intent) {
            val dbHelper = SQLiteRSSHelper.getInstance(applicationContext)
            val selectResult = dbHelper.getItems()
            conteudoRSS.adapter = RSSAdapter(selectResult)

        }
    }

}

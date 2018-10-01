package br.ufpe.cin.if710.rss

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class SQLiteRSSHelper private constructor(
    //alternativa
    internal var c: Context) : SQLiteOpenHelper(c, DATABASE_NAME, null, DB_VERSION) {
    val items: Cursor?
    @Throws(SQLException::class)
    get() = null

    override fun onCreate(db: SQLiteDatabase) {
        //Executa o comando de criação de tabela
        db.execSQL(CREATE_DB_COMMAND)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //estamos ignorando esta possibilidade no momento
        throw RuntimeException("nao se aplica")
    }

    //IMPLEMENTAR ABAIXO
    //Implemente a manipulação de dados nos métodos auxiliares para não ficar criando consultas manualmente
    fun insertItem(item: ItemRSS): Long {
        return insertItem(item.title, item.pubDate, item.description, item.link)
    }

    fun insertItem(title: String, pubDate: String, description: String, link: String): Long {

        val cv = ContentValues()
        cv.put(ITEM_TITLE, title)
        cv.put(ITEM_DATE, pubDate)
        cv.put(ITEM_DESC, description)
        cv.put(ITEM_LINK, link)
        cv.put(ITEM_UNREAD, "true")

        db?.writableDatabase?.insert(DATABASE_TABLE, columns.takeLast(columns.size-1).joinToString(), cv)

        return 0L
    }

    @Throws(SQLException::class)
    fun getItemRSS(link: String): ItemRSS {
        val result = db?.readableDatabase?.query(true ,DATABASE_TABLE, columns, "$ITEM_LINK = ?", arrayOf(link), null, null, null, null)
        val ret = ItemRSS(result!!.getString(result.getColumnIndex(ITEM_TITLE)), result.getString(result.getColumnIndex(ITEM_LINK)), result.getString(result.getColumnIndex(ITEM_DATE)), result.getString(result.getColumnIndex(ITEM_DESC)))
        result.close()
        return ret
    }

    fun getItems(): List<ItemRSS> {
        var itens: List<ItemRSS> = emptyList()
        val result = db?.readableDatabase?.query(false ,DATABASE_TABLE, columns, ITEM_UNREAD + "= ?", arrayOf("true"), null, null, null, null)
        Log.e("teste", result!!.count.toString())
        result.moveToFirst()
        for (i in 0..39) {
            itens += ItemRSS(result.getString(result.getColumnIndex(ITEM_TITLE)), result.getString(result.getColumnIndex(ITEM_LINK)), result.getString(result.getColumnIndex(ITEM_DATE)), result.getString(result.getColumnIndex(ITEM_DESC)))
            result.moveToNext()
        }
        result.close()
        return itens
    }

    fun markAsUnread(link: String): Boolean {
        return false
    }

    fun markAsRead(link: String): Boolean {
        return false
    }

    companion object {
        //Nome do Banco de Dados
        private val DATABASE_NAME = "rss"
        //Nome da tabela do Banco a ser usada
        val DATABASE_TABLE = "items"
        //Versão atual do banco
        private val DB_VERSION = 1

        @SuppressLint("StaticFieldLeak")
        private var db: SQLiteRSSHelper? = null

        //Definindo Singleton
        fun getInstance(c: Context): SQLiteRSSHelper {
            if (db == null) {
                db = SQLiteRSSHelper(c.applicationContext)
            }
            return db as SQLiteRSSHelper
        }

        //Definindo constantes que representam os campos do banco de dados
        val ITEM_ROWID = RssProviderContract._ID
        val ITEM_TITLE = RssProviderContract.TITLE
        val ITEM_DATE = RssProviderContract.DATE
        val ITEM_DESC = RssProviderContract.DESCRIPTION
        val ITEM_LINK = RssProviderContract.LINK
        val ITEM_UNREAD = RssProviderContract.UNREAD

        //Definindo constante que representa um array com todos os campos
        val columns = arrayOf<String>(ITEM_ROWID, ITEM_TITLE, ITEM_DATE, ITEM_DESC, ITEM_LINK, ITEM_UNREAD)

        //Definindo constante que representa o comando de criação da tabela no banco de dados
        private val CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
                ITEM_ROWID + " integer primary key autoincrement, " +
                ITEM_TITLE + " text not null, " +
                ITEM_DATE + " text not null, " +
                ITEM_DESC + " text not null, " +
                ITEM_LINK + " text not null, " +
                ITEM_UNREAD + " boolean not null);"
    }

}
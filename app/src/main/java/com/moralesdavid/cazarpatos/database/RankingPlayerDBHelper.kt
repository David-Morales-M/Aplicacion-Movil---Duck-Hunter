package com.moralesdavid.cazarpatos.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.moralesdavid.cazarpatos.Jugador

class RankingPlayerDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // If you change the database schema, you must increment the database version.
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "CazarPatos.db"
        private const val TABLE_NAME = "TBL_RANKING"
        private const val COLUMN_NAME_PLAYER = "PLAYER_NAME"
        private const val COLUMN_NAME_DUCKS = "DUCKS_HUNTED"
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${COLUMN_NAME_PLAYER} TEXT," +
                    "${COLUMN_NAME_DUCKS} INTEGER)"
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    // Insertando en el ranking a un jugador
    fun insertRanking(player: Jugador) {
        // Gets the data repository in write mode
        val db = this.writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(COLUMN_NAME_PLAYER, player.usuario)
            put(COLUMN_NAME_DUCKS, player.patosCazados)
        }
        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(TABLE_NAME, null, values)
        db?.close()
    }

    // Insertando en el ranking a un jugador
    fun insertRankingByQuery(player: Jugador) {
        // Gets the data repository in write mode
        val db = this.writableDatabase
        // Create a new map of values, where column names are the keys
        val sqlSentence =
            "INSERT INTO ${TABLE_NAME} " +
                    "(${COLUMN_NAME_PLAYER}, ${COLUMN_NAME_DUCKS}) " +
                    "VALUES('${player.usuario}',${player.patosCazados})"
        db.execSQL(sqlSentence)
        db?.close()
    }

    // Lectura de todos los rankins por búsqueda
    fun readAllRankingByQuery(): ArrayList<Jugador> {
        val db = this.readableDatabase
        val sqlSelect = "SELECT * FROM ${TABLE_NAME}"
        val cursor = db.rawQuery(sqlSelect, null)
        val players = arrayListOf<Jugador>()
        with(cursor) {
            while (moveToNext()) {
                val playerId = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                val playerName = getString(getColumnIndexOrThrow(COLUMN_NAME_PLAYER))
                val ducksHunted = getInt(getColumnIndexOrThrow(COLUMN_NAME_DUCKS))
                players.add(Jugador(playerName, ducksHunted))
            }
        }

        cursor.close()
        db.close()
        return players
    }

    // Lectura de todos los rankins
    fun readAllRanking(): ArrayList<Jugador> {
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID, COLUMN_NAME_PLAYER, COLUMN_NAME_DUCKS)
        // How you want the results sorted in the resulting Cursor
        val sortOrder = "${COLUMN_NAME_DUCKS} DESC"
        val cursor = db.query(
            TABLE_NAME,       // Tabla de la consulta
            projection,       // Arreglo de columnas a retornar (Con null se obtienen todas)
            null,        // Columnas de la clausula WHERE
            null,    // Valores de la clausula WHERE
            null,     // Sin agrupar las filas
            null,      // Sin filtrar por grupos de filas
            sortOrder        // Orden
        )
        val players = arrayListOf<Jugador>()
        with(cursor) {
            while (moveToNext()) {
                val playerId = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                val playerName = getString(getColumnIndexOrThrow(COLUMN_NAME_PLAYER))
                val ducksHunted = getInt(getColumnIndexOrThrow(COLUMN_NAME_DUCKS))
                players.add(Jugador(playerName, ducksHunted))
            }
        }
        cursor.close()
        db.close()
        return players
    }

    // Lectura de patos cazados por el jugador
    fun readDucksHuntedByPlayer(player: String): Int {
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID, COLUMN_NAME_PLAYER, COLUMN_NAME_DUCKS)
        val selection = "${COLUMN_NAME_PLAYER} = ?"
        val selectionArgs = arrayOf(player)
        val sortOrder = "${COLUMN_NAME_PLAYER} DESC"
        val cursor = db.query(
            TABLE_NAME,       // Tabla de la consulta
            projection,       // Arreglo de columnas a retornar (Con null se obtienen todas)
            selection,        // Columnas de la clausula WHERE
            selectionArgs,    // Valores de la clausula WHERE
            null,     // Sin agrupar las filas
            null,      // Sin filtrar por grupos de filas
            sortOrder        // Orden
        )
        var ducksHunted = 0
        with(cursor) {
            if (moveToFirst()) {
                //val playerId = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                // val playerName = getString(getColumnIndexOrThrow(RankingPlayerContract.PlayerEntry.COLUMN_NAME_PLAYER))
                ducksHunted = getInt(getColumnIndexOrThrow(COLUMN_NAME_DUCKS))
            }
        }
        cursor.close()
        db.close()
        return ducksHunted
    }

    fun updateRanking(player: Jugador) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME_DUCKS, player.patosCazados)
        }
        val selection = "${COLUMN_NAME_PLAYER} = ?"
        val selectionArgs = arrayOf(player.usuario)
        val count = db.update(
            TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        db.close()
    }

    fun deleteRanking(player: String) {
        val db = this.writableDatabase
        val selection = "${COLUMN_NAME_PLAYER} = ?"
        val selectionArgs = arrayOf(player)
        val deletedRows = db.delete(TABLE_NAME, selection, selectionArgs)
        db.close()
    }

    fun deleteAllRanking() {
        val db = this.writableDatabase
        val deletedRows = db.delete(TABLE_NAME, null, null)
        db.close ()
    }
}

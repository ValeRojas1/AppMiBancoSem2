package com.example.appmibancosem2.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appmibancosem2.data.model.SolicitudCredito

class SolicitudDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "banco_local.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "solicitudes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MONTO = "monto"
        private const val COLUMN_PLAZO = "plazo"
        private const val COLUMN_TIPO = "tipo"
        private const val COLUMN_DNI = "dni"
        private const val COLUMN_ESTADO = "estado"
        private const val COLUMN_FECHA = "fecha"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_MONTO REAL," +
                "$COLUMN_PLAZO INTEGER," +
                "$COLUMN_TIPO TEXT," +
                "$COLUMN_DNI TEXT," +
                "$COLUMN_ESTADO TEXT," +
                "$COLUMN_FECHA INTEGER)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertarSolicitud(solicitud: SolicitudCredito): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MONTO, solicitud.monto)
            put(COLUMN_PLAZO, solicitud.plazoMeses)
            put(COLUMN_TIPO, solicitud.tipoCredito)
            put(COLUMN_DNI, solicitud.dniSolicitante)
            put(COLUMN_ESTADO, solicitud.estado)
            put(COLUMN_FECHA, solicitud.fecha)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun obtenerTodas(): List<SolicitudCredito> {
        val lista = mutableListOf<SolicitudCredito>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_FECHA DESC")

        if (cursor.moveToFirst()) {
            do {
                lista.add(SolicitudCredito(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO)),
                    plazoMeses = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAZO)),
                    tipoCredito = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                    dniSolicitante = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DNI)),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)),
                    fecha = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun actualizarEstado(id: Int, nuevoEstado: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ESTADO, nuevoEstado)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun eliminarSolicitud(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun contarPendientes(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_ESTADO = 'pendiente'", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }
}

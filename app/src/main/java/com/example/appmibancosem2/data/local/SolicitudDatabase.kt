package com.example.appmibancosem2.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appmibancosem2.data.model.SolicitudCredito

class SolicitudDatabase(context: Context) : SQLiteOpenHelper(context, "mibanco.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE solicitudes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                monto REAL,
                plazo INTEGER,
                tipo TEXT,
                dni TEXT,
                estado TEXT,
                fecha INTEGER
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS solicitudes")
        onCreate(db)
    }

    fun insertar(sol: SolicitudCredito): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("monto", sol.monto)
            put("plazo", sol.plazoMeses)
            put("tipo", sol.tipoCredito)
            put("dni", sol.dniSolicitante)
            put("estado", sol.estado)
            put("fecha", sol.fecha)
        }
        return db.insert("solicitudes", null, values)
    }

    fun obtenerTodas(): List<SolicitudCredito> {
        val lista = mutableListOf<SolicitudCredito>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM solicitudes ORDER BY fecha DESC", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(SolicitudCredito(
                    id = cursor.getInt(0),
                    monto = cursor.getDouble(1),
                    plazoMeses = cursor.getInt(2),
                    tipoCredito = cursor.getString(3),
                    dniSolicitante = cursor.getString(4),
                    estado = cursor.getString(5),
                    fecha = cursor.getLong(6)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun actualizarEstado(id: Int, nuevoEstado: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply { put("estado", nuevoEstado) }
        db.update("solicitudes", values, "id = ?", arrayOf(id.toString()))
    }

    fun eliminar(id: Int) {
        val db = this.writableDatabase
        db.delete("solicitudes", "id = ?", arrayOf(id.toString()))
    }

    fun contarPendientes(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM solicitudes WHERE estado = 'pendiente'", null)
        var count = 0
        if (cursor.moveToFirst()) count = cursor.getInt(0)
        cursor.close()
        return count
    }
}

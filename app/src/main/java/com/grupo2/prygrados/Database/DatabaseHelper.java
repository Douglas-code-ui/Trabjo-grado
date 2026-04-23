package com.grupo2.prygrados.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "prygrados.db";
    private static final int DATABASE_VERSION = 4; // 🔥 subir versión

    // =========================
    // TABLA ONLINE
    // =========================

    public static final String TABLE_USUARIOS_ONLINE = "usuario_online";

    // =========================
    // TABLA OFFLINE
    // =========================

    public static final String TABLE_USUARIOS_OFFLINE = "usuario_offline";

    // =========================
    // COLUMNAS
    // =========================

    public static final String COLUMN_ID = "idUsuario";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_CORREO = "correo_electronico";
    public static final String COLUMN_CONTRASENA = "contrasena";
    public static final String COLUMN_ROL = "rol";
    public static final String COLUMN_ESTADO = "estado";
    public static final String COLUMN_FECHA = "fecha_creacion";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // =========================
    // CREAR TABLAS
    // =========================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TABLA ONLINE
        String CREATE_TABLE_ONLINE =
                "CREATE TABLE " + TABLE_USUARIOS_ONLINE + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NOMBRE + " TEXT NOT NULL, " +
                        COLUMN_CORREO + " TEXT UNIQUE, " +
                        COLUMN_CONTRASENA + " TEXT NOT NULL, " +
                        COLUMN_ROL + " TEXT NOT NULL, " +
                        COLUMN_ESTADO + " INTEGER DEFAULT 1, " +
                        COLUMN_FECHA + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ")";

        // TABLA OFFLINE
        String CREATE_TABLE_OFFLINE =
                "CREATE TABLE " + TABLE_USUARIOS_OFFLINE + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NOMBRE + " TEXT NOT NULL, " +
                        COLUMN_CORREO + " TEXT UNIQUE, " +
                        COLUMN_CONTRASENA + " TEXT NOT NULL, " +
                        COLUMN_ROL + " TEXT NOT NULL, " +
                        COLUMN_ESTADO + " INTEGER DEFAULT 1, " +
                        COLUMN_FECHA + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ")";

        db.execSQL(CREATE_TABLE_ONLINE);
        db.execSQL(CREATE_TABLE_OFFLINE);
    }

    // =========================
    // ACTUALIZAR DB
    // =========================

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS_ONLINE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS_OFFLINE);

        onCreate(db);
    }
}
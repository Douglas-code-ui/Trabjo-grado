package com.grupo2.prygrados.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.grupo2.prygrados.Modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private DatabaseHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // =========================
    // INSERTAR ONLINE
    // =========================

    public long insertarUsuarioOnline(Usuario usuario) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(DatabaseHelper.COLUMN_NOMBRE,
                usuario.getNombre());

        values.put(DatabaseHelper.COLUMN_CORREO,
                usuario.getCorreo());

        values.put(DatabaseHelper.COLUMN_CONTRASENA,
                usuario.getContrasena());

        values.put(DatabaseHelper.COLUMN_ROL,
                usuario.getRol());

        values.put(DatabaseHelper.COLUMN_ESTADO,
                1);

        long resultado = db.insert(
                DatabaseHelper.TABLE_USUARIOS_ONLINE,
                null,
                values
        );

        db.close();

        return resultado;
    }

    // =========================
    // INSERTAR OFFLINE
    // =========================

    public long insertarUsuarioOffline(Usuario usuario) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(DatabaseHelper.COLUMN_NOMBRE,
                usuario.getNombre());

        values.put(DatabaseHelper.COLUMN_CORREO,
                usuario.getCorreo());

        values.put(DatabaseHelper.COLUMN_CONTRASENA,
                usuario.getContrasena());

        values.put(DatabaseHelper.COLUMN_ROL,
                usuario.getRol());

        values.put(DatabaseHelper.COLUMN_ESTADO,
                1);

        long resultado = db.insert(
                DatabaseHelper.TABLE_USUARIOS_OFFLINE,
                null,
                values
        );

        db.close();

        return resultado;
    }

    // =========================
    // OBTENER OFFLINE
    // =========================

    public List<Usuario> obtenerUsuariosOffline() {

        List<Usuario> lista =
                new ArrayList<>();

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM "
                        + DatabaseHelper.TABLE_USUARIOS_OFFLINE,
                null
        );

        if (cursor.moveToFirst()) {

            do {

                Usuario usuario =
                        new Usuario();

                usuario.setId(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_ID)));

                usuario.setNombre(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_NOMBRE)));

                usuario.setCorreo(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_CORREO)));

                usuario.setContrasena(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_CONTRASENA)));

                usuario.setRol(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_ROL)));

                lista.add(usuario);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();



        return lista;
    }

    // =========================
    // ELIMINAR OFFLINE
    // =========================

    // =========================
// ELIMINAR UN USUARIO OFFLINE
// =========================

    public void eliminarUsuarioOffline(int idUsuario) {

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        db.delete(
                DatabaseHelper.TABLE_USUARIOS_OFFLINE,
                DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(idUsuario)}
        );

        db.close();
    }
}
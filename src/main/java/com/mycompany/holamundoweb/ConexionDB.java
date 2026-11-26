package com.mycompany.holamundoweb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Entrega una conexion nueva en cada llamada. Antes se compartia una sola
 * conexion estatica entre todas las peticiones, y como cada servlet/JSP la
 * cerraba con try-with-resources, las peticiones concurrentes se la cerraban
 * unas a otras.
 *
 * Los datos de conexion se pueden cambiar con las variables de entorno
 * DB_URL, DB_USER y DB_PASSWORD.
 */
public class ConexionDB {

    private static final String URL = env("DB_URL", "jdbc:sqlserver://localhost:1433;"
            + "databaseName=EmpresaDB;"
            + "encrypt=true;"
            + "trustServerCertificate=true;");
    private static final String USER = env("DB_USER", "sa");
    private static final String PASSWORD = env("DB_PASSWORD", "HolaMundo#2026");

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("No se encontro el driver mssql-jdbc: " + e.getMessage());
        }
    }

    private ConexionDB() {}

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String env(String nombre, String porDefecto) {
        String valor = System.getenv(nombre);
        return (valor == null || valor.isBlank()) ? porDefecto : valor;
    }
}

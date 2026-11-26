package com.mycompany.holamundoweb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla Empleados. */
public class EmpleadoDAO {

    public List<Empleado> listar() throws SQLException {
        String sql = "SELECT IdEmpleado, Nombre, Apellido, Puesto, Salario FROM Empleados ORDER BY Nombre, Apellido";
        List<Empleado> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Empleado buscar(int id) throws SQLException {
        String sql = "SELECT IdEmpleado, Nombre, Apellido, Puesto, Salario FROM Empleados WHERE IdEmpleado = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public void insertar(Empleado e) throws SQLException {
        String sql = "INSERT INTO Empleados(Nombre, Apellido, Puesto, Salario) VALUES(?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getApellido());
            stmt.setString(3, e.getPuesto());
            stmt.setBigDecimal(4, e.getSalario());
            stmt.executeUpdate();
        }
    }

    public void actualizar(Empleado e) throws SQLException {
        String sql = "UPDATE Empleados SET Nombre = ?, Apellido = ?, Puesto = ?, Salario = ? WHERE IdEmpleado = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getApellido());
            stmt.setString(3, e.getPuesto());
            stmt.setBigDecimal(4, e.getSalario());
            stmt.setInt(5, e.getIdEmpleado());
            stmt.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Empleados WHERE IdEmpleado = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Empleado mapear(ResultSet rs) throws SQLException {
        return new Empleado(
                rs.getInt("IdEmpleado"),
                rs.getString("Nombre"),
                rs.getString("Apellido"),
                rs.getString("Puesto"),
                rs.getBigDecimal("Salario"));
    }
}

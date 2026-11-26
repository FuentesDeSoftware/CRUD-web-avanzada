package com.mycompany.holamundoweb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla Planilla. */
public class PlanillaDAO {

    private static final String SELECT = "SELECT p.IdPago, p.idEmpleado, e.Nombre + ' ' + e.Apellido AS nombre, "
            + "p.fechaPago, p.horasExtras, p.viaticos "
            + "FROM Planilla p INNER JOIN Empleados e ON p.idEmpleado = e.IdEmpleado ";

    public List<Pago> listar() throws SQLException {
        List<Pago> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(SELECT + "ORDER BY p.fechaPago DESC, p.IdPago DESC");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Pago buscar(int idPago) throws SQLException {
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(SELECT + "WHERE p.IdPago = ?")) {
            stmt.setInt(1, idPago);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public void insertar(Pago p) throws SQLException {
        String sql = "INSERT INTO Planilla(idEmpleado, horasExtras, viaticos, fechaPago) VALUES(?, ?, ?, GETDATE())";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getIdEmpleado());
            stmt.setBigDecimal(2, p.getHorasExtras());
            stmt.setBigDecimal(3, p.getViaticos());
            stmt.executeUpdate();
        }
    }

    public void actualizar(Pago p) throws SQLException {
        String sql = "UPDATE Planilla SET idEmpleado = ?, horasExtras = ?, viaticos = ? WHERE IdPago = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getIdEmpleado());
            stmt.setBigDecimal(2, p.getHorasExtras());
            stmt.setBigDecimal(3, p.getViaticos());
            stmt.setInt(4, p.getIdPago());
            stmt.executeUpdate();
        }
    }

    public void eliminar(int idPago) throws SQLException {
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Planilla WHERE IdPago = ?")) {
            stmt.setInt(1, idPago);
            stmt.executeUpdate();
        }
    }

    private Pago mapear(ResultSet rs) throws SQLException {
        Pago p = new Pago();
        p.setIdPago(rs.getInt("IdPago"));
        p.setIdEmpleado(rs.getInt("idEmpleado"));
        p.setNombreEmpleado(rs.getString("nombre"));
        p.setFechaPago(rs.getDate("fechaPago"));
        p.setHorasExtras(rs.getBigDecimal("horasExtras"));
        p.setViaticos(rs.getBigDecimal("viaticos"));
        return p;
    }
}

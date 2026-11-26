package com.mycompany.holamundoweb;

import java.math.BigDecimal;
import java.sql.Date;

/** Un registro de la tabla Planilla. */
public class Pago {
    private int idPago;
    private int idEmpleado;
    private String nombreEmpleado;
    private Date fechaPago;
    private BigDecimal horasExtras;
    private BigDecimal viaticos;

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }

    public BigDecimal getHorasExtras() { return horasExtras; }
    public void setHorasExtras(BigDecimal horasExtras) { this.horasExtras = horasExtras; }

    public BigDecimal getViaticos() { return viaticos; }
    public void setViaticos(BigDecimal viaticos) { this.viaticos = viaticos; }
}

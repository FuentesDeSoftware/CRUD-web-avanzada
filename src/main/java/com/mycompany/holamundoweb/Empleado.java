package com.mycompany.holamundoweb;

import java.math.BigDecimal;

/**
 *
 * @author Julio Fuentes
 */
public class Empleado {
    private int idEmpleado;
    private String nombre;
    private String apellido;
    private String puesto;
    private BigDecimal salario;

    public Empleado() {}

    public Empleado(int idEmpleado, String nombre, String apellido, String puesto, BigDecimal salario) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.puesto = puesto;
        this.salario = salario;
    }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}

<%--
    Document   : empleados
    Author     : Julio Fuentes
    Se muestra a traves de EmpleadoServlet, que deja en el request:
    "empleados", y opcionalmente "empleado" (en edicion) y "errorBD".
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, com.mycompany.holamundoweb.*"%>
<%@page import="static com.mycompany.holamundoweb.Util.h"%>
<%
    List<Empleado> empleados = (List<Empleado>) request.getAttribute("empleados");
    Empleado emp = (Empleado) request.getAttribute("empleado");
    if (empleados == null) empleados = Collections.emptyList();
    boolean editando = emp != null;
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Empleados - HolaMundoWeb</title>
        <link rel="stylesheet" href="estilos.css">
    </head>
    <body>
        <nav>
            <a href="PlanillaServlet">Planilla</a>
            <a href="EmpleadoServlet" class="activo">Empleados</a>
            <a href="reportes.jsp">Reportes</a>
        </nav>

        <h2>Empleados</h2>

        <% if (request.getParameter("msg") != null) { %>
            <p class="ok"><%= h(request.getParameter("msg")) %></p>
        <% } %>
        <% if (request.getParameter("error") != null) { %>
            <p class="err"><%= h(request.getParameter("error")) %></p>
        <% } %>
        <% if (request.getAttribute("errorBD") != null) { %>
            <p class="err"><%= h(request.getAttribute("errorBD")) %></p>
        <% } %>

        <form action="EmpleadoServlet" method="post">
            <fieldset>
                <legend><%= editando ? "Editar empleado #" + emp.getIdEmpleado() : "Nuevo empleado" %></legend>
                <input type="hidden" name="accion" value="guardar">
                <% if (editando) { %>
                    <input type="hidden" name="id" value="<%= emp.getIdEmpleado() %>">
                <% } %>

                <div class="form-group">
                    <label for="nombre">Nombre:</label>
                    <input type="text" name="nombre" id="nombre" maxlength="50" required
                           value="<%= editando ? h(emp.getNombre()) : "" %>">
                </div>
                <div class="form-group">
                    <label for="apellido">Apellido:</label>
                    <input type="text" name="apellido" id="apellido" maxlength="50" required
                           value="<%= editando ? h(emp.getApellido()) : "" %>">
                </div>
                <div class="form-group">
                    <label for="puesto">Puesto:</label>
                    <input type="text" name="puesto" id="puesto" maxlength="60" required
                           value="<%= editando ? h(emp.getPuesto()) : "" %>">
                </div>
                <div class="form-group">
                    <label for="salario">Salario:</label>
                    <input type="number" step="0.01" min="0" name="salario" id="salario" required
                           value="<%= editando ? h(emp.getSalario()) : "" %>">
                </div>

                <button type="submit" class="btn btn-primario"><%= editando ? "Guardar cambios" : "Crear empleado" %></button>
                <% if (editando) { %>
                    <a href="EmpleadoServlet" class="btn">Cancelar</a>
                <% } %>
            </fieldset>
        </form>

        <table>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Apellido</th>
                <th>Puesto</th>
                <th>Salario</th>
                <th>Acciones</th>
            </tr>
            <% if (empleados.isEmpty()) { %>
                <tr><td colspan="6">No hay empleados registrados.</td></tr>
            <% } %>
            <% for (Empleado e : empleados) { %>
                <tr>
                    <td><%= e.getIdEmpleado() %></td>
                    <td><%= h(e.getNombre()) %></td>
                    <td><%= h(e.getApellido()) %></td>
                    <td><%= h(e.getPuesto()) %></td>
                    <td class="num">L <%= String.format("%,.2f", e.getSalario()) %></td>
                    <td class="acciones">
                        <a class="btn" href="EmpleadoServlet?accion=editar&id=<%= e.getIdEmpleado() %>">Editar</a>
                        <form action="EmpleadoServlet" method="post"
                              onsubmit="return confirm('¿Eliminar al empleado #<%= e.getIdEmpleado() %>?');">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="id" value="<%= e.getIdEmpleado() %>">
                            <button type="submit" class="btn btn-peligro">Eliminar</button>
                        </form>
                    </td>
                </tr>
            <% } %>
        </table>
    </body>
</html>

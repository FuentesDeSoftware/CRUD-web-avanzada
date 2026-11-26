<%--
    Document   : planilla
    Author     : Julio Fuentes
    Se muestra a traves de PlanillaServlet, que deja en el request:
    "empleados", "pagos", y opcionalmente "pago" (en edicion) y "errorBD".
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, com.mycompany.holamundoweb.*"%>
<%@page import="static com.mycompany.holamundoweb.Util.h"%>
<%
    List<Empleado> empleados = (List<Empleado>) request.getAttribute("empleados");
    List<Pago> pagos = (List<Pago>) request.getAttribute("pagos");
    Pago pago = (Pago) request.getAttribute("pago");
    if (empleados == null) empleados = Collections.emptyList();
    if (pagos == null) pagos = Collections.emptyList();
    boolean editando = pago != null;
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Gestión de Planilla - HolaMundoWeb</title>
        <link rel="stylesheet" href="estilos.css">
    </head>
    <body>
        <nav>
            <a href="PlanillaServlet" class="activo">Planilla</a>
            <a href="EmpleadoServlet">Empleados</a>
            <a href="reportes.jsp">Reportes</a>
        </nav>

        <h2>Control de Planilla de Empleados</h2>

        <% if (request.getParameter("msg") != null) { %>
            <p class="ok"><%= h(request.getParameter("msg")) %></p>
        <% } %>
        <% if (request.getParameter("error") != null) { %>
            <p class="err"><%= h(request.getParameter("error")) %></p>
        <% } %>
        <% if (request.getAttribute("errorBD") != null) { %>
            <p class="err"><%= h(request.getAttribute("errorBD")) %></p>
        <% } %>

        <form action="PlanillaServlet" method="post">
            <fieldset>
                <legend><%= editando ? "Editar pago #" + pago.getIdPago() : "Registrar pago" %></legend>
                <input type="hidden" name="accion" value="guardar">
                <% if (editando) { %>
                    <input type="hidden" name="idPago" value="<%= pago.getIdPago() %>">
                <% } %>

                <div class="form-group">
                    <label for="idEmpleado">Empleado:</label>
                    <select name="idEmpleado" id="idEmpleado" required>
                        <% if (empleados.isEmpty()) { %>
                            <option value="">No hay empleados registrados</option>
                        <% } %>
                        <% for (Empleado e : empleados) { %>
                            <option value="<%= e.getIdEmpleado() %>"
                                <%= editando && pago.getIdEmpleado() == e.getIdEmpleado() ? "selected" : "" %>>
                                <%= h(e.getNombreCompleto()) %>
                            </option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="horasExtras">Horas extras:</label>
                    <input type="number" step="0.01" min="0" name="horasExtras" id="horasExtras" required
                           value="<%= editando ? h(pago.getHorasExtras()) : "" %>">
                </div>

                <div class="form-group">
                    <label for="viaticos">Viáticos:</label>
                    <input type="number" step="0.01" min="0" name="viaticos" id="viaticos" required
                           value="<%= editando ? h(pago.getViaticos()) : "" %>">
                </div>

                <button type="submit" class="btn btn-primario"><%= editando ? "Guardar cambios" : "Registrar pago" %></button>
                <% if (editando) { %>
                    <a href="PlanillaServlet" class="btn">Cancelar</a>
                <% } %>
            </fieldset>
        </form>

        <h3>Historial de Planilla</h3>
        <table>
            <tr>
                <th>ID Pago</th>
                <th>Empleado</th>
                <th>Fecha</th>
                <th>Horas Extras</th>
                <th>Viáticos</th>
                <th>Acciones</th>
            </tr>
            <% if (pagos.isEmpty()) { %>
                <tr><td colspan="6">No hay pagos registrados.</td></tr>
            <% } %>
            <% for (Pago p : pagos) { %>
                <tr>
                    <td><%= p.getIdPago() %></td>
                    <td><%= h(p.getNombreEmpleado()) %></td>
                    <td><%= p.getFechaPago() %></td>
                    <td class="num"><%= String.format("%,.2f", p.getHorasExtras()) %></td>
                    <td class="num">L <%= String.format("%,.2f", p.getViaticos()) %></td>
                    <td class="acciones">
                        <a class="btn" href="PlanillaServlet?accion=editar&idPago=<%= p.getIdPago() %>">Editar</a>
                        <a class="btn" href="ReporteServlet?r=colilla&idPago=<%= p.getIdPago() %>" target="_blank">Colilla</a>
                        <form action="PlanillaServlet" method="post"
                              onsubmit="return confirm('¿Eliminar el pago #<%= p.getIdPago() %>?');">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="idPago" value="<%= p.getIdPago() %>">
                            <button type="submit" class="btn btn-peligro">Eliminar</button>
                        </form>
                    </td>
                </tr>
            <% } %>
        </table>
    </body>
</html>

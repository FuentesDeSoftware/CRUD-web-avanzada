<%--
    Document   : reportes
    Author     : Julio Fuentes
    Menu de reportes de JasperReports (los genera ReporteServlet).
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Reportes - HolaMundoWeb</title>
        <link rel="stylesheet" href="estilos.css">
    </head>
    <body>
        <nav>
            <a href="PlanillaServlet">Planilla</a>
            <a href="EmpleadoServlet">Empleados</a>
            <a href="reportes.jsp" class="activo">Reportes</a>
        </nav>

        <h2>Reportes</h2>

        <div class="reportes">
            <fieldset>
                <legend>Planilla general</legend>
                <p>Todos los pagos registrados, con totales de horas extras y viáticos.</p>
                <a class="btn btn-primario" href="ReporteServlet?r=planilla" target="_blank">Ver PDF</a>
                <a class="btn" href="ReporteServlet?r=planilla&formato=xlsx">Descargar Excel</a>
            </fieldset>

            <fieldset>
                <legend>Empleados</legend>
                <p>Listado de empleados con puesto y salario; incluye total y promedio de salarios.</p>
                <a class="btn btn-primario" href="ReporteServlet?r=empleados" target="_blank">Ver PDF</a>
                <a class="btn" href="ReporteServlet?r=empleados&formato=xlsx">Descargar Excel</a>
            </fieldset>

            <fieldset>
                <legend>Planilla por empleado</legend>
                <p>Pagos agrupados por empleado con subtotales. Deje las fechas vacías para ver todo.</p>
                <form action="ReporteServlet" method="get" target="_blank">
                    <input type="hidden" name="r" value="porEmpleado">
                    <div class="form-group">
                        <label for="desde">Desde:</label>
                        <input type="date" name="desde" id="desde">
                    </div>
                    <div class="form-group">
                        <label for="hasta">Hasta:</label>
                        <input type="date" name="hasta" id="hasta">
                    </div>
                    <button type="submit" name="formato" value="pdf" class="btn btn-primario">Ver PDF</button>
                    <button type="submit" name="formato" value="xlsx" class="btn">Descargar Excel</button>
                </form>
            </fieldset>

            <fieldset>
                <legend>Comprobante de pago</legend>
                <p>Colilla individual de un pago. También se abre con el botón «Colilla» en la
                   <a href="PlanillaServlet">planilla</a>.</p>
                <form action="ReporteServlet" method="get" target="_blank">
                    <input type="hidden" name="r" value="colilla">
                    <div class="form-group">
                        <label for="idPago">ID de pago:</label>
                        <input type="number" min="1" name="idPago" id="idPago" required>
                    </div>
                    <button type="submit" class="btn btn-primario">Ver PDF</button>
                </form>
            </fieldset>
        </div>
    </body>
</html>

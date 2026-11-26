package com.mycompany.holamundoweb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

/**
 * Genera los reportes de JasperReports.
 *
 * GET /ReporteServlet?r=REPORTE&formato=pdf|xlsx[&desde=yyyy-MM-dd&hasta=yyyy-MM-dd][&idPago=N]
 *
 * Reportes (r):
 *   planilla     Planilla general (por defecto)
 *   empleados    Listado de empleados con totales de salario
 *   porEmpleado  Planilla agrupada por empleado, filtrable por fechas
 *   colilla      Comprobante de un pago (requiere idPago)
 *
 * @author Julio Fuentes
 */
@WebServlet(name = "ReporteServlet", urlPatterns = {"/ReporteServlet"})
public class ReporteServlet extends HttpServlet {

    private static final String AUTOR = "Julio Fuentes";

    /** Nombre en la URL -> archivo .jrxml en /WEB-INF/reports/ */
    private static final Map<String, String> REPORTES = Map.of(
            "planilla", "ReporteAvanzadaII",
            "empleados", "ReporteEmpleados",
            "porEmpleado", "ReportePlanillaPorEmpleado",
            "colilla", "ReporteColillaPago");

    /** Cada .jrxml se compila la primera vez que se pide y luego se reutiliza. */
    private final Map<String, JasperReport> compilados = new ConcurrentHashMap<>();

    private JasperReport getReporte(String archivo) throws JRException {
        try {
            return compilados.computeIfAbsent(archivo, a -> {
                String ruta = "/WEB-INF/reports/" + a + ".jrxml";
                try (InputStream in = getServletContext().getResourceAsStream(ruta)) {
                    if (in == null) {
                        throw new IllegalStateException("No se encontro el reporte: " + ruta);
                    }
                    return JasperCompileManager.compileReport(in);
                } catch (IOException | JRException e) {
                    throw new IllegalStateException("No se pudo compilar " + ruta + ": " + e.getMessage(), e);
                }
            });
        } catch (IllegalStateException e) {
            if (e.getCause() instanceof JRException jr) throw jr;
            throw new JRException(e.getMessage(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String clave = request.getParameter("r");
        if (clave == null || clave.isBlank()) clave = "planilla";
        String archivo = REPORTES.get(clave);
        if (archivo == null) {
            response.sendError(404, "Reporte desconocido: " + clave);
            return;
        }
        boolean excel = "xlsx".equalsIgnoreCase(request.getParameter("formato"));

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("AUTOR", AUTOR);
        try {
            LocalDate desde = fecha(request, "desde");
            LocalDate hasta = fecha(request, "hasta");
            if (desde != null && hasta != null && desde.isAfter(hasta)) {
                throw new IllegalArgumentException("La fecha inicial no puede ser mayor que la final");
            }
            if (desde != null) parametros.put("FECHA_INICIO", java.sql.Date.valueOf(desde));
            if (hasta != null) parametros.put("FECHA_FIN", java.sql.Date.valueOf(hasta));
            if ("colilla".equals(clave)) {
                parametros.put("ID_PAGO", Util.entero(request, "idPago"));
            }
        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage());
            return;
        }

        try {
            JasperReport reporte = getReporte(archivo);
            JasperPrint jasperPrint;
            try (Connection conn = ConexionDB.getConexion()) {
                jasperPrint = JasperFillManager.fillReport(reporte, parametros, conn);
            }

            byte[] datos = excel ? exportarXlsx(jasperPrint) : JasperExportManager.exportReportToPdf(jasperPrint);

            response.setContentType(excel
                    ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    : "application/pdf");
            response.setHeader("Content-Disposition",
                    (excel ? "attachment" : "inline") + "; filename=" + archivo + (excel ? ".xlsx" : ".pdf"));
            response.setContentLength(datos.length);
            OutputStream out = response.getOutputStream();
            out.write(datos);
            out.flush();

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(500, "No hay conexion con la base de datos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Error al generar el reporte: " + e.getMessage());
        }
    }

    private static byte[] exportarXlsx(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(bytes));
        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        config.setDetectCellType(true);
        config.setRemoveEmptySpaceBetweenRows(true);
        config.setWhitePageBackground(false);
        exporter.setConfiguration(config);
        exporter.exportReport();
        return bytes.toByteArray();
    }

    private static LocalDate fecha(HttpServletRequest request, String nombre) {
        String v = request.getParameter(nombre);
        if (v == null || v.isBlank()) return null;
        try {
            return LocalDate.parse(v.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha invalida en '" + nombre + "': " + v);
        }
    }

    @Override
    public String getServletInfo() {
        return "Genera los reportes de JasperReports en PDF o Excel";
    }
}

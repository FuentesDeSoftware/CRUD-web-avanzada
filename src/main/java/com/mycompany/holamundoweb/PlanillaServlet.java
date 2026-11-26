package com.mycompany.holamundoweb;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CRUD de la planilla (pagos).
 * GET  /PlanillaServlet                          -> lista (index.jsp)
 * GET  /PlanillaServlet?accion=editar&idPago=N   -> lista con el formulario cargado
 * POST accion=guardar (con o sin idPago), accion=eliminar&idPago=N
 */
@WebServlet(name = "PlanillaServlet", urlPatterns = {"/PlanillaServlet"})
public class PlanillaServlet extends HttpServlet {

    private static final String PAGINA = "PlanillaServlet";
    private final PlanillaDAO planillaDAO = new PlanillaDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if ("editar".equals(request.getParameter("accion"))) {
                Pago p = planillaDAO.buscar(Util.entero(request, "idPago"));
                if (p == null) {
                    Util.redirigir(request, response, PAGINA, "error", "El pago no existe");
                    return;
                }
                request.setAttribute("pago", p);
            }
            request.setAttribute("empleados", empleadoDAO.listar());
            request.setAttribute("pagos", planillaDAO.listar());
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorBD", "No se pudo consultar la base de datos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorBD", e.getMessage());
        }
        request.getRequestDispatcher("/planilla.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        try {
            if ("eliminar".equals(accion)) {
                planillaDAO.eliminar(Util.entero(request, "idPago"));
                Util.redirigir(request, response, PAGINA, "msg", "Pago eliminado");
                return;
            }

            Pago p = new Pago();
            p.setIdEmpleado(Util.entero(request, "idEmpleado"));
            p.setHorasExtras(Util.decimal(request, "horasExtras"));
            p.setViaticos(Util.decimal(request, "viaticos"));

            String idPago = request.getParameter("idPago");
            if (idPago == null || idPago.isBlank()) {
                planillaDAO.insertar(p);
                Util.redirigir(request, response, PAGINA, "msg", "Pago registrado exitosamente");
            } else {
                p.setIdPago(Util.entero(request, "idPago"));
                planillaDAO.actualizar(p);
                Util.redirigir(request, response, PAGINA, "msg", "Pago actualizado");
            }
        } catch (IllegalArgumentException e) {
            Util.redirigir(request, response, PAGINA, "error", e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            Util.redirigir(request, response, PAGINA, "error", "Error de base de datos: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "CRUD de la planilla";
    }
}

package com.mycompany.holamundoweb;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CRUD de empleados.
 * GET  /EmpleadoServlet                     -> lista (empleados.jsp)
 * GET  /EmpleadoServlet?accion=editar&id=N  -> lista con el formulario cargado
 * POST accion=guardar (con o sin id), accion=eliminar&id=N
 */
@WebServlet(name = "EmpleadoServlet", urlPatterns = {"/EmpleadoServlet"})
public class EmpleadoServlet extends HttpServlet {

    private static final String PAGINA = "EmpleadoServlet";
    private final EmpleadoDAO dao = new EmpleadoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if ("editar".equals(request.getParameter("accion"))) {
                Empleado e = dao.buscar(Util.entero(request, "id"));
                if (e == null) {
                    Util.redirigir(request, response, PAGINA, "error", "El empleado no existe");
                    return;
                }
                request.setAttribute("empleado", e);
            }
            request.setAttribute("empleados", dao.listar());
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorBD", "No se pudo consultar la base de datos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorBD", e.getMessage());
        }
        request.getRequestDispatcher("/empleados.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        try {
            if ("eliminar".equals(accion)) {
                dao.eliminar(Util.entero(request, "id"));
                Util.redirigir(request, response, PAGINA, "msg", "Empleado eliminado");
                return;
            }

            Empleado e = new Empleado();
            e.setNombre(Util.texto(request, "nombre"));
            e.setApellido(Util.texto(request, "apellido"));
            e.setPuesto(Util.texto(request, "puesto"));
            e.setSalario(Util.decimal(request, "salario"));

            String id = request.getParameter("id");
            if (id == null || id.isBlank()) {
                dao.insertar(e);
                Util.redirigir(request, response, PAGINA, "msg", "Empleado creado");
            } else {
                e.setIdEmpleado(Util.entero(request, "id"));
                dao.actualizar(e);
                Util.redirigir(request, response, PAGINA, "msg", "Empleado actualizado");
            }
        } catch (IllegalArgumentException e) {
            Util.redirigir(request, response, PAGINA, "error", e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            // 547 = violacion de llave foranea: el empleado tiene pagos en la planilla
            String msg = e.getErrorCode() == 547
                    ? "No se puede eliminar: el empleado tiene pagos registrados en la planilla"
                    : "Error de base de datos: " + e.getMessage();
            Util.redirigir(request, response, PAGINA, "error", msg);
        }
    }

    @Override
    public String getServletInfo() {
        return "CRUD de empleados";
    }
}

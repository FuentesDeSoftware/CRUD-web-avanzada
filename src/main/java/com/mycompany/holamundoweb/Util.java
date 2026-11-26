package com.mycompany.holamundoweb;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Utilidades compartidas por servlets y JSP. */
public final class Util {

    private Util() {}

    /** Escapa texto para mostrarlo en HTML (evita XSS). */
    public static String h(Object valor) {
        if (valor == null) return "";
        String s = valor.toString();
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            switch (c) {
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '&' -> sb.append("&amp;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String texto(HttpServletRequest req, String nombre) {
        String v = req.getParameter(nombre);
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException("El campo '" + nombre + "' es obligatorio");
        }
        return v.trim();
    }

    public static int entero(HttpServletRequest req, String nombre) {
        try {
            return Integer.parseInt(texto(req, nombre));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo '" + nombre + "' debe ser un numero entero");
        }
    }

    public static BigDecimal decimal(HttpServletRequest req, String nombre) {
        try {
            BigDecimal v = new BigDecimal(texto(req, nombre));
            if (v.signum() < 0) {
                throw new IllegalArgumentException("El campo '" + nombre + "' no puede ser negativo");
            }
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo '" + nombre + "' debe ser un numero");
        }
    }

    public static void redirigir(HttpServletRequest req, HttpServletResponse resp, String pagina,
                                 String clave, String mensaje) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/" + pagina + "?" + clave + "="
                + URLEncoder.encode(mensaje, StandardCharsets.UTF_8));
    }
}

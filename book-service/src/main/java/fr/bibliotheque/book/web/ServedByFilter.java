package fr.bibliotheque.book.web;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Ajoute l'en-tête "X-Served-By" avec le nom du pod (variable HOSTNAME).
 * Le front l'affiche : pratique pour montrer le load-balancing entre réplicas Kubernetes.
 */
@Component
public class ServedByFilter extends OncePerRequestFilter {

    private final String servedBy;

    public ServedByFilter(@Value("${spring.application.name}") String appName,
                          @Value("${HOSTNAME:local}") String hostname) {
        this.servedBy = appName + "@" + hostname;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        response.setHeader("X-Served-By", servedBy);
        chain.doFilter(request, response);
    }
}

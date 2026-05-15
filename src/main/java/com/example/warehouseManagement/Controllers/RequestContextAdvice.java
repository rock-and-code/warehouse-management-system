package com.example.warehouseManagement.Controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Exposes the raw HTTP query string as a {@code currentQueryString} model
 * attribute on every Thymeleaf view. Used by {@code fragments/pagination} to
 * rebuild pager links while preserving any active advanced-search filters.
 *
 * Thymeleaf 3.1 removed the {@code #request} / {@code #httpServletRequest}
 * implicit expression objects for security reasons; injecting the request and
 * publishing the query string ourselves is the supported replacement.
 */
@ControllerAdvice
public class RequestContextAdvice {

    @ModelAttribute("currentQueryString")
    public String currentQueryString(HttpServletRequest request) {
        String qs = request.getQueryString();
        return qs == null ? "" : qs;
    }
}

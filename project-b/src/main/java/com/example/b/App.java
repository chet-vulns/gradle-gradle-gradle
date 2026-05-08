package com.example.b;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Demo servlet for project-b.
 *
 * NOTE: This class intentionally contains first-party security
 * vulnerabilities for SAST / code-scanning tool demonstrations.
 * Do not use any of this code in production.
 */
public class App extends HttpServlet {

    /**
     * VULN 1: Reflected Cross-Site Scripting (CWE-79)
     * The `name` query parameter (a CodeQL-recognized remote source) is
     * written straight into the HTML response body.
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html><body><h1>Hello, " + name + "!</h1></body></html>");
    }

    /**
     * VULN 2: Server-Side Request Forgery (CWE-918)
     * The server fetches an arbitrary user-supplied URL, allowing access
     * to internal services such as the cloud metadata endpoint.
     */
    public void fetchUrl(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String target = req.getParameter("url");
        URL u = new URL(target);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        try (InputStream in = conn.getInputStream()) {
            resp.getOutputStream().write(in.readAllBytes());
        }
    }

    /**
     * VULN 3: Open Redirect (CWE-601)
     * The redirect target is taken straight from a request parameter.
     */
    public void redirect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String next = req.getParameter("next");
        resp.sendRedirect(next);
    }

    /**
     * VULN 4: XML External Entity (XXE) Injection (CWE-611)
     * `DocumentBuilderFactory` is used with default settings — external
     * entities and DTDs are enabled, so a malicious XML body posted by a
     * client can read local files or trigger SSRF.
     */
    public void parseXml(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        String root = db.parse(req.getInputStream())
                .getDocumentElement()
                .getNodeName();
        resp.getWriter().println(root);
    }
}

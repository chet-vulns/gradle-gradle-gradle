package com.example.b;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Demo HTTP-style handler for project-b.
 *
 * NOTE: This class intentionally contains first-party security
 * vulnerabilities for SAST / code-scanning tool demonstrations.
 * Do not use any of this code in production.
 */
public class App {

    /**
     * VULN 1: Reflected Cross-Site Scripting (CWE-79)
     * The `name` parameter is interpolated straight into the HTML
     * response body, so `<script>alert(1)</script>` executes.
     */
    public String renderGreeting(Map<String, String> params) {
        String name = params.get("name");
        return "<html><body><h1>Hello, " + name + "!</h1></body></html>";
    }

    /**
     * VULN 2: Server-Side Request Forgery (CWE-918)
     * The server fetches an arbitrary user-supplied URL, allowing
     * attackers to reach internal services such as the cloud metadata
     * endpoint (e.g. http://169.254.169.254/...).
     */
    public String fetchUrl(String target) throws IOException {
        URL u = new URL(target);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        try (InputStream in = conn.getInputStream()) {
            return new String(in.readAllBytes());
        }
    }

    /**
     * VULN 3: Open Redirect (CWE-601)
     * The redirect target is taken from a request parameter without
     * validation, enabling phishing redirects to attacker-controlled hosts.
     */
    public String buildRedirectHeader(Map<String, String> params) {
        String next = params.get("next");
        return "Location: " + next;
    }

    /**
     * VULN 4: XML External Entity (XXE) Injection (CWE-611)
     * `DocumentBuilderFactory` is used with default settings — external
     * entities and DTDs are enabled, so a malicious XML body can read
     * local files or trigger SSRF.
     */
    public String parseXml(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new ByteArrayInputStream(xml.getBytes()))
                .getDocumentElement()
                .getNodeName();
    }

    public static void main(String[] args) {
        System.out.println("project-b demo — see App.java for vulnerable handlers");
    }
}

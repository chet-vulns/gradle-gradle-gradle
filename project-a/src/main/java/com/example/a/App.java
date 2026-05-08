package com.example.a;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Demo servlet for project-a.
 *
 * NOTE: This class intentionally contains first-party security
 * vulnerabilities for SAST / code-scanning tool demonstrations.
 * Do not use any of this code in production.
 */
public class App extends HttpServlet {

    /**
     * VULN 1: SQL Injection (CWE-89)
     * The `username` request parameter is concatenated directly into the
     * SQL statement, allowing arbitrary SQL to be executed.
     */
    public void findUserByName(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String username = req.getParameter("username");
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:demo");
        Statement stmt = connection.createStatement();
        String sql = "SELECT id, name FROM users WHERE name = '" + username + "'";
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            resp.getWriter().println(rs.getInt("id") + ":" + rs.getString("name"));
        }
    }

    /**
     * VULN 2: OS Command Injection (CWE-78)
     * The `host` request parameter is passed straight to the shell via
     * `sh -c`, allowing arbitrary command execution.
     */
    public void pingHost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InterruptedException {
        String host = req.getParameter("host");
        Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 1 " + host});
        p.waitFor();
        resp.getOutputStream().write(p.getInputStream().readAllBytes());
    }

    /**
     * VULN 3: Path Traversal (CWE-22)
     * The `filename` request parameter is appended to a base directory
     * without sanitization, so `../../etc/passwd` reads files outside
     * the intended directory.
     */
    public void readUserFile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseDir = "/var/app/userdata";
        String filename = req.getParameter("filename");
        try (BufferedReader r = new BufferedReader(new FileReader(baseDir + "/" + filename))) {
            String line;
            while ((line = r.readLine()) != null) {
                resp.getWriter().println(line);
            }
        }
    }

    /**
     * VULN 4: Use of broken cryptographic hash (CWE-327, CWE-916)
     * MD5 is used to hash passwords, with no salt and no key-stretching.
     */
    public String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

package com.example.a;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Demo application for project-a.
 *
 * NOTE: This class intentionally contains first-party security
 * vulnerabilities for SAST / code-scanning tool demonstrations.
 * Do not use any of this code in production.
 */
public class App {

    private final Connection connection;

    public App(Connection connection) {
        this.connection = connection;
    }

    /**
     * VULN 1: SQL Injection (CWE-89)
     * User-controlled `username` is concatenated directly into the SQL
     * statement, allowing arbitrary SQL to be executed.
     */
    public String findUserByName(String username) throws Exception {
        Statement stmt = connection.createStatement();
        String sql = "SELECT id, name FROM users WHERE name = '" + username + "'";
        ResultSet rs = stmt.executeQuery(sql);
        StringBuilder out = new StringBuilder();
        while (rs.next()) {
            out.append(rs.getInt("id")).append(":").append(rs.getString("name")).append("\n");
        }
        return out.toString();
    }

    /**
     * VULN 2: OS Command Injection (CWE-78)
     * User input is passed straight to the shell via `sh -c`, allowing
     * arbitrary command execution (e.g. `host; rm -rf /`).
     */
    public String pingHost(String host) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ping -c 1 " + host});
        p.waitFor();
        return new String(p.getInputStream().readAllBytes());
    }

    /**
     * VULN 3: Path Traversal (CWE-22)
     * `filename` is appended to a base directory without sanitization,
     * so `../../etc/passwd` reads files outside the intended directory.
     */
    public String readUserFile(String baseDir, String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new FileReader(baseDir + "/" + filename))) {
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
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

    public static void main(String[] args) {
        System.out.println("project-a demo — see App.java for vulnerable methods");
    }
}

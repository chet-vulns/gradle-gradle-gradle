package com.example.c;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;
import java.util.Random;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * Demo servlet for project-c.
 *
 * NOTE: This class intentionally contains first-party security
 * vulnerabilities for SAST / code-scanning tool demonstrations.
 * Do not use any of this code in production.
 */
public class App extends HttpServlet {

    // VULN 1: Hard-coded credentials (CWE-798)
    // Pattern-based: secrets must never be embedded in source.
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "P@ssw0rd-prod-2024!";
    private static final String API_KEY = "sk_live_51HxYzABCDEF1234567890abcdefghij";

    /**
     * VULN 2: Deserialization of Untrusted Data (CWE-502)
     * `ObjectInputStream.readObject` is called directly on the request
     * body, allowing arbitrary code execution via gadget chains.
     */
    public void loadSession(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(req.getInputStream())) {
            Object session = ois.readObject();
            resp.getWriter().println(session);
        }
    }

    /**
     * VULN 3: Insecure randomness for security-sensitive tokens (CWE-330, CWE-338)
     * `java.util.Random` is not cryptographically secure — its output is
     * predictable. The token is then sent back to the client as a
     * password-reset / session identifier.
     */
    public void issueResetToken(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Random rng = new Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            token.append(Integer.toHexString(rng.nextInt(16)));
        }
        resp.addCookie(new jakarta.servlet.http.Cookie("reset_token", token.toString()));
        resp.getWriter().println(token);
    }

    /**
     * VULN 4: LDAP Injection (CWE-90)
     * `username` is concatenated into the LDAP search filter without
     * escaping, so input like `*)(uid=*` returns every user.
     */
    public void findLdapUser(HttpServletRequest req, HttpServletResponse resp)
            throws NamingException, IOException {
        String username = req.getParameter("username");

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://ldap.internal:389");
        env.put(Context.SECURITY_PRINCIPAL, "cn=" + DB_USER);
        env.put(Context.SECURITY_CREDENTIALS, DB_PASSWORD);

        DirContext ctx = new InitialDirContext(env);
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String filter = "(&(objectClass=person)(uid=" + username + "))";
        NamingEnumeration<SearchResult> results = ctx.search("ou=users", filter, controls);

        while (results.hasMore()) {
            resp.getWriter().println(results.next().getNameInNamespace());
        }
        resp.getWriter().println("api_key_len=" + API_KEY.length());
    }
}

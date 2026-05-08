package com.example.c;

import java.io.ByteArrayInputStream;
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
 * Demo application for project-c.
 *
 * NOTE: This class intentionally contains first-party security
 * vulnerabilities for SAST / code-scanning tool demonstrations.
 * Do not use any of this code in production.
 */
public class App {

    // VULN 1: Hard-coded credentials (CWE-798)
    // Secrets must never be embedded in source — they leak via VCS history,
    // binaries, and crash dumps, and cannot be rotated without a rebuild.
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "P@ssw0rd-prod-2024!";
    private static final String API_KEY = "sk_live_51HxYzABCDEF1234567890abcdefghij";

    /**
     * VULN 2: Deserialization of Untrusted Data (CWE-502)
     * `ObjectInputStream.readObject` is called on attacker-controlled
     * bytes, allowing arbitrary code execution via gadget chains.
     */
    public Object loadSession(byte[] sessionBytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(sessionBytes))) {
            return ois.readObject();
        }
    }

    /**
     * VULN 3: Insecure randomness for security-sensitive tokens (CWE-330, CWE-338)
     * `java.util.Random` is not cryptographically secure — its output is
     * predictable, so attackers can forge password-reset / session tokens.
     */
    public String generateResetToken() {
        Random rng = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(Integer.toHexString(rng.nextInt(16)));
        }
        return sb.toString();
    }

    /**
     * VULN 4: LDAP Injection (CWE-90)
     * `username` is concatenated into the LDAP search filter without
     * escaping, so input like `*)(uid=*` returns every user.
     */
    public String findLdapUser(String ldapUrl, String username) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_PRINCIPAL, "cn=" + DB_USER);
        env.put(Context.SECURITY_CREDENTIALS, DB_PASSWORD);

        DirContext ctx = new InitialDirContext(env);
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String filter = "(&(objectClass=person)(uid=" + username + "))";
        NamingEnumeration<SearchResult> results = ctx.search("ou=users", filter, controls);
        StringBuilder out = new StringBuilder();
        while (results.hasMore()) {
            out.append(results.next().getNameInNamespace()).append("\n");
        }
        return out.toString();
    }

    public static void main(String[] args) {
        App app = new App();
        System.out.println("API key length: " + API_KEY.length());
        System.out.println("Reset token: " + app.generateResetToken());
    }
}

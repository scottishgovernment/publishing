package scot.gov.migration;

import org.hippoecm.repository.api.HippoSession;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Provide credentials usd to log into JCR.
 */
public class MigrationUserCredentialsSource {

    /**
     * Provide credentials for the migration user by getting the migration password from environment variable.
     */
    public Credentials get(String authHeader) {
        String base64Credentials = authHeader.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        String[] values = credentials.split(":", 2);
        SimpleCredentials simpleCredentials = new SimpleCredentials(values[0], values[1].toCharArray());
        simpleCredentials.setAttribute(HippoSession.NO_SYSTEM_IMPERSONATION, Boolean.TRUE);
        return simpleCredentials;    }
}

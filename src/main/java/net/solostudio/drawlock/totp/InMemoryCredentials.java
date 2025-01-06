package net.solostudio.drawlock.totp;

import com.warrenstrange.googleauth.ICredentialRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCredentials implements ICredentialRepository {
    private final Map<String, String> credentials = new HashMap<>();

    @Override
    public String getSecretKey(String username) {
        return credentials.get(username);
    }

    @Override
    public void saveUserCredentials(String username, String secretKey, int validationCode, List<Integer> scratchCodes) {
        credentials.put(username, secretKey);
    }

    public List<String> getAllUsernames() {
        return new ArrayList<>(credentials.keySet());
    }
}

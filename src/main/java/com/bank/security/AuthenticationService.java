package com.bank.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Very small authentication stub to map username -> User (with role).
 * In real system: use secure authentication, tokens, sessions.
 */
public class AuthenticationService {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public AuthenticationService() {
        // seed some demo users
        users.put("alice", new User("alice", new ManagerState()));
        users.put("bob", new User("bob", new CustomerState()));
        users.put("admin", new User("admin", new AdminState()));
    }

    public User authenticate(String username) {
        // stub: returns user if known
        return users.get(username);
    }

    public void registerUser(User user) {
        users.put(user.getUsername(), user);
    }
}
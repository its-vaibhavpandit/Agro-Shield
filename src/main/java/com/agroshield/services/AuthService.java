package com.agroshield.services;
import com.agroshield.models.User;
import com.agroshield.models.Role;
import com.agroshield.exceptions.InvalidLoginException;
import com.agroshield.exceptions.WeakPasswordException;
import com.agroshield.storage.FileStorageManager;
import java.util.HashMap;

public class AuthService {
    private HashMap<String, User> users;
    private User currentUser;

    public AuthService() {
        users = FileStorageManager.loadUsers();
        if(users.isEmpty()) {
            users.put("admin", new User("admin", "admin123", Role.ADMIN));
            users.put("user", new User("user", "user123", Role.USER));
            FileStorageManager.saveUsers(users);
        }
    }

    public synchronized void register(String username, String password) throws WeakPasswordException, Exception {
        if (users.containsKey(username)) throw new Exception("User already exists.");
        if (password.length() < 6) throw new WeakPasswordException("Password must be at least 6 characters.");
        users.put(username, new User(username, password, Role.USER));
        FileStorageManager.saveUsers(users);
    }

    public synchronized User login(String username, String password) throws InvalidLoginException {
        User user = users.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new InvalidLoginException("Invalid username or password.");
        }
        currentUser = user;
        return user;
    }

    public void logout() { currentUser = null; }
    public User getCurrentUser() { return currentUser; }
    public HashMap<String, User> getAllUsers() { return users; }
    public synchronized void deleteUser(String username) {
        users.remove(username);
        FileStorageManager.saveUsers(users);
    }
}
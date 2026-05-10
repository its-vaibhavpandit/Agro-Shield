package com.agroshield.storage;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import com.agroshield.models.User;
import com.agroshield.models.CropReport;

public class FileStorageManager {
    private static final String USERS_FILE = "users.dat";
    private static final String REPORTS_FILE = "reports.dat";

    public static synchronized void saveUsers(HashMap<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static synchronized HashMap<String, User> loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (HashMap<String, User>) ois.readObject();
        } catch (Exception e) { return new HashMap<>(); }
    }

    public static synchronized void saveReports(List<CropReport> reports) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REPORTS_FILE))) {
            oos.writeObject(reports);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static synchronized List<CropReport> loadReports() {
        File f = new File(REPORTS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<CropReport>) ois.readObject();
        } catch (Exception e) { return new ArrayList<>(); }
    }
}
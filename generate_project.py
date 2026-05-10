import os

files = {}

# 1. pom.xml
files['pom.xml'] = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.agroshield</groupId>
    <artifactId>agroshield-ai</artifactId>
    <version>1.0-SNAPSHOT</version>
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>"""

# 2. Exceptions
files['src/main/java/com/agroshield/exceptions/InvalidLoginException.java'] = """package com.agroshield.exceptions;
public class InvalidLoginException extends Exception {
    public InvalidLoginException(String message) { super(message); }
}"""

files['src/main/java/com/agroshield/exceptions/WeakPasswordException.java'] = """package com.agroshield.exceptions;
public class WeakPasswordException extends Exception {
    public WeakPasswordException(String message) { super(message); }
}"""

files['src/main/java/com/agroshield/exceptions/InvalidCropDataException.java'] = """package com.agroshield.exceptions;
public class InvalidCropDataException extends Exception {
    public InvalidCropDataException(String message) { super(message); }
}"""

# 3. Models
files['src/main/java/com/agroshield/models/Role.java'] = """package com.agroshield.models;
public enum Role { USER, ADMIN }"""

files['src/main/java/com/agroshield/models/User.java'] = """package com.agroshield.models;
import java.io.Serializable;
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private Role role;
    public User(String username, String password, Role role) {
        this.username = username; this.password = password; this.role = role;
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
}"""

files['src/main/java/com/agroshield/models/CropReport.java'] = """package com.agroshield.models;
import java.io.Serializable;
import java.time.LocalDateTime;
public class CropReport implements Serializable, Comparable<CropReport> {
    private static final long serialVersionUID = 1L;
    private String id;
    private String cropName;
    private String disease;
    private double confidence;
    private LocalDateTime timestamp;
    public CropReport(String id, String cropName, String disease, double confidence) {
        this.id = id; this.cropName = cropName; this.disease = disease;
        this.confidence = confidence; this.timestamp = LocalDateTime.now();
    }
    public String getId() { return id; }
    public String getCropName() { return cropName; }
    public String getDisease() { return disease; }
    public double getConfidence() { return confidence; }
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public int compareTo(CropReport o) { return o.timestamp.compareTo(this.timestamp); }
}"""

# 4. Storage
files['src/main/java/com/agroshield/storage/FileStorageManager.java'] = """package com.agroshield.storage;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import com.agroshield.models.User;
import com.agroshield.models.CropReport;

public class FileStorageManager {
    private static final String USERS_FILE = "users.dat";
    private static final String REPORTS_FILE = "reports.dat";

    public static void saveUsers(HashMap<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, User> loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (HashMap<String, User>) ois.readObject();
        } catch (Exception e) { return new HashMap<>(); }
    }

    public static void saveReports(List<CropReport> reports) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REPORTS_FILE))) {
            oos.writeObject(reports);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static List<CropReport> loadReports() {
        File f = new File(REPORTS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<CropReport>) ois.readObject();
        } catch (Exception e) { return new ArrayList<>(); }
    }
}"""

# 5. Services
files['src/main/java/com/agroshield/services/AuthService.java'] = """package com.agroshield.services;
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

    public void register(String username, String password) throws WeakPasswordException, Exception {
        if (users.containsKey(username)) throw new Exception("User already exists.");
        if (password.length() < 6) throw new WeakPasswordException("Password must be at least 6 characters.");
        users.put(username, new User(username, password, Role.USER));
        FileStorageManager.saveUsers(users);
    }

    public User login(String username, String password) throws InvalidLoginException {
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
    public void deleteUser(String username) {
        users.remove(username);
        FileStorageManager.saveUsers(users);
    }
}"""

files['src/main/java/com/agroshield/services/TranslationService.java'] = """package com.agroshield.services;
import java.util.HashMap;
import java.util.Map;

public class TranslationService {
    private String currentLanguage = "en";
    private Map<String, Map<String, String>> dictionary = new HashMap<>();

    public TranslationService() {
        Map<String, String> en = new HashMap<>();
        en.put("login", "Login"); en.put("register", "Register");
        en.put("dashboard", "Dashboard"); en.put("scan", "Disease Scanner");

        Map<String, String> hi = new HashMap<>();
        hi.put("login", "लॉग इन करें"); hi.put("register", "रजिस्टर करें");
        hi.put("dashboard", "डैशबोर्ड"); hi.put("scan", "रोग स्कैनर");

        Map<String, String> pa = new HashMap<>();
        pa.put("login", "ਲਾਗਿਨ"); pa.put("register", "ਰਜਿਸਟਰ");
        pa.put("dashboard", "ਡੈਸ਼ਬੋਰਡ"); pa.put("scan", "ਬਿਮਾਰੀ ਸਕੈਨਰ");

        dictionary.put("en", en); dictionary.put("hi", hi); dictionary.put("pa", pa);
    }

    public void setLanguage(String lang) { currentLanguage = lang; }
    public String translate(String key) {
        return dictionary.getOrDefault(currentLanguage, dictionary.get("en")).getOrDefault(key, key);
    }
}"""

# 6. Disease
files['src/main/java/com/agroshield/disease/DiseaseScannerService.java'] = """package com.agroshield.disease;
import java.util.*;

public class DiseaseScannerService<T> {
    private Map<String, List<String>> diseaseDb = new HashMap<>();

    public DiseaseScannerService() {
        diseaseDb.put("Wheat", Arrays.asList("Leaf Rust", "Powdery Mildew", "Healthy"));
        diseaseDb.put("Rice", Arrays.asList("Brown Spot", "Blast", "Healthy"));
        diseaseDb.put("Tomato", Arrays.asList("Late Blight", "Early Blight", "Healthy"));
    }

    public String scanImage(String cropType) {
        try {
            Thread.sleep(2000); // Simulate AI scanning delay
        } catch (InterruptedException e) { e.printStackTrace(); }
        
        List<String> diseases = diseaseDb.getOrDefault(cropType, Arrays.asList("Unknown Disease", "Healthy"));
        Random rand = new Random();
        return diseases.get(rand.nextInt(diseases.size()));
    }
    
    public double getConfidence() {
        return 75.0 + new Random().nextDouble() * 24.0;
    }
}"""

# 7. Soil
files['src/main/java/com/agroshield/soil/SoilAnalyzerService.java'] = """package com.agroshield.soil;
public class SoilAnalyzerService {
    public String analyze(double ph, double moisture, double nitrogen) {
        StringBuilder result = new StringBuilder("Soil Analysis Result:\\n");
        if(ph < 5.5) result.append("- High Acidity. Consider adding lime.\\n");
        else if(ph > 7.5) result.append("- High Alkalinity. Add sulfur.\\n");
        else result.append("- Perfect pH.\\n");

        if(moisture < 30) result.append("- Low Moisture. Needs irrigation.\\n");
        if(nitrogen < 20) result.append("- Low Nitrogen. Suggest NPK fertilizer.\\n");

        result.append("\\nSuggested Crop: ");
        if(ph >= 6 && ph <= 7.5 && moisture > 40) result.append("Rice or Wheat.");
        else result.append("Potato or Cotton.");
        
        return result.toString();
    }
}"""

# 8. Weather
files['src/main/java/com/agroshield/weather/WeatherService.java'] = """package com.agroshield.weather;
import java.util.Random;

public class WeatherService {
    private double temp = 25.0;
    private double humidity = 60.0;
    private Random rand = new Random();

    public void updateWeather() {
        temp += (rand.nextDouble() * 2 - 1);
        humidity += (rand.nextDouble() * 4 - 2);
        if(humidity > 100) humidity = 100;
        if(humidity < 0) humidity = 0;
    }

    public double getTemp() { return temp; }
    public double getHumidity() { return humidity; }
    public String getForecast() {
        return (humidity > 80) ? "Rainy" : (temp > 30 ? "Sunny" : "Cloudy");
    }
}"""

# 9. Chatbot
files['src/main/java/com/agroshield/chatbot/ChatbotService.java'] = """package com.agroshield.chatbot;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ChatbotService {
    private Map<String, Function<String, String>> rules = new HashMap<>();

    public ChatbotService() {
        rules.put("hello", s -> "Hello! I am AgriBot. How can I help you today?");
        rules.put("fertilizer", s -> "For nitrogen deficiency, use Urea. For potassium, use Muriate of Potash.");
        rules.put("disease", s -> "Please use the Disease Scanner module for accurate detection.");
        rules.put("weather", s -> "You can check live weather in the Weather Panel.");
    }

    public String getResponse(String input) {
        String lowerInput = input.toLowerCase();
        return rules.entrySet().stream()
                .filter(entry -> lowerInput.contains(entry.getKey()))
                .map(entry -> entry.getValue().apply(input))
                .findFirst()
                .orElse("I'm not sure about that. Try asking about fertilizers, diseases, or weather.");
    }
}"""

# 10. Utils
files['src/main/java/com/agroshield/utils/AudioPlayer.java'] = """package com.agroshield.utils;
public class AudioPlayer {
    public static void playSimulatedVoice(String text) {
        new Thread(() -> {
            System.out.println("[VOICE ASSISTANT]: " + text);
            try { Thread.sleep(text.length() * 50); } catch (InterruptedException e) {}
        }).start();
    }
}"""

files['src/main/java/com/agroshield/utils/UIConstants.java'] = """package com.agroshield.utils;
import java.awt.Color;
import java.awt.Font;

public class UIConstants {
    public static final Color BG_DARK = new Color(18, 18, 18);
    public static final Color SURFACE_DARK = new Color(30, 30, 30);
    public static final Color PRIMARY = new Color(0, 230, 118);
    public static final Color TEXT_LIGHT = new Color(255, 255, 255);
    public static final Color TEXT_MUTED = new Color(170, 170, 170);
    public static final Color ERROR = new Color(255, 82, 82);

    public static final Font FONT_LARGE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
}"""

# 11. UI Components
files['src/main/java/com/agroshield/ui/components/CustomButton.java'] = """package com.agroshield.ui.components;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.agroshield.utils.UIConstants;

public class CustomButton extends JButton {
    private boolean isHover = false;
    private Color hoverColor;

    public CustomButton(String text) {
        super(text);
        setFont(UIConstants.FONT_TITLE);
        setForeground(UIConstants.TEXT_LIGHT);
        setBackground(UIConstants.PRIMARY);
        hoverColor = UIConstants.PRIMARY.darker();
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { isHover = true; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { isHover = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isHover ? hoverColor : getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g);
        g2.dispose();
    }
}"""

files['src/main/java/com/agroshield/ui/components/RoundedPanel.java'] = """package com.agroshield.ui.components;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;

public class RoundedPanel extends JPanel {
    public RoundedPanel() {
        setOpaque(false);
        setBackground(UIConstants.SURFACE_DARK);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        super.paintComponent(g);
        g2.dispose();
    }
}"""

# 12. Main UI Logic
files['src/main/java/com/agroshield/ui/MainFrame.java'] = """package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.services.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.admin.AdminPanel;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AuthService authService;

    public MainFrame() {
        authService = new AuthService();
        setTitle("AgroShield AI - Smart Crop & Farm Management");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIConstants.BG_DARK);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UIConstants.BG_DARK);

        mainPanel.add(new LoginPanel(this, authService), "LOGIN");
        mainPanel.add(new RegisterPanel(this, authService), "REGISTER");

        add(mainPanel);
    }

    public void showPanel(String name) { cardLayout.show(mainPanel, name); }

    public void navigateToDashboard() {
        if (authService.getCurrentUser() != null) {
            if(authService.getCurrentUser().getRole() == com.agroshield.models.Role.ADMIN) {
                mainPanel.add(new AdminPanel(this, authService), "ADMIN");
                showPanel("ADMIN");
            } else {
                mainPanel.add(new DashboardPanel(this), "DASHBOARD");
                showPanel("DASHBOARD");
            }
        }
    }

    public void logout() {
        authService.logout();
        showPanel("LOGIN");
    }
}"""

files['src/main/java/com/agroshield/ui/LoginPanel.java'] = """package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.services.AuthService;
import com.agroshield.ui.components.*;
import com.agroshield.utils.UIConstants;

public class LoginPanel extends JPanel {
    public LoginPanel(MainFrame frame, AuthService auth) {
        setBackground(UIConstants.BG_DARK);
        setLayout(new GridBagLayout());

        RoundedPanel box = new RoundedPanel();
        box.setPreferredSize(new Dimension(400, 400));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = new JTextField();
        userField.setMaximumSize(new Dimension(300, 40));
        JPasswordField passField = new JPasswordField();
        passField.setMaximumSize(new Dimension(300, 40));

        CustomButton loginBtn = new CustomButton("Login");
        loginBtn.setMaximumSize(new Dimension(300, 40));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton regBtn = new JButton("Create new account");
        regBtn.setForeground(UIConstants.PRIMARY);
        regBtn.setContentAreaFilled(false);
        regBtn.setBorderPainted(false);
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            try {
                auth.login(userField.getText(), new String(passField.getPassword()));
                frame.navigateToDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        regBtn.addActionListener(e -> frame.showPanel("REGISTER"));

        box.add(title);
        box.add(Box.createRigidArea(new Dimension(0, 30)));
        box.add(new JLabel("Username")).setForeground(UIConstants.TEXT_LIGHT);
        box.add(userField);
        box.add(Box.createRigidArea(new Dimension(0, 15)));
        box.add(new JLabel("Password")).setForeground(UIConstants.TEXT_LIGHT);
        box.add(passField);
        box.add(Box.createRigidArea(new Dimension(0, 30)));
        box.add(loginBtn);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        box.add(regBtn);

        add(box);
    }
}"""

files['src/main/java/com/agroshield/ui/RegisterPanel.java'] = """package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.services.AuthService;
import com.agroshield.ui.components.*;
import com.agroshield.utils.UIConstants;

public class RegisterPanel extends JPanel {
    public RegisterPanel(MainFrame frame, AuthService auth) {
        setBackground(UIConstants.BG_DARK);
        setLayout(new GridBagLayout());

        RoundedPanel box = new RoundedPanel();
        box.setPreferredSize(new Dimension(400, 400));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Register");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = new JTextField();
        userField.setMaximumSize(new Dimension(300, 40));
        JPasswordField passField = new JPasswordField();
        passField.setMaximumSize(new Dimension(300, 40));

        CustomButton regBtn = new CustomButton("Register");
        regBtn.setMaximumSize(new Dimension(300, 40));
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = new JButton("Back to Login");
        backBtn.setForeground(UIConstants.PRIMARY);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        regBtn.addActionListener(e -> {
            try {
                auth.register(userField.getText(), new String(passField.getPassword()));
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                frame.showPanel("LOGIN");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> frame.showPanel("LOGIN"));

        box.add(title);
        box.add(Box.createRigidArea(new Dimension(0, 30)));
        box.add(new JLabel("Username")).setForeground(UIConstants.TEXT_LIGHT);
        box.add(userField);
        box.add(Box.createRigidArea(new Dimension(0, 15)));
        box.add(new JLabel("Password")).setForeground(UIConstants.TEXT_LIGHT);
        box.add(passField);
        box.add(Box.createRigidArea(new Dimension(0, 30)));
        box.add(regBtn);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        box.add(backBtn);

        add(box);
    }
}"""

files['src/main/java/com/agroshield/ui/DashboardPanel.java'] = """package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.CustomButton;

public class DashboardPanel extends JPanel {
    private JPanel contentPanel;
    private CardLayout contentLayout;

    public DashboardPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_DARK);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.SURFACE_DARK);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("🌱 AgroShield AI");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.PRIMARY);
        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        String[] menus = {"Home", "Disease Scanner", "Soil Analysis", "Weather", "Chatbot"};
        
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(UIConstants.BG_DARK);

        // Add sub-panels
        contentPanel.add(createHomePanel(), "Home");
        contentPanel.add(new DiseaseScannerPanel(), "Disease Scanner");
        contentPanel.add(new SoilAnalysisPanel(), "Soil Analysis");
        contentPanel.add(new WeatherPanel(), "Weather");
        contentPanel.add(new ChatbotPanel(), "Chatbot");

        for (String m : menus) {
            JButton btn = new JButton(m);
            btn.setForeground(UIConstants.TEXT_LIGHT);
            btn.setBackground(UIConstants.SURFACE_DARK);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(UIConstants.FONT_REGULAR);
            btn.setMaximumSize(new Dimension(200, 40));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> contentLayout.show(contentPanel, m));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        sidebar.add(Box.createVerticalGlue());
        CustomButton logoutBtn = new CustomButton("Logout");
        logoutBtn.setMaximumSize(new Dimension(200, 40));
        logoutBtn.addActionListener(e -> frame.logout());
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHomePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIConstants.BG_DARK);
        JLabel lbl = new JLabel("Welcome to AgroShield Dashboard!");
        lbl.setFont(UIConstants.FONT_LARGE);
        lbl.setForeground(UIConstants.TEXT_LIGHT);
        p.add(lbl);
        return p;
    }
}"""

files['src/main/java/com/agroshield/ui/DiseaseScannerPanel.java'] = """package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.*;
import com.agroshield.disease.DiseaseScannerService;

public class DiseaseScannerPanel extends JPanel {
    private DiseaseScannerService<Object> scanner = new DiseaseScannerService<>();

    public DiseaseScannerPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Crop Disease Scanner");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        add(title, BorderLayout.NORTH);

        RoundedPanel center = new RoundedPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        String[] crops = {"Wheat", "Rice", "Tomato", "Potato", "Cotton"};
        JComboBox<String> cropCombo = new JComboBox<>(crops);
        cropCombo.setMaximumSize(new Dimension(300, 40));
        
        CustomButton scanBtn = new CustomButton("Scan Image");
        scanBtn.setMaximumSize(new Dimension(300, 40));

        JTextArea resultArea = new JTextArea(8, 30);
        resultArea.setFont(UIConstants.FONT_REGULAR);
        resultArea.setBackground(UIConstants.BG_DARK);
        resultArea.setForeground(UIConstants.PRIMARY);
        resultArea.setEditable(false);

        scanBtn.addActionListener(e -> {
            scanBtn.setEnabled(false);
            scanBtn.setText("Scanning...");
            new Thread(() -> {
                String crop = (String) cropCombo.getSelectedItem();
                String disease = scanner.scanImage(crop);
                double conf = scanner.getConfidence();
                SwingUtilities.invokeLater(() -> {
                    resultArea.setText("Analysis Complete!\\n\\nCrop: " + crop + 
                    "\\nDetected Disease: " + disease + 
                    "\\nConfidence: " + String.format("%.2f%%", conf));
                    scanBtn.setEnabled(true);
                    scanBtn.setText("Scan Image");
                });
            }).start();
        });

        center.add(new JLabel("Select Crop Type")).setForeground(UIConstants.TEXT_LIGHT);;
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(cropCombo);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(scanBtn);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(new JScrollPane(resultArea));

        add(center, BorderLayout.CENTER);
    }
}"""

files['src/main/java/com/agroshield/ui/SoilAnalysisPanel.java'] = """package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.*;
import com.agroshield.soil.SoilAnalyzerService;

public class SoilAnalysisPanel extends JPanel {
    private SoilAnalyzerService soilAnalyzer = new SoilAnalyzerService();

    public SoilAnalysisPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Soil Analysis Module");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        add(title, BorderLayout.NORTH);

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(4, 2, 20, 20));
        form.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JTextField phField = new JTextField();
        JTextField moistField = new JTextField();
        JTextField nitroField = new JTextField();

        JLabel phLbl = new JLabel("pH Level (1-14):"); phLbl.setForeground(UIConstants.TEXT_LIGHT);
        JLabel mLbl = new JLabel("Moisture (%):"); mLbl.setForeground(UIConstants.TEXT_LIGHT);
        JLabel nLbl = new JLabel("Nitrogen (mg/kg):"); nLbl.setForeground(UIConstants.TEXT_LIGHT);

        form.add(phLbl); form.add(phField);
        form.add(mLbl); form.add(moistField);
        form.add(nLbl); form.add(nitroField);

        CustomButton analyzeBtn = new CustomButton("Analyze Soil");
        form.add(analyzeBtn);

        JTextArea resArea = new JTextArea();
        resArea.setEditable(false);
        resArea.setBackground(UIConstants.BG_DARK);
        resArea.setForeground(UIConstants.PRIMARY);

        analyzeBtn.addActionListener(e -> {
            try {
                double ph = Double.parseDouble(phField.getText());
                double moist = Double.parseDouble(moistField.getText());
                double nitro = Double.parseDouble(nitroField.getText());
                resArea.setText(soilAnalyzer.analyze(ph, moist, nitro));
            } catch (Exception ex) {
                resArea.setText("Please enter valid numeric values!");
            }
        });

        add(form, BorderLayout.CENTER);
        add(new JScrollPane(resArea), BorderLayout.SOUTH);
    }
}"""

files['src/main/java/com/agroshield/ui/WeatherPanel.java'] = """package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.RoundedPanel;
import com.agroshield.weather.WeatherService;

public class WeatherPanel extends JPanel {
    private WeatherService weatherService = new WeatherService();
    private JLabel tempLbl, humLbl, forecastLbl;

    public WeatherPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Live Weather Forecast");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        add(title, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel();
        card.setLayout(new GridLayout(3, 1));
        
        tempLbl = new JLabel(); tempLbl.setFont(UIConstants.FONT_TITLE); tempLbl.setForeground(UIConstants.PRIMARY);
        humLbl = new JLabel(); humLbl.setFont(UIConstants.FONT_TITLE); humLbl.setForeground(UIConstants.TEXT_LIGHT);
        forecastLbl = new JLabel(); forecastLbl.setFont(UIConstants.FONT_TITLE); forecastLbl.setForeground(UIConstants.TEXT_LIGHT);

        card.add(tempLbl); card.add(humLbl); card.add(forecastLbl);
        add(card, BorderLayout.CENTER);

        // Multi-threading for live updates
        Thread t = new Thread(() -> {
            while(true) {
                weatherService.updateWeather();
                SwingUtilities.invokeLater(() -> {
                    tempLbl.setText(String.format("Temperature: %.1f °C", weatherService.getTemp()));
                    humLbl.setText(String.format("Humidity: %.1f %%", weatherService.getHumidity()));
                    forecastLbl.setText("Forecast: " + weatherService.getForecast());
                });
                try { Thread.sleep(2000); } catch (Exception e) {}
            }
        });
        t.setDaemon(true);
        t.start();
    }
}"""

files['src/main/java/com/agroshield/ui/ChatbotPanel.java'] = """package com.agroshield.ui;
import javax.swing.*;
import java.awt.*;
import com.agroshield.utils.UIConstants;
import com.agroshield.ui.components.CustomButton;
import com.agroshield.chatbot.ChatbotService;

public class ChatbotPanel extends JPanel {
    private ChatbotService bot = new ChatbotService();

    public ChatbotPanel() {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("AI AgriBot");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.TEXT_LIGHT);
        add(title, BorderLayout.NORTH);

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(UIConstants.SURFACE_DARK);
        chatArea.setForeground(UIConstants.TEXT_LIGHT);
        chatArea.setFont(UIConstants.FONT_REGULAR);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setOpaque(false);
        JTextField input = new JTextField();
        CustomButton send = new CustomButton("Send");
        
        send.addActionListener(e -> {
            String text = input.getText().trim();
            if(!text.isEmpty()) {
                chatArea.append("You: " + text + "\\n");
                chatArea.append("AgriBot: " + bot.getResponse(text) + "\\n\\n");
                input.setText("");
            }
        });

        input.addActionListener(e -> send.doClick());

        bottom.add(input, BorderLayout.CENTER);
        bottom.add(send, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }
}"""

files['src/main/java/com/agroshield/admin/AdminPanel.java'] = """package com.agroshield.admin;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import com.agroshield.utils.UIConstants;
import com.agroshield.services.AuthService;
import com.agroshield.models.User;
import com.agroshield.ui.components.CustomButton;
import com.agroshield.ui.MainFrame;

public class AdminPanel extends JPanel {
    public AdminPanel(MainFrame frame, AuthService auth) {
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Admin Dashboard - User Management");
        title.setFont(UIConstants.FONT_LARGE);
        title.setForeground(UIConstants.PRIMARY);
        add(title, BorderLayout.NORTH);

        String[] cols = {"Username", "Role"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setBackground(UIConstants.SURFACE_DARK);
        table.setForeground(UIConstants.TEXT_LIGHT);
        table.setFillsViewportHeight(true);

        Map<String, User> users = auth.getAllUsers();
        for (User u : users.values()) {
            model.addRow(new Object[]{u.getUsername(), u.getRole().name()});
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        CustomButton delBtn = new CustomButton("Delete User");
        CustomButton logoutBtn = new CustomButton("Logout");

        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String username = (String) model.getValueAt(row, 0);
                if(username.equals("admin")) {
                    JOptionPane.showMessageDialog(this, "Cannot delete admin!");
                    return;
                }
                auth.deleteUser(username);
                model.removeRow(row);
            }
        });

        logoutBtn.addActionListener(e -> frame.logout());

        bottom.add(delBtn);
        bottom.add(logoutBtn);
        add(bottom, BorderLayout.SOUTH);
    }
}"""

files['src/main/java/com/agroshield/Main.java'] = """package com.agroshield;
import com.agroshield.ui.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Run UI safely in Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}"""

for path, content in files.items():
    dirname = os.path.dirname(path)
    if dirname:
        os.makedirs(dirname, exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

print("All files generated successfully!")

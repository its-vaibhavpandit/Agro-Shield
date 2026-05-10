# 🌱 AgroShield AI - Smart Farm Management System

[![Java](https://img.shields.io/badge/Java-21-blue?logo=java)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Active-success.svg)]()

A professional-grade **Java Swing desktop application** for intelligent agricultural farm management. Built with modern Java practices including multithreading, serialization, custom exceptions, and comprehensive collections handling. Designed for MCA academic standards while maintaining production-quality code.

## 🎯 Overview

AgroShield AI provides Indian farmers with an intelligent, user-friendly desktop platform to manage crops, detect diseases, analyze soil conditions, and access real-time weather data. The application combines AI-powered diagnostics with practical farm management tools in an intuitive interface.

✨ Key Features

 1. 🔐 **Multi-User Authentication System**
- Secure login and registration with role-based access (Admin/User)
- Synchronized access control with custom `InvalidLoginException` and `WeakPasswordException`
- Persistent user storage with binary serialization (`users.dat`)
- Admin panel for user management

 2. 📸 **AI-Powered Disease Scanner**
- Real-time asynchronous image recognition simulation using multithreading
- Support for multiple crop types: Wheat, Rice, Tomato, and more
- Confidence scoring (75-99%) for disease predictions
- Automatic report generation with unique IDs and timestamps
- Complete scan history with detailed analytics

3. 📊 **Dynamic Reporting & Persistence**
- Automatic serialization of crop scan reports (`reports.dat`)
- Comprehensive report storage with crop type, disease diagnosis, and confidence scores
- Timestamp tracking for all scans
- Searchable scan history with sorting capabilities

 4. 🌤️ **Live Weather Integration**
- Real-time weather data from OpenMeteo API
- Auto-location detection using IP geolocation
- Temperature, humidity, and weather forecast updates
- Daemon thread for continuous background updates
- Graceful offline fallback with error handling

 5. 🧪 **Soil Analysis Module**
- Interactive pH, moisture, and nitrogen level analysis
- Crop-specific recommendations based on soil metrics
- Real-time calculation engine with validation
- User-friendly numeric input with error handling

 6. 🤖 **AI AgriBot Chatbot**
- Multi-language support: English, Hindi, and Punjabi
- Voice input recognition with speech-to-text conversion
- Context-aware agricultural troubleshooting assistance
- Real-time text and voice interaction

 🛠️ Technical Highlights

- **Language**: Java 21
- **UI Framework**: Swing with custom components
- **Architecture**: MVC-inspired with separated concerns
- **Threading**: Multi-threaded daemon threads for weather updates and voice processing
- **Data Persistence**: Object serialization with synchronized file I/O
- **Exception Handling**: Custom exception hierarchy with proper error propagation
- **Collections**: HashMap, ArrayList, and List implementations for data management

 📋 System Requirements

- **Java Development Kit (JDK)**: Version 21 or higher
- **Python**: 3.7+ (for speech recognition module)
- **Operating System**: Windows, macOS, or Linux
- **RAM**: Minimum 512MB
- **Internet**: Required for weather API and speech services (optional for other features)

 🚀 Getting Started

 Quick Start
```bash
# Clone the repository
git clone https://github.com/yourusername/agroshield-ai.git
cd agroshield-ai

# Run the application directly
./run.bat          # Windows
# or manually
java -cp bin com.agroshield.Main
```

 Building from Source
```bash
# Compile Java source files
python compile.py

# Run the compiled application
java -cp bin com.agroshield.Main
```

 📁 Project Structure

```
AgroShield/
├── src/main/java/com/agroshield/
│   ├── Main.java                    # Application entry point
│   ├── admin/                       # Admin management
│   ├── chatbot/                     # ChatbotService for AI responses
│   ├── disease/                     # DiseaseScannerService engine
│   ├── exceptions/                  # Custom exception classes
│   ├── models/                      # CropReport, User, Role data models
│   ├── services/                    # AuthService, TranslationService
│   ├── soil/                        # SoilAnalyzerService
│   ├── storage/                     # FileStorageManager for persistence
│   ├── ui/                          # Swing UI components
│   ├── utils/                       # UIConstants, AudioPlayer helpers
│   └── weather/                     # WeatherService with API integration
├── bin/                             # Compiled .class files
├── users.dat                        # Serialized user database
├── reports.dat                      # Serialized scan reports
├── compile.py                       # Build script
└── README.md                        # This file
```

 🔐 Security Features

- **Password Protection**: Validated during registration
- **Role-Based Access Control**: Admin vs. User privileges
- **Synchronized Methods**: Thread-safe data access
- **File Validation**: Existence checks before deserialization
- **Exception Safety**: Graceful error handling throughout

 🐛 Recent Improvements (v1.0.1)

- Fixed deprecated `Runtime.exec()` → Replaced with `ProcessBuilder`
- Updated deprecated `URL(String)` → Using `URI.toURL()`
- Improved resource management with try-with-resources
- Enhanced exception handling for thread interruptions
- Fixed null pointer exceptions in list operations

 📝 Usage Examples

 Login/Register
1. Launch the application
2. Register a new account with username and password
3. Login with your credentials
4. Navigate to dashboard or admin panel (based on role)

 Scan Crop for Disease
1. Click "Disease Scanner" in the dashboard
2. Select crop type (Wheat, Rice, Tomato, etc.)
3. Click "Scan Image" to simulate AI detection
4. View results with confidence score
5. Automatic report generation and storage

 Check Weather
1. Access the Weather panel
2. Real-time updates show temperature, humidity, forecast
3. Location auto-detected or uses India default coordinates
4. Updates refresh every 60 seconds

 Soil Analysis
1. Enter pH, moisture, and nitrogen levels
2. Click "Analyze" for crop-specific recommendations
3. Get actionable insights for soil improvement

 🔧 Development & Contribution

We welcome contributions! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

 Setting Up Development Environment
```bash
# Install dependencies (if any beyond JDK 21)
# Ensure Python 3.7+ for speech module

# Compile with debug info
python compile.py

# Run tests
java -cp bin com.agroshield.Main
```

 👥 Authors & Contributors

  Development**: AgroShield AI Development Team
  Architecture**: BCA Java Standards Compliant


 Acknowledgments

- OpenMeteo API for weather data
- Java Swing framework for UI
- Community contributions and feedback

---

**Built with ❤️ for sustainable agriculture | Made in India** 🇮🇳

*Last Updated: May 2026 | Status: Production Ready*

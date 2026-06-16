package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.security.SecureRandom;

public class Main {

    private static final String URL = "jdbc:mysql://localhost:3306/password_db";
    private static final String USER = "root";
    private static final String PASSWORD = "sumitiitian12";

    // Character Pools
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[{]};:,.<>/?";

    // Dynamic Password Generator Method
    public static String generateCustomPassword(int length, boolean useUpper, boolean useDigits, boolean useSymbols) {
        StringBuilder characterPool = new StringBuilder(LOWERCASE);
        StringBuilder password = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        // Jo user ne select kiya, usko pool mein add karna h aur kam se kam ek character fix
        if (useUpper) {
            characterPool.append(UPPERCASE);
            password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        }
        if (useDigits) {
            characterPool.append(DIGITS);
            password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        if (useSymbols) {
            characterPool.append(SYMBOLS);
            password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        }

        // Baki bachi hui length ke liye random characters fill krne h
        int remainingLength = length - password.length();
        for (int i = 0; i < remainingLength; i++) {
            int randomIndex = random.nextInt(characterPool.length());
            password.append(characterPool.charAt(randomIndex));
        }

        // Characters ko shuffle karna h taaki guaranteed characters hamesha shuruat mein na rahein
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 🛡️ Advanced Maven + SQL Password Shield ===");

        // 1. Account Name Input
        System.out.print("👉 Enter account name (e.g., Instagram, GitHub): ");
        String account = scanner.nextLine();

        // 2. Length Input with Validation (Minimum 8)
        int length = 0;
        while (length < 8) {
            System.out.print("👉 Enter password length (Minimum 8 characters recommended): ");
            if (scanner.hasNextInt()) {
                length = scanner.nextInt();
                if (length < 8) {
                    System.out.println("⚠️ Password length should be minimum of 8. Please try again");
                }
            } else {
                System.out.println("⚠️ Input a valid number.");
                scanner.next();
            }
        }

        // 3. Customization Questions
        System.out.print("👉 Capital letters (A-Z) mandatory ? (yes/no): ");
        boolean useUpper = scanner.next().equalsIgnoreCase("yes");

        System.out.print("👉 Numbers (0-9) mandatory ? (yes/no): ");
        boolean useDigits = scanner.next().equalsIgnoreCase("yes");

        System.out.print("👉 Special characters (!@#$) mandatory ? (yes/no): ");
        boolean useSymbols = scanner.next().equalsIgnoreCase("yes");

        // Password Generation
        String generatedPass = generateCustomPassword(length, useUpper, useDigits, useSymbols);
        System.out.println("\n🎯 Your Password is generated: " + generatedPass);

        // Database Saving Logic (JDBC)
        String sql = "INSERT INTO saved_passwords (account_name, password_value) VALUES (?, ?)";

        System.out.println("\n🔄Connecting to Databases...");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account);
            pstmt.setString(2, generatedPass);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("🟢 Success: Custom password has been saved in SQL Database !");
            }

        } catch (Exception e) {
            System.out.println("🔴 Database Error: Connection not done.Check the Driver!!");
            e.printStackTrace();
        }

        scanner.close();
    }
}
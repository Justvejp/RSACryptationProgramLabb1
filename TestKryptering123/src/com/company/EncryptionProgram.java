package com.company;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Scanner;

public class EncryptionProgram {

    private Scanner read;
    private String keyName;
    private final Scanner sc = new Scanner(System.in);
    private final Scanner sc2 = new Scanner(System.in);
    private final Scanner sc3 = new Scanner(System.in);
    private final File encryptedStringFile = new File("C:\\Users\\Gurra\\DokumentsGitub\\TestKryptering123\\src\\com\\company\\savedEncryptedString.txt");
    private final File binaryFile = new File("C:\\Users\\Gurra\\DokumentsGitub\\TestKryptering123\\src\\com\\company\\binaryTextFile.txt");
    private final File stringFile = new File("C:\\Users\\Gurra\\DokumentsGitub\\TestKryptering123\\src\\com\\company\\stringToEncrypt.txt");

    private EncryptionProgram() {
        startMenu();
        encryptionMenu();
    }

    private void startMenu() {

        System.out.println("Welcome to my Encryption Program!");
        System.out.println("(1) Generate Keys");
        System.out.println("(2) Use existing Keys");
        System.out.print("Type here: ");

        int menuChoice = sc.nextInt();

        if (menuChoice == 1) {
            System.out.print("Choose a bit length: ");
            int bitLength = sc.nextInt();
            System.out.println("Choose a name for the keys: ");
            keyName = sc.next();
            generateKeys(keyName, bitLength);
            System.out.println("Created Keys under name: " + keyName);

        } else if (menuChoice == 2) {
            System.out.print("Enter name of the keys you want to use: ");
            keyName = sc.next();
        }
    }

    private void encryptionMenu() {
        KeyPair publicKey = readKey(keyName + "publicKey");
        KeyPair privateKey = readKey(keyName + "privateKey");

        while (true) {

            System.out.println("(1) Encrypt a text String you enter manually.");
            System.out.println("(2) Decrypt a text String you enter manually.");
            System.out.println("(3) Encrypt a .txt file.");
            System.out.println("(4) Decrypt a .txt file.");
            System.out.println("(5) Encrypt a binaryString.");
            System.out.println("(6) Decrypt a binaryString.");
            System.out.println("(7) Exit.");
            System.out.print("Type here: ");

            int choice = sc.nextInt();

            if (choice == 1) {
                encryptStringInTerminal(publicKey);

            } else if (choice == 2) {
                decryptStringInTerminal(privateKey);

            } else if (choice == 3) {
                encryptTxtFile(publicKey);

            } else if (choice == 4) {
                decryptTxtFile(privateKey);

            } else if (choice == 5) {
                encryptABinaryString(publicKey);

            } else if (choice == 6) {
                decryptABinaryString(privateKey);

            } else {
                break;
            }
        }
    }

    private void decryptABinaryString(KeyPair privateKey) {

        String binaryString = "";
        System.out.println("Reading the saved encrypted binary string from the file " + binaryFile.getAbsolutePath());
        try {
            Scanner read2 = new Scanner(binaryFile);
            while (read2.hasNextLine()) {
                binaryString = read2.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String convertedBinaryStringToString = convertBinaryToString(binaryString);
        String result = decrypt(convertedBinaryStringToString, privateKey);
        System.out.println("Converting binaryString to string and decrypting it against the private key result is: " + result);
        String resultString = convertBinaryToString(result);
        System.out.println("Converting result to string: " + resultString);
    }

    private void encryptABinaryString(KeyPair publicKey) {

        String binaryString = generateABinaryStringToTest();
        System.out.println("String converted to Binary: " + binaryString + ", and now encrypting it against the public key.");
        String encryptedBinaryString = encrypt(binaryString, publicKey);
        System.out.println("Result is: " + encryptedBinaryString);

        byte[] l = encryptedBinaryString.getBytes();
        String binaryStringFromEncryptedString = convertStringToBinary(l);

        System.out.println("Saving the encrypted string result as binary in file: " + binaryFile.getAbsolutePath());

        try {
            FileWriter fw = new FileWriter(binaryFile);
            fw.write(binaryStringFromEncryptedString);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateABinaryStringToTest() {
        System.out.print("Enter a string that we convert to binary: ");
        String testString = sc3.nextLine();

        byte[] k = testString.getBytes();
        return convertStringToBinary(k);
    }

    private String convertBinaryToString(String b) {
        String s = "";

        for (int i = 0; i < b.length(); i += 9) {
            String temp = b.substring(i, i + 8);
            int num = Integer.parseInt(temp, 2);
            char letter = (char) num;
            s += letter;
        }
        return s;
    }

    private String convertStringToBinary(byte[] l) {
        StringBuilder binary = new StringBuilder();
        for (byte b : l)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(' ');
        }
        return ""+binary;
    }

    private void decryptStringInTerminal(KeyPair privateKey) {
        System.out.print("Enter encrypted String: ");
        String s = sc2.nextLine();
        String decryptedString = decrypt(s, privateKey);
        System.out.println("The text has been decrypted against the private key, and the result is: ");
        System.out.println(decryptedString);
    }

    private void encryptStringInTerminal(KeyPair publicKey) {
        System.out.print("Type String here: ");
        String s = sc2.nextLine();
        String encryptedString = encrypt(s, publicKey);
        System.out.println("Your text has been encrypted against the public key, and the result is: ");
        System.out.println(encryptedString);
    }

    private void decryptTxtFile(KeyPair privateKey) {
        System.out.println("Decrypting string from: " + encryptedStringFile.getAbsolutePath());

        String stringFromFile = "";

        try {
            read = new Scanner(encryptedStringFile);
            while (read.hasNextLine()) {
                stringFromFile = read.nextLine();
            }
            System.out.println("The decrypted result from the string in the .txt file is: " + decrypt(stringFromFile, privateKey));
            decrypt(stringFromFile, privateKey);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void encryptTxtFile(KeyPair publicKey) {
        Scanner sc1 = new Scanner(System.in);
        System.out.println("We create a text file here just to demonstrate in the program, text will be saved to: " + stringFile.getAbsolutePath());
        System.out.print("Text to save: ");
        String testString = sc1.nextLine();

        String stringFromFile = "";

        try {
            FileWriter fw = new FileWriter(stringFile);
            fw.write(testString);
            fw.close();

            read = new Scanner(stringFile);
            while (read.hasNextLine()) {
                stringFromFile = read.nextLine();
            }

            System.out.println("Encrypting text in: " + stringFile.getAbsolutePath());

            String encryptedStringFromFile = encrypt(stringFromFile, publicKey);
            System.out.println("The text in: " + stringFile.getAbsolutePath() + " is: " + stringFromFile + "\n" + " and has been encrypted to: " + encryptedStringFromFile);
            saveEncryptedStringToFile(encryptedStringFromFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveEncryptedStringToFile(String encryptedString) {
        try {
            FileWriter fw = new FileWriter(encryptedStringFile);
            fw.write(encryptedString);
            fw.close();
            System.out.println("Encrypted result has been saved to: " + encryptedStringFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateKeys(String fileName, int bitLength) {

        SecureRandom rand = new SecureRandom();

        BigInteger p = new BigInteger(bitLength / 2, 100, rand); //med största sannolik ett primtal
        BigInteger q = new BigInteger(bitLength / 2, 100, rand);
        BigInteger n = p.multiply(q);

        BigInteger phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        BigInteger e = new BigInteger("3");
        while (phiN.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }

        BigInteger d = e.modInverse(phiN);

        KeyPair publicKey = new KeyPair(e, n);
        KeyPair privateKey = new KeyPair(d, n);

        saveKey(fileName + "publicKey", publicKey);
        saveKey(fileName + "privateKey", privateKey);
    }

    private void saveKey(String fileName, KeyPair key) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(key);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private KeyPair readKey(String fileName) {
        KeyPair key = null;

        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            key = (KeyPair) in.readObject();
            in.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return key;
    }

    private String encrypt(String message, KeyPair key) {
        return (new BigInteger(message.getBytes(StandardCharsets.UTF_8))).modPow(key.getKey(), key.getN()).toString();
    }

    private String decrypt(String message, KeyPair key) {
        String msg = new String(message.getBytes(StandardCharsets.UTF_8));
        return new String((new BigInteger(msg)).modPow(key.getKey(), key.getN()).toByteArray());
    }

    public static void main(String[] args) {
        new EncryptionProgram();
    }
}
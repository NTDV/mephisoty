package ru.valkovets.mephisoty.security.credentials;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordManager {
public static final char[] backpack = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz#$_+=".toCharArray();
public static final SecureRandom secureRandom = new SecureRandom();
private static final Random random = new Random(secureRandom.nextLong());

public static String generateRandomPassword() {
    return generateRandomPassword(random.nextInt(10, 12 + 1));
}

private static String generateRandomPassword(final int len) {
    final char[] chars = new char[len];
    random.ints(len, 0, backpack.length).forEach(i -> chars[i] = backpack[i]);
    return new String(chars);
}
}
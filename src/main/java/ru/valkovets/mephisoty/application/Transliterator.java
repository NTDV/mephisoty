package ru.valkovets.mephisoty.application;

public class Transliterator {
private static final String[] enFromRu = new String[] {
    "A", "B", "V", "G", "D",
    "E", "Zh", "Z", "I",
    "Y", "K", "L", "M", "N",
    "O", "P", "R", "S", "T",
    "U", "F", "H", "C", "Ch",
    "Sh", "Shch", "", "Y", "",
    "E", "Yu", "Ya",

    "a", "b", "v", "g", "d",
    "e", "zh", "z", "i",
    "y", "k", "l", "m", "n",
    "o", "p", "r", "s", "t",
    "u", "f", "h", "c", "ch",
    "sh", "shch", "", "y", "",
    "e", "yu", "ya"
};

public static String translit(final String text) {
  final char[] charArray = text.toCharArray();
  final StringBuilder sb = new StringBuilder();

  for (final char c : charArray) {
    if (c >= 'А' && c <= 'я') {
      sb.append(enFromRu[c - 'А']);
    } else if (c == ' ') {
      sb.append('_');
    } else if (c >= 'A' && c <= 'Z' ||
               c >= 'a' && c <= 'z' ||
               c == '.' ||
               c == '_' || c == '-' ||
               c == '(' || c == ')' ||
               c >= '0' && c <= '9') {
      sb.append(c);
    } else if (c == 'ё') {
      sb.append("yo");
    } else if (c == 'Ё') {
      sb.append("Yo");
    }
  }

  return sb.toString();
}
}

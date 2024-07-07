package ru.valkovets.mephisoty.db.projection;

public record NamesProj(String firstName, String getSecondName, String getThirdName) {
public static NamesProj EMPTY = new NamesProj("Unknown user!", "", "");

public String getFullName() {
    return (firstName + " " + getSecondName + " " + getThirdName).trim();
}
}

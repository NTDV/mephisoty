package ru.valkovets.mephisoty.settings;

public enum AllowState {
    DISALLOW_ALL_FOR_PARTICIPANTS,
    ALLOW_READ_FOR_PARTICIPANTS,
    ALLOW_CREATE_READ_FOR_PARTICIPANTS,
    ALLOW_CREATE_EDIT_READ_FOR_PARTICIPANTS;

public static final AllowState YES = ALLOW_READ_FOR_PARTICIPANTS;
public static final AllowState NO = DISALLOW_ALL_FOR_PARTICIPANTS;

    public boolean equals(final String string) {
        return name().equals(string);
    }
}

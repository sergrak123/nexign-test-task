package ru.grak.nexigntask.enums;

public enum TypeCall {
    OUTGOING("01"),
    INCOMING("02");

    private final String numericValue;

    TypeCall(String numericValue) {
        this.numericValue = numericValue;
    }

    public String getNumericValueOfType() {
        return numericValue;
    }

    public static TypeCall fromNumericValueOfType(String numericValueOfType) {

        if (numericValueOfType.equals("01")) {
            return OUTGOING;
        }
        return INCOMING;
    }
}

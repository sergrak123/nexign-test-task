package ru.grak.nexigntask.dto;

import ru.grak.nexigntask.enums.TypeCall;

import java.util.Objects;

public class CallDataRecord {

    private final TypeCall typeCall;
    private final String msisdn;
    private final long dateTimeStartCall;
    private final long dateTimeEndCall;

    public CallDataRecord(TypeCall typeCall, String msisdn, long dateTimeStartCall, long dateTimeEndCall) {
        this.typeCall = typeCall;
        this.msisdn = msisdn;
        this.dateTimeStartCall = dateTimeStartCall;
        this.dateTimeEndCall = dateTimeEndCall;
    }

    public TypeCall getTypeCall() {
        return typeCall;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public long getDateTimeStartCall() {
        return dateTimeStartCall;
    }

    public long getDateTimeEndCall() {
        return dateTimeEndCall;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallDataRecord that = (CallDataRecord) o;
        return dateTimeStartCall == that.dateTimeStartCall && dateTimeEndCall == that.dateTimeEndCall && typeCall == that.typeCall && Objects.equals(msisdn, that.msisdn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeCall, msisdn, dateTimeStartCall, dateTimeEndCall);
    }

    @Override
    public String toString() {
        return "CallDataRecord{" +
                "typeCall=" + typeCall +
                ", msisdn='" + msisdn + '\'' +
                ", dateTimeStartCall=" + dateTimeStartCall +
                ", dateTimeEndCall=" + dateTimeEndCall +
                '}';
    }
}

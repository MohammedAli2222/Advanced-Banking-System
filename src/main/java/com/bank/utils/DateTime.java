package com.bank.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {
    private LocalDateTime dateTime;

    public DateTime() {
        this.dateTime = LocalDateTime.now();
    }

    public DateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String format(String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    @Override
    public String toString() {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
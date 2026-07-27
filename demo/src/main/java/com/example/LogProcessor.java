package com.example;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Demonstrates custom equals/hashCode and the difference between HashSet and LinkedHashSet.
 * UserLog equality is based ONLY on ipAddress (ignoring userAgent), so duplicate IPs are removed.
 * LinkedHashSet preserves insertion order.
 */
public class LogProcessor {

    /** A user log entry with IP address and user agent string. */
    public static class UserLog {
        private final String ipAddress;
        private final String userAgent;

        public UserLog(String ipAddress, String userAgent) {
            this.ipAddress = ipAddress;
            this.userAgent = userAgent;
        }

        public String getIpAddress() { return ipAddress; }
        public String getUserAgent() { return userAgent; }

        /**
         * Two UserLogs are equal if they have the SAME ipAddress (userAgent is ignored).
         * This means duplicate IPs will be treated as duplicates in a Set.
         */
        @Override
        public boolean equals(Object obj) {
           if (this == obj) return true;
           if (obj == null || obj.getClass() != this.getClass()) return false;
           UserLog userLog = (UserLog) obj;
           return userLog.ipAddress.equals(this.ipAddress);
        }

        /**
         * hashCode must be consistent with equals: based ONLY on ipAddress.
         * This ensures that equal objects have the same hash code.
         */
        @Override
        public int hashCode() {
            return Objects.hash(ipAddress);
        }

        @Override
        public String toString() {
            return String.format("UserLog{ip='%s', agent='%s'}", ipAddress, userAgent);
        }
    }

    public static void main(String[] args) {
        // LinkedHashSet preserves insertion order (unlike HashSet which is unordered)
        Set<UserLog> uniqueLogs = new LinkedHashSet<>();

        // Add logs in arrival order
        uniqueLogs.add(new UserLog("192.168.1.1", "Chrome/120"));
        uniqueLogs.add(new UserLog("10.0.0.5", "Safari/17"));
        uniqueLogs.add(new UserLog("192.168.1.1", "Chrome/120")); // Duplicate IP! (same ipAddress → treated as duplicate)
        uniqueLogs.add(new UserLog("172.16.0.2", "Firefox/121"));

        // Print uniqueLogs to verify:
        // 1. Duplicate "192.168.1.1" is removed.
        // 2. Order remains: 192.168.1.1 -> 10.0.0.5 -> 172.16.0.2
        for (UserLog log : uniqueLogs) {
            System.out.println(log);
        }
    }
}
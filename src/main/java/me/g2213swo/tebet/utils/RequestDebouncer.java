package me.g2213swo.tebet.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequestDebouncer {
    private static final int ALLOWED_INTERVAL_SECONDS = 3;

    private final Map<Long, LocalDateTime> userRequests = new HashMap<>();
    private final Set<Long> activeUsers = new HashSet<>();

    public boolean shouldAllowRequest(long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastRequestTime = userRequests.get(userId);

        if (lastRequestTime != null) {
            long secondsSinceLastRequest = ChronoUnit.SECONDS.between(lastRequestTime, now);
            if (secondsSinceLastRequest < ALLOWED_INTERVAL_SECONDS) {
                return false;
            }
        }

        if (!activeUsers.contains(userId)) {
            userRequests.put(userId, now);
            activeUsers.add(userId);
            return true;
        }

        return false;
    }

    public void onRequestFinished(long userId) {
        LocalDateTime now = LocalDateTime.now();
        userRequests.put(userId, now);
        activeUsers.remove(userId);
    }
}
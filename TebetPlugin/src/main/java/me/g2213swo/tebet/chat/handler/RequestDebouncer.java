package me.g2213swo.tebet.chat.handler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RequestDebouncer {
    private static final int ALLOWED_INTERVAL_SECONDS = 3;

    private final Map<UUID, LocalDateTime> userRequests = new HashMap<>();

    private final Set<UUID> users = new HashSet<>();

    public boolean shouldAllowRequest(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastRequestTime = userRequests.get(userId);

        if (lastRequestTime != null) {
            long secondsSinceLastRequest = ChronoUnit.SECONDS.between(lastRequestTime, now);
            if (secondsSinceLastRequest < ALLOWED_INTERVAL_SECONDS) {
                return false;
            }
        }

        if (!users.contains(userId)) {
            userRequests.put(userId, now);
            users.add(userId);
            return true;
        }

        return false;
    }

    public void onRequestFinished(UUID uuid) {
        LocalDateTime now = LocalDateTime.now();
        userRequests.put(uuid, now);
        users.remove(uuid);
    }

    public Set<UUID> getUsers() {
        return users;
    }
}
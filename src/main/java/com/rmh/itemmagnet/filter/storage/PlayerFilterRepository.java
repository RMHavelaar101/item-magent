package com.rmh.itemmagnet.filter.storage;

import java.util.List;
import java.util.UUID;

public interface PlayerFilterRepository {

    PlayerFilterBackend getBackend();

    void initialize();

    void shutdown();

    List<PlayerFilterRecord> loadAll();

    void saveRecord(PlayerFilterRecord record);

    void deleteRules(UUID uuid);

    boolean isEmpty();
}

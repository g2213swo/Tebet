package me.g2213swo.tebetapi.integration;

public interface ChatRequest {
    void setUser(String user);

    void setModel(String model);

    String getModel();
}

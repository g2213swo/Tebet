package me.g2213swo.tebetapi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class TebetAPIProvider {
    private static TebetAPI instance = null;

    /**
     * Provides static access to the {@link TebetAPI} API.
     *
     * <p>Ideally, the ServiceManager for the platform should be used to obtain an
     * instance, however, this provider can be used if this is not viable.</p>
     *
     * @return an instance of the GemsEconomy API
     * @throws IllegalStateException if the API is not loaded yet
     */
    public static @NotNull TebetAPI get() {
        TebetAPI instance = TebetAPIProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("Instance is not loaded yet.");
        }
        return instance;
    }

    @ApiStatus.Internal
    public static void register(TebetAPI instance) {
        TebetAPIProvider.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        TebetAPIProvider.instance = null;
    }

    @ApiStatus.Internal
    private TebetAPIProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }
}

package ch.epfl.chacun.logic;

/**
 * Represents the possible actions that can be sent by a game client.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Simon Lefort (sciper: 371918)
 */
public enum ServerAction {
    UNKNOWN,
    GAMEJOIN,
    GAMEJOIN_ACCEPT,
    GAMEJOIN_DENY,
    GAMELEAVE,
    GAMEACTION,
    GAMEACTION_ACCEPT,
    GAMEACTION_DENY,
    GAMEEND,
    GAMEMSG,
    GAMEMSG_DENY;

    /**
     * Convert a string to a ServerAction.
     *
     * @param action The string to convert.
     * @return The corresponding ServerAction.
     */
    public static ServerAction fromString(String action) {
        try {
            return ServerAction.valueOf(action);
        } catch (IllegalArgumentException _) {
            return ServerAction.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        return this.name();
    }
}

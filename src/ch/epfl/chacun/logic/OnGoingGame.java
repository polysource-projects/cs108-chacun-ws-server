package ch.epfl.chacun.logic;

import ch.epfl.chacun.game.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;

/**
 * Represents an ongoing game.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Simon Lefort (sciper: 371918)
 */
public class OnGoingGame {

    /**
     * The name of the game.
     */
    private final String gameName;

    /**
     * The players of the game.
     */
    private final Map<PlayerColor, String> players;

    /**
     * The seed used to shuffle the tiles.
     */
    private final int seed;

    /**
     * The state of the game.
     */
    private GameState gameState;

    /**
     * Create a new game lobby with the provided game name and players.
     *
     * @param gameName The name of the game.
     * @param players  The players of the game
     */
    public OnGoingGame(String gameName, Map<PlayerColor, String> players) {
        List<PlayerColor> playerColors = players.keySet().stream().sorted().toList();
        // Shuffle the tiles
        int seed = gameName.hashCode();
        RandomGenerator shuffler = RandomGeneratorFactory.getDefault().create(seed);
        List<Tile> tiles = new ArrayList<>(Tiles.TILES);
        Collections.shuffle(tiles, shuffler);
        // Group tiles by kind to create the decks
        TileDecks decks = new TileDecks(tiles.stream().collect(Collectors.groupingBy(Tile::kind)));
        // Initialize the game state
        this.gameState = GameState.initial(playerColors, decks, new TextMakerFr(players));
        // Store game data
        this.gameName = gameName;
        this.seed = seed;
        this.players = players;
        // Start the game
        gameState = gameState.withStartingTilePlaced();
    }

    /**
     * Apply the given action to the game state.
     *
     * @param action   the action to apply
     * @param username the username of the player who sent the action
     * @return the result of the action
     */
    public GameActionData applyAction(String action, String username) {
        // VAC NET will ban you if you're trying to cheat :/
        if (players.get(gameState.currentPlayer()).equals(username)) {
            ActionEncoder.StateAction stateAction = ActionEncoder.decodeAndApply(gameState, action);
            if (stateAction != null) {
                gameState = stateAction.gameState();
                // If the action was valid, broadcast it to all players
                return new GameActionData(ServerAction.GAMEACTION_ACCEPT, action, true);
            }
            return new GameActionData(ServerAction.GAMEACTION_DENY, "INVALID_ACTION");
        }
        return new GameActionData(ServerAction.GAMEACTION_DENY, "NOT_YOUR_TURN");
    }

    /**
     * Whether the game has ended.
     *
     * @return true if the game has ended, false otherwise
     */
    public boolean hasEnded() {
        return gameState.nextAction() == GameState.Action.END_GAME;
    }

    /**
     * Get the game state.
     *
     * @return the game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Get the name of the game.
     *
     * @return the name of the game
     */
    public String getName() {
        return gameName;
    }

    /**
     * Get the seed used to shuffle the tiles.
     *
     * @return the seed used to shuffle the tiles
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Get the players of the game.
     *
     * @return the players of the game
     */
    public Map<PlayerColor, String> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

}

package de.jvstvshd.localstream.common.title;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public enum TitleAction {

    /**
     * Starts the adding process.<br>
     * Possible response codes:<br>
     * - 0 if the title does not exist<br>
     * - 1 if the title exists.<br>
     */
    ADD_START(0),
    /**
     * Ends the adding process.<br>
     */
    ADD_END(1),
    /**
     * Adds a title.<br>
     * Possible response codes:<br>
     * - 0 for success<br>
     * - 1 for failure<br>
     */
    REMOVE(2),
    /**
     * Checks for the existence of a title.<br>
     * Possible response codes:<br>
     * - 0 if the title exists.<br>
     * - 1 if not.
     */
    CHECK(3),
    /**
     * Plays a title.<br>
     * Needed data: {@link UUID}.<br>
     * Possible response codes:<br>
     * - 0 if the title cannot be played<br>
     * - 1 if the title can be played
     */
    PLAY(4),
    /**
     * Default value to avoid null.<br>
     * Response code: -1.<br>
     * <b>SHOULD NOT BE USED IF IT IS NOT A REPLACEMENT FOR <code>null</code>!</b>
     */
    PAUSE(5),
    RESUME(6),
    STOP(7),
    ACQUIRE_DATA(8),
    NOTHING(-1);

    private final int action;

    TitleAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    /**
     * Retrieves a {@link TitleAction} as optional with the given <code>action</code>
     *
     * @param action action as {@link Integer}
     * @return an {@link Optional} of the action matching with <code>action</code> or {@link Optional#empty()} if nothing matches.
     * @see #getTitleAction(int)
     */
    public static Optional<TitleAction> getTitleActionOptional(int action) {
        return Arrays.stream(values()).filter(titleAction -> titleAction.getAction() == action).findFirst();
    }

    /**
     * Retrieves a {@link TitleAction} with the given <code>action</code>.
     *
     * @param action action as {@link Integer}
     * @return the matching action or {@link #NOTHING} if {@link Optional#isEmpty()} of {@link #getTitleActionOptional(int)} returned true.
     */
    public static TitleAction getTitleAction(int action) {
        return getTitleActionOptional(action).orElse(NOTHING);
    }
}

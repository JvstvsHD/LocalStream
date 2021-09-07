package de.jvstvshd.localstream.common.network.packets;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PacketPriority implements Comparable<PacketPriority> {


    private final static Set<PacketPriority> priorities = new HashSet<>();

    public static final PacketPriority LOWEST = create(0);
    public static final PacketPriority LOW = create(25);
    public static final PacketPriority NORMAL = create(50);
    public static final PacketPriority HIGH = create(75);
    public static final PacketPriority HIGHEST = create(100);

    /**
     *  Packets with this {@link PacketPriority} will fail at handling.
     *  Use this to stop packets from being executed.
     */
    private static final PacketPriority UNBELIEVABLE = create(Integer.MIN_VALUE);
    public static final PacketPriority FAIL = UNBELIEVABLE;

    /**
     * priority of this {@link PacketPriority}; 0 is the lowest, <code>maxPriority</code> the highest
     */
    private final int priority;
    /**
     * Maximum priority possible to create. If a {@link PacketPriority} with an higher priority is created, an error will be thrown.
     */
    private final static int maxPriority = 100;

    private boolean notUsable;

    private PacketPriority(int priority) {
        this.priority = priority;
        this.notUsable = false;
    }

    public PacketPriority(int priority, boolean notUsable) {
        this.priority = priority;
        this.notUsable = notUsable;
    }

    /**
     * Crates a new {@link PacketPriority}
     *
     * @param prio priority as int
     * @return an {@link Optional} of a new {@link PacketPriority}, {@link Optional#empty()} if a {@link PacketPriority} with this priority already exists.
     */
    private static Optional<PacketPriority> createPriority(int prio) {
        PacketPriority packetPriority = new PacketPriority(prio);
        if (!priorities.add(packetPriority))
            return Optional.empty();
        return Optional.of(packetPriority);
    }

    /**
     * Creates the priority and may throw errors.
     *
     * @param priority priority in int
     * @return a new {@link PacketPriority}. If these is illegal/half-null, the {@link PacketPriority} created is set to not usable, which means, that all further operations are illegal.
     * @throws IllegalArgumentException if <code>priority</code> is bigger than the <code>maxPriority</code>
     */
    private static PacketPriority create(int priority) {
        if (priority > maxPriority || priority < 0) {
            return new PacketPriority(-1, true);
        }
        PacketPriority pp;
        Optional<PacketPriority> opt = createPriority(priority);
        if (opt.isEmpty()) {
            pp = new PacketPriority(0);
            pp.setUsable(false);
        } else
            pp = opt.get();
        return pp;
    }

    public static Optional<PacketPriority> get(int priority) {
        return priorities.stream().filter(prio -> prio.getPriority() == priority).findFirst();
    }

    public int getPriority() {
        if (!isUsable())
            throw new IllegalArgumentException("Cannot retrieve data of unusable priority.");
        return priority;
    }

    public static int getMaxPriority() {
        return maxPriority;
    }

    public static Set<PacketPriority> getPriorities() {
        return new HashSet<>(getPriorities0());
    }

    private static Set<PacketPriority> getPriorities0() {
        return priorities;
    }

    @Override
    public int compareTo(PacketPriority o) {
        if (!isUsable())
            throw new IllegalArgumentException("Cannot retrieve data of unusable priority.");
        return Integer.compare(getPriority(), o.getPriority());
    }

    public Optional<PacketPriority> compare(PacketPriority packetPriority) {
        return packetPriority.getPriority() > getPriority() ? Optional.of(packetPriority) : packetPriority.getPriority() == getPriority() ? Optional.empty() : Optional.of(this);
    }

    public static Optional<PacketPriority> compare(PacketPriority o1, PacketPriority o2) {
        return o1.compare(o2);
    }

    protected boolean isUsable() {
        return !notUsable;
    }

    private void setUsable(boolean usable) {
        this.notUsable = !notUsable;
    }
}

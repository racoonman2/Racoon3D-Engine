package racoonman.r3d.util;

import java.util.Objects;

public interface FloatConsumer {
    void accept(float value);

    default FloatConsumer andThen(FloatConsumer after) {
        Objects.requireNonNull(after);
        return (f) -> { this.accept(f); after.accept(f); };
    }
}

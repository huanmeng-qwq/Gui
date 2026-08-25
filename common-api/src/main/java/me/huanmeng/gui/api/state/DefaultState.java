package me.huanmeng.gui.api.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Small thread-safe state implementation suitable for platform-independent values. */
public final class DefaultState<T> implements State<T> {
    private final List<StateListener<? super T>> listeners = new ArrayList<StateListener<? super T>>();
    private T value;

    public DefaultState(T value) {
        this.value = value;
    }

    @Override
    public synchronized T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        List<StateListener<? super T>> snapshot;
        T previous;
        synchronized (this) {
            if (Objects.equals(this.value, value)) {
                return;
            }
            previous = this.value;
            this.value = value;
            snapshot = new ArrayList<StateListener<? super T>>(listeners);
        }
        for (StateListener<? super T> listener : snapshot) {
            listener.onChange(previous, value);
        }
    }

    @Override
    public synchronized Subscription subscribe(final StateListener<? super T> listener) {
        Objects.requireNonNull(listener, "listener");
        listeners.add(listener);
        return new Subscription() {
            private boolean closed;

            @Override
            public synchronized void close() {
                if (!closed) {
                    closed = true;
                    synchronized (DefaultState.this) {
                        listeners.remove(listener);
                    }
                }
            }
        };
    }
}

package remote.listeners;

import remote.datatypes.Error;

public interface ErrorListener {
    public void call(Error e);
}

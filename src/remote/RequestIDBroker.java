package remote;

import exceptions.MissingIDException;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Package ID broker class handles active request and respons IDs.
 *
 * @author Henrik Nilsson
 */
public class RequestIDBroker {
    private static final int ID_RANGE_MAX = 0xFFFF;
    private static final int ID_RANGE_MIN = 0x0000;

    private Set<Integer> usedIDs;
    private Random random;

    public RequestIDBroker() {
        usedIDs = new HashSet<>();
        random = new Random();
    }

    public int getID() {
        while(true) {
            int prop = ID_RANGE_MIN + random.nextInt(ID_RANGE_MAX);

            if (!usedIDs.contains(prop)) {
                usedIDs.add(prop);
                return prop;
            }
        }
    }

    public void releaseID(int id) throws MissingIDException {
        if (!usedIDs.contains(id))
            throw new MissingIDException("Tried to release unused ID.");
        usedIDs.remove(id);
    }
}

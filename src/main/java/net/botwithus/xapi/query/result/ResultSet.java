package net.botwithus.xapi.query.result;

import net.botwithus.util.Rand;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ResultSet<T> implements Iterable<T> {
    private static final java.util.Random RANDOM = Rand.getSecureThreadLocalRandom();
    protected final List<T> results;

    /**
     * Constructs a ResultSet with the given results.
     *
     * @param results the list of results
     */
    public ResultSet(List<T> results) {
        this.results = results;
    }

    /**
     * Retrieves the first element in the result set.
     *
     * @return the first element, or null if the result set is empty
     */
    public T first() {
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    /**
     * Retrieves the last element in the result set.
     *
     * @return the last element, or null if the result set is empty
     */
    public T last() {
        if (results.isEmpty()) {
            return null;
        }
        return results.get(results.size() - 1);
    }

    /**
     * Retrieves a random element from the result set.
     *
     * @return a random element, or null if the result set is empty
     */
    public T random() {
        int size = results.size();
        if (size == 0) {
            return null;
        } else {
            // get a random element at the index between [0, size)
            return results.get(RANDOM.nextInt(size));
        }
    }

    /**
     * Returns an iterator over the elements in the result set.
     *
     * @return an Iterator over the elements in the result set
     */
    @Override
    public Iterator<T> iterator() {
        return results.iterator();
    }

    /**
     * Returns a sequential Stream with the elements in the result set.
     *
     * @return a Stream with the elements in the result set
     */
    public Stream<T> stream() {
        return results.stream();
    }

    /**
     * Returns the number of elements in the result set.
     *
     * @return the number of elements in the result set
     */
    public int size() {
        return results.size();
    }
}
package iat265.xha98;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MechanismIterator implements Iterator<Scrubbable> {
    private Wheel wheel;

    public MechanismIterator(Wheel wheel) {
        this.wheel = wheel;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Scrubbable next() {
        if (!hasNext()) throw new NoSuchElementException();
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }
}

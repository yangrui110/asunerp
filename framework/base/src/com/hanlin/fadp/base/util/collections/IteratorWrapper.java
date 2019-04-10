
package com.hanlin.fadp.base.util.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class IteratorWrapper<DEST, SRC> implements Iterator<DEST> {
    private final Iterator<? extends SRC> it;
    private boolean nextCalled;
    private DEST lastDest;
    private SRC lastSrc;

    protected IteratorWrapper(Iterator<? extends SRC> it) {
        this.it = it;
    }

    public boolean hasNext() {
        if (nextCalled) return true;
        if (!it.hasNext()) return false;
        do {
            lastSrc = it.next();
            DEST nextDest = convert(lastSrc);
            if (isValid(lastSrc, nextDest)) {
                nextCalled = true;
                lastDest = nextDest;
                return true;
            }
        } while (it.hasNext());
        return false;
    }

    public DEST next() {
        if (!nextCalled) {
            if (!hasNext()) throw new NoSuchElementException();
        }
        nextCalled = false;
        return lastDest;
    }

    public void remove() {
        if (lastSrc != null) {
            noteRemoval(lastDest, lastSrc);
            it.remove();
            lastDest = null;
            lastSrc = null;
        } else {
            throw new IllegalStateException();
        }
    }

    protected boolean isValid(SRC src, DEST dest) {
        return true;
    }

    protected abstract void noteRemoval(DEST dest, SRC src);
    protected abstract DEST convert(SRC src);
}


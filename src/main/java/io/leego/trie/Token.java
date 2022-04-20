package io.leego.trie;

import java.io.Serializable;

/**
 * @author Leego Yih
 */
public class Token implements Serializable {
    private static final long serialVersionUID = -7918430275428907853L;
    private final String fragment;
    private final Emit emit;

    public Token(String fragment, Emit emit) {
        this.fragment = fragment;
        this.emit = emit;
    }

    public String getFragment() {
        return this.fragment;
    }

    public Emit getEmit() {
        return emit;
    }

    public boolean isMatch() {
        return emit != null;
    }

    @Override
    public String toString() {
        if (emit == null) {
            return fragment;
        } else {
            return fragment + "(" + emit + ")";
        }
    }
}

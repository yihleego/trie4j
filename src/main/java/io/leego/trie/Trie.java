package io.leego.trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @author Leego Yih
 */
public class Trie implements Serializable {
    private static final long serialVersionUID = 7464998650081881647L;
    private final State root;

    public Trie() {
        this.root = new State(0);
    }

    public Trie(Set<String> keywords) {
        this.root = new State(0);
        this.load(keywords);
    }

    public Trie(InputStream src) {
        this.root = new State(0);
        this.load(src);
    }

    public Emits findAll(CharSequence text, boolean ignoreCase) {
        Emits emits = new Emits(text);
        State state = root;
        for (int i = 0, len = text.length(); i < len; i++) {
            state = nextState(state, text.charAt(i), ignoreCase);
            for (String keyword : state.getKeywords()) {
                emits.add(new Emit(i - keyword.length() + 1, i + 1, keyword));
            }
        }
        return emits;
    }

    public Emits findAll(CharSequence text) {
        return findAll(text, false);
    }

    public Emits findAllIgnoreCase(CharSequence text) {
        return findAll(text, true);
    }

    public Emit findFirst(CharSequence text, boolean ignoreCase) {
        State state = root;
        for (int i = 0, len = text.length(); i < len; i++) {
            state = nextState(state, text.charAt(i), ignoreCase);
            String keyword = state.getFirstKeyword();
            if (keyword != null) {
                return new Emit(i - keyword.length() + 1, i + 1, keyword);
            }
        }
        return null;
    }

    public Emit findFirst(CharSequence text) {
        return findFirst(text, false);
    }

    public Emit findFirstIgnoreCase(CharSequence text) {
        return findFirst(text, true);
    }

    private State nextState(State state, char c, boolean ignoreCase) {
        State next = state.nextState(c, ignoreCase);
        while (next == null) {
            state = state.getFailure();
            next = state.nextState(c, ignoreCase);
        }
        return next;
    }

    public Trie load(Set<String> keywords) {
        for (String keyword : keywords) {
            if (keyword != null && !keyword.isEmpty()) {
                root.addState(keyword).addKeyword(keyword);
            }
        }
        Queue<State> states = new LinkedList<>();
        root.getSuccess().forEach((ignored, state) -> {
            state.setFailure(root);
            states.add(state);
        });
        while (!states.isEmpty()) {
            State state = states.poll();
            state.getSuccess().forEach((c, next) -> {
                State f = state.getFailure();
                State fn = f.nextState(c);
                while (fn == null) {
                    f = f.getFailure();
                    fn = f.nextState(c);
                }
                next.setFailure(fn);
                next.addKeywords(fn.getKeywords());
                states.add(next);
            });
        }
        return this;
    }

    public Trie load(InputStream src) {
        Set<String> keywords = new HashSet<>();
        try (InputStreamReader inputStreamReader = new InputStreamReader(src);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                keywords.add(line);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return load(keywords);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Set<String> keywords = new HashSet<>();

        public Builder addKeyword(String keyword) {
            if (keyword != null) {
                this.keywords.add(keyword);
            }
            return this;
        }

        public Builder addKeywords(String... keywords) {
            if (keywords != null) {
                Collections.addAll(this.keywords, keywords);
            }
            return this;
        }

        public Builder addKeywords(Collection<String> keywords) {
            if (keywords != null) {
                this.keywords.addAll(keywords);
            }
            return this;
        }

        public Trie build() {
            return new Trie(keywords);
        }
    }
}

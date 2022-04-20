package io.leego.trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author Leego Yih
 */
public class Emits extends ArrayList<Emit> {
    private static final long serialVersionUID = -9117361135147927914L;
    private final CharSequence source;

    public Emits(CharSequence source) {
        this.source = source;
    }

    private Emits(Emits emits) {
        super(emits);
        this.source = emits.source;
    }

    public CharSequence getSource() {
        return source;
    }

    public List<Token> tokenize() {
        Emits emits = this.copy();
        emits.removeContains();
        String source = emits.getSource().toString();
        List<Token> tokens = new ArrayList<>(emits.size() * 2 + 1);
        if (emits.isEmpty()) {
            tokens.add(new Token(source, null));
            return tokens;
        }
        int index = 0;
        for (Emit emit : emits) {
            if (index < emit.getBegin()) {
                tokens.add(new Token(source.substring(index, emit.getBegin()), null));
            }
            tokens.add(new Token(source.substring(emit.getBegin(), emit.getEnd()), emit));
            index = emit.getEnd();
        }
        Emit last = emits.get(emits.size() - 1);
        if (last.getEnd() < source.length()) {
            tokens.add(new Token(source.substring(last.getEnd()), null));
        }
        return tokens;
    }

    public String replaceWith(String replacement) {
        Emits emits = this.copy();
        emits.removeContains();
        String source = emits.getSource().toString();
        if (emits.isEmpty()) {
            return source;
        }
        int index = 0;
        StringBuilder sb = new StringBuilder();
        for (Emit emit : this) {
            if (index < emit.getBegin()) {
                sb.append(source, index, emit.getBegin());
                index = emit.getBegin();
            }
            sb.append(mask(replacement, index, emit.getEnd()));
            index = emit.getEnd();
        }
        Emit last = emits.get(emits.size() - 1);
        if (last.getEnd() < source.length()) {
            sb.append(source, last.getEnd(), source.length());
        }
        return sb.toString();
    }

    public void removeOverlaps() {
        removeIf(Emit::overlaps);
    }

    public void removeContains() {
        removeIf(Emit::contains);
    }

    private void removeIf(BiPredicate<Emit, Emit> predicate) {
        if (this.size() <= 1) {
            return;
        }
        this.sort();
        Iterator<Emit> iterator = this.iterator();
        Emit emit = iterator.next();
        while (iterator.hasNext()) {
            Emit next = iterator.next();
            if (predicate.test(emit, next)) {
                iterator.remove();
            } else {
                emit = next;
            }
        }
    }

    private void sort() {
        this.sort((a, b) -> {
            if (a.getBegin() != b.getBegin()) {
                return Integer.compare(a.getBegin(), b.getBegin());
            } else {
                return Integer.compare(b.getEnd(), a.getEnd());
            }
        });
    }

    private String mask(String replacement, int begin, int end) {
        int count = end - begin;
        int len = replacement != null ? replacement.length() : 0;
        if (len == 0) {
            return repeat("*", count);
        } else if (len == 1) {
            return repeat(replacement, count);
        } else {
            char[] chars = new char[count];
            for (int i = 0; i < count; i++) {
                chars[i] = replacement.charAt((i + begin) % len);
            }
            return new String(chars);
        }
    }

    private String repeat(String s, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
        if (count == 1) {
            return s;
        }
        final int len = s.length();
        if (len == 0 || count == 0) {
            return "";
        }
        if (Integer.MAX_VALUE / count < len) {
            throw new OutOfMemoryError("Required length exceeds implementation limit");
        }
        if (len == 1) {
            final char[] single = new char[count];
            Arrays.fill(single, s.charAt(0));
            return new String(single);
        }
        final int limit = len * count;
        final char[] multiple = new char[limit];
        System.arraycopy(s.toCharArray(), 0, multiple, 0, len);
        int copied = len;
        for (; copied < limit - copied; copied <<= 1) {
            System.arraycopy(multiple, 0, multiple, copied, copied);
        }
        System.arraycopy(multiple, 0, multiple, copied, limit - copied);
        return new String(multiple);
    }

    private Emits copy() {
        return new Emits(this);
    }
}

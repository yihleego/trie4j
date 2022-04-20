package io.leego.trie;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Leego Yih
 */
public class Emit implements Serializable {
    private static final long serialVersionUID = -8879895979621579720L;
    /** The beginning index, inclusive. */
    private final int begin;
    /** The ending index, exclusive. */
    private final int end;
    private final String keyword;

    public Emit(int begin, int end, String keyword) {
        this.begin = begin;
        this.end = end;
        this.keyword = keyword;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getLength() {
        return end - begin;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean overlaps(Emit o) {
        return this.begin < o.end && this.end > o.begin;
    }

    public boolean contains(Emit o) {
        return this.begin <= o.begin && this.end >= o.end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Emit))
            return false;
        Emit that = (Emit) o;
        return this.begin == that.begin
                && this.end == that.end
                && Objects.equals(this.keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, end, keyword);
    }

    @Override
    public String toString() {
        return begin + ":" + end + "=" + keyword;
    }
}

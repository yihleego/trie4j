package io.leego.trie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Leego Yih
 */
public class TrieTests {
    static final Logger logger = Logger.getLogger(TrieTests.class.getName());

    @Test
    public void testFindAll() {
        String text = "昨夜雨疏风骤，浓睡不消残酒。试问卷帘人，却道海棠依旧。知否，知否？应是绿肥红瘦。";
        Trie trie = new Trie("雨疏", "风骤", "残酒", "卷帘人", "知否");
        Emits emits = trie.findAll(text, false);
        log(emits);
        equalsEmit(emits.get(0), 2, 4, "雨疏");
        equalsEmit(emits.get(1), 4, 6, "风骤");
        equalsEmit(emits.get(2), 11, 13, "残酒");
        equalsEmit(emits.get(3), 16, 19, "卷帘人");
        equalsEmit(emits.get(4), 27, 29, "知否");
        equalsEmit(emits.get(5), 30, 32, "知否");
        equalsSize(emits, 6);
    }

    @Test
    public void testFindFirst() {
        String text = "昨夜雨疏风骤，浓睡不消残酒。试问卷帘人，却道海棠依旧。知否，知否？应是绿肥红瘦。";
        Trie trie = new Trie("雨疏", "风骤", "残酒", "卷帘人", "知否");
        Emit emit = trie.findFirst(text, false);
        log(emit);
        equalsEmit(emit, 2, 4, "雨疏");
    }

    @Test
    public void testFindAllIgnoreCase() {
        String text = "Poetry is what gets lost in translation.";
        Trie trie = new Trie("poetry", "TRANSLATION");
        Emits emits = trie.findAll(text, true);
        log(emits);
        equalsEmit(emits.get(0), 0, 6, "poetry");
        equalsEmit(emits.get(1), 28, 39, "TRANSLATION");
        equalsSize(emits, 2);
    }

    @Test
    public void testFindFirstIgnoreCase() {
        String text = "Poetry is what gets lost in translation.";
        Trie trie = new Trie("poetry", "TRANSLATION");
        Emit emit = trie.findFirst(text, true);
        log(emit);
        equalsEmit(emit, 0, 6, "poetry");
    }

    @Test
    public void testIgnoreCase() {
        String text = "TurninG OnCe AgAiN BÖRKÜ";
        Trie trie = new Trie("turning", "once", "again", "börkü");
        Emits emits = trie.findAll(text, true);
        log(emits);
        equalsEmit(emits.get(0), 0, 7, "turning");
        equalsEmit(emits.get(1), 8, 12, "once");
        equalsEmit(emits.get(2), 13, 18, "again");
        equalsEmit(emits.get(3), 19, 24, "börkü");
        equalsSize(emits, 4);
    }

    @Test
    public void testTokenize() {
        String text = "常记溪亭日暮，沉醉不知归路。兴尽晚回舟，误入藕花深处。争渡，争渡，惊起一滩鸥鹭。";
        Trie trie = new Trie("溪亭", "归路", "藕花", "争渡");
        Emits emits = trie.findAll(text, false);
        List<Token> tokens = emits.tokenize();
        log("size: ", emits.size(), ", emits: ", emits);
        log("size: ", tokens.size(), ", tokens: ", tokens);
        equalsToken(tokens.get(0), -1, -1, "常记");
        equalsToken(tokens.get(1), 2, 4, "溪亭");
        equalsToken(tokens.get(2), -1, -1, "日暮，沉醉不知");
        equalsToken(tokens.get(3), 11, 13, "归路");
        equalsToken(tokens.get(4), -1, -1, "。兴尽晚回舟，误入");
        equalsToken(tokens.get(5), 22, 24, "藕花");
        equalsToken(tokens.get(6), -1, -1, "深处。");
        equalsToken(tokens.get(7), 27, 29, "争渡");
        equalsToken(tokens.get(8), -1, -1, "，");
        equalsToken(tokens.get(9), 30, 32, "争渡");
        equalsToken(tokens.get(10), -1, -1, "，惊起一滩鸥鹭。");
        equalsSize(emits, 5);
        equalsSize(tokens, 11);
    }

    @Test
    public void testReplace() {
        String text = "我正在参加砍价，砍到0元就可以免费拿啦。亲~帮我砍一刀呗，咱们一起免费领好货。";
        Trie trie = new Trie("0元", "砍一刀", "免费拿", "免费领");
        Emits emits = trie.findAll(text, false);
        String r1 = emits.replaceWith("*");
        String r2 = emits.replaceWith("@#$%^&*");
        log(emits, "\n", r1, "\n", r2);
        equalsString("我正在参加砍价，砍到**就可以***啦。亲~帮我***呗，咱们一起***好货。", r1);
        equalsString("我正在参加砍价，砍到%^就可以#$%啦。亲~帮我%^&呗，咱们一起&*@好货。", r2);
        equalsSize(emits, 4);
    }

    @Test
    public void testOverlaps() {
        String text = "a123,456b";
        Trie trie = new Trie("123", "12", "23", "45", "56");
        Emits emits = trie.findAll(text, false);
        log("before: ", emits);
        equalsSize(emits, 5);
        emits.removeOverlaps();
        log("after: ", emits);
        equalsEmit(emits.get(0), 1, 4, "123");
        equalsEmit(emits.get(1), 5, 7, "45");
        equalsSize(emits, 2);
    }

    @Test
    public void testContains() {
        String text = "a123,456b";
        Trie trie = new Trie("123", "12", "23", "45", "56");
        Emits emits = trie.findAll(text, false);
        log("before: ", emits);
        equalsSize(emits, 5);
        emits.removeContains();
        log("after: ", emits);
        equalsEmit(emits.get(0), 1, 4, "123");
        equalsEmit(emits.get(1), 5, 7, "45");
        equalsEmit(emits.get(2), 6, 8, "56");
        equalsSize(emits, 3);
    }

    @Test
    public void testDuplicate() {
        String text = "123456";
        Trie trie = new Trie("123", "123", "456", "456");
        Emits emits = trie.findAll(text, false);
        log(emits);
        equalsEmit(emits.get(0), 0, 3, "123");
        equalsEmit(emits.get(1), 3, 6, "456");
        equalsSize(emits, 2);
    }

    @Test
    public void testAddKeywords() {
        String text = "ushers";
        Trie trie1 = new Trie("he", "she", "his", "hers");
        Trie trie2 = new Trie().addKeywords("he", "she", "his", "hers");
        Trie trie3 = new Trie().addKeywords("he").addKeywords("she").addKeywords("his").addKeywords("hers");
        Emits emits1 = trie1.findAll(text, false);
        Emits emits2 = trie2.findAll(text, false);
        Emits emits3 = trie3.findAll(text, false);
        log(emits1);
        log(emits2);
        log(emits3);
        equalsEmits(emits1, emits2);
        equalsEmits(emits1, emits3);
        equalsEmits(emits2, emits3);
    }

    public void equalsEmit(Emit emit, int begin, int end, String kw) {
        Assertions.assertEquals(emit, new Emit(begin, end, kw));
    }

    public void equalsEmits(Emits emits1, Emits emits2) {
        if (emits1.size() != emits2.size()) {
            Assertions.fail("The emits are not equal");
            return;
        }
        for (int i = 0; i < emits1.size(); i++) {
            Emit emit1 = emits1.get(i);
            Emit emit2 = emits2.get(i);
            if (!emit1.equals(emit2)) {
                Assertions.fail("The emits are not equal");
                return;
            }
        }
    }

    public void equalsToken(Token token, int begin, int end, String kw) {
        if (!Objects.equals(token.getFragment(), kw)) {
            Assertions.fail("The token is not equal");
            return;
        }
        if (token.isMatch()) {
            equalsEmit(token.getEmit(), begin, end, kw);
        }
    }

    public void equalsString(String s1, String s2) {
        Assertions.assertEquals(s1, s2);
    }

    public void equalsSize(Emits emits, int size) {
        Assertions.assertEquals(emits.size(), size);
    }

    public void equalsSize(List<Token> tokens, int size) {
        Assertions.assertEquals(tokens.size(), size);
    }

    public void log(Object... objects) {
        if (objects == null || objects.length == 0) {
            return;
        } else if (objects.length == 1) {
            logger.info(objects[0].toString());
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Object object : objects) {
            sb.append(object);
        }
        logger.info(sb.toString());
    }
}

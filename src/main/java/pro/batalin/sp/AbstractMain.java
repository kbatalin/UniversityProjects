package pro.batalin.sp;

import java.io.IOException;

import pro.batalin.sp.model.Symbol;
import pro.batalin.sp.service.SLexer;
import pro.batalin.sp.service.SParser;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
public abstract class AbstractMain {

    protected Symbol getSymbol(final String treeFile) throws IOException {
        try (SParser parser = new SParser(new SLexer(HtmlGeneratorMain.class.getResourceAsStream(treeFile)), false)) {
            return parser.parse();
        }
    }
}

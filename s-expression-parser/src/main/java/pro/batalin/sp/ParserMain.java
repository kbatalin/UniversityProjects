package pro.batalin.sp;

import java.io.IOException;

import pro.batalin.sp.model.Symbol;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
public class ParserMain extends AbstractMain {
    public static void main(String[] args) throws IOException {
        new ParserMain().run();
    }

    /**
     * Парсинг дерева и вывод его в терминал.
     * На входе - файл с деревом.
     */
    public void run() throws IOException {
        final String treeFile = "s_tree"; // input

        final Symbol symbol = getSymbol(treeFile);
        System.out.println(symbol);
    }
}

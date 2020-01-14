package pro.batalin.sp;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import pro.batalin.sp.model.Symbol;
import pro.batalin.sp.service.HtmlGenerator;

/**
 * Запуск генератора html.
 *
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
public class HtmlGeneratorMain extends AbstractMain {

    private final HtmlGenerator htmlGenerator = new HtmlGenerator();

    public static void main(String[] args) throws IOException {
        new HtmlGeneratorMain().run();
    }

    /**
     * Сгенерировать html для s-дерева.
     * На вход даем дерево и пути нод, которые нужно вывести.
     * На выход создается html.
     */
    public void run() throws IOException {
        final String treeFile = "s_tree"; // input
        final String resultFile = "s_tree.html"; // output

        final List<String> sPaths = List.of(
                "/id",
                "/responses/messages",
                "//speech",
                "/webhookForSlotFilling"
        );

        final Symbol symbol = getSymbol(treeFile);
        writeToHTML(resultFile, sPaths, symbol);
    }

    private void writeToHTML(final String resultFile, final List<String> sPaths, final Symbol symbol) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        htmlGenerator.generate(output, symbol, sPaths);
        try (final FileOutputStream os = new FileOutputStream(resultFile)) {
            os.write(output.toByteArray());
        }
    }
}

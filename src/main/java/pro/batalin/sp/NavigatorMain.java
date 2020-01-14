package pro.batalin.sp;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import pro.batalin.sp.model.Symbol;
import pro.batalin.sp.service.SNavigator;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
public class NavigatorMain extends AbstractMain {

    private SNavigator navigator = new SNavigator();

    public static void main(String[] args) throws IOException {
        new NavigatorMain().run();
    }

    /**
     * Аналог xPath.
     * На вход путь до дерева и список xpath.
     * В терминал будет выведен результат.
     */
    public void run() throws IOException {
        final String treeFile = "s_tree"; // input

        final List<String> sPaths = List.of(
                "/id",
                "/responses/messages",
                "//speech",
                "/webhookForSlotFilling"
        );

        final Symbol symbol = getSymbol(treeFile);

        sPaths.stream()
                .map(e -> String.format("sPath: %s, Node: %s", e, toString(navigator.select(symbol, e))))
                .forEach(System.out::println);
    }

    private String toString(final List<Symbol> nodes) {
        return nodes.stream().map(Symbol::toString).collect(Collectors.joining("\n"));
    }
}

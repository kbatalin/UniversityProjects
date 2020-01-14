package pro.batalin.sp;

import java.io.IOException;
import java.io.InputStream;

import pro.batalin.sp.service.SValidator;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
public class ValidatorMain extends AbstractMain {

    private SValidator validator = new SValidator();

    public static void main(String[] args) throws IOException {
        new ValidatorMain().run();
    }

    /**
     * Валидация данных по схеме.
     * На вход файл со схемой и деревом.
     * Результат проверки будет выведен в терминал.
     * OK - все норм.
     * !OK - есть ошибка.
     */
    public void run() throws IOException {
        final String treeFile = "s_tree"; // input
        final String schemaFile = "s_tree_schema"; // schema

        validate(treeFile, schemaFile);
    }

    private void validate(final String treeFile, final String schemaFile) {
        try (final InputStream treeInput = getClass().getResourceAsStream(treeFile);
             final InputStream schemaInput = getClass().getResourceAsStream(schemaFile)) {
            validator.validate(schemaInput, treeInput);
            System.out.println("OK");
        } catch (Exception e) {
            System.out.println("!OK");
        }
    }
}

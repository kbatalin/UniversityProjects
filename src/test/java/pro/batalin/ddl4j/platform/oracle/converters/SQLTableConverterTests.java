package pro.batalin.ddl4j.platform.oracle.converters;

import org.junit.Assert;
import org.junit.Test;
import pro.batalin.ddl4j.model.Column;
import pro.batalin.ddl4j.model.SQLConvertible;
import pro.batalin.ddl4j.model.Table;
import pro.batalin.ddl4j.platforms.oracle.converters.SQLConverter;
import pro.batalin.ddl4j.platforms.oracle.converters.SQLConverterFactory;
import pro.batalin.ddl4j.platforms.oracle.converters.table.SQLColumnConverter;
import pro.batalin.ddl4j.platforms.oracle.converters.table.SQLCreateTableConverter;
import pro.batalin.ddl4j.platforms.statement_generator.StatementGenerator;

import java.sql.JDBCType;

/**
 * Created by Kirill Batalin (kir55rus) on 08.05.17.
 */
public class SQLTableConverterTests {

    @Test
    public void factoryTest() throws Exception {
        SQLConvertible table = new Table();

        SQLConverterFactory factory = new SQLConverterFactory();
        SQLConverter converter = factory.create(table);

        Assert.assertEquals("SQLConverter type", SQLCreateTableConverter.class, converter.getClass());
    }

    @Test
    public void columnTest() throws Exception {
        Column column = new Column();
        column.setName("testName");
        column.setType(JDBCType.INTEGER);

        SQLConverter sqlConverter = new SQLColumnConverter(column);
        String test = StatementGenerator.generate(sqlConverter);
        Assert.assertEquals("column name + type", "testName INTEGER", test.trim());

        column.setSize(2);
        sqlConverter = new SQLColumnConverter(column);
        test = StatementGenerator.generate(sqlConverter);
        Assert.assertEquals("column name + type", "testName INTEGER(2)", test.trim());

        column.setDefaultValue("100");
        sqlConverter = new SQLColumnConverter(column);
        test = StatementGenerator.generate(sqlConverter);
        Assert.assertEquals("column name + type", "testName INTEGER(2) DEFAULT 100", test.trim());

        column.setSize(null);
        sqlConverter = new SQLColumnConverter(column);
        test = StatementGenerator.generate(sqlConverter);
        Assert.assertEquals("column name + type", "testName INTEGER DEFAULT 100", test.trim());
    }

    @Test
    public void createTableTest() throws Exception {

    }
}

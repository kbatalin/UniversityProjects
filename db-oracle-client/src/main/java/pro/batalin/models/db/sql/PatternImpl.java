package pro.batalin.models.db.sql;

import pro.batalin.ddl4j.model.Schema;

/**
 * @author Kirill Batalin (kir55rus)
 */
public abstract class PatternImpl implements Pattern {
    private Schema schema;
    private String table;

    public PatternImpl() {
    }

    public PatternImpl(Schema schema, String table) {
        this.schema = schema;
        this.table = table;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}

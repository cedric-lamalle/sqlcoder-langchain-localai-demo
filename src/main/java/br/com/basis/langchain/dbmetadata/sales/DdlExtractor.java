package br.com.basis.langchain.dbmetadata.sales;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DdlExtractor {
    private static final Logger logger = LoggerFactory.getLogger(DdlExtractor.class);
    private static final String TEMPLATE_CREATE_TABLE = "CREATE TABLE %s (\n";
    private static final String TEMPLATE_COMMENT_FK = "-- %s.%s can be joined with %s.%s";
    public static final String[] TABLE_TYPES = {"TABLE"};
    public static final String BOOLEAN_NO = "NO";
    private final DataSource dataSource;

    public DdlExtractor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Cacheable("salesDdl")
    public String getSalesDdl() {
        logger.debug("Generating DDL");
        try {
            DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            String ddl = getCreateTableScripts(metaData);
            logger.debug("Generated DDL:\n{}", ddl);
            return ddl;
        } catch (SQLException e) {
            throw new SalesException("Sorry, something went generating DDL...", e);
        }
    }

    private void addForeignKeys(String tableName, StringBuilder builder, DatabaseMetaData metaData) throws SQLException {
        ResultSet importedKeys = metaData.getImportedKeys(null, null, tableName);
        while (importedKeys.next()) {
            builder.append(String.format(TEMPLATE_COMMENT_FK,
                            importedKeys.getString("FKTABLE_NAME"),
                            importedKeys.getString("FKCOLUMN_NAME"),
                            importedKeys.getString("PKTABLE_NAME"),
                            importedKeys.getString("PKCOLUMN_NAME")
                    ))
                    .append("\n");
        }
    }

    private String getCreateTableScripts(DatabaseMetaData metaData) throws SQLException {
        ResultSet tables = metaData.getTables(null, null, "%", TABLE_TYPES);
        StringBuilder builderCreateTable = new StringBuilder();
        StringBuilder builderFks = new StringBuilder();
        while(tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            addCreateTable(tableName, builderCreateTable, metaData);
            addForeignKeys(tableName, builderFks, metaData);
        }
        builderCreateTable.append("\n").append(builderFks);
        return builderCreateTable.toString();
    }

    private void addCreateTable(String tableName, StringBuilder builder, DatabaseMetaData metaData) throws SQLException {
        builder.append(String.format(TEMPLATE_CREATE_TABLE, tableName));
        ResultSet columns = metaData.getColumns(null, null, tableName, "%");
        List<ColumnDefinition> columnDefinitions = new ArrayList<>();
        while(columns.next()) {
            columnDefinitions.add(getColumnDefinition(columns,
                    getPrimaryKey(metaData.getPrimaryKeys(null, null, tableName))));
        }
        addColumnDefinitions(columnDefinitions, builder);
        builder.append(");\n\n");
    }

    private void addColumnDefinitions(List<ColumnDefinition> columnDefinitions, StringBuilder builder) {
        int size = columnDefinitions.size();
        for(int i = 0; i < size; i++) {
            ColumnDefinition columnDefinition = columnDefinitions.get(i);
            builder.append("\t").append(columnDefinition.typeDefinition());
            if(i != size -1) {
                builder.append(",");
            }
            if (columnDefinition.remarks() != null) {
                builder.append(" -- ").append(columnDefinition.remarks());
            }
            builder.append("\n");
        }
    }

    //TODO deal with composite keys...
    private Optional<String> getPrimaryKey(ResultSet primaryKeys) throws SQLException {
        String keyName = null;
        if(primaryKeys.next()) {
            keyName = primaryKeys.getString("COLUMN_NAME");
        }
        return Optional.ofNullable(keyName);
    }

    // TODO deal with other types
    private ColumnDefinition getColumnDefinition(ResultSet column, Optional<String> primaryKey) throws SQLException {
        String columnName = column.getString("COLUMN_NAME");
        StringBuilder builder = new StringBuilder(columnName);
        int dataType = column.getInt("DATA_TYPE");
        // Fix for SQLCoder that doesn't recognize NUMERIC, only DECIMAL
        if (dataType == Types.NUMERIC) {
            dataType = Types.DECIMAL;
        }
        builder.append(" ")
                .append(JDBCType.valueOf(dataType).getName());
        if(dataType == Types.VARCHAR) {
            builder.append("(").append(column.getInt("COLUMN_SIZE")).append(")");
        } else if (typeHasPrecision(dataType)) {
            builder.append("(").append(column.getInt("COLUMN_SIZE")).append(",")
                    .append(column.getInt("DECIMAL_DIGITS")).append(")");
        }
        if(columnName.equals(primaryKey.orElse(""))) {
            builder.append(" PRIMARY KEY");
        } else if (BOOLEAN_NO.equals(column.getString("IS_NULLABLE"))){
            builder.append(" NOT NULL");
        }
        return new ColumnDefinition(builder.toString(), column.getString("REMARKS"));
    }

    private static boolean typeHasPrecision(int dataType) {
        return dataType == Types.NUMERIC || dataType == Types.DECIMAL;
    }

    public record ColumnDefinition (String typeDefinition, String remarks) { }
}

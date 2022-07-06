package de.unibi.agbi.biodwh2.procedures;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ResultSet implements Iterable<ResultRow> {
    private final String[] columns;
    private final List<ResultRow> rows;

    public ResultSet(final List<String> columns) {
        this.columns = columns.toArray(new String[0]);
        rows = new LinkedList<>();
    }

    public ResultSet(final String... columns) {
        this.columns = Arrays.copyOf(columns, columns.length);
        rows = new LinkedList<>();
    }

    public static ResultSet empty() {
        return new ResultSet();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public String getColumn(final int index) {
        return columns[index];
    }

    public String[] getColumns() {
        return Arrays.copyOf(columns, columns.length);
    }

    public int getRowCount() {
        return rows.size();
    }

    public void addRow(final ResultRow row) {
        rows.add(row);
    }

    public ResultRow getRow(final int index) {
        return rows.get(index);
    }

    @Override
    public Iterator<ResultRow> iterator() {
        return rows.iterator();
    }
}

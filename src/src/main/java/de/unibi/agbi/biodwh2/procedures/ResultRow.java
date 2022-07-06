package de.unibi.agbi.biodwh2.procedures;

import java.util.Arrays;

public final class ResultRow {
    private final String[] columns;
    private final Object[] values;

    public ResultRow(final String[] columns, final Object[] values) {
        this.columns = Arrays.copyOf(columns, columns.length);
        this.values = Arrays.copyOf(values, values.length);
    }

    public String getColumn(final int index) {
        return columns[index];
    }

    public String[] getColumns() {
        return Arrays.copyOf(columns, columns.length);
    }

    public int getValueCount() {
        return values.length;
    }

    public Object getValue(final int index) {
        return values[index];
    }

    public Object getValue(final String key) {
        for (int i = 0; i < columns.length; i++)
            if (columns[i].equals(key))
                return values[i];
        return null;
    }

    public ResultRow filter(final String... columns) {
        final Object[] values = new Object[columns.length];
        for (int i = 0; i < columns.length; i++)
            values[i] = getValue(columns[i]);
        return new ResultRow(columns, values);
    }

    public ResultRow filter(final String[] columns, final String[] renamedColumns) {
        final Object[] values = new Object[columns.length];
        for (int i = 0; i < columns.length; i++)
            values[i] = getValue(columns[i]);
        return new ResultRow(renamedColumns, values);
    }
}

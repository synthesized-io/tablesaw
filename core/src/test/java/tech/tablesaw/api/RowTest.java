package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.api.ColumnType.BOOLEAN;
import static tech.tablesaw.api.ColumnType.DOUBLE;
import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.INTEGER;
import static tech.tablesaw.api.ColumnType.LOCAL_DATE;
import static tech.tablesaw.api.ColumnType.LOCAL_TIME;
import static tech.tablesaw.api.ColumnType.LONG;
import static tech.tablesaw.api.ColumnType.SHORT;
import static tech.tablesaw.api.ColumnType.STRING;
import static tech.tablesaw.api.ColumnType.TEXT;

import com.google.common.collect.Streams;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.Sort;
import tech.tablesaw.sorting.Sort.Order;
import tech.tablesaw.table.TableSlice;

/** TODO All the methods on this class should be tested carefully */
public class RowTest {

  private static Table bush;
  private static Table baseball;
  private static Table tornado;
  private static final ColumnType[] BASEBALL_COLUMN_TYPES = {
    STRING, STRING, INTEGER, INTEGER, INTEGER,
    INTEGER, DOUBLE, DOUBLE, DOUBLE, BOOLEAN,
    INTEGER, INTEGER, INTEGER, DOUBLE, DOUBLE
  };

  private static final ColumnType[] BUSH_COLUMN_TYPES = {LOCAL_DATE, INTEGER, STRING};
  private static final ColumnType[] TORNADO_COLUMN_TYPES = {
    LOCAL_DATE, LOCAL_TIME, STRING, STRING, SHORT, SHORT, SHORT, DOUBLE, DOUBLE, DOUBLE, DOUBLE
  };

  @BeforeAll
  static void readTables() {
    bush = readBush(BUSH_COLUMN_TYPES);
    tornado = readTornado(TORNADO_COLUMN_TYPES);
    baseball = readBaseball(BASEBALL_COLUMN_TYPES);
  }

  static Table readBush(ColumnType[] columnTypes) {
    return Table.read()
        .csv(CsvReadOptions.builder(new File("../data/bush.csv")).columnTypes(columnTypes));
  }

  static Table readBaseball(ColumnType[] columnTypes) {
    return Table.read()
        .csv(CsvReadOptions.builder(new File("../data/baseball.csv")).columnTypes(columnTypes));
  }

  static Table readTornado(ColumnType[] columnTypes) {
    return Table.read()
        .csv(
            CsvReadOptions.builder(new File("../data/rev_tornadoes_1950-2014.csv"))
                .columnTypes(columnTypes));
  }

  @Test
  public void columnNames() throws IOException {
    Row row = new Row(bush);
    assertEquals(bush.columnNames(), row.columnNames());
  }

  @Test
  public void testColumnCount() {
    Row row = new Row(bush);
    assertEquals(bush.columnCount(), row.columnCount());
  }

  @Test
  public void testGetBoolean() {

    Table table = baseball;

    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      assertEquals(table.booleanColumn(9).get(row.getRowNumber()), row.getBoolean(9));
      assertEquals(
          table.booleanColumn("Playoffs").get(row.getRowNumber()), row.getBoolean("Playoffs"));
    }
  }

  @Test
  public void testGetDate() {
    Row row = new Row(bush);
    while (row.hasNext()) {
      row.next();
      LocalDate date = bush.dateColumn("date").get(row.getRowNumber());
      assertEquals(date, row.getDate(0));
      assertEquals(date, row.getDate("date"));
    }
  }

  @Test
  public void testGetDate2() {
    Row row = new Row(bush);
    while (row.hasNext()) {
      row.next();
      assertEquals(bush.dateColumn("date").get(row.getRowNumber()), row.getDate("DATE"));
    }
  }

  @Test
  public void testGetDateTime() {
    Table table = tornado.copy();
    DateTimeColumn dateTimeCol = table.dateColumn("Date").atTime(table.timeColumn("Time"));
    dateTimeCol.setName("DateTime");
    table.addColumns(dateTimeCol);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      LocalDateTime dttm = dateTimeCol.get(row.getRowNumber());
      assertEquals(dttm, row.getDateTime(11));
      assertEquals(dttm, row.getDateTime("DateTime"));
    }
  }

  @Test
  public void testGetDouble() {
    Table table = baseball;
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      assertEquals(table.doubleColumn(6).getDouble(row.getRowNumber()), row.getDouble(6), 0.01);
      assertEquals(
          table.doubleColumn("OBP").getDouble(row.getRowNumber()), row.getDouble("OBP"), 0.01);
    }
  }

  @Test
  public void testGetFloat() {
    ColumnType[] types = {
      STRING, STRING, INTEGER, INTEGER, INTEGER,
      INTEGER, FLOAT, FLOAT, FLOAT, INTEGER,
      INTEGER, INTEGER, INTEGER, FLOAT, FLOAT
    };

    Table table = readBaseball(types);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      assertEquals(table.floatColumn(6).getFloat(row.getRowNumber()), row.getFloat(6), 0.01);
      assertEquals(
          table.floatColumn("OBP").getFloat(row.getRowNumber()), row.getFloat("OBP"), 0.01);
    }
  }

  @Test
  public void testGetLong() {
    ColumnType[] bushColumnTypes = {LOCAL_DATE, LONG, STRING};
    Table table = readBush(bushColumnTypes);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      assertEquals(table.longColumn(1).getLong(row.getRowNumber()), row.getLong(1));
      assertEquals(
          table.longColumn("approval").getLong(row.getRowNumber()), row.getLong("approval"));
    }
  }

  @Test
  public void testGetObject() {
    Table table = readBush(BUSH_COLUMN_TYPES);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      assertEquals(table.dateColumn(0).get(row.getRowNumber()), row.getObject(0));
      assertEquals(table.dateColumn("date").get(row.getRowNumber()), row.getObject("date"));
    }
  }

  @Test
  public void testGetPackedDate() {
    Row row = new Row(bush);
    while (row.hasNext()) {
      row.next();
      assertEquals(bush.dateColumn(0).getIntInternal(row.getRowNumber()), row.getPackedDate(0));
      assertEquals(
          bush.dateColumn("date").getIntInternal(row.getRowNumber()), row.getPackedDate("date"));
    }
  }

  @Test
  public void testGetPackedDateTime() {
    Table table = tornado.copy();
    DateTimeColumn dateTimeCol = table.dateColumn("Date").atTime(table.timeColumn("Time"));
    dateTimeCol.setName("DateTime");
    table.addColumns(dateTimeCol);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      assertEquals(
          table.dateTimeColumn(11).getLongInternal(row.getRowNumber()), row.getPackedDateTime(11));
      assertEquals(
          table.dateTimeColumn("DateTime").getLongInternal(row.getRowNumber()),
          row.getPackedDateTime("DateTime"));
    }
  }

  @Test
  public void testGetPackedTime() {
    Table table = tornado.copy();
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      assertEquals(table.timeColumn(1).getIntInternal(row.getRowNumber()), row.getPackedTime(1));
      assertEquals(
          table.timeColumn("Time").getIntInternal(row.getRowNumber()), row.getPackedTime("Time"));
    }
  }

  @Test
  public void testGetShort() {
    ColumnType[] types = {LOCAL_DATE, SHORT, STRING};
    Table table =
        Table.read().csv(CsvReadOptions.builder(new File("../data/bush.csv")).columnTypes(types));
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      assertEquals(table.shortColumn(1).getShort(row.getRowNumber()), row.getShort(1));
      assertEquals(
          table.shortColumn("approval").getShort(row.getRowNumber()), row.getShort("approval"));
    }
  }

  @Test
  public void testGetString() {
    ColumnType[] types = {LOCAL_DATE, SHORT, STRING};
    Table table =
        Table.read().csv(CsvReadOptions.builder(new File("../data/bush.csv")).columnTypes(types));
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      assertEquals(table.stringColumn(2).get(row.getRowNumber()), row.getString(2));
      assertEquals(table.stringColumn("who").get(row.getRowNumber()), row.getString("who"));
    }
  }

  @Test
  public void testGetText() {
    ColumnType[] types = {LOCAL_DATE, SHORT, TEXT};
    Table table =
        Table.read().csv(CsvReadOptions.builder(new File("../data/bush.csv")).columnTypes(types));
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      assertEquals(table.textColumn(2).get(row.getRowNumber()), row.getText(2));
      assertEquals(table.textColumn("who").get(row.getRowNumber()), row.getText("who"));
    }
  }

  @Test
  public void testGetTime() {
    Table table = tornado.copy();
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      LocalTime time = table.timeColumn("Time").get(row.getRowNumber());
      assertEquals(time, row.getTime(1));
      assertEquals(time, row.getTime("Time"));
    }
  }

  @Test
  public void testSetBoolean() {
    Table table = baseball.copy();
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      Boolean rowVal = table.booleanColumn("Playoffs").get(row.getRowNumber());
      row.setBoolean("Playoffs", !rowVal);
      assertEquals(!rowVal, row.getBoolean(9));
      row.setBoolean("Playoffs", rowVal);
      assertEquals(rowVal, row.getBoolean("Playoffs"));
    }
  }

  @Test
  public void testSetDate() {
    Table table = readBush(BUSH_COLUMN_TYPES);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      LocalDate date = table.dateColumn("date").get(row.getRowNumber());

      // test setDate(index, value)
      LocalDate dateIncrementedByOne = date.plusDays(1);
      row.setDate(0, dateIncrementedByOne);
      assertEquals(dateIncrementedByOne, row.getDate(0));

      // test setDate(key, value)
      LocalDate dateIncrementedByTwo = date.plusDays(2);
      row.setDate("date", dateIncrementedByTwo);
      assertEquals(dateIncrementedByTwo, row.getDate("date"));
    }
  }

  @Test
  public void testSetDateTime() {
    Table table = tornado.copy();
    DateTimeColumn dateTimeCol = table.dateColumn("Date").atTime(table.timeColumn("Time"));
    dateTimeCol.setName("DateTime");
    table.addColumns(dateTimeCol);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();

      LocalDateTime dttm_less5 = dateTimeCol.get(row.getRowNumber()).minusHours(5);
      row.setDateTime(11, dttm_less5);
      assertEquals(dttm_less5, row.getDateTime(11));

      LocalDateTime dttm_add5 = dateTimeCol.get(row.getRowNumber()).plusHours(5);
      row.setDateTime("DateTime", dttm_add5);
      assertEquals(dttm_add5, row.getDateTime("DateTime"));
    }
  }

  @Test
  public void testSetDouble() {
    Table table = baseball.copy();
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();

      double rowVal = table.doubleColumn("OBP").getDouble(row.getRowNumber());

      // setDouble(columnIndex, value)
      row.setDouble(6, rowVal + Math.PI);
      assertEquals(rowVal + Math.PI, row.getDouble(6), 0.001);

      // setDouble(columnName, value)
      row.setDouble("OBP", rowVal + 2 * Math.PI);
      assertEquals(rowVal + 2 * Math.PI, row.getDouble("OBP"), 0.001);
    }
  }

  @Test
  public void testSetFloat() {

    ColumnType[] types = {
      STRING, STRING, INTEGER, INTEGER, INTEGER,
      INTEGER, FLOAT, FLOAT, FLOAT, INTEGER,
      INTEGER, INTEGER, INTEGER, FLOAT, FLOAT
    };
    Table table = readBaseball(types);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();

      float rowVal = table.floatColumn("OBP").getFloat(row.getRowNumber());

      // setFloat(columnIndex, value)
      row.setFloat(6, rowVal + (float) Math.PI);
      assertEquals(rowVal + (float) Math.PI, row.getFloat(6), 0.001);

      // setFloat(columnName, value)
      row.setFloat("OBP", rowVal + 2 * (float) Math.PI);
      assertEquals(rowVal + 2 * (float) Math.PI, row.getFloat("OBP"), 0.001);
    }
  }

  @Test
  public void testSetInt() {

    Table table = baseball.copy();
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();

      int rowVal = table.intColumn("RS").getInt(row.getRowNumber());

      // setDouble(columnIndex, value)
      row.setInt(3, rowVal + 1);
      assertEquals(rowVal + 1, row.getInt(3));

      // setDouble(columnName, value)
      row.setInt("RS", rowVal + 2);
      assertEquals(rowVal + 2, row.getInt("RS"));
    }
  }

  @Test
  public void testSetLong() {
    ColumnType[] types = {LOCAL_DATE, LONG, STRING};
    Table table = readBush(types);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      long rowVal = table.longColumn(1).getLong(row.getRowNumber());

      // setLong(columnIndex, value)
      row.setLong(1, rowVal + 1);
      assertEquals(rowVal + 1, row.getLong(1));

      // setLong(columnName, value)
      row.setLong("approval", rowVal + 2);
      assertEquals(rowVal + 2, row.getLong("approval"));
    }
  }

  @Test
  public void testSetShort() {
    ColumnType[] types = {LOCAL_DATE, SHORT, STRING};
    Table table = readBush(types);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      short rowVal = table.shortColumn(1).getShort(row.getRowNumber());

      // setShort(columnIndex, value)
      row.setShort(1, (short) (rowVal + 1));
      assertEquals((short) (rowVal + 1), row.getShort(1));

      // setShort(columnName, value)
      row.setShort("approval", (short) (rowVal + 2));
      assertEquals(rowVal + 2, row.getShort("approval"));
    }
  }

  @Test
  public void testSetString() {
    Table table = readBush(BUSH_COLUMN_TYPES);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      String rowVal = table.stringColumn(2).get(row.getRowNumber());
      String updateVal1 = rowVal.concat("2");
      String updateVal2 = rowVal.concat("3");

      // setString(columnIndex, value)
      row.setString(2, updateVal1);
      assertEquals(updateVal1, row.getString(2));

      // setString(columnName, value)
      row.setString("who", updateVal2);
      assertEquals(updateVal2, row.getString("who"));
    }
  }

  @Test
  public void testSetText() {
    ColumnType[] bushColumnTypes = {LOCAL_DATE, INTEGER, TEXT};
    Table table = readBush(bushColumnTypes);
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();
      String rowVal = table.textColumn(2).get(row.getRowNumber());
      String updateVal1 = rowVal.concat("2");
      String updateVal2 = rowVal.concat("3");

      // setText(columnIndex, value)
      row.setText(2, updateVal1);
      assertEquals(updateVal1, row.getText(2));

      // setText(columnName, value)
      row.setText("who", updateVal2);
      assertEquals(updateVal2, row.getText("who"));
    }
  }

  @Test
  public void testSetTime() {
    Table table = tornado.copy();
    Row row = new Row(table);
    while (row.hasNext()) {
      row.next();

      LocalTime dttm_less5 = table.timeColumn("Time").get(row.getRowNumber()).minusHours(5);
      row.setTime(1, dttm_less5);
      assertEquals(dttm_less5, row.getTime(1));

      LocalTime dttm_add5 = table.timeColumn("Time").get(row.getRowNumber()).plusHours(5);
      row.setTime("Time", dttm_add5);
      assertEquals(dttm_add5, row.getTime("Time"));
    }
  }

  @Test
  public void iterationWithSelection() {
    int[] sourceIndex = new int[] {10, 20, 30};
    Row row = new Row(new TableSlice(bush, Selection.with(10, 20, 30)), -1);

    int count = 0;
    while (row.hasNext()) {
      row.next();
      LocalDate date = bush.dateColumn("date").get(sourceIndex[row.getRowNumber()]);
      assertEquals(date, row.getDate(0));
      assertEquals(date, row.getDate("date"));
      count++;
    }
    assertEquals(3, count);
  }

  @Test
  public void setWithSelectionSortOrder() {
    Table table = bush.copy();
    int[] sourceIndex = new int[] {3, 6};
    Row row = new Row(new TableSlice(table, Selection.with(3, 6)));

    while (row.hasNext()) {
      row.next();
      Integer rowVal = table.intColumn(1).get(sourceIndex[row.getRowNumber()]);
      row.setInt(1, rowVal + 1);
      assertEquals(rowVal + 1, table.get(sourceIndex[row.getRowNumber()], 1));
    }
  }

  @Test
  public void iterationWithSelectionAndOrder() {
    TableSlice tableSlice = new TableSlice(bush, Selection.withRange(0, 5));
    tableSlice.sortOn(Sort.on("approval", Order.ASCEND));

    Row row = new Row(tableSlice);
    Integer[] expected = new Integer[] {52, 52, 53, 53, 58};
    Integer[] actual = Streams.stream(row).map(r -> r.getInt("approval")).toArray(Integer[]::new);

    assertArrayEquals(expected, actual);
  }

  @Test
  public void columnDoesNotExistOnRow() {
    Table table = Table.create("myTable", IntColumn.create("col1", new int[] {1}));

    Throwable thrown =
        assertThrows(IllegalStateException.class, () -> table.forEach(r -> r.getInt("col2")));

    assertEquals("Column col2 is not present in table myTable", thrown.getMessage());
  }

  @Test
  public void columnExistsButWrongType() {
    Table table = Table.create("myTale", DateColumn.create("col1", new LocalDate[] {null}));

    Throwable thrown =
        assertThrows(
            IllegalArgumentException.class, () -> table.forEach(r -> r.setTime("col1", null)));

    assertEquals(
        "Column col1 is of type LOCAL_DATE and cannot be cast to LOCAL_TIME."
            + " Use the method for LOCAL_DATE.",
        thrown.getMessage());
  }

  @Test
  void testGetNumber() {
    ColumnType[] bushColumnTypes = {LOCAL_DATE, DOUBLE, STRING};
    Table table = readBush(bushColumnTypes);
    assertEquals(53.0, table.row(0).getNumber("approval"));
  }

  @Test
  void testIsMissing() {
    assertFalse(bush.row(0).isMissing("approval"));
    bush.row(0).setMissing("approval");
    assertTrue(bush.row(0).isMissing("approval"));
  }
}

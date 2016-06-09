package com.deathrayresearch.outlier.integration;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.api.ColumnType;
import com.deathrayresearch.outlier.io.CsvReader;
import com.deathrayresearch.outlier.io.CsvWriter;
import org.junit.Test;

import static com.deathrayresearch.outlier.api.QueryHelper.column;
import static com.deathrayresearch.outlier.api.ColumnType.*;

/**
 * Some example code using the API
 */
public class ExamplesTest  {

  @Test
  public void simpleExample() throws Exception {

    out("");
    out("Some Examples: ");

    // Read the CSV file
    ColumnType[] types = {INTEGER, CATEGORY, CATEGORY, FLOAT, FLOAT};
    Table table = CsvReader.read(types, "data/bus_stop_test.csv");

    // Look at the column names
    out(table.columnNames());

    // Peak at the data
    out(table.head(5).print());

    // Remove the description column
    table.removeColumns("stop_desc");

    // Check the column names to see that it's gone
    out(table.columnNames());

    // Take a look at some data
    out("In 'examples. Printing first(5)");
    out(table.head(5).print());

    // Lets take a look at the latitude and longitude columns
    // out(table.realColumn("stop_lat").rowSummary().out());
    out(table.floatColumn("stop_lat").describe());

    // Now lets fill a column based on data in the existing columns

    // Apply the map function and fill the resulting column to the original table

    // Lets filter out some of the rows. We're only interested in records with IDs between 524-624

    Table filtered = table.selectIf(column("stop_id").isBetween(524, 624));
    out(filtered.head(5).print());

    // Write out the new CSV file
    CsvWriter.write("data/filtered_bus_stops.csv", filtered);
  }

  private void out(Object o) {
    System.out.println(o);
  }
}

package com.deathrayresearch.outlier.integration;

import com.deathrayresearch.outlier.api.ColumnType;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.CsvReader;
import com.deathrayresearch.outlier.store.StorageManager;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.deathrayresearch.outlier.api.ColumnType.*;

/**
 *
 */
public class AirlineDelays {

  private static Table flights2008;

  public static void main(String[] args) throws Exception {

    new AirlineDelays();

    Stopwatch stopwatch = Stopwatch.createStarted();
    flights2008.sortAscendingOn("Origin", "UniqueCarrier");
    System.out.println("Sorting " + stopwatch.elapsed(TimeUnit.SECONDS));
  }

  private AirlineDelays() throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();
    System.out.println("loading");
    flights2008 = CsvReader.read(reduced_set, "bigdata/2015.csv");
    System.out.println(String.format("loaded %d records in %d seconds",
        flights2008.rowCount(),
        stopwatch.elapsed(TimeUnit.SECONDS)));
    out(flights2008.shape());
    out(flights2008.columnNames().toString());
    flights2008.head(10).print();
    StorageManager.saveTable("bigdata", flights2008);
    stopwatch.reset().start();
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  // The full set of all available columns in tbe dataset
  static ColumnType[] heading = {
      INTEGER, // year
      INTEGER, // month
      INTEGER, // day
      CATEGORY,  // dow
      LOCAL_TIME, // DepTime
      LOCAL_TIME, // CRSDepTime
      LOCAL_TIME, // ArrTime
      LOCAL_TIME, // CRSArrTime
      CATEGORY, // Carrier
      CATEGORY, // FlightNum
      CATEGORY, // TailNum
      INTEGER, // ActualElapsedTime
      INTEGER, // CRSElapsedTime
      INTEGER, // AirTime
      INTEGER, // ArrDelay
      INTEGER, // DepDelay
      CATEGORY, // Origin
      CATEGORY, // Dest
      INTEGER, // Distance
      INTEGER, // TaxiIn
      INTEGER, // TaxiOut
      BOOLEAN, // Cancelled
      CATEGORY, // CancellationCode
      BOOLEAN, // Diverted
      FLOAT, // CarrierDelay
      FLOAT, // WeatherDelay
      FLOAT, // NASDelay
      FLOAT, // SecurityDelay
      FLOAT  // LateAircraftDelay
  };

  // A filtered set of columns
  private static ColumnType[] reduced_set = {
      SKIP, // year
      INTEGER, // month
      INTEGER, // day
      CATEGORY,  // dow
      SKIP, // DepTime
      LOCAL_TIME, // CRSDepTime
      SKIP, // ArrTime
      SKIP, // CRSArrTime
      CATEGORY, // Carrier
      SKIP, // FlightNum
      SKIP, // TailNum
      SKIP, // ActualElapsedTime
      SKIP, // CRSElapsedTime
      SKIP, // AirTime
      SKIP, // ArrDelay
      INTEGER, // DepDelay
      CATEGORY, // Origin
      CATEGORY, // Dest
      INTEGER, // Distance
      SKIP, // TaxiIn
      SKIP, // TaxiOut
      BOOLEAN, // Cancelled
      SKIP, // CancellationCode
      SKIP, // Diverted
      SKIP, // CarrierDelay
      SKIP, // WeatherDelay
      SKIP, // NASDelay
      SKIP, // SecurityDelay
      SKIP  // LateAircraftDelay
  };
}

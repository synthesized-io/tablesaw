package com.github.lwhite1.tablesaw.aggregator;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.FloatColumn;
import com.github.lwhite1.tablesaw.columns.IntColumn;
import org.apache.commons.math3.stat.StatUtils;

/**
 * Contains common utilities for double and long types
 */
public interface NumReduceUtils extends Column {


  NumericReduceFunction mean = new NumericReduceFunction() {

    @Override
    public String functionName() {
      return "Mean";
    }

    @Override
    public double reduce(Column data) {
      if (data.type() == ColumnType.FLOAT) {
        return StatUtils.mean(((FloatColumn) data).toDoubleArray());
      } else if (data.type() == ColumnType.INTEGER) {
        return StatUtils.mean(((IntColumn) data).toDoubleArray());
      }
      throw new IllegalArgumentException("Attempted to reduce numeric function to non-numeric column");
    }
  };



  // TODO(lwhite): Reimplement these methods to work natively with float[], instead of converting to double[]

  default double range() {
    double[] array = this.toDoubleArray();
    return StatUtils.max(array) - StatUtils.min(array);
  }

  default double product() {
    return StatUtils.product(toDoubleArray());
  }

  default double geometricMean() {
    return StatUtils.geometricMean(toDoubleArray());
  }

  default double sumOfSquares() {
    return StatUtils.sumSq(toDoubleArray());
  }

  default double[] normalize() {
    return StatUtils.normalize(toDoubleArray());
  }

  default double sumOfLogs() {
    return StatUtils.sumLog(toDoubleArray());
  }

  default double percentile(double percentile) {
    return StatUtils.percentile(toDoubleArray(), percentile);
  }

  default float quartile1() {
    return (float) StatUtils.percentile(toDoubleArray(), 25.0);
  }

  default float median() {
    return (float) StatUtils.percentile(toDoubleArray(), 50.0);
  }

  default float quartile3() {
    return (float) StatUtils.percentile(toDoubleArray(), 75.0);
  }

  default double percentile99() {
    return StatUtils.percentile(toDoubleArray(), 99.0);
  }

  default double variance() {
    return StatUtils.variance(toDoubleArray());
  }

  default double stdDev() {
    return Math.sqrt(StatUtils.variance(toDoubleArray()));
  }

  default double meanDifference(FloatColumn column2) {
    return StatUtils.meanDifference(toDoubleArray(), column2.toDoubleArray());
  }

  default double sumDifference(FloatColumn column2) {
    return StatUtils.sumDifference(toDoubleArray(), column2.toDoubleArray());
  }

  double[] toDoubleArray();
}

package checks

import checks.DataQualityChecks.{duplicatesCheck, nullCheck, schemaValidationCheck}
import exceptions.Exceptions.{DuplicateValuesExistException, NullValuesExistException}
import helper.Helper.{FINAL_INCORRECT_SCHEMA, FINAL_TABLE_SCHEMA, SCHEMA_TEST_PATH}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.scalatest.flatspec.AnyFlatSpec

import java.sql.{Date, Timestamp}

class DataQualityTest extends AnyFlatSpec {
  implicit val spark: SparkSession = helper.Helper.createSparkSession()

  "nullCheck " should " remove records having more than 60% of null values" in {

    val sampleDF = Seq(
      Row("B741", 30503, Timestamp.valueOf("2020-11-15 15:27:00"), "android", "B000078", "I7099", "facebook", true, true, 192.2, "B003", "Furniture", 4, "LARVEL", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00")),
      Row("D8142", 30542, Timestamp.valueOf("2020-01-20 15:00:00"), "unknown", "unknown", "unknown", "unknown", false, true, null, null, null, null, null, Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00")),
      Row("H1406", 13931, Timestamp.valueOf("2020-11-15 09:07:00"), "android", "B000092", "C2146", "facebook", false, false, 1292.5, "C182", "Apps & Games", 3, "MOJO", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00")),
      Row("G6601", 13931, Timestamp.valueOf("2020-11-15 19:07:00"), "android", "unknown", "C2146", "facebook", false, false, 92.5, "I116", "Clothing & Accessories", 1, "KOROL", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00"))
    )
    val inputDF: DataFrame = spark.createDataFrame(spark.sparkContext.parallelize(sampleDF), FINAL_TABLE_SCHEMA)

    assertThrows[NullValuesExistException](nullCheck(inputDF, constants.ApplicationConstants.FINAL_TABLE_COL))
  }

//test cases for data quality check - duplicate removal
"duplicatesCheck method()" should "remove duplicate records from joined dataframe" in {

  val sampleDF = Seq(
    Row("B741", 30503, Timestamp.valueOf("2020-11-15 15:27:00"), "android", "B000078", "I7099", "facebook", true, true, 192.2, "B003", "Furniture", 4, "LARVEL", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00")),
    Row("B741", 30503, Timestamp.valueOf("2020-11-15 15:00:00"), "android", "B000078", "I7099", "facebook", true, true, 192.2, "B003", "Furniture", 4, "LARVEL", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00")),
    Row("H1406", 13931, Timestamp.valueOf("2020-11-15 09:07:00"), "android", "B000092", "C2146", "facebook", false, false, 1292.5, "C182", "Apps & Games", 3, "MOJO", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00")),
    Row("G6601", 13932, Timestamp.valueOf("2020-11-15 19:07:00"), "android", "A000091", "A2141", "google", false, false, 92.5, "I116", "Clothing & Accessories", 1, "KOROL", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00"))
  )
  val inputDF: DataFrame = spark.createDataFrame(spark.sparkContext.parallelize(sampleDF), FINAL_TABLE_SCHEMA)

  assertThrows[DuplicateValuesExistException](duplicatesCheck(inputDF, constants.ApplicationConstants.CLICK_STREAM_PRIMARY_KEYS, constants.ApplicationConstants.TIME_STAMP_COL))
}

  "schemaValidationCheck method()" should "return a dataframe with correct schema" in {

    val sampleDF = Seq(
      Row("B741", 30503, Timestamp.valueOf("2020-11-15 15:27:00"), "android", "B000078", "I7099", "facebook", true, true, 192.2, "B003", "Furniture", 4, "LARVEL", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00")),
      Row("B742", 30502, Timestamp.valueOf("2020-11-15 15:02:00"), "android", "B000079", "I7059", "pinterest", true, true, 190.2, "C003", "Baby", 4, "MOJO", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:29:00")),
      Row("H1406", 13931, Timestamp.valueOf("2020-11-15 09:07:00"), "android", "B000092", "C2146", "facebook", false, false, 1292.5, "C182", "Apps & Games", 3, "MOJO", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00")),
      Row("G6601", 13932, Timestamp.valueOf("2020-11-15 19:07:00"), "android", "A000091", "A2141", "google", false, false, 92.5, "I116", "Clothing & Accessories", 1, "KOROL", Date.valueOf("2020-11-15"), Timestamp.valueOf("2020-11-15 15:27:00"))
    )
    val inputDF: DataFrame = spark.createDataFrame(spark.sparkContext.parallelize(sampleDF), FINAL_INCORRECT_SCHEMA)

    val outputDF = schemaValidationCheck(inputDF, SCHEMA_TEST_PATH)

    assertResult(outputDF.schema)(FINAL_TABLE_SCHEMA)

  }
}
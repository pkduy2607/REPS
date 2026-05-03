package com.rockthejvm

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object AnalyzeData {
  // mathematical functions to analyze data
  def calculateMean(values: List[Double]): Double =
    if(values.isEmpty) 0.0 else values.sum / values.size

  def calculateMedian(values: List[Double]): Double = {
    if (values.isEmpty) return 0.0
    val sorted = values.sorted
    val size = sorted.size
    if (size % 2 == 1) sorted(size / 2)
    else (sorted(size / 2 - 1) + sorted(size / 2)) / 2.0
  }

  def calculateMode(values: List[Double]): Double = {
    if (values.isEmpty) 0.0
    else {
      val frequencyMap = values.groupBy(identity).map {
        case (key, list) => (key, list.size)
      }
      val maxFreq = frequencyMap.values.max
      val modes = frequencyMap.filter(_._2 == maxFreq).keys
      modes.min
    }
  }

  def calculateRange(values: List[Double]): Double =
    if (values.isEmpty) 0.0 else values.max - values.min

  def calculateMidrange(values: List[Double]): Double =
    if (values.isEmpty) 0.0 else (values.max + values.min) / 2.0

  // functions to filter by time
  def isWithinWindow(recordTime: String, unit: ChronoUnit, amount: Long): Boolean = {
    Try {
      val now = ZonedDateTime.now()
      val time = ZonedDateTime.parse(recordTime)
      val diff = unit.between(time, now)
      diff >= 0 && diff < amount
    }.getOrElse(false)
  }

  def displayResults(values: List[Double], count: Int): Unit = {
    println(s"\n=== Analyze results ($count records) ===")
    println(f"- Mean: ${calculateMean(values)}%10.2f MW")
    println(f"- Median: ${calculateMedian(values)}%10.2f MW")
    println(f"- Mode: ${calculateMode(values)}%10.2f MW")
    println(f"- Range: ${calculateRange(values)}%10.2f MW")
    println(f"- Midrange: ${calculateMidrange(values)}%10.2f MW")
    println("==========================================")
  }

  // main flow to analyze data
  def analyzeData(): Unit = {
    ViewData.loadRecords("data.txt") match {
      case Success(records) =>
        println("\n--- ANALYZE DATA ---")
        // filter data by type
        print(
          """Select type of data to analyze:
            |1) All 2) Wind 3) Solar 4) Hydro
            |Enter your choice: """.stripMargin)
        val choice = Try(StdIn.readLine().trim.toInt).getOrElse(-1)
        val filteredRecords = choice match {
          case 1 => records
          case 2 => records.filter(_.datasetId == 245)
          case 3 => records.filter(_.datasetId == 248)
          case 4 => records.filter(_.datasetId == 191)
          case _ => Nil
        }

        // filter data by time
        print(
          """Select time window:
            |1) Last 1 hour 2) Last 1 day 3) Last 1 week 4) Last 1 month
            |Enter your choice: """.stripMargin)
        val timeChoice = Try(StdIn.readLine().trim.toInt).getOrElse(-1)
        val finalFiltered = timeChoice match {
          case 1 => filteredRecords.filter(r => isWithinWindow(r.startTime, ChronoUnit.HOURS, 1))
          case 2 => filteredRecords.filter(r => isWithinWindow(r.startTime, ChronoUnit.DAYS, 1))
          case 3 => filteredRecords.filter(r => isWithinWindow(r.startTime, ChronoUnit.WEEKS, 1))
          case 4 => filteredRecords.filter(r => isWithinWindow(r.startTime, ChronoUnit.MONTHS, 1))
          case _ => Nil
        }

        // analyze data
        if(finalFiltered.nonEmpty) {
          val values = finalFiltered.map(_.value)
          displayResults(values, finalFiltered.size)
        } else {
          println("Cannot find data in this time window to analyze!")
        }

      case Failure(e) => println(s"Error reading file: ${e.getMessage}")
    }
  }
}

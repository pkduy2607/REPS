package com.rockthejvm

import scala.util.{Success, Failure}

object IssueDetector {
  def checkIssues(name: String, current: Double, mean: Double, time: String): Unit = {
    val threshold = mean * 0.23

    if(current == 0 && name != "Solar") { // check equipment malfunction
      println(f"ALERT $time%-25s!!! Malfunction: $name current output is 0.0!")
    } else if(current < threshold && current > 0) {
      println(f"ALERT $time%-25s! Low Output: $name is $current%.2f (Avg: $mean%.2f) MW")
    } else {
      println(f"OK $time%-25s! $name is operating normally.")
    }
  }

  def detectAndAlert(): Unit = {
    // read data from file
    println("\n--- SYSTEM HEALTH CHECK ---")
    ViewData.loadRecords("data.txt") match {
      case Success(records) =>
        val grouped = records.groupBy(_.datasetId)
        grouped.foreach { case(id, records) =>
          val values = records.map(_.value)
          val latestRecord = records.maxBy(_.startTime)
          val mean = AnalyzeData.calculateMean(values)

          val sourceName = id match {
            case 245 => "Wind"
            case 248 => "Solar"
            case 191 => "Hydro"
            case _ => "Unknown"
          }

          checkIssues(sourceName, latestRecord.value, mean, latestRecord.startTime)
        }

      case Failure(e) => println(s"Cannot read file for detection: ${e.getMessage}")
    }
  }
}

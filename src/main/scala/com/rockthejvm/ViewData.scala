package com.rockthejvm

import scala.io.StdIn
import scala.util.{Try, Success, Failure}
import scala.io.Source

case class FingridRecord(
  datasetId: Int,
  startTime: String,
  endTime: String,
  value: Double
                        )

object ViewData {
  // use regex to deal with 4 cases in json format in data.txt
  val recordRegex = """\"datasetId\":(\d+),\"startTime\":\"([^\"]+)\",\"endTime\":\"([^\"]+)\",\"value\":([\d.]+)""".r

  //step 1: read data from file
  def loadRecords(filename: String): Try[List[FingridRecord]] = Try {
    val source = Source.fromFile(filename)
    try {
      source.getLines().flatMap { line =>
        recordRegex.findAllMatchIn(line).map { m =>
          FingridRecord(m.group(1).toInt, m.group(2), m.group(3), m.group(4).toDouble)
        }
      }.toList
        .distinctBy(r => (r.datasetId, r.startTime, r.endTime)) // remove all duplication data before shows it to user
    } finally {
      source.close()
    }
  }

  def displayData(records: List[FingridRecord]): Unit = {
    if(records.isEmpty) {
      println("Cannot find any data or invalid option.")
    } else {
      println(f"${"ID"}%-5s | ${"Start Time"}%-25s | ${"End Time"}%-25s | ${"Value (MW)"}%-10s")
      println("-" * 80)

      records.foreach { r =>
        println(f"${r.datasetId}%-5d | ${r.startTime}%-25s | ${r.endTime}%-25s | ${r.value}%-10.2f")
      }

      println("-" * 80)
    }
  }

  def viewData(): Unit = {
    loadRecords("data.txt") match {
      case Success(records) => {
        println("\n--- VIEW DATA ---")
        // request data type from user
        print(
          """Select type of data to view:
            |1) All 2) Wind 3) Solar 4) Hydro
            |Enter your choice: """.stripMargin)
        val choice = Try(StdIn.readLine().trim.toInt).getOrElse(-1)
        // filter the records based on the user choice
        val filteredRecords = choice match {
          case 1 => records
          case 2 => records.filter(_.datasetId == 245) // 245 = wind
          case 3 => records.filter(_.datasetId == 248) // 248 = solar
          case 4 => records.filter(_.datasetId == 191) // 191 = hydro
          case _ => List.empty
        }

        // sort the data by start time
        val sortedRecords = filteredRecords.sortBy(_.startTime)
        //display the data that has been sorted
        displayData(sortedRecords)
      }

      case Failure(e) =>
        println(s"Error: Cannot read the data from file: ${e.getMessage}")
    }
  }
}

package com.rockthejvm

import scala.util.{Try, Success, Failure}
import java.net.{HttpURLConnection, URL}
import scala.io.Source
import java.io.{FileWriter, BufferedWriter, PrintWriter}
import scala.io.StdIn

object CollectData {
  val baseUrl = "https://data.fingrid.fi/api/datasets"
  val apiKey = "" // change your API key here

  // fetch the data from Fingrid
  def fetchFingridData(datasetId: Int): Try[String] = Try {
    val url = s"$baseUrl/$datasetId/data/?format=json"
    val connection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestProperty("x-api-key", apiKey)
    connection.setRequestMethod("GET")
    connection.setConnectTimeout(5000)

    val responseCode = connection.getResponseCode
    // case 200: OK: read data normally
    if(responseCode == HttpURLConnection.HTTP_OK) {
      val inputStream = connection.getInputStream
      try {
        Source.fromInputStream(inputStream).mkString
      } finally {
        inputStream.close()
      }
    } else {
      // case error: read error notification from ErrorStream
      val errorStream = connection.getErrorStream
      val errorMessage = if(errorStream != null) {
        try {
          Source.fromInputStream(errorStream).mkString
        } finally {
          errorStream.close()
        }
      } else "No error message provided by server."
      throw new RuntimeException(s"HTTP $responseCode: $errorMessage")
    }

  }

  // write data into file
  def writeToFile(filename: String, data: String): Try[Unit] = Try {
    val fileWriter = new FileWriter(filename, true)
    val bufferedWriter = new BufferedWriter(fileWriter)
    val writer = new PrintWriter(bufferedWriter)

    try {
      writer.println(data)
    } finally {
      writer.close()
    }
  }

  // main flow follow the CollectData.puml
  def collectData(): Unit = {
    print("""Select type of data to retrieve:
1) Wind 2) Solar 3) Hydro
Enter your choice: """.stripMargin)
    val choice = Try(StdIn.readLine().trim.toInt).getOrElse(-1)

    val datasetId: Option[Int] = choice match {
      case 1 => Some(245) // wind
      case 2 => Some(248) // solar
      case 3 => Some(191) // hydro
      case _ => None  // wrong input
    }

    datasetId match {
      case Some(id) =>
        val result = for {
          data <- fetchFingridData(id)
          _ <- writeToFile("data.txt", data)
        } yield "Successfully saved data!"

        result match {
          case Success(msg) => println(msg)
          case Failure(e) => println(s"Failed to save data: ${e.getMessage}")
        }

      case None =>
        println("Invalid option!")
    }
  }
}
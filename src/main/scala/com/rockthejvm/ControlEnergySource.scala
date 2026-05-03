package com.rockthejvm

import scala.util.{Try, Success, Failure}
import scala.io.Source
import java.io.PrintWriter

case class SourceStatus(sourceType: String, status: String)

object ControlEnergySource {
  val filename = "status.json"

  private val statusRegex = """\"type\":\s*\"([^\"]+)\",\s*\"status\":\s*\"([^\"]+)\"""".r
  // load status from status.json
  def loadStatus(): Try[List[SourceStatus]] = Try {
    val source = Source.fromFile(filename)
    try {
      val content = source.mkString
      statusRegex.findAllMatchIn(content).map { m =>
        SourceStatus(m.group(1), m.group(2))
      }.toList
    } finally {
      source.close()
    }
  }
  //save new source status to status.json
  def saveStatus(l: List[SourceStatus]): Try[Unit] = Try {
    val out = new PrintWriter(filename)
    try {
      out.println("[")
      val jsonEntries = l.map { s =>
        s""" { "type": "${s.sourceType}", "status": "${s.status}" }"""
      }
      out.print(jsonEntries.mkString(",\n"))
      out.println("\n]")
    } finally {
      out.close()
    }
  }
  // control energy source status (flow in UML diagram)
  def controlEnergySource(): Unit = {
    loadStatus() match {
      case Success(l) =>
        // show current source
        println("\n--- CURRENT SOURCE STATUS ---")
        l.zipWithIndex.foreach { case(s, i) =>
          println(s"${i + 1}. ${s.sourceType}: ${s.status}")
        }

        // promppt and select source
        print("\nSelect source number to toggle status: ")
        val choice = Try(scala.io.StdIn.readLine().trim.toInt).getOrElse(-1)
        if(choice > 0 && choice <= l.size) {
          val index = choice - 1
          val selected = l(index)

          // toggle the source status ACTIVE <-> INACTIVE
          val newStatus = if(selected.status == "ACTIVE") "INACTIVE" else "ACTIVE"
          val updatedList = l.updated(index, selected.copy(status = newStatus))

          // save new source status
          saveStatus(updatedList) match {
            case Success(_) =>
              // display new source status
              println(s"\nSUCCESS: ${selected.sourceType} is now $newStatus")
              println("Updated list:")
              updatedList.foreach(s => println(s"- ${s.sourceType}: ${s.status}"))

            case Failure(e) => println(s"ERROR: Save failed: ${e.getMessage}")
          }
        } else {
          println("Invalid option")
        }

      case Failure(e) =>
        println(s"ERROR: Could not read status.json: ${e.getMessage}")
    }
  }
}

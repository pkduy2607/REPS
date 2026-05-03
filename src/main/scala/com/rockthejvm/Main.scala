package com.rockthejvm

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object Main extends App {
  @tailrec
  def showMenu(): Unit = {
    print(
      """
Renewable energy plant system menu:
0. Exit program
1. Collect data
2. View data
3. Analyze data
4. Check system health
5. Monitor and control energy sources
Enter your choice: """.stripMargin)

    val choice = Try(StdIn.readLine().trim.toInt).getOrElse(-1)

    choice match {
      case 0 =>
        println("Existing program...")
        ()

      case 1 =>
        CollectData.collectData()
        showMenu()

      case 2 =>
        ViewData.viewData()
        showMenu()

      case 3 =>
        AnalyzeData.analyzeData()
        showMenu()

      case 4 =>
        IssueDetector.detectAndAlert()
        showMenu()

      case 5 =>
        ControlEnergySource.controlEnergySource()
        showMenu()

      case _ =>
        println("Invalid option")
        showMenu()
    }
  }

  showMenu()
}

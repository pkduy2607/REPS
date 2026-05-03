# REPS - Renewable Energy Plant System

## Introduction
The renewable energy plant system is an application developed using the functional
programming paradigm and was created as part of the project for the course CT60A9602
Functional Programming at LUT University.

This project focuses on the development of a Renewable Energy Plant System (REPS) for
monitoring and managing renewable energy sources such as solar, wind, and hydro power. The
system collects and stores energy generation data, allows users to view and analyze the data
using filtering and basic statistical methods, and supports issue detection by generating alerts
for abnormal situations like low energy output or equipment malfunctions.

## Key features
* **Collect data:** Uses a Fingrid API key to fetch renewable energy generation data and store it locally.
* **View data:** Allows users to view stored renewable energy generation data.
* **Analyze data:** Allows users to analyze stored energy data with basic statistics based on selected time ranges (last hour, day, week, or month).
* **Detect issues:** Detects potential issues by analyzing the most recently stored energy record and alerts the user if abnormal conditions are identified.
* **Control and monitor energy system: ** Allows users to monitor and toggle the operational status of renewable energy sources such as wind turbines, solar panels, and hydro power.

## Installation 
To run this application locally, follow these steps:

1. Clone the repository:
```bash
   git clone https://github.com/pkduy2607/REPS.git
   cd REPS
   ```
2. Install Amazon Corretto 17 from [Amazon AWS](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html).
3. Install sbt from [Scala Sbt](https://www.scala-sbt.org/download/)
4. Add your own Fingrid API key to the `CollectData.scala` file  
(`src/main/scala/com/rockthejvm/CollectData.scala`).  
The API key is not included in this repository for security reasons.

5. Run the program
```bash
    sbt run
```
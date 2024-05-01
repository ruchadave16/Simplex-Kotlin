# Simplex-Kotlin
Simplex Algorithm implementation in Kotlin

## Overview
We aim to implement the Simplex Algorithm to solve Linear Programming maximization problems in Kotlin. Simplex is an approach that iteratively uses slack variables, a table, and pivot variables to arrive at an optimal solution. A linear program is a problem that aims to arrive at the optimal outcome in a mathematical model that has requirements and objectives that are linearly represented. This project aims to concisely implment Simplex to demonstrate a strong understanding of the algorithm's principles. 

## Try It Yourself!
### Installation Instructions
1. Clone the repository.<br>
   - Using SSH: `git clone git@github.com:ruchadave16/Simplex-Kotlin.git`
   - Using HTML (not recommended): `https://github.com/ruchadave16/Simplex-Kotlin.git`
2. Navigate to the local repository using your terminal.<br>
   ex: `cd Simplex-Kotlin`
   If this project is already compiled, the following file should be present:
   - src/main/kotlin
     + Simplex.kt
3. Configure the project.<br>
  (a) Since this is build through Gradle, make sure this is installed
    ```
    $ gradle init
    ```
    (b) Follow the instructions on the terminal to set up the project properly. You should now have the following outside the `src` directory, inside the `Simplex-Kotlin` repo:
        - src/main/kotlin
          + Simplex.kt
        - gradle
          - wrapper
            + gradle-wrapper.jar
            + gradle-wrapper.properties
        + build.gradle.kts
        + gradle.properties
        + gradlew
        + gradlew.bat
        + settings.gradle.kts
        + README.kts
4. Run the project.<br>
    Use the following command to run the Simplex algorithm on the default objective function and constraints and get the resulting table and solution (rounded and not rounded) outputted on the terminal.
    ```
    $ ./gradlew run
    ``` 


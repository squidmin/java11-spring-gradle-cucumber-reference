# java11-spring-gradle-cucumber-reference

Cucumber framework for Java reference.


Made with:
- **IntelliJ IDEA 2023.1 (Ultimate Edition)**
- **openjdk 11.0.17**
- **Gradle 7.6.1**


---


### Build jar

<details>
<summary>Expand</summary>

```shell
./gradlew clean build
```

</details>


---


### Run Cucumber tests

<details>
<summary>Expand</summary>

```shell
./gradlew cukes
```

<img src="img/gradle_cukes_cmd.gif" alt="Running the gradle cukes command." width="730">

</details>


---


### View BDD test results

After running the tests, find the `cucumber-report.html` file under the `target` directory.
Open the file with a web browser:

<img src="img/view_test_results.png" alt="" width="500">

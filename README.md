# FintechAutomation

A robust and scalable automation framework built using **Java**, **Selenium**, **Appium**, **Rest-Assured**, and **TestNG** — designed for automating **web**, **mobile**, and **API** testing across fintech or any modular product landscape.

---

## 🚀 Tech Stack

| Layer        | Tools / Frameworks                          |
|--------------|---------------------------------------------|
| Language     | Java                                        |
| Build Tool   | Maven                                       |
| Web Testing  | Selenium WebDriver                          |
| Mobile Testing | Appium                                    |
| API Testing  | Rest-Assured                                |
| Test Framework | TestNG                                    |
| Logging      | SLF4J                                       |

---

## 📁 Project Structure
FintechAutomation/ │ ├── base/ # Base classes for shared setup/teardown │ └── BaseWebTest.java │ └── BaseMobileTest.java │ └── BaseApiTest.java │ ├── driver/ # DriverFactory for Web & Mobile │ └── DriverFactory.java │ ├── config/ # JSON Config files and handler │ └── config.json │ └── PropertyFileHandler.java │ ├── tests/ │ ├── web/ # Web-based test cases │ ├── app/ # Mobile App test cases │ └── api/ # API test cases │ ├── utils/ # Utility classes and helpers │ └── JsonUtils.java │ └── LoggerUtil.java │ ├── testng/ # TestNG XMLs for different runs │ └── test-web.xml │ └── test-api.xml │ └── pom.xml # Maven configuration


## ⚙️ Features

- ✅ **Cross-browser execution**: Execute on Chrome, Firefox, etc.
- ✅ **Parallel test execution** using TestNG
- ✅ **Configurable environment setup** via JSON files
- ✅ **Integrated Web + App + API test coverage**
- ✅ **DriverFactory abstraction** for dynamic browser/device setup
- ✅ **Auth token capture via DevTools (CDP)** for secure API calls
- ✅ **CLI support** for running via `java -jar` with property injection
- ✅ **CI/CD ready** structure

---

## 🧪 How to Execute

### 1. Clone the Repo

```bash
git clone https://github.com/shofin-islam/FintechAutomation.git
cd FintechAutomation

**Build the Project**
mvn clean install

******** Test TestNG Suite *******
mvn test -Dsuite=testng.xml

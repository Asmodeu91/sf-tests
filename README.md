# UI Testing Project

Проект содержит автоматизированные UI-тесты для следующих платформ:

- **Веб-приложение**: `ru.wikipedia.org`
- **Мобильное приложение**: Wikipedia для **Android**

Тесты реализованы с использованием Page Object-паттерна и запускаются через Maven-профили.

---

## Технологический стек

- **Java** 11+
- **Selenium WebDriver**
- **Appium** (UiAutomator2)
- **TestNG**
- **Maven**
- **WebDriverManager**
- **Appium Java Client**

---

## Требования к окружению

### Общие

- Java 11 или выше
- Maven 3.8+
- Git

### Для веб-тестов

- Браузер **Google Chrome**
- Доступ к сайту `https://ru.wikipedia.org`

### Для мобильных тестов

- **Node.js** и **Appium**
  ```bash
  npm install -g appium
  appium driver install uiautomator2
  ```
- **Android Studio** и **Android SDK**
- Настроенный Android-эмулятор  
  *(рекомендуется API 30+, минимально допустимый — API 16)*
- Установленное приложение **Wikipedia** на эмуляторе или реальном устройстве

---

## Структура проекта

```
src/
└── test/
    ├── java/
    │   └── com/automation/
    │       ├── config/    # Конфигурация тестов (TestConfig)
    │       ├── web/       # Page Object'ы и веб-тесты
    │       └── mobile/    # Page Object'ы и мобильные тесты
    └── resources/
        ├── config.properties   # Настройки веб- и мобильных тестов
        ├── testng-web.xml      # TestNG suite для веб-тестов
        └── testng-mobile.xml   # TestNG suite для мобильных тестов
```

---

## Настройка окружения

### Android SDK и переменные среды (Windows)

```
ANDROID_HOME=C:\Users\<USER>\AppData\Local\Android\Sdk
ANDROID_SDK_ROOT=C:\Users\<USER>\AppData\Local\Android\Sdk
```

В `PATH` добавить:

```
C:\Users\<USER>\AppData\Local\Android\Sdk\platform-tools
```

Проверка:
```bash
adb version
```

---

## Конфигурация проекта

Файл: `src/test/resources/config.properties`

### Веб-тесты

```properties
web.base.url=https://ru.wikipedia.org
web.browser=chrome
web.timeout.seconds=10
```

### Мобильные тесты

```properties
mobile.platform.name=Android
mobile.platform.version=16
mobile.device.name=emulator-5554
mobile.automation.name=UiAutomator2
mobile.appium.server.url=http://127.0.0.1:4723

mobile.app.package=org.wikipedia
mobile.app.activity=org.wikipedia.main.MainActivity
mobile.app.path=
```

---

## Запуск тестов

```bash
mvn clean test
```

### Веб
```bash
mvn clean test -Pweb
```

### Мобильные
```bash
emulator -avd Pixel_5
adb devices
appium -p 4723
mvn clean test -Pmobile
```

---

## Тестовые сценарии

### Веб
- Главная страница
- Поиск статьи
- Навигация
- Страница входа

### Мобильные
- Запуск приложения
- Поиск статьи
- Открытие статьи
- Навигация по контенту
- Поиск несуществующей статьи

---

## Устранение проблем

```bash
appium --version
appium driver uninstall uiautomator2
appium driver install uiautomator2

emulator -list-avds
adb kill-server
adb start-server
adb devices

mvn clean install -U
```

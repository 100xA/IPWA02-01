# Ghost Net Fishing (IPWA02-01)

JSF/CDI/JPA-Prototyp für das Melden und Bergen von Geisternetzen.

## Technologie-Stack
- Java 17
- Maven (WAR)
- TomEE 9 (Jakarta EE 9)
- JSF/Facelets + CDI
- JPA + Hibernate
- MySQL (lokale Installation)
- Leaflet + OpenStreetMap (Weltkarte)

## Projektstruktur
- `src/main/java/de/iu/ipwa/ghostnet/...` - Domain, Service, DAO, Web-Beans
- `src/main/webapp/*.xhtml` - JSF-Seiten
- `src/main/resources/META-INF/persistence.xml` - JPA-Konfiguration
- `src/main/resources/sql/schema.sql` - DB-Schema
- `src/main/resources/sql/seed.sql` - Beispieldaten
- `docs/manual-tests.md` - manuelle Testfälle T01-T08
- `docs/uml/ghostnet-structure.puml` - UML-Strukturdiagramm

## Voraussetzungen
1. TomEE 9 installiert
2. MySQL lokal erreichbar
3. Java 17 installiert

## Java 17 für Maven setzen
`mvn` kann lokal mit einer anderen Java-Version laufen. Vor dem Build:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
mvn -version
```

## Datenbank einrichten
Die Standardwerte in `persistence.xml` sind:
- URL: `jdbc:mysql://localhost:3306/ghostnetfishing?...`
- User: `root`
- Passwort: `root`

Falls nötig, Werte in `src/main/resources/META-INF/persistence.xml` anpassen.
Alternativ kannst du ohne Code-Änderung DB-Werte per Env setzen:
- `GHOSTNET_DB_URL`
- `GHOSTNET_DB_USER`
- `GHOSTNET_DB_PASSWORD`

DB anlegen und SQL ausführen:

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ghostnetfishing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p ghostnetfishing < src/main/resources/sql/schema.sql
mysql -u root -p ghostnetfishing < src/main/resources/sql/seed.sql
```

## Build
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn clean package
```

WAR-Ausgabe:
- `target/ghostnetfishing-1.0.0-SNAPSHOT.war`

## Deployment auf TomEE
1. WAR nach `<TOMEE_HOME>/webapps/` kopieren.
2. TomEE starten.
3. Anwendung aufrufen:
   - `http://localhost:8080/ghostnetfishing-1.0.0-SNAPSHOT/`

## Schnellstart per Script
Im Projekt liegen zwei Scripts:
- `run-app.sh` (build + deployment + TomEE start)
- `stop-app.sh` (TomEE stop)

Starten:
```bash
cd /Users/janbiernacki/Dev/CollegeDev/IPWA02-01
TOMEE_HOME=/absolute/path/to/apache-tomee-9 ./run-app.sh
```

Mit eigenen DB-Zugangsdaten:
```bash
cd /Users/janbiernacki/Dev/CollegeDev/IPWA02-01
GHOSTNET_DB_URL="jdbc:mysql://localhost:3306/ghostnetfishing?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true" \
GHOSTNET_DB_USER="dein_mysql_user" \
GHOSTNET_DB_PASSWORD="dein_mysql_passwort" \
./run-app.sh
```

Beispiel für leeres DB-Passwort:
```bash
GHOSTNET_DB_USER="root" GHOSTNET_DB_PASSWORD="" ./run-app.sh
```

Ohne Angabe von `TOMEE_HOME` verwenden die Skripte automatisch:
`/opt/homebrew/opt/tomee-plume/libexec`

```bash
cd /Users/janbiernacki/Dev/CollegeDev/IPWA02-01
./run-app.sh
```

Stoppen:
```bash
cd /Users/janbiernacki/Dev/CollegeDev/IPWA02-01
TOMEE_HOME=/absolute/path/to/apache-tomee-9 ./stop-app.sh
```

Oder mit Default-Pfad:
```bash
cd /Users/janbiernacki/Dev/CollegeDev/IPWA02-01
./stop-app.sh
```

## Kernseiten
- `/index.xhtml`
- `/report-net.xhtml`
- `/open-nets.xhtml`
- `/my-recoveries.xhtml`

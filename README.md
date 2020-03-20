# FrorageMobileApp
Aplikacja mobilna na platformę Android, stworzona na potrzeby projektu z przedmiotu Inżynieria Oprogramowania.

## To run server:

1. clone repo

2. `cd fridge-project\backend\server`

3. wykonaj skrypt fridge_database.sql na lokalnym serwerze mysql

4. zmień ustawienia dotyczące polaczenia sie z baza danych w pliku  /src/main/resources/application.properties (domyślnie: host - localhost, port - 3306, database - frorage, username - root, password jest puste)

5. `gradlew bootRun`

## Api documentation:

`http://localhost:8080/docs`

## Project documentation

[Link here!](https://github.com/jserweta/FrorageMobileApp/blob/master/Dokumentacja.pdf)

## Screenshots:

![Screenshot](https://github.com/jserweta/FrorageMobileApp/blob/master/loginScreen.jpg)

![Screenshot](https://github.com/jserweta/FrorageMobileApp/blob/master/productsScreen.jpg)

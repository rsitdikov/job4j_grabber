[![Build Status](https://travis-ci.org/rsitdikov/job4j_grabber.svg?branch=master)](https://travis-ci.org/rsitdikov/job4j_grabber)

[![codecov](https://codecov.io/gh/rsitdikov/job4j_grabber/branch/master/graph/badge.svg?token=UAME4ZPBK9)](https://codecov.io/gh/rsitdikov/job4j_grabber)

# job4j_grabber
Описание.

Система запускается по расписанию. Период запуска указывается в настройках - app.properties. 

Первый сайт будет sql.ru. В нем есть раздел job. Программа должно считывать все вакансии относящие к Java и записывать их в базу.

Доступ к интерфейсу будет через REST API.

 

Расширение.

1. В проект можно добавить новые сайты без изменения кода.

2. В проекте можно сделать параллельный парсинг сайтов.

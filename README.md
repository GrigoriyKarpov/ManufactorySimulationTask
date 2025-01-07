Решение тестового задания на позицию Java-разработчик (Специалист по имитационному моделированию)

Запуск через консоль, например: java -jar manufactory.jar "тестовый сценарий №1 3 сотрудников.xlsx" "тестовый сценарий №1 6 сотрудников.xlsx".

В качестве аргументов передаем имена файлов. Для каждого выполняется симуляция, а результат сохраняется в новый файл "[имя входного файла] result.csv".

В моменты начала и окончания обработки каждого файла в консоль выводятся соответствующие сообщения.

Коротко об алгоритме: Во время симуляции все ПЦ проходят итерации, если в ПЦ есть свободные "рабочие места" в работу берется деталь из буфера, также назначается свободный сотрудник. Управление сотрудниками происходит из единого сервиса, ссылка на который есть в каждом ПЦ. Когда обработка завершена, если в буфере есть детали, берется очередная. Завершенная передается в следующий ПЦ. Учитываются остатки времени, если деталь была завершена между итерациями. Если в буфере нет детали, работник возвращается в резерв и может быть задействован в другом ПЦ. В конце могут остаться свободные работники и ПЦ со свободными местами, поэтому еще раз распределяем сотрудников.

Добавил в репозиторий сам jar'ник и файл "karpov result.xlsx" для предоставленных тестовых данных с графиками JupyterNotebook, в файле 2 страницы для 3 и для 6 работников.
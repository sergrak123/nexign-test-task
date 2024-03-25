Проект для Nexign Bootcamp. tg: @amanisky

Задание:

1. Создать сервис, эмулирующий работу коммутатора для генерации CDR файлов. Каждый CDR файл представляет собой данные о звонках абонентов за один месяц в течение года в хронологическом порядке. Данные в файле должны быть в формате: тип вызова (исходящий или входящий), номер абонента, дата и время начала и окончания звонка в unixtime. Условия включают случайную генерацию данных звонков, обработку и сохранение данных в локальной бд.

2. Генерация UDR отчетов. Данные из CDR должны быть агрегированы по каждому абоненту в отчете. Сгенерированные отчеты сохраняются в директории /reports с шаблонным именем номер_месяц.json. Класс генератора отчетов должен содержать методы: generateReport(), generateReport(msisdn), generateReport(msisdn, month) для сохранения всех отчетов и вывода в консоль соответствующих таблиц.

Краткое описание решения: 

Для каждого месяца генерируем cdr файлы из списка cdr записей, рандомно сгенерированных по каждому месяцу. Данные находятся в хронологическом порядке по дате и времени звонка. Записи формируются за счет вспомогательных методов по рандомной генерации даты и времени звонка (создается по заданному году и месяцу и конвертируется в unixtime), его продолжительности, количества и типа вызовов, номеров абонентов. Номера абонентов предварительно генерируем и сохраняем в бд для последующего использования. После формирования cdr файлов, сохраняем их в заданную директорию в формате .txt и сохраняем в h2 бд. 

Для генерации UDR отчетов считываем все cdr файлы, парсим из них данные. Затем агрегируем полученные записи по номеру абонента и месяцам в HashMap. Формируем отчеты по каждому абоненту в каждом месяце с просчетом общего времени по каждому типу вызова. Полученные отчеты сохраняем в .json файлах (я решил формировать отчеты у абонентов по всем месяцам, даже если в них не было звонков, так как это более удобно для бизнеса) и выводим в консоль один из трех вариантов сводной таблицы. 

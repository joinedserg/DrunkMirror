DrunkMirror

...


Autumn

"Свой" "Spring" (кривой и косой). 
любителям windows заглянуть в dev.Seperator
запускать из dev.autumn.Autumn

примеры лежать в dev.example.*
платформа ориентирована на преимущество конфигурации (т.е. если в 
конфигурации xml указано некое свойство и оно же указано в коде, в виде 
аннотации, то использовано будет то, что в xml)

на данный момент работают следующие аннотации:

@Value - устанавливает значение поля, может браться из конфигурации, явно 
из аннотации, и по умолчанию

@OutputContextHandler - подбирает и инициализирует "менеджер" вывода
может быть задан в конфигурации, аннотации, если не задан подбирается из 
любого класса наследника, доступного в предлах проанализированных классов

Структура xml

available_components av_package - путь к пакетам, внутри которых будет поиск,
если не определен, то искать будет только в явно определенных нодах

node id ид ноды, 
     class путь к ноде
в ноде могут быть указаны свойства (например для @Value) или же нода "менеджера"
вывода


У осени очень много проблем: дожди, слякоть, сильная связность, в основном от 
того, что не сделал единый класс для хранения всей инфы о нодах, а попытался
как-то хранить данные отдельно

В своей IDE делать импорт из pom

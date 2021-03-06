# Системы генерации запросов по структуре онтологии
##### Система генерации естественно-языковых поисковых запросов по структуре онтологии для обогащения ее новыми фактами
 
## Цель: 
Разработка системы обогащения онтологии  новыми фактами на основе выявления по ее структуре элементов, требующих дополнительной информации.

### Существует онтология, которая образована следующим образом:
* Проводится кроулинг новостных сайтов;
* Из найденных новостей извлекаются факты и отношения при помощи контекстно-свободной грамматики и нейросети; 
* Факты размещаются в графовой БД.

По результатам данных процессов получается база данных, в которой возникает **проблема отсутствующих данных** для построения выводов и проведения аналитики по ней, так как в статьях зачастую опускают некоторые подробности, например, понятные из прошлых статей.

### Используются:
* **GraphDB** - графовая база данных, в которой хранится база данных;
* **Java SpringBoot** - технология, при помощи которой реализован алгоритм по обработке фактов и генерации запросов.

### Модули, и мера их ответственности:
* **[GeneratorService.java](https://github.com/GospodarikovaVS/generator/blob/main/src/main/java/com/ontology/generator/service/GeneratorService.java "com/ontology/generator/service/GeneratorService.java")** - сервис, который ответственнен, за непосредственную генерацию поисковых запросов для обогащения онтологии новыми фактами. Данный сервис обрабатывает запросы от контроллера и выполняет алгоритм поиска фактов, требующих дополнения и уточнения.
* **[GraphDBService.java](https://github.com/GospodarikovaVS/generator/blob/main/src/main/java/com/ontology/generator/service/GraphDBService.java "com/ontology/generator/service/GraphDBService.java")** - сервис, который ответственнен, за обращения к графовой базе данных, а именно выполнения запросов к ней, не включая формирование этих запросов. Содержит в себе также все конфигурации базы, доступные для использования
* **[SparqlQuery.java](https://github.com/GospodarikovaVS/generator/blob/main/src/main/java/com/ontology/generator/service/SparqlQuery.java "com/ontology/generator/service/SparqlQuery.java")** - вспомогательный Enum элемент, который хранит коллекцию шаблонов SPARQL запросов.

### Алгоритм:

<img src="https://user-images.githubusercontent.com/34095981/119878966-6abde800-bf33-11eb-8f67-654237baf24c.jpg" alt="drawing" width="300"/>


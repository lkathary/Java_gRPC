# Java_gRPC

Тестовый проект по **gRPC фреймворк** 

Реализация Клиент-Серверного приложения на базе опенсорсного фреймворка для удаленного вызова процедур **gRPC (remote procedure call)**.

---

Многомодульный проект:
1. gRPC_server - серверная часть (***Server*** - запускать первой)
2. gRPC_client - клиентская часть, состоящая из двух модулей: (***Client*** и  ***StockClient***)


В приложении реализованы все основные сценарии взаимодейтвия gRPC через протокол HTTP 2.0:
   - Client-Server simple request ***[request -> response]***
   - Server-Side Streaming ***[request -> (stream)response]***
   - Client-Side Streaming ***[(stream)request -> response]***
   - Bidirectional Streaming  ***[(stream)request -> (stream)response]***

-----
Использованные материалы:

https://www.baeldung.com/grpc-introduction

https://www.baeldung.com/java-grpc-streaming


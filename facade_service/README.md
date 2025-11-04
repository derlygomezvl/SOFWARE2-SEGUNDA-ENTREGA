# facade_service (generated)
Proyecto generado con las convenciones solicitadas por el usuario (prefijos atr/obj/prm y javadoc author).

## Cómo ejecutar
1. Asegúrate de tener Java 17 y Maven instalados.
2. Inicia los servicios externos (docente-service, formato-service, notificacion-service) en los puertos esperados.
3. Desde la raíz del proyecto:

```bash
mvn clean package
mvn spring-boot:run
```

El API estará disponible en `http://localhost:8080/facade/...`

## Notas
- Las clases usan `RestTemplate` para llamadas a servicios externos.
- RabbitMQ y OAuth2 están añadidos en `pom.xml` pero no configurados.

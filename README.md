# 🎓 Sistema de Gestión de Trabajo de Grado - Microservicios

Sistema basado en microservicios para la gestión de trabajos de grado

## 📋 Arquitectura

El sistema está compuesto por los siguientes servicios:

- **Gateway Service** (Puerto 8080): Punto de entrada único, enrutamiento y autenticación
- **Identity Service** (Puerto 8081): Gestión de usuarios y autenticación JWT
- **Submission Service** (Puerto 8082): Gestión de entregas y documentos
- **Notification Service** (Puerto 8083): Envío de notificaciones y emails
- **RabbitMQ** (Puertos 5672, 15672): Message broker para comunicación asíncrona
- **PostgreSQL**: 3 bases de datos independientes (puertos 5432, 5433, 5434)

## 🚀 Inicio Rápido con Docker Compose

### Prerequisitos

- Docker Desktop instalado
- Docker Compose (incluido en Docker Desktop)
- Al menos 4GB de RAM disponible
- Puertos 8080-8083, 5432-5434, 5672, 15672 disponibles

### Pasos para Iniciar

1. **Configurar variables de entorno**
   ```bash
   # Copiar el archivo de ejemplo
   copy .env.example .env
   
   # Editar .env con tus valores reales
   notepad .env
   ```

2. **Iniciar todos los servicios**
   ```bash
   docker-compose up -d
   ```

3. **Verificar el estado de los servicios**
   ```bash
   docker-compose ps
   ```

4. **Ver logs de todos los servicios**
   ```bash
   docker-compose logs -f
   ```

5. **Ver logs de un servicio específico**
   ```bash
   docker-compose logs -f gateway
   docker-compose logs -f identity
   docker-compose logs -f submission
   docker-compose logs -f notification
   ```

### Detener los Servicios

```bash
# Detener sin eliminar volúmenes (datos se mantienen)
docker-compose down

# Detener y eliminar volúmenes (limpieza completa)
docker-compose down -v

## 🔐 Variables de Entorno Requeridas

El archivo `.env` debe contener:

```env
# JWT - Secret para firmar tokens (mínimo 32 caracteres)
JWT_SECRET=your-super-secret-jwt-key-change-this

# RabbitMQ - Credenciales del message broker
RABBITMQ_USER=admin
RABBITMQ_PASS=admin_password

# Bases de Datos
IDENTITY_DB_USER=identity_user
IDENTITY_DB_PASS=identity_password

SUBMISSION_DB_USER=submission_user
SUBMISSION_DB_PASS=submission_password

NOTIFICATION_DB_USER=notification_user
NOTIFICATION_DB_PASS=notification_password

# SMTP - Configuración para envío de emails
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password
SMTP_FROM=noreply@trabajogrado.com
```

## 📞 Endpoints Principales

### Gateway (Punto de Entrada Único)
- Base URL: http://localhost:8080
- Health: http://localhost:8080/api/gateway/health

### A través del Gateway:
- Auth: `POST http://localhost:8080/api/identity/auth/login`
- Submissions: `http://localhost:8080/api/submissions/*`
- Notifications: `http://localhost:8080/api/notifications/*`


**Autor**: Equipo de Desarrollo - Unicauca  
**Fecha**: Octubre 2025  
**Versión**: 1.0.0

"# MicroservicioSubmission" 

-- Crear la base de datos
CREATE DATABASE SistemaCitasMedicas;
GO

-- Usar la base de datos
USE SistemaCitasMedicas;
GO

-- Tabla de usuarios (solo para login de secretaria)
CREATE TABLE Users (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(100) NOT NULL,
    passwordHash VARCHAR(64) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    status TINYINT NOT NULL  -- 1 = Activo, 0 = Inactivo
);
GO

-- Tabla de pacientes
CREATE TABLE Pacientes (
    id INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(20),
    fechaNacimiento DATETIME,
    genero TINYINT NOT NULL  -- 1 = Masculino, 2 = Femenino, 3 = Otro
);
GO

-- Tabla de doctores
CREATE TABLE Doctores (
    id INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    experiencia FLOAT NOT NULL,
    disponibilidad TINYINT NOT NULL  -- 1 = Disponible, 0 = No disponible
);
GO

-- Tabla de citas
CREATE TABLE Citas (
    id INT PRIMARY KEY IDENTITY(1,1),
    pacienteId INT NOT NULL,
    doctorId INT NOT NULL,
    fechaHora DATETIME NOT NULL,
    estado TINYINT NOT NULL DEFAULT 1,  -- 1 = Programada, 2 = Completada, 3 = Cancelada
    CONSTRAINT FK_Cita_Paciente FOREIGN KEY (pacienteId) REFERENCES Pacientes(id),
    CONSTRAINT FK_Cita_Doctor FOREIGN KEY (doctorId) REFERENCES Doctores(id)
);
GO

-- Tabla de pagos
CREATE TABLE Pagos (
    id INT PRIMARY KEY IDENTITY(1,1),
    citaId INT NOT NULL,
    monto FLOAT NOT NULL,
    fechaPago DATETIME NOT NULL,
    CONSTRAINT FK_Pago_Cita FOREIGN KEY (citaId) REFERENCES Citas(id)
);
GO

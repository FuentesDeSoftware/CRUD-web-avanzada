-- Crea la base EmpresaDB con las tablas que usa la aplicacion.
-- Se puede ejecutar varias veces: solo crea lo que falta.

IF DB_ID('EmpresaDB') IS NULL
    CREATE DATABASE EmpresaDB;
GO

USE EmpresaDB;
GO

IF OBJECT_ID('dbo.Empleados', 'U') IS NULL
CREATE TABLE dbo.Empleados (
    IdEmpleado INT IDENTITY(1,1) PRIMARY KEY,
    Nombre     NVARCHAR(50)  NOT NULL,
    Apellido   NVARCHAR(50)  NOT NULL,
    Puesto     NVARCHAR(60)  NOT NULL,
    Salario    DECIMAL(12,2) NOT NULL CHECK (Salario >= 0)
);
GO

IF OBJECT_ID('dbo.Planilla', 'U') IS NULL
CREATE TABLE dbo.Planilla (
    IdPago      INT IDENTITY(1,1) PRIMARY KEY,
    idEmpleado  INT           NOT NULL REFERENCES dbo.Empleados(IdEmpleado),
    horasExtras DECIMAL(10,2) NOT NULL CHECK (horasExtras >= 0),
    viaticos    DECIMAL(10,2) NOT NULL CHECK (viaticos >= 0),
    fechaPago   DATETIME      NOT NULL DEFAULT GETDATE()
);
GO

-- Datos de ejemplo (solo si la tabla esta vacia)
IF NOT EXISTS (SELECT 1 FROM dbo.Empleados)
BEGIN
    INSERT INTO dbo.Empleados(Nombre, Apellido, Puesto, Salario) VALUES
        (N'Ana',    N'Martínez', N'Contadora',     25000.00),
        (N'Carlos', N'López',    N'Desarrollador', 30000.00),
        (N'María',  N'Hernández',N'Recepcionista', 15000.00);

    INSERT INTO dbo.Planilla(idEmpleado, horasExtras, viaticos) VALUES
        (1, 5.00, 1200.00),
        (2, 8.50,  800.00);
END
GO

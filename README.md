🚦 FineTrack – Gestión de Multas de Tránsito

FineTrack es una aplicación desarrollada por NexGen System en Java (Swing) para la gestión de multas de tránsito.
Permite registrar, consultar, pagar y eliminar multas de forma organizada, validando pagos en cuotas y generando reportes históricos.

✨ Características

✅ Registro de multas: Agregar nuevas multas con validaciones (placa, cédula, monto, tipo, etc.).
✅ Consulta de multas: Buscar por placa o cédula y listar las multas asociadas.
✅ Pagos en cuotas:
  Máximo 3 pagos por multa.
  Los primeros 2 pagos pueden ser parciales.
  El tercer pago debe cubrir el saldo pendiente completo.
  El estado de la multa se actualiza automáticamente (Pendiente → Pagada).
✅ Gestión de eliminaciones:
  Se pueden eliminar multas ya pagadas.
  Al eliminar, se guardan en un archivo de Reporte de Multas Eliminadas para mantener historial.
✅ Archivos planos como base de datos:
  Multas_Registradas.txt → Registro principal.
  Pagos_Multas.txt → Historial de pagos.
  Multas_Eliminadas.txt → Reporte histórico de eliminaciones.
✅ Interfaz amigable: Diseñada con Swing usando formularios y paneles organizados.

⚙️ Instalación y ejecución
1. Requisitos previos
  Tener instalado Java 8+.
  IDE recomendado: NetBeans (aunque puede ejecutarse en cualquier IDE Java).

2. Clonar el repositorio
   <img width="395" height="68" alt="image" src="https://github.com/user-attachments/assets/278acb2d-44e0-43c2-8b21-089dc0815e35" />
3. Compilar y ejecutar
  Abrir el proyecto en NetBeans.
  Ejecutar la clase principal del proyecto (Main.java).
  El sistema creará y gestionará automáticamente los archivos de texto necesarios.

package multas.models;

import java.util.Date;

//
//  Clase modelo que representa una multa.
//  Contiene atributos como código, placa, cédula, nombre, tipo, fecha,
//  monto y estado, además de sus getters y setters.


public class Multa {
    private String codigo;
    private String placa;
    private String cedula;
    private String nombre;
    private String tipo;
    private Date fecha;
    private double monto;
    private String estado;

    public Multa(String codigo, String placa, String cedula, String nombre, String tipo, Date fecha, double monto, String estado) {
        this.codigo = codigo;
        this.placa = placa;
        this.cedula = cedula;
        this.nombre = nombre;
        this.tipo = tipo;
        this.fecha = fecha;
        this.monto = monto;
        this.estado = estado;
    }
    
        // Getters
    public String getCodigo() { return codigo; }
    public String getPlaca() { return placa; }
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public Date getFecha() { return fecha; }
    public double getMonto() { return monto; }
    public String getEstado() { return estado; }

    // Setters
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setPlaca(String placa) { this.placa = placa; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public void setMonto(double monto) { this.monto = monto; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Multa{" +
                "codigo='" + codigo + '\'' +
                ", placa='" + placa + '\'' +
                ", cedula='" + cedula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", estado='" + estado + '\'' +
                '}';
    }
}


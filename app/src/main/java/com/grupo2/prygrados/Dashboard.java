package com.grupo2.prygrados;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.grupo2.prygrados.Api.ApiClient;
import com.grupo2.prygrados.Api.ApiService;
import com.grupo2.prygrados.Modelo.Producto;
import com.grupo2.prygrados.Modelo.Reporte;
import com.grupo2.prygrados.Modelo.Venta;
import com.grupo2.prygrados.Modelo.VentaTabla;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {

    // Stock igual o menor a este número se considera "bajo"
    private static final int STOCK_BAJO = 5;
    private static final int DIAS_GRAFICO = 7;

    // Vistas: ventas
    private TextView txtFechaHoy, txtVentasHoy, txtTotalHoy, txtTotalHistorico;
    private TextView txtTicketPromedio, txtVentaMayor, txtNumVentas, txtResumenGrafico;
    private LinearLayout layoutGrafico, layoutTopEmpleados;
    private TextView txtEfectivo, txtTarjeta, txtTransferencia;
    private ProgressBar barEfectivo, barTarjeta, barTransferencia;

    // Vistas: inventario
    private TextView txtStockTotal, txtValorInventario, txtStockBajo, txtAgotados;
    private TextView txtDashBuzos, txtDashPantalones, txtDetalleCategorias;
    private ProgressBar barBuzos, barPantalones;
    private LinearLayout layoutTopProductos, layoutStockCritico;

    // Vistas: reportes
    private TextView txtTotalReportes, txtReportesInventario, txtUltimoReporte;

    private ApiService api;
    private FirebaseFirestore db;

    private long ultimoAviso = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        txtFechaHoy = findViewById(R.id.txtFechaHoy);
        txtVentasHoy = findViewById(R.id.txtVentasHoy);
        txtTotalHoy = findViewById(R.id.txtTotalHoy);
        txtTotalHistorico = findViewById(R.id.txtTotalHistorico);
        txtTicketPromedio = findViewById(R.id.txtTicketPromedio);
        txtVentaMayor = findViewById(R.id.txtVentaMayor);
        txtNumVentas = findViewById(R.id.txtNumVentas);
        txtResumenGrafico = findViewById(R.id.txtResumenGrafico);
        layoutGrafico = findViewById(R.id.layoutGrafico);
        layoutTopEmpleados = findViewById(R.id.layoutTopEmpleados);
        txtEfectivo = findViewById(R.id.txtEfectivo);
        txtTarjeta = findViewById(R.id.txtTarjeta);
        txtTransferencia = findViewById(R.id.txtTransferencia);
        barEfectivo = findViewById(R.id.barEfectivo);
        barTarjeta = findViewById(R.id.barTarjeta);
        barTransferencia = findViewById(R.id.barTransferencia);

        txtStockTotal = findViewById(R.id.txtStockTotal);
        txtValorInventario = findViewById(R.id.txtValorInventario);
        txtStockBajo = findViewById(R.id.txtStockBajo);
        txtAgotados = findViewById(R.id.txtAgotados);
        txtDashBuzos = findViewById(R.id.txtDashBuzos);
        txtDashPantalones = findViewById(R.id.txtDashPantalones);
        txtDetalleCategorias = findViewById(R.id.txtDetalleCategorias);
        barBuzos = findViewById(R.id.barBuzos);
        barPantalones = findViewById(R.id.barPantalones);
        layoutTopProductos = findViewById(R.id.layoutTopProductos);
        layoutStockCritico = findViewById(R.id.layoutStockCritico);

        txtTotalReportes = findViewById(R.id.txtTotalReportes);
        txtReportesInventario = findViewById(R.id.txtReportesInventario);
        txtUltimoReporte = findViewById(R.id.txtUltimoReporte);

        api = ApiClient.getClient().create(ApiService.class);
        db = FirebaseFirestore.getInstance();

        txtFechaHoy.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date()));

        logEvento("SUCCESS", "APP_DASHBOARD", "Entró al dashboard");
    }

    // Los datos se recargan cada vez que se abre o se vuelve a la pantalla
    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos();
        cargarVentas();
        cargarVentasTabla();
        cargarReportes();
    }

    // =========================
    // INVENTARIO
    // =========================
    private void cargarProductos() {

        api.listarProductos().enqueue(new Callback<List<Producto>>() {

            @Override
            public void onResponse(Call<List<Producto>> call,
                                   Response<List<Producto>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    logEvento("ERROR", "DASHBOARD_PRODUCTOS", "Código: " + response.code());
                    return;
                }

                List<Producto> productos = response.body();

                int totalUnidades = 0, bajo = 0, agotados = 0;
                int unidadesBuzos = 0, unidadesPantalones = 0;
                double valorTotal = 0, valorBuzos = 0, valorPantalones = 0;

                for (Producto p : productos) {

                    int stock = p.getStockActual();
                    double valor = aNumero(p.getPrecioVenta()) * stock;

                    totalUnidades += stock;
                    valorTotal += valor;

                    if (stock <= STOCK_BAJO) bajo++;
                    if (stock == 0) agotados++;

                    if (p.getCategoria() == null) continue;

                    if (p.getCategoria().equalsIgnoreCase("BUZOS")) {
                        unidadesBuzos += stock;
                        valorBuzos += valor;
                    } else if (p.getCategoria().equalsIgnoreCase("PANTALONES")) {
                        unidadesPantalones += stock;
                        valorPantalones += valor;
                    }
                }

                txtStockTotal.setText(String.valueOf(totalUnidades));
                txtValorInventario.setText(moneda(valorTotal));
                txtStockBajo.setText(String.valueOf(bajo));
                txtAgotados.setText(String.valueOf(agotados));

                // Barras por categoría, escaladas contra la que tiene más unidades
                txtDashBuzos.setText(unidadesBuzos + " unidades");
                txtDashPantalones.setText(unidadesPantalones + " unidades");

                int maximo = Math.max(Math.max(unidadesBuzos, unidadesPantalones), 1);
                barBuzos.setMax(maximo);
                barBuzos.setProgress(unidadesBuzos);
                barPantalones.setMax(maximo);
                barPantalones.setProgress(unidadesPantalones);

                txtDetalleCategorias.setText(
                        "Valor en inventario\nBuzos: " + moneda(valorBuzos)
                                + "\nPantalones: " + moneda(valorPantalones));

                mostrarMasValiosos(productos);
                mostrarStockCritico(productos);

                logEvento("SUCCESS", "DASHBOARD_PRODUCTOS",
                        "Unidades: " + totalUnidades + ", stock bajo: " + bajo);
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                logEvento("ERROR", "ERROR_CONEXION_DASHBOARD", String.valueOf(t.getMessage()));
                avisarError(t);
            }
        });
    }

    // Top 3 prendas con más valor en inventario (precio x stock)
    private void mostrarMasValiosos(List<Producto> productos) {

        List<Producto> copia = new ArrayList<>(productos);

        Collections.sort(copia, (a, b) -> Double.compare(
                aNumero(b.getPrecioVenta()) * b.getStockActual(),
                aNumero(a.getPrecioVenta()) * a.getStockActual()));

        layoutTopProductos.removeAllViews();

        int n = 0;
        for (Producto p : copia) {
            if (n == 3) break;

            double valor = aNumero(p.getPrecioVenta()) * p.getStockActual();
            if (valor <= 0) continue;

            agregarFila(layoutTopProductos, p.getNombre(), moneda(valor), colorDorado());
            n++;
        }

        if (n == 0) agregarVacio(layoutTopProductos, "Sin datos");
    }

    // Hasta 5 prendas con menos stock (solo las que están en nivel bajo)
    private void mostrarStockCritico(List<Producto> productos) {

        List<Producto> copia = new ArrayList<>(productos);

        Collections.sort(copia, (a, b) ->
                Integer.compare(a.getStockActual(), b.getStockActual()));

        layoutStockCritico.removeAllViews();

        int n = 0;
        for (Producto p : copia) {
            if (n == 5 || p.getStockActual() > STOCK_BAJO) break;

            String texto = p.getStockActual() == 0
                    ? "Agotado"
                    : "Stock: " + p.getStockActual();

            agregarFila(layoutStockCritico, p.getNombre(), texto, colorRojo());
            n++;
        }

        if (n == 0) agregarVacio(layoutStockCritico, "No hay prendas con stock bajo");
    }

    // =========================
    // VENTAS: totales, gráfico de 7 días y métodos de pago
    // =========================
    private void cargarVentas() {

        api.obtenerVentas().enqueue(new Callback<List<Venta>>() {

            @Override
            public void onResponse(Call<List<Venta>> call,
                                   Response<List<Venta>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    logEvento("ERROR", "DASHBOARD_VENTAS", "Código: " + response.code());
                    return;
                }

                SimpleDateFormat formato =
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat formatoEtiqueta =
                        new SimpleDateFormat("dd/MM", Locale.getDefault());

                String hoy = formato.format(new Date());

                // Claves y etiquetas de los últimos 7 días (el último es hoy)
                List<String> claves = new ArrayList<>();
                List<String> etiquetas = new ArrayList<>();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -(DIAS_GRAFICO - 1));
                for (int i = 0; i < DIAS_GRAFICO; i++) {
                    claves.add(formato.format(cal.getTime()));
                    etiquetas.add(formatoEtiqueta.format(cal.getTime()));
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                }
                double[] sumasDia = new double[DIAS_GRAFICO];

                int cantidadHoy = 0, cantidadTotal = 0;
                double totalHoy = 0, totalHistorico = 0, mayor = 0;

                int nEfectivo = 0, nTarjeta = 0, nTransferencia = 0;
                double tEfectivo = 0, tTarjeta = 0, tTransferencia = 0;

                for (Venta venta : response.body()) {

                    double monto = aNumero(venta.getTotal());

                    cantidadTotal++;
                    totalHistorico += monto;
                    if (monto > mayor) mayor = monto;

                    String metodo = String.valueOf(venta.getMetodoPago()).toLowerCase();

                    if (metodo.contains("efectivo")) {
                        nEfectivo++;
                        tEfectivo += monto;
                    } else if (metodo.contains("tarjeta")) {
                        nTarjeta++;
                        tTarjeta += monto;
                    } else if (metodo.contains("transferencia")) {
                        nTransferencia++;
                        tTransferencia += monto;
                    }

                    if (venta.getFecha() == null) continue;

                    String clave = formato.format(venta.getFecha());

                    if (clave.equals(hoy)) {
                        cantidadHoy++;
                        totalHoy += monto;
                    }

                    int idx = claves.indexOf(clave);
                    if (idx >= 0) sumasDia[idx] += monto;
                }

                txtVentasHoy.setText(String.valueOf(cantidadHoy));
                txtTotalHoy.setText(moneda(totalHoy));
                txtTotalHistorico.setText(moneda(totalHistorico));
                txtNumVentas.setText(String.valueOf(cantidadTotal));
                txtVentaMayor.setText(moneda(mayor));
                txtTicketPromedio.setText(moneda(
                        cantidadTotal > 0 ? totalHistorico / cantidadTotal : 0));

                // Métodos de pago: monto, número de ventas y porcentaje del total
                mostrarMetodo(txtEfectivo, barEfectivo, tEfectivo, nEfectivo, totalHistorico);
                mostrarMetodo(txtTarjeta, barTarjeta, tTarjeta, nTarjeta, totalHistorico);
                mostrarMetodo(txtTransferencia, barTransferencia, tTransferencia,
                        nTransferencia, totalHistorico);

                dibujarGrafico(sumasDia, etiquetas);

                logEvento("SUCCESS", "DASHBOARD_VENTAS",
                        "Ventas: " + cantidadTotal + ", hoy: " + cantidadHoy);
            }

            @Override
            public void onFailure(Call<List<Venta>> call, Throwable t) {
                logEvento("ERROR", "ERROR_CONEXION_DASHBOARD", String.valueOf(t.getMessage()));
                avisarError(t);
            }
        });
    }

    private void mostrarMetodo(TextView texto, ProgressBar barra,
                               double monto, int cantidad, double totalGeneral) {

        int porcentaje = totalGeneral > 0
                ? (int) Math.round(monto * 100 / totalGeneral)
                : 0;

        texto.setText(moneda(monto) + " (" + cantidad + ") · " + porcentaje + "%");

        barra.setMax(100);
        barra.setProgress(porcentaje);
    }

    // Gráfico de barras hecho con vistas: ventas de los últimos 7 días
    private void dibujarGrafico(double[] sumas, List<String> etiquetas) {

        layoutGrafico.removeAllViews();

        double maximo = 0, suma = 0;
        int mejor = 0;

        for (int i = 0; i < sumas.length; i++) {
            suma += sumas[i];
            if (sumas[i] > maximo) {
                maximo = sumas[i];
                mejor = i;
            }
        }

        int altoMaximo = dp(100);

        for (int i = 0; i < sumas.length; i++) {

            LinearLayout columna = new LinearLayout(this);
            columna.setOrientation(LinearLayout.VERTICAL);
            columna.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            columna.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

            TextView valor = new TextView(this);
            valor.setText(corto(sumas[i]));
            valor.setTextSize(10);
            valor.setTextColor(colorHint());

            int alto = maximo > 0 ? (int) (altoMaximo * sumas[i] / maximo) : 0;
            alto = Math.max(alto, dp(3));

            View barra = new View(this);
            LinearLayout.LayoutParams lpBarra =
                    new LinearLayout.LayoutParams(dp(18), alto);
            lpBarra.topMargin = dp(4);
            barra.setLayoutParams(lpBarra);
            // El último día (hoy) en dorado; los anteriores en dorado oscuro
            barra.setBackgroundColor(ContextCompat.getColor(this,
                    i == sumas.length - 1 ? R.color.kemba_gold : R.color.kemba_gold_dark));

            TextView etiqueta = new TextView(this);
            etiqueta.setText(etiquetas.get(i));
            etiqueta.setTextSize(10);
            etiqueta.setTextColor(colorHint());
            etiqueta.setPadding(0, dp(4), 0, 0);

            columna.addView(valor);
            columna.addView(barra);
            columna.addView(etiqueta);

            layoutGrafico.addView(columna);
        }

        if (maximo == 0) {
            txtResumenGrafico.setText("Sin ventas en los últimos " + DIAS_GRAFICO + " días");
        } else {
            txtResumenGrafico.setText(
                    "Mejor día: " + etiquetas.get(mejor) + " · " + moneda(maximo)
                            + "\nTotal " + DIAS_GRAFICO + " días: " + moneda(suma)
                            + " · Promedio diario: " + moneda(suma / DIAS_GRAFICO));
        }
    }

    // =========================
    // MEJORES EMPLEADOS (usa la tabla de ventas por empleado)
    // =========================
    private void cargarVentasTabla() {

        api.obtenerVentasTabla().enqueue(new Callback<List<VentaTabla>>() {

            @Override
            public void onResponse(Call<List<VentaTabla>> call,
                                   Response<List<VentaTabla>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    logEvento("ERROR", "DASHBOARD_EMPLEADOS", "Código: " + response.code());
                    return;
                }

                // empleado -> [cantidad de ventas, total vendido]
                final Map<String, double[]> porEmpleado = new HashMap<>();

                for (VentaTabla v : response.body()) {

                    String nombre = v.getEmpleado() != null ? v.getEmpleado() : "Sin nombre";

                    double[] datos = porEmpleado.get(nombre);
                    if (datos == null) {
                        datos = new double[2];
                        porEmpleado.put(nombre, datos);
                    }

                    datos[0] += 1;
                    datos[1] += aNumero(v.getTotal());
                }

                List<String> nombres = new ArrayList<>(porEmpleado.keySet());

                Collections.sort(nombres, (a, b) -> Double.compare(
                        porEmpleado.get(b)[1], porEmpleado.get(a)[1]));

                layoutTopEmpleados.removeAllViews();

                int n = 0;
                for (String nombre : nombres) {
                    if (n == 3) break;

                    double[] datos = porEmpleado.get(nombre);
                    int ventas = (int) datos[0];

                    agregarFila(layoutTopEmpleados,
                            (n + 1) + ". " + nombre + " (" + ventas
                                    + (ventas == 1 ? " venta)" : " ventas)"),
                            moneda(datos[1]), colorDorado());
                    n++;
                }

                if (n == 0) agregarVacio(layoutTopEmpleados, "Sin ventas registradas");
            }

            @Override
            public void onFailure(Call<List<VentaTabla>> call, Throwable t) {
                logEvento("ERROR", "ERROR_CONEXION_DASHBOARD", String.valueOf(t.getMessage()));
                avisarError(t);
            }
        });
    }

    // =========================
    // REPORTES
    // =========================
    private void cargarReportes() {

        api.listarReportes().enqueue(new Callback<List<Reporte>>() {

            @Override
            public void onResponse(Call<List<Reporte>> call,
                                   Response<List<Reporte>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    logEvento("ERROR", "DASHBOARD_REPORTES", "Código: " + response.code());
                    return;
                }

                List<Reporte> reportes = response.body();

                int deInventario = 0;

                for (Reporte r : reportes) {
                    if ("INVENTARIO".equalsIgnoreCase(String.valueOf(r.getTipo()))) {
                        deInventario++;
                    }
                }

                txtTotalReportes.setText(String.valueOf(reportes.size()));
                txtReportesInventario.setText(String.valueOf(deInventario));

                if (reportes.isEmpty()) {
                    txtUltimoReporte.setText("Aún no hay reportes");
                    return;
                }

                // Se toma el último de la lista como el más reciente
                Reporte ultimo = reportes.get(reportes.size() - 1);

                String fecha = ultimo.getFechaGeneracion() != null
                        ? ultimo.getFechaGeneracion() : "";
                if (fecha.length() >= 10) fecha = fecha.substring(0, 10);

                String descripcion = ultimo.getDescripcion() != null
                        ? ultimo.getDescripcion() : "";
                if (descripcion.length() > 120) {
                    descripcion = descripcion.substring(0, 120) + "...";
                }

                txtUltimoReporte.setText("📅 " + fecha + "\n\n" + descripcion);

                logEvento("SUCCESS", "DASHBOARD_REPORTES", "Reportes: " + reportes.size());
            }

            @Override
            public void onFailure(Call<List<Reporte>> call, Throwable t) {
                logEvento("ERROR", "ERROR_CONEXION_DASHBOARD", String.valueOf(t.getMessage()));
                avisarError(t);
            }
        });
    }

    // =========================
    // FILAS CREADAS POR CÓDIGO
    // =========================
    private void agregarFila(LinearLayout contenedor, String izquierda,
                             String derecha, int colorDerecha) {

        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setPadding(0, dp(6), 0, dp(6));

        TextView txtIzq = new TextView(this);
        txtIzq.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        txtIzq.setText(izquierda != null ? izquierda : "");
        txtIzq.setTextColor(ContextCompat.getColor(this, R.color.kemba_white));
        txtIzq.setTextSize(14);

        TextView txtDer = new TextView(this);
        txtDer.setText(derecha);
        txtDer.setTextColor(colorDerecha);
        txtDer.setTextSize(14);
        txtDer.setTypeface(txtDer.getTypeface(), Typeface.BOLD);

        fila.addView(txtIzq);
        fila.addView(txtDer);

        contenedor.addView(fila);
    }

    private void agregarVacio(LinearLayout contenedor, String mensaje) {

        TextView tv = new TextView(this);
        tv.setText(mensaje);
        tv.setTextColor(colorHint());
        tv.setTextSize(14);

        contenedor.addView(tv);
    }

    // =========================
    // AYUDAS
    // =========================
    private int colorDorado() { return ContextCompat.getColor(this, R.color.kemba_gold); }
    private int colorHint() { return ContextCompat.getColor(this, R.color.kemba_hint); }
    private int colorRojo() { return android.graphics.Color.parseColor("#E24B4A"); }

    private int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }

    // Convierte un valor a número sin importar si el modelo lo guarda
    // como Double, Integer, BigDecimal o texto
    private double aNumero(Object valor) {
        try {
            return Double.parseDouble(String.valueOf(valor));
        } catch (Exception e) {
            return 0;
        }
    }

    // Pesos sin decimales y con puntos de miles: $ 85.000
    private String moneda(double valor) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "CO"));
        nf.setMaximumFractionDigits(0);
        return "$ " + nf.format(valor);
    }

    // Formato corto para las barras del gráfico: 85k, 1.2M
    private String corto(double valor) {
        if (valor >= 1000000) return String.format(Locale.US, "%.1fM", valor / 1000000);
        if (valor >= 1000) return String.format(Locale.US, "%.0fk", valor / 1000);
        return String.format(Locale.US, "%.0f", valor);
    }

    // Un solo aviso de error aunque fallen varias consultas a la vez
    private void avisarError(Throwable t) {

        long ahora = System.currentTimeMillis();

        if (ahora - ultimoAviso < 3000) return;
        ultimoAviso = ahora;

        Toast.makeText(this,
                "Error de conexión: " + t.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    // =========================
    // FIREBASE LOG
    // =========================
    private void logEvento(String tipo, String evento, String detalle) {

        HashMap<String, Object> log = new HashMap<>();

        log.put("tipo", tipo);
        log.put("evento", evento);
        log.put("detalle", detalle);
        log.put("fecha", System.currentTimeMillis());

        db.collection("logs_dashboard").add(log);
    }
}
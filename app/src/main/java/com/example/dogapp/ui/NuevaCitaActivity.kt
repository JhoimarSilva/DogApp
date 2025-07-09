package com.example.dogapp.ui

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.widget.doAfterTextChanged
import com.example.dogapp.R
import com.example.dogapp.data.AppDatabase
import com.example.dogapp.data.Cita
import com.example.dogapp.data.CitaRepository
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class NuevaCitaActivity : ComponentActivity() {

    private lateinit var etNombreMascota: EditText
    private lateinit var etRaza: AutoCompleteTextView
    private lateinit var etPropietario: EditText
    private lateinit var etTelefono: EditText
    private lateinit var spinnerSintomas: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnBack: ImageView
    private lateinit var tvEstadoRaza: TextView

    private lateinit var repository: CitaRepository
    private val sintomas = listOf("Solo duerme", "No come", "Fractura extremidad", "Tiene pulgas", "Tiene garrapatas", "Bota demasiado pelo")

    private var razaValida = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_cita)

        val citaDao = AppDatabase.getDatabase(this).citaDao()
        repository = CitaRepository(citaDao)

        inicializarUI()
        configurarEventos()
        cargarRazasDesdeAPI()
    }

    private fun inicializarUI() {
        etNombreMascota = findViewById(R.id.etNombreMascota)
        etRaza = findViewById(R.id.etRaza)
        etPropietario = findViewById(R.id.etPropietario)
        etTelefono = findViewById(R.id.etTelefono)
        spinnerSintomas = findViewById(R.id.spinnerSintomas)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnBack = findViewById(R.id.btnBack)
        tvEstadoRaza = findViewById(R.id.tvEstadoRaza)

        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sintomas)
        spinnerSintomas.adapter = adapterSpinner

        btnGuardar.isEnabled = false
        tvEstadoRaza.visibility = TextView.GONE
    }

    private fun configurarEventos() {
        val validacion = {
            val nombre = etNombreMascota.text.toString().trim()
            val raza = etRaza.text.toString().trim()
            val propietario = etPropietario.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val sintomaSeleccionado = spinnerSintomas.selectedItem.toString()

            btnGuardar.isEnabled = nombre.isNotEmpty()
                    && raza.isNotEmpty()
                    && propietario.isNotEmpty()
                    && telefono.isNotEmpty()
                    && sintomaSeleccionado != "Síntomas"
                    && razaValida
        }

        etNombreMascota.doAfterTextChanged { validacion() }
        etPropietario.doAfterTextChanged { validacion() }
        etTelefono.doAfterTextChanged { validacion() }

        etRaza.doAfterTextChanged {
            razaValida = false
            tvEstadoRaza.visibility = TextView.VISIBLE
            validacion()
        }

        spinnerSintomas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>, p1: android.view.View?, p2: Int, p3: Long) {
                validacion()
            }

            override fun onNothingSelected(p0: AdapterView<*>) {}
        }

        btnGuardar.setOnClickListener {
            guardarCita()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun cargarRazasDesdeAPI() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL("https://dog.ceo/api/breeds/list/all").readText()
                val json = JSONObject(response)
                val lista = mutableListOf<String>()
                val message = json.getJSONObject("message")

                message.keys().forEach { raza ->
                    val subrazas = message.getJSONArray(raza)
                    if (subrazas.length() == 0) {
                        lista.add(raza)
                    } else {
                        for (i in 0 until subrazas.length()) {
                            lista.add("${subrazas.getString(i)} $raza")
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(this@NuevaCitaActivity, android.R.layout.simple_dropdown_item_1line, lista)
                    etRaza.setAdapter(adapter)
                    etRaza.threshold = 1

                    etRaza.setOnItemClickListener { _, _, position, _ ->
                        val razaSeleccionada = adapter.getItem(position).toString()
                        etRaza.setText(razaSeleccionada)
                        razaValida = true
                        tvEstadoRaza.visibility = TextView.GONE
                        btnGuardar.isEnabled = true
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun guardarCita() {
        val cita = Cita(
            nombreMascota = etNombreMascota.text.toString().trim(),
            raza = etRaza.text.toString().trim(),
            propietario = etPropietario.text.toString().trim(),
            telefono = etTelefono.text.toString().trim(),
            sintoma = spinnerSintomas.selectedItem.toString(),
            imagenUrl = ""
        )

        CoroutineScope(Dispatchers.IO).launch {
            repository.insertarCita(cita)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@NuevaCitaActivity, "Cita guardada exitosamente", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}

package com.example.dogapp

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.dogapp.databinding.ActivityEditBinding
import com.example.dogapp.model.Cita
import com.example.dogapp.viewmodel.CitaViewModel
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class EditActivity : AppCompatActivity(){

    private lateinit var binding: ActivityEditBinding
    private val citaViewModel: CitaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de la barra de herramientas (Toolbar)
        val toolbar = findViewById<Toolbar>(R.id.edit_toolbar)
        setSupportActionBar(toolbar)

        //Obtener el ID de la cita desde el Intent
        val citaId = intent.getIntExtra("CITA_ID", -1)

        // Obtener referencias a las vistas
        val btnEditarDatos = findViewById<ImageButton>(R.id.btneditarDatos)
        val textNombreDog  = findViewById<TextInputEditText>(R.id.nombrePerro)
        val textRazaDog = findViewById<AutoCompleteTextView>(R.id.raza)
        val textNombreDueno = findViewById<TextInputEditText>(R.id.nombreDueno)
        val numberoTelefono = findViewById<TextInputEditText>(R.id.telefono)
        val textbtn = findViewById<TextView>(R.id.enviarDatosText)
        lateinit var sintoma : String


        // Configuración del clic en el ícono de navegación
        toolbar.setNavigationOnClickListener {
            // Cierra esta actividad y regresa a la actividad anterior
            finish()
        }

        //obtiene los datos de la cita y los coloca en los input text (solo para no tener que llenar todos los datos nuevamente)
        citaViewModel.getCitaById(citaId).observe(this) { cita ->
            cita?.let {
                textNombreDog.setText(it.nombreMascota)
                textRazaDog.setText(it.raza)
                textNombreDueno.setText(it.nombrePropietario)
                numberoTelefono.setText(it.tele)
                sintoma = it.sintoma
            }
        }


        // Definir el TextWatcher para verificar los campos llenos
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Verificar si todos los EditText contienen información
                val isAllFilled = !textNombreDog.text.isNullOrBlank() &&
                        !textRazaDog.text.isNullOrBlank() &&
                        !textNombreDueno.text.isNullOrBlank() &&
                        !numberoTelefono.text.isNullOrBlank()

                // Si todos los EditText contienen información, habilitar el ImageButton
                btnEditarDatos.isEnabled = isAllFilled

                // Cambiar el color del texto según si todos los campos están llenos o no
                textbtn.setTextColor(if (isAllFilled) Color.WHITE else Color.BLACK)
            }
            override fun afterTextChanged(s: Editable?) {}

        }

        // Aplicar el TextWatcher a cada EditText
        textRazaDog.addTextChangedListener(textWatcher)
        textNombreDog.addTextChangedListener(textWatcher)
        textNombreDueno.addTextChangedListener(textWatcher)
        numberoTelefono.addTextChangedListener(textWatcher)

        // Configuración del clic en el botón de envío de datos
        btnEditarDatos.setOnClickListener {
            // Verificar si todos los campos están llenos y realiza una accion, en este caso actualizar la cita
            if (btnEditarDatos.isEnabled) {

                val updatedCita = Cita(
                    id = citaId,
                    nombreMascota = textNombreDog.text.toString(),
                    raza = textRazaDog.text.toString(),
                    nombrePropietario = textNombreDueno.text.toString(),
                    tele = numberoTelefono.text.toString(),
                    sintoma = sintoma,
                    img =  ""
                )
                //se pasa el modelo de cita nuevo para actualizar la bd
                citaViewModel.updateCita(updatedCita)

                //toast interactivo nada del otro mundo
                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()

                //ingresar aqui el redireccionamiento a la cita modificada

            } else {
                // Realizar alguna otra acción si no todos los campos están lleno
            }
        }

        // Deshabilitar el botón de envío de datos inicialmente
        btnEditarDatos.isEnabled = false

        setupAutoCompleteTextView()
    }

    //hice el autocompletado sin los modelos, directamente aqui
    private fun setupAutoCompleteTextView() {
        // Crear una instancia de Retrofit para realizar llamadas a la API
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/") // Especificar la URL base de la API
            .addConverterFactory(GsonConverterFactory.create()) // Agregar un convertidor de Gson para procesar los datos JSON
            .build()

        // Crear una instancia del servicio Retrofit definido por la interfaz DogService
        val service = retrofit.create(DogService::class.java)

        // Realizar una llamada asíncrona para obtener la lista de razas de perros
        service.getBreeds().enqueue(object : Callback<BreedsResponse> {
            // Manejar la respuesta
            override fun onResponse(call: Call<BreedsResponse>, response: Response<BreedsResponse>) {
                if (response.isSuccessful) {
                    // Extraer la lista de razas de perros del cuerpo de la respuesta
                    val breeds = response.body()?.message?.keys?.toList() ?: emptyList()
                    // Crear un adaptador de ArrayAdapter para el AutoCompleteTextView
                    val adapter = ArrayAdapter(this@EditActivity, android.R.layout.simple_dropdown_item_1line, breeds)
                    // Establecer el adaptador para el AutoCompleteTextView
                    binding.raza.setAdapter(adapter)
                }
            }

            // Manejar el caso de fallo en la llamada a la API
            override fun onFailure(call: Call<BreedsResponse>, t: Throwable) {
                // Mostrar un mensaje de error si no se puede obtener la lista de razas de perros
                Toast.makeText(this@EditActivity, "Error al obtener las razas de perros", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Interfaz que define las operaciones de la API para obtener la lista de razas de perros
    interface DogService {
        @GET("api/breeds/list/all")
        fun getBreeds(): Call<BreedsResponse> // Método para obtener la lista de razas de perros
    }

    // Clase de datos que representa la respuesta de la API de razas de perros
    data class BreedsResponse(
        val message: Map<String, List<String>>, // Mapa de razas de perros
        val status: String // Estado de la respuesta (éxito o fallo)
    )
}

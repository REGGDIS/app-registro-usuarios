package com.reg.evaluacionunidad2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity2 : AppCompatActivity() {

    private lateinit var textViewUsuario: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewPassword: TextView

    private lateinit var editTextUsuario: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText

    private var isEditing = false // Para saber si está en modo edición
    private lateinit var database: DatabaseReference

    // Inicializa la actividad y sus componentes.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        database = FirebaseDatabase.getInstance().getReference("users")

        val btnEditar: Button = findViewById(R.id.btnEditar)
        val btnEliminar: Button = findViewById(R.id.btnEliminar)
        val btnGuardar: Button = findViewById(R.id.btnGuardar)
        val btnVolver: Button = findViewById(R.id.btnVolver)
        val btnListar: Button = findViewById(R.id.btnListar) // Esto es nuevo

        textViewUsuario = findViewById(R.id.textViewUsuario)
        textViewEmail = findViewById(R.id.textViewEmail)
        textViewPassword = findViewById(R.id.textViewPassword)

        editTextUsuario = findViewById(R.id.editTextUsuario)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)

        toggleEditMode(false)

        // Obtener los datos extras del Intent.
        val intent = intent
        val userId = intent.getStringExtra("USER_ID")

        textViewUsuario.text = intent.getStringExtra("EXTRA_USUARIO")
        textViewEmail.text = intent.getStringExtra("EXTRA_EMAIL")
        textViewPassword.text = intent.getStringExtra("EXTRA_PASSWORD")

        btnEditar.setOnClickListener {
            isEditing = !isEditing
            toggleEditMode(isEditing)

            // Listener para validar el nombre de usuario (solo letras y números)
            editTextUsuario.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val usuario = s.toString()

                    // Expresión regular para validar que solo tenga letras y números
                    val usuarioPattern = Regex("^[A-Za-z0-9]{2,50}$")

                    if (!usuario.matches(usuarioPattern)) {
                        editTextUsuario.error = "El nombre de usuario solo puede contener letras y números (2-50 caracteres)."
                    } else {
                        editTextUsuario.error = null
                    }
                }
            })

            // Listener para validar la sintaxis del email
            editTextEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val email = s.toString()

                    // Expresión regular para validar el correo electrónico
                    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

                    if (!email.matches(emailPattern)) {
                        editTextEmail.error =
                            "Correo no válido (Debe contener una parte local, @ y un dominio)."
                    } else {
                        editTextEmail.error = null
                    }
                }
            })

            //Listener para validar la sintaxis del password
            editTextPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val password = s.toString()

                    // Expresión regular para validar la contraseña
                    val passwordPattern =
                        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$#!%*?&])[A-Za-z\\d@\$#!%*?&]{8,}$")

                    when {
                        password.length < 8 -> {
                            editTextPassword.error = "La contraseña debe tener al menos 8 caracteres."
                        }

                        !password.matches(passwordPattern) -> {
                            editTextPassword.error =
                                "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial."
                        }

                        else -> {
                            editTextPassword.error = null
                        }
                    }
                }
            })
        }

        btnEliminar.setOnClickListener {
            deleteData(userId, btnGuardar, btnEditar, btnEliminar)
        }

        btnGuardar.setOnClickListener {
            saveChanges(userId)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // Esto es nuevo
        btnListar.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Cambia entre los modos de edición y visualización.
    private fun toggleEditMode(enable: Boolean) {
        if (enable) {
            // Mostrar EditText y ocultar TextView
            textViewUsuario.visibility = TextView.GONE
            textViewEmail.visibility = TextView.GONE
            textViewPassword.visibility = TextView.GONE

            editTextUsuario.visibility = EditText.VISIBLE
            editTextEmail.visibility = EditText.VISIBLE
            editTextPassword.visibility = EditText.VISIBLE

            // Pasar los valores actuales de TextView a los EditText
            editTextUsuario.setText(textViewUsuario.text.toString())
            editTextEmail.setText(textViewEmail.text.toString())
            editTextPassword.setText(textViewPassword.text.toString())
        } else {
            // Ocultar EditText y mostrar TextView
            textViewUsuario.visibility = TextView.VISIBLE
            textViewEmail.visibility = TextView.VISIBLE
            textViewPassword.visibility = TextView.VISIBLE

            editTextUsuario.visibility = EditText.GONE
            editTextEmail.visibility = EditText.GONE
            editTextPassword.visibility = EditText.GONE
        }
    }

    // Guarda los cambios realizados en los campos de texto y actualiza la información.
    private fun saveChanges(userId: String?) {
        textViewUsuario.text = editTextUsuario.text.toString()
        textViewEmail.text = editTextEmail.text.toString()
        textViewPassword.text = editTextPassword.text.toString()

        if (!userId.isNullOrEmpty()) {
            val updatedUserData = mapOf(
                "usuario" to textViewUsuario.text.toString(),
                "email" to textViewEmail.text.toString(),
                "password" to textViewPassword.text.toString()
            )

            database.child(userId).updateChildren(updatedUserData).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Datos actualizados con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No se encontró el ID del usuario", Toast.LENGTH_SHORT).show()
        }
    }

    // Elimina la información del usuario de la base de datos de Firebase.
    private fun deleteData(
        userId: String?,
        btnEditar: Button,
        btnEliminar: Button,
        btnGuardar: Button
    ) {
        editTextUsuario.setText("")
        editTextEmail.setText("")
        editTextPassword.setText("")

        textViewUsuario.text = ""
        textViewEmail.text = ""
        textViewPassword.text = ""

        //Deshabilitar botones Guardar, Editar y Eliminar
        btnEditar.isEnabled = false
        btnEliminar.isEnabled = false
        btnGuardar.isEnabled = false

        if (userId != null) {
            database.child(userId).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Información eliminada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al eliminar la información", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No se encontró el ID del usuario", Toast.LENGTH_SHORT).show()
        }
    }
}
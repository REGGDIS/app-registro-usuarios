package com.reg.evaluacionunidad2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance().getReference("users")

        val editTextUsuario: EditText = findViewById(R.id.editTextUsuario)
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val btnIngresar: Button = findViewById(R.id.btnIngresar)
        val btnListar: Button = findViewById(R.id.btnListar)

        //Listener para validar el usuario
        editTextUsuario.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val nombre = s.toString()

                // Permite letras, números y caracteres especiales como - ' _ @.
                val nombrePattern = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9'@_.\\- ]{2,50}\$")

                if (!nombre.matches(nombrePattern)) {
                    editTextUsuario.error = "El nombre debe contener entre 2 y 50 caracteres válidos"
                } else {
                    editTextUsuario.error = null
                }
            }
        })

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

        //Listener para validar el ingreso de datos
        btnIngresar.setOnClickListener {
            val txtUsuario = editTextUsuario.text.toString()
            val txtEmail = editTextEmail.text.toString()
            val txtPassword = editTextPassword.text.toString()

            if (txtUsuario.isEmpty() || txtEmail.isEmpty() || txtPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("EXTRA_USUARIO", txtUsuario)
                intent.putExtra("EXTRA_EMAIL", txtEmail)
                intent.putExtra("EXTRA_PASSWORD", txtPassword)
                startActivityForResult(intent, 1)
                saveUserData(txtUsuario, txtEmail, txtPassword)
            }
        }

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

    // Función para ingresar datos
    private fun saveUserData(txtUsuario: String, txtEmail: String, txtPassword: String) {
        val userId = database.push().key ?: return
        val user = User(userId, txtUsuario, txtEmail, txtPassword)

        database.child(userId).setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("USER_ID", userId)
                intent.putExtra("EXTRA_USUARIO", txtUsuario)
                intent.putExtra("EXTRA_EMAIL", txtEmail)
                intent.putExtra("EXTRA_PASSWORD", txtPassword)
                startActivityForResult(intent, 1)
            } else {
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Manejar el resultado de la segunda actividad
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data?.getBooleanExtra("CLEAR_FIELDS", false) == true) {
                // Limpiar los campos si el resultado lo indica
                findViewById<EditText>(R.id.editTextUsuario).text.clear()
                findViewById<EditText>(R.id.editTextEmail).text.clear()
                findViewById<EditText>(R.id.editTextPassword).text.clear()
            }
        }
    }
}
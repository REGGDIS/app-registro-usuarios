package com.reg.evaluacionunidad2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

// Clase que representa la tercera actividad de la aplicación
class MainActivity3 : AppCompatActivity() {

    // Declaración de variables necesarias
    private lateinit var listViewUsers: ListView // Lista para mostrar los usuarios
    private lateinit var database: DatabaseReference // Referencia a la base de datos de Firebase
    private lateinit var userList: ArrayList<String> // Lista de usuarios a mostrar
    private lateinit var adapter: ArrayAdapter<String> // Adaptador para conectar la lista de usuarios con el ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        // Inicializar la referencia de Firebase a la ruta "users"
        database = FirebaseDatabase.getInstance().getReference("users")

        val btnVolver: Button = findViewById(R.id.btnVolver)

        // Vincula el ListView del layout con la variable
        listViewUsers = findViewById(R.id.listViewUsers)
        // Inicializa la lista de usuarios
        userList = ArrayList()

        // Crea un adaptador para el ListView, utilizando un layout simple para las filas
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        // Asocia el adaptador al ListView
        listViewUsers.adapter = adapter

        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Agrega un listener para recibir cambios en la base de datos
        database.addValueEventListener(object : ValueEventListener {
            // Método llamado cuando hay un cambio en los datos
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpia la lista de usuarios antes de agregar nuevos datos
                userList.clear()
                // Itera sobre los datos obtenidos de Firebase
                for (userSnapshot in snapshot.children) {
                    // Convierte cada snapshot en un objeto User
                    val userData = userSnapshot.getValue(User::class.java)
                    // Crea un texto para mostrar en el ListView
                    val displayText = "${userData?.usuario} - ${userData?.email}"
                    // Agrega el texto a la lista de usuarios
                    userList.add(displayText)
                }
                // Notifica al adaptador que los datos han cambiado para actualizar el ListView
                adapter.notifyDataSetChanged()
            }

            // Método llamado si ocurre un error al obtener los datos
            override fun onCancelled(error: DatabaseError) {
                // Muestra un mensaje de error
                Toast.makeText(this@MainActivity3, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
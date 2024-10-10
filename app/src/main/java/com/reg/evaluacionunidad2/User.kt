package com.reg.evaluacionunidad2

data class User(
    val id: String = "",
    val usuario: String = "",
    val email: String = "",
    val password: String = "",
    /*val rut: String = "",
    val phone: String = "",
    val address: String = ""*/
) {
    // Constructor sin argumentos // ESTO ES NUEVO
    constructor() : this("", "", "")
}

//Cuando defines valores predeterminados para todos los parámetros en el constructor de una clase (por ejemplo, en User),
// permites que se cree una instancia de la clase sin necesidad de pasar argumentos. Esto es esencial para Firebase, ya que
// requiere un constructor sin argumentos para deserializar los datos almacenados.
//
//Además, al agregar un constructor adicional sin parámetros, le das a Firebase la capacidad de instanciar objetos de User
// cuando los recupera de la base de datos. Así, aseguras la compatibilidad y evitas errores como el que mencionabas.
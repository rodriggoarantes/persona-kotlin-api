package br.com.in6.kotlin.domain.users

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

/**
 * BOAS PRATICAS PARA KOTLIN
 */
// https://github.com/phauer/blog-related/tree/master/kotlin-idiomatic
class Basic() {
    private val LOG = LoggerFactory.getLogger(javaClass)

    val listaUsuarios: MutableList<Usuario> = ArrayList(0)

    fun find(name: String, recursive: Boolean = true) : String {
        return " $name :: $recursive "
    }

    fun usuario(id: Long = 1, name: String = "Rodrigo") : Usuario {
        return Usuario(
                id = id,
                name = name
        )
    }

    fun geral() {
        LOG.debug("Funcao geral para debug de melhores praticas de Kotlin")

        val user = usuario()

        var user2 = Usuario(id = 1, name="rodrigo")
        var userDto = user2.toDTO()

        // find com sobreescrita
        find("rodrigo")
        find ("rodrigo", false)

        // if-null
        val userMaybeNull = user2?.id ?: throw IllegalArgumentException("Invalid User")

        // inline classes
        email(EmailAddress("rodriggoarantes@gmail.com"))

        "teste de metodo add a String".countAmountOfX()

        print("Print date ${LocalDate.now()}")
    }

    fun email(target: EmailAddress) : String {
        LOG.debug("Envia email :: Usando inline-class")
        return target.value
    }

    fun transformWhen(color: String) : Int {
        LOG.debug("Usa When para realizar o retorno sem if e elses")
        return when (color) {
            "Red" -> 0
            "Green" -> 1
            "Blue" -> 2
            else -> throw IllegalArgumentException("Invalid color param value")
        }
    }
    // expressao in line
    fun defaultLocaleWhen(deliveryArea: String) = when (deliveryArea.toLowerCase()) {
        "germany", "austria" -> Locale.GERMAN
        "usa", "great britain" -> Locale.ENGLISH
        "france" -> Locale.FRENCH
        else -> Locale.ENGLISH
    }

    fun findIndex(id: Long) {
        LOG.debug("Usa LET para realizar açao apenas se nao for NULL")
        val index : Int = listaUsuarios.indexOfFirst { u -> u.id == id }
        index?.let {
            listaUsuarios.removeAt(index)
        }
    }

    // Filtro com Expressoes inline
    fun filter(id: Long) : Usuario? {
        LOG.debug("Filter Lista :: {}", id)
        return listaUsuarios.filter { it.id == id }.firstOrNull()
    }
    fun doubleList(values: Array<Int>) : List<Int> {
        return values.map { it * 2 }
    }

    fun tryCathExpression() {
        LOG.debug("try in LINE")

        val json = """{"message":"HELLO"}"""
        val message = try {
            jacksonObjectMapper().readValue<Map<String, Any>>(json)["message"]
        } catch (ex: Exception) {
            json
        }
    }

    // extendendo a funcionalidade do string
    fun String.countAmountOfX() : Int {
        LOG.debug("extendendo a funcionalidade do string")
        return length - replace("x", "").length
    }

    fun destructuring() {
        data class ServiceConfig(val host: String, val port: Int)
        fun createServiceConfig() : ServiceConfig {
            return ServiceConfig("api.domain.io", 9389)
        }

        //destructuring in action:
        val (host, port) = createServiceConfig()

        //Do
        val customer = mapOf(
                "name" to "Clair Grube",
                "age" to 30,
                "languages" to listOf("german", "english"),
                "server" to mapOf(
                        "host" to host,
                        "port" to port
                )
        )
        for ((key, value) in customer) {
            print(key)
        }
    }

    /* Sealed Classes Instead of Exceptions */
    /*Definition*/
    sealed class UserProfileResult {
        data class Success(val userProfile: UsuarioDto) : UserProfileResult()
        data class Error(val message: String, val cause: Exception? = null) : UserProfileResult()
    }
    // Generic
    sealed class Outcome<out T : Any> {
        data class Success<out T : Any>(val value: T) : Outcome<T>()
        data class Error(val message: String, val cause: Exception? = null) : Outcome<Nothing>()
    }

    fun requestUserProfile(userId: String) : UserProfileResult = try {
        val userProfile = usuario()
        UserProfileResult.Success(userProfile = userProfile.toDTO())
    } catch (ex: Exception) {
        UserProfileResult.Error(
                message = "Server request failed. Id: $userId.",
                cause = ex
        )
    }


    fun sealedClasses() {
        // Usage
        val avatarUrl = when (val result = requestUserProfile("rodrigo")) {
            is UserProfileResult.Success -> result.userProfile.name
            is UserProfileResult.Error -> "Desconhecido"
        }
    }
}

inline class EmailAddress(val value: String)
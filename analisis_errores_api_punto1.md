# 🔍 Análisis de Errores - Aplicación API (Punto 1.a)

## Resumen de errores encontrados: **7 errores intencionales**

---

## Error 1: `pom.xml` — Dependencias con scope `test` incorrecto

**Archivo:** [pom.xml](file:///c:/Users/usuario/Downloads/challenge_monnet/challenge/pom.xml)

```diff
 <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter</artifactId>
-    <scope>test</scope>
 </dependency>
 <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-web</artifactId>
-    <scope>test</scope>
 </dependency>
```

> [!CAUTION]
> `spring-boot-starter` y `spring-boot-starter-web` tenían `<scope>test</scope>`, lo que significa que **solo estarían disponibles durante la ejecución de tests**, no en compilación ni en runtime. Sin estas dependencias en el scope correcto (por defecto `compile`), la aplicación no compila ni levanta.

---

## Error 2: `application.yml` — Puerto incorrecto

**Archivo:** [application.yml](file:///c:/Users/usuario/Downloads/challenge_monnet/challenge/src/main/resources/application.yml)

```diff
 server:
-  port: 8080
+  port: 8099
```

> [!IMPORTANT]
> El requisito exige que el endpoint sea accesible en `http://localhost:8099/challenge/greet/`. El puerto estaba configurado en `8080`.

---

## Error 3: `ApiApplication.java` — `scanBasePackages` apunta al paquete equivocado

**Archivo:** [ApiApplication.java](file:///c:/Users/usuario/Downloads/challenge_monnet/challenge/src/main/java/com/monnetpayments/challenge/api/ApiApplication.java)

```diff
-@SpringBootApplication(scanBasePackages = "com.monnetpayments.challenge.console.*")
+@SpringBootApplication(scanBasePackages = "com.monnetpayments.challenge.api")
```

> [!CAUTION]
> El `scanBasePackages` apuntaba a `com.monnetpayments.challenge.console.*` (la aplicación consola) en lugar del paquete de la API. Esto impedía que Spring detectara los `@RestController`, `@Service`, `@Configuration` y `@Bean` definidos en el paquete `api`. Además, el uso de `.*` en scanBasePackages es incorrecto — se espera un nombre de paquete, no un patrón glob.

---

## Error 4: `ApiApplication.java` — Método `main` no es `static`

**Archivo:** [ApiApplication.java](file:///c:/Users/usuario/Downloads/challenge_monnet/challenge/src/main/java/com/monnetpayments/challenge/api/ApiApplication.java)

```diff
-    public void main(String[] args) {
+    public static void main(String[] args) {
```

> [!CAUTION]
> El método `main` debe ser `static` para que la JVM pueda invocarlo como punto de entrada de la aplicación. Sin `static`, la aplicación no puede arrancar.

---

## Error 5: `SpainQ.java` — Recursión infinita + texto incorrecto

**Archivo:** [SpainQ.java](file:///c:/Users/usuario/Downloads/challenge_monnet/challenge/src/main/java/com/monnetpayments/challenge/api/domain/SpainQ.java)

```diff
     @Override
     public String greet() {
-        return "hola!!!!!! " + this.greet();
+        return "hola!!!!";
     }
```

> [!CAUTION]
> **Dos problemas en una línea:**
> 1. **Recursión infinita:** `this.greet()` se llama a sí mismo, provocando un `StackOverflowError` en runtime.
> 2. **Texto incorrecto:** Tenía `"hola!!!!!! "` (6 signos de exclamación + espacio), pero el resultado esperado es `"hola!!!!"` (4 signos de exclamación).

---

## Error 6: `Config.java` — Múltiples beans de tipo `Q` sin `@Primary`

**Archivo:** [Config.java](file:///c:/Users/usuario/Downloads/challenge_monnet/challenge/src/main/java/com/monnetpayments/challenge/api/config/Config.java)

```diff
+    @Primary
     @Bean
     Q getQ(){
         return new SpainQ();
     }
```

> [!CAUTION]
> Hay **3 beans** de tipo `Q` registrados (`getQ`, `getQ2`, `getQ3`). El `ServiceWithQ` inyecta `Q` con `@Autowired` sin `@Qualifier`, lo que causa una `NoUniqueBeanDefinitionException` porque Spring no sabe cuál inyectar. Se agrega `@Primary` al bean `getQ()` (SpainQ) ya que el resultado esperado es `"hola!!!!"`.

---

## Error 7: `Config.java` — Clase `@Configuration` declarada como `final`

**Archivo:** [Config.java](file:///c:/Users/usuario/Downloads/challenge_monnet/challenge/src/main/java/com/monnetpayments/challenge/api/config/Config.java)

```diff
 @Configuration
-public final class Config {
+public class Config {
```

> [!CAUTION]
> Las clases anotadas con `@Configuration` no pueden ser `final`. Spring necesita crear un **proxy CGLIB** (subclase) de la clase de configuración para interceptar las llamadas a métodos `@Bean` y garantizar que devuelvan siempre la misma instancia singleton. Al ser `final`, la clase no puede ser subclasificada, lo que provoca un `BeanDefinitionParsingException` al iniciar el contexto de Spring.

---

## Resumen de archivos modificados

| # | Archivo | Error | Tipo |
|---|---------|-------|------|
| 1 | `pom.xml` | `scope=test` en dependencias principales | Compilación |
| 2 | `application.yml` | Puerto 8080 en vez de 8099 | Configuración |
| 3 | `ApiApplication.java` | `scanBasePackages` apunta a paquete incorrecto | Contexto Spring |
| 4 | `ApiApplication.java` | `main` no es `static` | Arranque JVM |
| 5 | `SpainQ.java` | Recursión infinita + texto incorrecto | Runtime |
| 6 | `Config.java` | Ambigüedad de beans (falta `@Primary`) | Inyección de dependencias |
| 7 | `Config.java` | Clase `@Configuration` declarada como `final` | Contexto Spring |

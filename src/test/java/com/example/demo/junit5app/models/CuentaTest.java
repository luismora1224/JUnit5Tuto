/**
 * 
 */
package com.example.demo.junit5app.models;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.demo.junit5app.exceptions.DineroInsuficienteException;

/**
 * @author Luis EMora
 *
 */
class CuentaTest {
	Cuenta cuenta;
	
	@BeforeEach
	void initMetodoTest() {
		this.cuenta = new Cuenta("Luis", new BigDecimal("1000.12345"));
		System.out.println("Iniciando el metodo");
	}
	
	@AfterEach
	void tearDown() {
		System.out.println("Finalizando el metodo de prueba");
	}
	
	@BeforeAll
	static void beforeAll() {
		System.out.println("Iniciando el test");
	}
	
	@AfterAll
	static void afterAll() {
		System.out.println("Finalizando el test");
	}
	
	@Nested
	@DisplayName("Probando atributos de la cuenta")
	class CuentaTestNombreSaldo{
		
		@Test
		@DisplayName("Probando el nombre de la cuenta corriente")
		void testNombreCuenta() {
			
			//Cuenta cuenta = new Cuenta("Luis", new BigDecimal("1000.12345"));
			cuenta.setPersona("Luis");
			String esperado = "Luis";
			String real = cuenta.getPersona();
			assertNotNull(real, () ->  "La cuenta no puede ser nula");
			//Assertions.assertEquals(esperado, real);
			assertEquals(esperado, real,() -> "El nombre de la cuenta no es que se esperaba");
			assertTrue(real.equals("Luis"), () -> "Nombre de la cuenta esperada debe ser igual a la real");
		}
		
		@Test
		@DisplayName("Comprobando saldo de la cuenta")
		void testSaldoCuenta() {
			//Cuenta cuenta = new Cuenta("Luis", new BigDecimal("1000.987"));
			cuenta = new Cuenta("Luis", new BigDecimal("1000.987"));
			assertNotNull(cuenta.getSaldo());
			assertEquals(1000.987, cuenta.getSaldo().doubleValue());
			assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		}
		
		@Test
		@DisplayName("Verificando que instacias sean iguales")
		void testReferenciaCuenta() {
			//Cuenta cuenta = new Cuenta("Juan", new BigDecimal("9800.12345"));
			cuenta = new Cuenta("Juan", new BigDecimal("9800.12345"));
			Cuenta cuenta2 = new Cuenta("Juan", new BigDecimal("9800.12345"));
			
			//assertNotEquals(cuenta, cuenta2);
			assertEquals(cuenta, cuenta2);
		}
	}

	@Nested
	class CuentaOperacionesTest{
		
		@Test
		@DisplayName("Realiza el resta de saldo a la cuenta")
		void testDebitoCuenta() {
			Cuenta cuenta = new Cuenta("Luis", new BigDecimal("1000.12345"));
			cuenta.debito(new BigDecimal(100));
			assertNotNull(cuenta.getSaldo());
			assertEquals(900, cuenta.getSaldo().intValue());
			assertEquals("900.12345", cuenta.getSaldo().toPlainString());
		}
		
		@Test
		@DisplayName("Realiza agregacion de saldo a la cuenta")
		void testCreditoCuenta() {
			//Cuenta cuenta = new Cuenta("Luis", new BigDecimal("1000.12345"));
			cuenta.credito(new BigDecimal(100));
			assertNotNull(cuenta.getSaldo());
			assertEquals(1100, cuenta.getSaldo().intValue());
			assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
		}
		
		@Test
		@DisplayName("Transfiere dinero entre cuentas")
		void testTransferirDineroCuentas() {
			Cuenta destino = new Cuenta("Luis", new BigDecimal("2500"));
			Cuenta origen = new Cuenta("Enrique", new BigDecimal("1500.8989"));
			
			Banco banco = new Banco();
			banco.setNombre("Banco del estado");
			banco.transferir(origen, destino, new BigDecimal(500));
			assertEquals("1000.8989", origen.getSaldo().toPlainString());
			assertEquals("3000", destino.getSaldo().toPlainString());
		}
	}
	

	@Test
	@DisplayName("Verifica que la cuenta tenga saldo suficiente")
	void testDineroInsuficienteExceptionCuenta() {
		//Cuenta cuenta = new Cuenta("Luis", new BigDecimal("1000.1235"));
		Exception exception = assertThrows(DineroInsuficienteException.class, ()-> {
			cuenta.debito(new BigDecimal(1500));
		});
		String actual = exception.getMessage();
		String esperado = "Dinero Insuficiente";
		assertEquals(esperado, actual);
	}
	

	
	@Test
	@Disabled // desabilita este metodo para que no sea testeado por si tiene alguna falla y luego seguir con el
	@DisplayName("Realiza relacion entre cuentas y verifica existencia de datos")
	void testRelacionBancoCuentas() {
		fail(); //genera un fallo inmediato
		Cuenta destino = new Cuenta("Luis", new BigDecimal("2500"));
		Cuenta origen = new Cuenta("Enrique", new BigDecimal("1500.8989"));
		
		Banco banco = new Banco();
		banco.addCuenta(origen);
		banco.addCuenta(destino);
		
		banco.setNombre("Banco del estado");
		banco.transferir(origen, destino, new BigDecimal(500));
		
		assertAll(
				() -> {assertEquals("1000.8989", origen.getSaldo().toPlainString());},
				() -> {assertEquals("3000", destino.getSaldo().toPlainString());},
				() -> {assertEquals(2, banco.getCuentas().size());},
				() -> {assertEquals("Banco del estado", destino.getBanco().getNombre());},
				() -> {
					//manera de verificar si esta nombre luis en alguna cuenta de tres maneras distintas
					assertEquals("Luis", banco.getCuentas().stream()
							.filter(c -> c.getPersona().equals("Luis"))
							.findFirst()
							.get()
							.getPersona()
							);
				},
				() -> {
					assertTrue(banco.getCuentas().stream()
							.filter(c -> c.getPersona().equals("Luis"))
							.findFirst()
							.isPresent()
							);
				},
				() -> {
					assertTrue(banco.getCuentas().stream()
							.anyMatch(c -> c.getPersona().equals("Luis"))
							);
				}
				);	

	}
	
	//decalramos como una clase anidada con @Nested
	@Nested
	class SistemaOperativoTest{
		
		@Test
		@EnabledOnOs(OS.WINDOWS)
		void testSoloWindows() {
			
		}
		
		@Test
		@EnabledOnOs({OS.LINUX, OS.MAC})
		void testSoloLinuxMac() {
			
		}
		
		@Test
		@DisabledOnOs(OS.WINDOWS)
		void testNoWindows() {
			
		}
	}
	
	@Nested
	class JavaVersionTest{
		
		@Test
		@EnabledOnJre(JRE.JAVA_8)
		void testJdk8() {
			
		}
		
		@Test
		@EnabledOnJre(JRE.JAVA_15)
		void testJdk15() {
			
		}
		
		@Test
		@DisabledOnJre(JRE.JAVA_8)
		void testJreDisabled() {
			
		}
	}
	
	@Nested
	class SistemPropertiesTest{
		
		//imprimir system properties
		@Test
		void imprimirSystemProperties() {
			Properties properties = System.getProperties();
			properties.forEach((k, v) -> System.out.println(k + ":" + v));
		}
		
		@Test
		@EnabledIfSystemProperty(named = "java.version", matches = ".*1.*")
		void testJavaVersion() {
			
		}
		
		@Test
		@DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
		void testSolo64() {
			
		}
		
		@Test
		@EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
		void testNo64() {
			
		}
		
		@Test
		@EnabledIfSystemProperty(named = "user.name", matches = "107284781")
		void tesUsername() {
			
		}
		
		@Test
		@EnabledIfSystemProperty(named = "ENV", matches = "dev")
		void testDev() {
			
		}
	}
	
	@Nested
	class VariableAmbienteTest{
		
		//imprimir system properties
		@Test
		void imprimirSystemProperties() {
			Properties properties = System.getProperties();
			properties.forEach((k, v) -> System.out.println(k + ":" + v));
		}
		
		@Test
		@EnabledIfSystemProperty(named = "java.version", matches = ".*1.*")
		void testJavaVersion() {
			
		}
		
		@Test
		@DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
		void testSolo64() {
			
		}
		
		@Test
		@EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
		void testNo64() {
			
		}
		
		@Test
		@EnabledIfSystemProperty(named = "user.name", matches = "107284781")
		void tesUsername() {
			
		}
		
		@Test
		@EnabledIfSystemProperty(named = "ENV", matches = "dev")
		void testDev() {
			
		}
		
		//impresion de variables de ambiente
		@Test
		void imprimirVariablesAmbiente() {
			Map<String, String> getenv = System.getenv();
			getenv.forEach((k, v) -> System.out.println(k + " = " + v));
		}
		
		@Test
		@EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-1.*")
		void testJavaHome() {
			
		}
		// limitar una prueba de acuerdo al numero de procesadores
		@Test
		@EnabledIfEnvironmentVariable(named = "PROCESSOR_LEVEL", matches = "6")
		void testProcesadores() {
			
		}
		
		@Test
		@EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
		void testEnv() {
			
		}
		
		@Test
		@DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
		void testEnvProdDisabled() {
			
		}
	}

	
	@Test
	@DisplayName("Comprobando saldo de la cuenta Dev")
	void testSaldoCuentaDev() {
		//Cuenta cuenta = new Cuenta("Luis", new BigDecimal("1000.987"));
		cuenta = new Cuenta("Luis", new BigDecimal("1000.987"));
		boolean esDev = "dev".equals(System.getProperty("ENV"));
		assumeTrue(esDev);
		assertNotNull(cuenta.getSaldo());
		assertEquals(1000.987, cuenta.getSaldo().doubleValue());
		assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
	}
	
	@Test
	@DisplayName("Comprobando saldo de la cuenta Dev 2")
	void testSaldoCuentaDev2() {
		//Cuenta cuenta = new Cuenta("Luis", new BigDecimal("1000.987"));
		cuenta = new Cuenta("Luis", new BigDecimal("1000.987"));
		boolean esDev = "dev".equals(System.getProperty("ENV"));
		
		//si esDev es verdadero entra a realizar pruebas con los assers , si es falso no entra
		assumingThat(esDev, () -> { 
			assertNotNull(cuenta.getSaldo());
			assertEquals(1000.987, cuenta.getSaldo().doubleValue());
			assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		});
	}
	
	//La anotacion @RepeatedTest() sirve para repetir una cantidad de veces un test y probar casos de uso
	@RepeatedTest(value=5, name = "Repeticion numero {currentRepetition} de {totalRepetitions}")
	@DisplayName("Repite Test")
	void testDebitoCuenta(RepetitionInfo info) {
		if(info.getCurrentRepetition() == 3){
			System.out.println("Estamos en la repeticion " + info.getCurrentRepetition());
		}
		Cuenta cuenta = new Cuenta("Luis", new BigDecimal("1000.12345"));
		cuenta.debito(new BigDecimal(100));
		assertNotNull(cuenta.getSaldo());
		assertEquals(900, cuenta.getSaldo().intValue());
		assertEquals("900.12345", cuenta.getSaldo().toPlainString());
	}
	
	//incluyendo un test con parametros
	@ParameterizedTest
	@ValueSource(strings = {"ABCD", "AB", "DEF", "GHI"})
	void testLengthStringUsingParameters(String str) {
		assertTrue(str.length()>0);
	}
	
	//incluyendo Csv a un test
	@ParameterizedTest
	@CsvSource(value= {"abcd,ABCD","abc,ABC","'',''","abcdef,ABCDEF"})
	void testUpperCase(String word, String capitalizedWord) {
		assertEquals(capitalizedWord, word.toUpperCase());
	}
	
	//agregando una nota en salida del test ejemplo "abcd length is 4
	@ParameterizedTest(name="{0} length is {1}")
	@CsvSource(value= {"abcd,4","abc,3","'',0","abcdef,6"})
	void testLength(String word, int expectedLength) {
		assertEquals(expectedLength, word.length());
	}
	
	//realizando test de rendimiento
	@Test
	void testPerformance() {
		assertTimeout(Duration.ofSeconds(5), ()->{
			for(int i=0;i<100000;i++) {
				int j = i;
				System.out.println(j);
			}
		}
	    );
	}
	
	@Test
	void TestArraySortRandomValues() {
		int[] numbers = {1,2,3,4,5};
		int[] expected = {3,4,5,2,1};
		Arrays.sort(expected); //ordenamos el arreglo
		assertArrayEquals(expected,numbers);
	}
}

package jp.co.axa.apidemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.axa.apidemo.entities.Employee;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:application.properties")
public class ApiDemoApplicationTests {
	@Autowired
	private TestRestTemplate restTemplate;

	@Value("${spring.security.user.name}")
	private String username;

	@Value("${spring.security.user.password}")
	private String password;

	@Test
	public void testGetEmployees() throws Exception  {
		ResponseEntity<String> response = restTemplate.withBasicAuth(username, password)
				.getForEntity("/api/v1/private/employees", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		String responseBody = response.getBody();
		assertThat(responseBody).isNotNull();
	}
	@Test
	public void testGetEmployee() throws Exception  {

		Employee expectedEmployee = new Employee(1, "John", 10000, "IT");
		ResponseEntity<Employee> response = restTemplate.withBasicAuth(username, password)
				.getForEntity("/api/v1/private/employees", Employee.class);
		Employee actualEmployee = response.getBody();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(actualEmployee).isEqualTo(expectedEmployee);
	}
	@Test
	public void testInsertEmployee() throws Exception  {
		Employee insertEmployee = new Employee(null, "John", 10000, "IT");
		ResponseEntity<Employee> response = restTemplate.withBasicAuth(username, password)
				.postForEntity("/api/v1/private/employees",insertEmployee, Employee.class);
		Employee actualEmployee = response.getBody();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(actualEmployee).isEqualTo(insertEmployee);
	}
	@Test
	public void testUpdateEmployee() throws Exception  {
		final Employee newCustomer = new Employee(99, "Margaret McGuinness", 27,"HR");
		restTemplate.postForEntity("/api/v1/private/employees", newCustomer, Void.class);
		Employee updated = new Employee(1, "Jerry", 10000, "IT");
		ResponseEntity<Void> response = restTemplate.withBasicAuth(username, password)
				.exchange("/api/v1/private/employees/1", HttpMethod.PUT,
						new HttpEntity<>(updated),
						Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(response.getBody()).isNull();
	}
	@Test
	public void testDeleteEmployee() throws Exception  {
		ResponseEntity<Void> response = restTemplate.withBasicAuth(username, password)
				.exchange("/api/v1/private/employees/1", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}
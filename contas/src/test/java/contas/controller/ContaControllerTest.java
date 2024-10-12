package contas.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import contas.model.Conta;
import contas.service.ContaService;

@WebMvcTest(ContaController.class)
public class ContaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ContaService contaService;

	private Conta conta;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		conta = new Conta();
		conta.setId(1L);
		conta.setTitular("Titular Test");
		conta.setSaldo(0.0);
	}

	@Test
	public void testCriarConta() throws Exception {
		when(contaService.criaConta("Titular Test")).thenReturn(conta);

		mockMvc.perform(post("/contas").param("titular", "Titular Test").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.titular").value("Titular Test"))
				.andExpect(jsonPath("$.saldo").value(0.0)).andDo(print());

		verify(contaService, times(1)).criaConta("Titular Test");
	}

	@Test
	public void testCreditar() throws Exception {
		doNothing().when(contaService).creditar(1L, 100.0);

		mockMvc.perform(post("/contas/1/creditar").param("valor", "100.0").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print());

		verify(contaService, times(1)).creditar(1L, 100.0);
	}

	@Test
	public void testDebitar() throws Exception {
		doNothing().when(contaService).debitar(1L, 50.0);

		mockMvc.perform(post("/contas/1/debitar").param("valor", "50.0").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print());

		verify(contaService, times(1)).debitar(1L, 50.0);
	}

	@Test
	public void testTransferir() throws Exception {
		doNothing().when(contaService).transferir(1L, 2L, 100.0);

		mockMvc.perform(post("/contas/transferir").param("idOrigem", "1").param("idDestino", "2")
				.param("valor", "100.0").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andDo(print());

		verify(contaService, times(1)).transferir(1L, 2L, 100.0);
	}

}

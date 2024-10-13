package contas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import contas.model.Conta;
import contas.repitory.ContaRepository;

public class ContaServiceTest {

	@InjectMocks
	private ContaService contaService;

	@Mock
	private ContaRepository contaRepository;

	private Conta contaNova;
	private Conta contaOrigem;
	private Conta contaDestino;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		contaNova = new Conta();
		contaNova.setId(1L);
		contaNova.setTitular("Titular Test");
		contaNova.setSaldo(0.0);

		contaOrigem = new Conta();
		contaOrigem.setId(1L);
		contaOrigem.setTitular("Titular Origem");
		contaOrigem.setSaldo(200.0);

		contaDestino = new Conta();
		contaDestino.setId(2L);
		contaDestino.setTitular("Titular Destino");
		contaDestino.setSaldo(0.0);
	}

	@Test
	public void testCriaConta() {
		when(contaRepository.save(any(Conta.class))).thenReturn(contaNova);

		Conta novaConta = contaService.criaConta("Titular Test");

		assertNotNull(novaConta);
		assertEquals("Titular Test", novaConta.getTitular());
		assertEquals(0.0, novaConta.getSaldo());
		verify(contaRepository, times(1)).save(any(Conta.class));
	}
	
    @Test
    public void testVisualizarConta() {
        Long contaId = 1L;
        Conta conta = new Conta();
        conta.setId(contaId);
        conta.setTitular("Titular Test");
        conta.setSaldo(100.0);

        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));

        Conta resultado = contaService.visualizarConta(contaId);

        assertNotNull(resultado);
        assertEquals("Titular Test", resultado.getTitular());
        assertEquals(100.0, resultado.getSaldo());
        verify(contaRepository, times(1)).findById(contaId);
    }

    @Test
    public void testVisualizarContaNoExistente() {
        Long contaId = 1L;

        when(contaRepository.findById(contaId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            contaService.visualizarConta(contaId);
        });

        assertEquals("Account not found: " + contaId, exception.getMessage());
        verify(contaRepository, times(1)).findById(contaId);
    }

	@Test
	public void testCreditar() {
		when(contaRepository.findByIdWithLock(1L)).thenReturn(Optional.of(contaOrigem)); 
	    when(contaRepository.save(contaOrigem)).thenReturn(contaOrigem);

	    contaOrigem.setSaldo(200.0); 
	    contaService.creditar(1L, 100.0);

	    assertEquals(300.0, contaOrigem.getSaldo()); 
	    verify(contaRepository, times(1)).findByIdWithLock(1L);
	    verify(contaRepository, times(1)).save(contaOrigem); 
	}

	@Test
	public void testDebitar() {
	    contaOrigem.setSaldo(200.0); 
	    when(contaRepository.findByIdWithLock(1L)).thenReturn(Optional.of(contaOrigem)); 
	    when(contaRepository.save(contaOrigem)).thenReturn(contaOrigem);

	    contaService.debitar(1L, 100.0); 

	    assertEquals(100.0, contaOrigem.getSaldo()); 
	    verify(contaRepository, times(1)).findByIdWithLock(1L);
	    verify(contaRepository, times(1)).save(contaOrigem); 
	}

	@Test
	public void testDebitar_SaldoInsuficiente() {
	    when(contaRepository.findByIdWithLock(1L)).thenReturn(Optional.of(contaOrigem)); 
	    contaOrigem.setSaldo(200.0); 

	    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
	        contaService.debitar(1L, 300.0); 
	    });

	    assertEquals("Saldo insuficiente!", exception.getMessage());
	    verify(contaRepository, times(1)).findByIdWithLock(1L); 
	    verify(contaRepository, never()).save(any(Conta.class)); 
	}

	@Test
	public void testTransferir() {
	    when(contaRepository.findByIdWithLock(1L)).thenReturn(Optional.of(contaOrigem));
	    when(contaRepository.findByIdWithLock(2L)).thenReturn(Optional.of(contaDestino));
	    when(contaRepository.save(contaOrigem)).thenReturn(contaOrigem);
	    when(contaRepository.save(contaDestino)).thenReturn(contaDestino);

	    contaOrigem.setSaldo(200.0);
	    contaDestino.setSaldo(0.0);

	    contaService.transferir(1L, 2L, 100.0);

	    assertEquals(100.0, contaOrigem.getSaldo()); 
	    assertEquals(100.0, contaDestino.getSaldo());
	    verify(contaRepository, times(2)).findByIdWithLock(1L); 
	    verify(contaRepository, times(2)).findByIdWithLock(2L); 
	    verify(contaRepository, times(1)).save(contaOrigem); 
	    verify(contaRepository, times(1)).save(contaDestino);
	}

	@Test
	public void testTransferir_ContaOrigemInexistente() {
	    when(contaRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

	    Exception exception = assertThrows(RuntimeException.class, () -> {
	        contaService.transferir(1L, 2L, 100.0);
	    });

	    assertEquals("Account not found: 1", exception.getMessage());
	    verify(contaRepository, times(1)).findByIdWithLock(1L); 
	    verify(contaRepository, never()).findByIdWithLock(2L);
	}

	@Test
	public void testTransferir_ContaDestinoInexistente() {
	    when(contaRepository.findByIdWithLock(1L)).thenReturn(Optional.of(contaOrigem));
	    when(contaRepository.findByIdWithLock(2L)).thenReturn(Optional.empty());

	    Exception exception = assertThrows(RuntimeException.class, () -> {
	        contaService.transferir(1L, 2L, 100.0);
	    });

	    assertEquals("Account not found: 2", exception.getMessage());
	    verify(contaRepository, times(1)).findByIdWithLock(1L); 
	    verify(contaRepository, times(1)).findByIdWithLock(2L);
	}

}
